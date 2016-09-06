package com.example.terp.abcwear;
import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Random;

public class BackgroundConnection extends AsyncTask<String, Void, String>{

    // For communications
    private AbcProofGenerator mProofGenerator;
    private Random rng = new Random();
    private MainActivity mMainActivity;

    public void RegisterMainActivity(MainActivity x){
        mMainActivity = x;
    }

    public void SetABcGenerator(AbcProofGenerator v){
        mProofGenerator = v;
    }

    protected String doInBackground(String... arguments) {
        String use_case = arguments[0];
        String ip = arguments[1];
        Log.i("use_Case_debug",use_case);
        try{
            // Creating new socket connection to the IP (first parameter) and its opened port (second parameter)
            Socket s = new Socket(ip, 8080);
            // Initialize output stream to write message to the socket stream
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            if (use_case == "GetCredentials"){
                out.write("USECASE getproof\n");
                out.write("I want proof that I my age is over 18\n");
                out.flush();
                String response1 = in.readLine();
                out.write("Proof: I swear I am 18\n");
                out.flush();
                String response2 = in.readLine();
                Integer user_alpha = mMainActivity.getAlpha();
                String alpha_resp = user_alpha.toString();
                // out.write(alpha_resp + "\n"); //Dont disclose alpha
                out.write("I have generated alpha\n");
                out.flush();
                String response3 = in.readLine();
                mProofGenerator.setProof(response3);
                String Proof=response2;
            }
            else if (use_case == "UseService"){
                out.write("I would like to use this service\n");
                out.flush();
                String response1 = in.readLine();
                // String proof ="Here is proof\n";
                String proof = mProofGenerator.getProof() +"\n";
                Log.i("Proof:",proof);
                out.write(proof);
                out.flush();
                String response2 = in.readLine();
                String Service=response2;
            }
            else //dummy use case for testing
            {
                out.write("Hello, this is the android wear app\n");
                out.write("What is the first challenge?\n");
                out.flush();
                int challenge1 = Integer.parseInt( in.readLine() );
                out.write("What is the second challenge?\n");
                out.flush();
                int challenge2 = Integer.parseInt( in.readLine() );
                int response = challenge1 * challenge2;
                out.write("The response is: " + response +"\n");
                out.flush();
            }
            out.flush();
            in.close();
            out.close();
            s.close();
        }
        catch(Exception ex){
            //Handle exceptions
            ex.printStackTrace();
        }
        return use_case;
    }

    protected void onPostExecute(String result){
        String TAG = "ConnectionLog";
        /* output result */
        Log.i(TAG,result);
        //todo fancy output
    }
}
