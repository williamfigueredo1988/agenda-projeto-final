package br.com.costa.agenda.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.telephony.SmsMessage;
import android.widget.Toast;

import br.com.costa.agenda.R;
import br.com.costa.agenda.dao.StudentDAO;
import br.com.costa.agenda.model.Student;

import static br.com.costa.agenda.R.raw.msg;

/**
 * Created by alexmartins on 28/04/17.
 */

public class SMSReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] pdus = (Object[]) intent.getSerializableExtra("pdus");
        byte[] pdu = (byte[]) pdus[0];
        String format = (String) intent.getSerializableExtra("format");

        SmsMessage sms = SmsMessage.createFromPdu(pdu, format);

        String phone = sms.getDisplayOriginatingAddress();

        StudentDAO dao = new StudentDAO(context);

        if(dao.getStudentePerNumber(phone)){
            Toast.makeText(context,"Chegou um SMS " + phone,Toast.LENGTH_SHORT).show();
            MediaPlayer media = MediaPlayer.create(context, R.raw.msg);
            media.start();

        }
    }
}
