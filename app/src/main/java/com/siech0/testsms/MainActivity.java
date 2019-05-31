package com.siech0.testsms;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.DexterBuilder;

import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<TextView> tv = new ArrayList<TextView>();
    private List<String> data = new ArrayList<String>();
    private boolean smsServiceRunning = false;

    DexterBuilder smsPermChecker;
    private SMSReceiver.Listener listener = new SMSReceiver.Listener() {
        @Override
        public void onSMSReceived(String sender, String body) {
            final String text =  String.format("Sender: %s, Body:%s", sender, body);
            data.add(text);

            TextView textView = new TextView(MainActivity.this);
            textView.setText(String.format("%s",data.get(data.size() - 1)));

            LinearLayout list = (LinearLayout) findViewById(R.id.SMSViewer);
            list.addView(textView);
            Log.e("MainActivity", text);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        smsPermChecker = Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS)
                .withListener(DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(this)
                        .withTitle("SMS Read/Receive Permission")
                        .withMessage("SMS Read/Receive permissisions are required for basic service functionality")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.app_icon)
                        .build()
                )
                .withErrorListener((new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Log.e("Dexter", error.toString());
                    }
                }));
        setContentView(R.layout.activity_main);
    }

    public void btnActivator_Click(View view){
        Button btnActivator = (Button) findViewById(R.id.btnActivator);
        smsPermChecker.check();
        if(smsServiceRunning){
            btnActivator.setText("Activate");
            SMSReceiver.removeListener(this.listener);
            smsServiceRunning = false;
        } else {
            btnActivator.setText("Deactivate");
            SMSReceiver.addListener(this.listener);
            smsServiceRunning = true;
        }
    }
}
