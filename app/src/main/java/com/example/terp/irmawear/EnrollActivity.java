package com.example.terp.irmawear;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.irmacard.cardemu.CredentialManager;
import org.irmacard.cardemu.selfenrol.EnrollSelectActivity;

public class EnrollActivity extends Activity {

    private TextView mTextView;

    Boolean onlineEnrolling = false; // not used by set in the document enroll function

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enroll);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });
    }

    public void DemoEnroll(View view){
        onlineEnrolling = true;
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse("https://demo.irmacard.org/tomcat/irma_api_server/examples/issue-all.html"));
		startActivity(i);
	}

    public void WifiEnroll(View view){
        MainActivity activity = (MainActivity) MainActivity.activity;
        activity.WifiEnroll(view);
    }

    public void DocumentEnroll(View v){
        Intent i = new Intent(this, EnrollSelectActivity.class);
        CredentialManager.save();
        startActivityForResult(i, EnrollSelectActivity.EnrollSelectActivityCode);
    }

    public void DebugEnroll(View view) {
        Log.i("debug","enroll");
        MainActivity activity = (MainActivity) MainActivity.activity;
        activity.DebugEnroll();
    }
}
