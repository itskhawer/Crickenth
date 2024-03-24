package com.example.crickenth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Switch;


public class MainActivity extends AppCompatActivity {

    // Importing views
    Button enterButton;
    EditText overs,targetScore,teamOne,teamTwo;
    TextView note;
    Switch switch_TargetScore;

    Boolean switchToggle = false;
    String oversText,targetText,team1_name,team2_name;
    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // 2 seconds
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        teamOne = findViewById(R.id.team1_editText);
        teamTwo = findViewById(R.id.team2_editText);
        overs = findViewById(R.id.overs);
        targetScore = findViewById(R.id.targetScore);
        switch_TargetScore = findViewById(R.id.switch_TargetScore);
        note = findViewById(R.id.note);
        enterButton = findViewById(R.id.enterButton);



        ////////////////// Disable Views ///////////////

        targetScore.setVisibility(View.GONE);

        overs.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // Check if the "Enter" key is pressed
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    // Hide the keyboard
                    hideKeyboard(MainActivity.this);
                    // Assuming getContext() is available in your scope

                    // Return true to indicate that you've handled the action
                    return true;
                }
                return false;
            }
        });

        switch_TargetScore.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    targetScore.setVisibility(View.VISIBLE);
                    note.setText(R.string.switch_on_note);
                    int color = Color.parseColor("#25D32F2F");
                    note.setBackgroundTintList(ColorStateList.valueOf(color));

                    switchToggle = true;

                }
                else {
                    targetScore.setVisibility(View.GONE);
                    note.setText(R.string.switch_off_note);
                    int color = Color.parseColor("#1E0288D1");
                    note.setBackgroundTintList(ColorStateList.valueOf(color));
                    switchToggle = false;
                }
            }
        });

        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                team1_name = teamOne.getText().toString().trim();
                team2_name = teamTwo.getText().toString().trim();

                if(switchToggle==true){
                    if (!validateTargetEntry() && !validateOversEntry()){
                        Intent intent = new Intent(MainActivity.this, ScoreBoard.class);
                        // Pass the data to ScoreBoard activity
                        intent.putExtra("team1Name", team1_name);
                        intent.putExtra("team2Name", team2_name);
                        Integer intOvers = Integer.parseInt(oversText);
                        Integer intTarget = Integer.parseInt(targetText);
                        intent.putExtra("overs", intOvers);
                        intent.putExtra("target", intTarget);
                        intent.putExtra("switchToggle",switchToggle);
                        startActivity(intent);
                    }
                }
                else {
                    if (!validateOversEntry()){
                        Intent intent = new Intent(MainActivity.this, ScoreBoard.class);
                        // Pass the data to ScoreBoard activity
                        intent.putExtra("team1Name", team1_name);
                        intent.putExtra("team2Name", team2_name);
                        Integer intOvers = Integer.parseInt(oversText);
                        intent.putExtra("overs", intOvers);
                        intent.putExtra("switchToggle",switchToggle);
                        startActivity(intent);
                    }
                }
            }
        });
        ConstraintLayout constraintLayout = findViewById(R.id.rootConstraintLayout);

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard(MainActivity.this);
                return false;
            }
        });
    }

    private Boolean validateOversEntry() {
        // Get the text from the Overs EditText
        oversText = overs.getText().toString().trim();
        // Check if the Overs field is empty
        if (TextUtils.isEmpty(oversText)) {
            Toast.makeText(MainActivity.this, "Please enter the Overs", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private Boolean validateTargetEntry(){
        targetText = targetScore.getText().toString().trim();
        if (TextUtils.isEmpty(targetText)) {
            Toast.makeText(MainActivity.this, "Enter Target Score to proceed", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

    private void hideKeyboard(Context context) {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESS_TIME_INTERVAL) {
            // If the time interval between two back button presses is less than 2 seconds, exit the app
            super.onBackPressed();
        } else {
            // Show a toast indicating the need to press back again to exit
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            backPressedTime = System.currentTimeMillis();
        }
    }


}