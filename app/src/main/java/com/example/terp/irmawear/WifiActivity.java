package com.example.terp.irmawear;

import android.app.Activity;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class WifiActivity extends Activity {

    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });


    }

    public void ShowIP(View view){
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Toast.makeText(this, ip, Toast.LENGTH_LONG).show();

    }

    public void getMessage(View view){
        Toast.makeText(this, "opening socket", Toast.LENGTH_LONG).show();
        BackgroundClient t = new BackgroundClient(this);
        t.execute();
    }
}
