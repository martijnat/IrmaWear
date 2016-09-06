package com.example.terp.abcwear;

import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class MainActivity extends WearableActivity {

    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;

    public Integer bit_length = 8; // Update if neccesary

    private AbcProofGenerator mProofGenerator = new AbcProofGenerator();
    private BackgroundConnection mBackgroundConnection;
    private Integer user_alpha;


    // WARNING builtin rng CANNOT handle large numbers
    private Random rng = new Random();

    public Integer getAlpha(){
        return user_alpha;
    }

    // Doesn't work for negative powers
    public Integer customPow(Integer base,Integer power){
        // Custom pow functions because the builtin one is for floats
        Integer product=1;
        while(power > 0){
            product = product * base;
            power = power -1;
        }
        return product;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();

        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
        mTextView = (TextView) findViewById(R.id.text);
        mClockView = (TextView) findViewById(R.id.clock);

    }


    // Real code starts here
    public void Click0(View view) {
        Toast.makeText(MainActivity.this, "Pressed (0)", Toast.LENGTH_SHORT).show();
        Integer maxAlpha = customPow(2,bit_length);
        user_alpha = rng.nextInt(maxAlpha);

        mBackgroundConnection = new BackgroundConnection();
        mBackgroundConnection.RegisterMainActivity(this);
        mBackgroundConnection.SetABcGenerator(mProofGenerator);
        mBackgroundConnection.execute("GetCredentials","10.0.2.2");
    }

    public void Click1(View view) {
        Toast.makeText(MainActivity.this, "Pressed (1)", Toast.LENGTH_SHORT).show();
        mBackgroundConnection = new BackgroundConnection();
        mBackgroundConnection.SetABcGenerator(mProofGenerator);
        mBackgroundConnection.execute("UseService","10.0.2.2");
    }

    public void ClickQuestion(View view) {
        Toast.makeText(MainActivity.this, "Pressed (?) test", Toast.LENGTH_SHORT).show();
        mBackgroundConnection = new BackgroundConnection();
        mBackgroundConnection.SetABcGenerator(mProofGenerator);
        mBackgroundConnection.execute("Test","10.0.2.2");
    }


    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }
}
