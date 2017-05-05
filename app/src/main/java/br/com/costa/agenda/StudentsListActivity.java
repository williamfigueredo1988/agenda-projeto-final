package br.com.costa.agenda;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import br.com.costa.agenda.adapter.StudentAdapter;
import br.com.costa.agenda.client.WebClient;
import br.com.costa.agenda.converter.StudentConverter;
import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;
import br.com.costa.agenda.task.SendStudentTask;

public class StudentsListActivity extends AppCompatActivity{

    ListView studentListView;

    @Override
    protected void onResume() {
        super.onResume();
        buildStudentsList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        studentListView = (ListView) findViewById(R.id.studentsList_listViewStudents);

        studentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View item, int position, long id) {
                Student student = (Student) studentListView.getItemAtPosition(position);
                Intent intentStudentInsert = new Intent(StudentsListActivity.this, StudentsInsertActivity.class);
                intentStudentInsert.putExtra("student", student);
                startActivity(intentStudentInsert);
            }
        });

        Button newButton = (Button) findViewById(R.id.studentsInsert_buttonNew);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentStudentInsert = new Intent(StudentsListActivity.this, StudentsInsertActivity.class);
                startActivity(intentStudentInsert);
            }
        });

        registerForContextMenu(studentListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, final ContextMenu.ContextMenuInfo menuInfo) {
        MenuItem deleteMenuItem = menu.add("Delete");
        MenuItem goToSiteMenuItem = menu.add("Go To Site");
        MenuItem sendSMSMenuItem = menu.add("Send SMS");
        MenuItem showGeoMenuItem = menu.add("Location");
        MenuItem callMenuItem = menu.add("Call");

        AdapterView.AdapterContextMenuInfo adapterMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Student student = (Student) studentListView.getItemAtPosition(adapterMenuInfo.position);


        buildGoToSite(goToSiteMenuItem, student);

        sendSMS(sendSMSMenuItem, student);

        geoLocation(showGeoMenuItem, student);

        buildCall(callMenuItem, student);

        deleteContact((AdapterView.AdapterContextMenuInfo) menuInfo, deleteMenuItem);
    }

    private void buildCall(final MenuItem callMenuItem, final Student student) {

        callMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (ActivityCompat.checkSelfPermission(StudentsListActivity.this, Manifest.permission.CALL_PHONE )!= PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(StudentsListActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 123);
                }else {
                    Intent call = new Intent(Intent.ACTION_CALL);
                    call.setData(Uri.parse("tel:" + student.getNumber()));
                    startActivity(call);
                }
                return false;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if( requestCode == 123){

            System.out.println("Chamada");

        }else if(requestCode == 5671) {

        }

    }

    private void call(MenuItem callMenuItem, String actionCall, Uri parse) {
        Intent callIntent = new Intent(actionCall);
        callIntent.setData(parse);
        callMenuItem.setIntent(callIntent);
    }

    private void geoLocation(MenuItem showGeoMenuItem, Student student) {
        Intent showAddressIntent = new Intent(Intent.ACTION_VIEW);
        showAddressIntent.setData(Uri.parse("geo:0,0?q="+student.getAddress()));
        showGeoMenuItem.setIntent(showAddressIntent);
    }

    private void sendSMS(MenuItem sendSMSMenuItem, Student student) {
        Intent sendSMSIntent = new Intent(Intent.ACTION_VIEW);
        sendSMSIntent.setData(Uri.parse("sms:"+student.getNumber()));
        sendSMSMenuItem.setIntent(sendSMSIntent);
    }

    private void deleteContact(final AdapterView.AdapterContextMenuInfo menuInfo, MenuItem deleteMenuItem) {
        deleteMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AdapterView.AdapterContextMenuInfo adapterMenuInfo = menuInfo;
                Student student = (Student) studentListView.getItemAtPosition(adapterMenuInfo.position);

                StudentDAO studentDAO = new StudentDAO(StudentsListActivity.this);
                studentDAO.delete(student.getId());
                studentDAO.close();

                buildStudentsList();

                Toast.makeText(StudentsListActivity.this, "Aluno "  + student.getName() + " removido!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void buildGoToSite(MenuItem goToSiteMenuItem, Student student) {
        Intent goToSiteIntent = new Intent(Intent.ACTION_VIEW);
        String site = student.getSite();
        if(!site.startsWith("http://")){
            goToSiteIntent.setData(Uri.parse("http://"+student.getSite()));
        }
        else if (!site.startsWith("https://")){
            goToSiteIntent.setData(Uri.parse("https://"+student.getSite()));
        }
        else{
            goToSiteIntent.setData(Uri.parse(site));
        }
        goToSiteMenuItem.setIntent(goToSiteIntent);
    }

    private void buildStudentsList() {

        StudentDAO studentDAO = new StudentDAO(StudentsListActivity.this);
        List<Student> studentList = studentDAO.read();
        studentDAO.close();

        StudentAdapter studentsListViewAdapter = new StudentAdapter(this, studentList);
        studentListView.setAdapter(studentsListViewAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.this_menu_send,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.Students_MenuEnviar:

                SendStudentTask sendStudentTask = (SendStudentTask) new SendStudentTask(StudentsListActivity.this).execute();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}