package com.example.terp.irmawear;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import android.bluetooth.*;

/**
 * Created by terp on 3-11-16.
 */

public class BackgroundClient  extends AsyncTask<String, Void, String>{

    public String message="";

    private Context mContext;

    public BackgroundClient(Context context){
        mContext = context;
    }

    protected String doInBackground(String... arguments) {

        try{
            int SERVERPORT = 8080;

            ServerSocket serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(SERVERPORT));

            // listen for incoming clients
            Socket client = serverSocket.accept();


            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));


            message = "";
            String line = "";
            while ((line = in.readLine()) != null) {
                Log.i("ServerActivity", line);
                message += line;}

            in.close();
            //out.close();
            client.close();
            serverSocket.close();
        }
        catch(Exception ex){
            //Handle exceptions
            Log.i("Catch{","Something went wrong");
            ex.printStackTrace();
        }
        return message;
    }

    protected void onPostExecute(String result){
        String TAG = "ConnectionLog";
        /* output result */
        Log.i(TAG,result);
        Toast.makeText(mContext,result, Toast.LENGTH_LONG).show();

    }
}
