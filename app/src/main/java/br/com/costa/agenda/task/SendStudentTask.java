package br.com.costa.agenda.task;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;

import br.com.costa.agenda.StudentsListActivity;
import br.com.costa.agenda.client.WebClient;
import br.com.costa.agenda.converter.StudentConverter;
import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;

/**
 * Created by alexmartins on 29/04/17.
 */

public class SendStudentTask extends AsyncTask {

    private Context context;
    private Dialog dialog;

    public SendStudentTask(Context context){
        this.context = context;
    }
    @Override
    protected Object doInBackground(Object[] params) {


        StudentDAO studentDAO = new StudentDAO(context);

        List<Student> students = studentDAO.read();

        StudentConverter studentConverter = new StudentConverter();

        String json = studentConverter.converterJson(students);

        WebClient webClient = new WebClient();

        String response = webClient.post(json);

        studentDAO.close();

        return response;

    }

    @Override
    protected void onPostExecute(Object o) {
        String response = (String) o;

        Toast.makeText(context, "Enviando notas "+ response, Toast.LENGTH_LONG).show();

        dialog.dismiss();

        super.onPostExecute(o);
    }

    @Override
    protected void onPreExecute() {

        dialog = ProgressDialog.show(context,"Aguarde...","Enviando dados dos alunos",true,true);
        super.onPreExecute();
    }
}
