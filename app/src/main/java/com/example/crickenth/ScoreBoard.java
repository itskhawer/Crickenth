package com.example.crickenth;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.app.Dialog;
import android.view.ViewGroup;
import android.widget.EditText;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;


public class ScoreBoard extends AppCompatActivity {

    TextView crrView, battingTeamView, outPlayersView, remainingOversView, remainingBallsView, targetTextView, rrrView, balledOversView, scoreDisplayView, targetScoreView;
    TextView requirementsDisplay;
    Button wide_button,noBall_button,ok_button,out_button,retire_button,endInning_button;
    RadioGroup radioGroup;
    ConstraintLayout rrrBox;
    LinearLayout remainingScoreBox;
    Integer totalScore = 0;
    Integer outPlayerCount = 0;
    Integer total_overs;
    Integer total_target=0,total_balls=0,remaining_overs=0, remaining_balls=0,remaining_score=0,balled_balls=0,balled_overs=0;
    private static final int BALLS_PER_OVER = 6;
    String team1Name,team2Name,winning_team;
    Boolean is_second_inning =false;

    private Dialog balledOversDialog;
    private Dialog CurrentScoreDialog;
    private static final int BACK_PRESS_TIME_INTERVAL = 2000; // 2 seconds
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);

//-------------------------     Set variables      ----------------------------

        team1Name = getIntent().getStringExtra("team1Name");
        team2Name = getIntent().getStringExtra("team2Name");
        if (TextUtils.isEmpty(team1Name)) team1Name = "Team 1";
        if (TextUtils.isEmpty(team2Name)) team2Name = "Team 2";
        int received_overs = getIntent().getIntExtra("overs",0);
        int received_target = getIntent().getIntExtra("target",0);
        total_overs = received_overs;
        total_target = received_target;
        total_balls = total_overs * BALLS_PER_OVER;
        remaining_overs=total_overs;
        remaining_balls = total_balls;
        is_second_inning = getIntent().getBooleanExtra("switchToggle",false);

//-------------------------     Find Views      ----------------------------

        battingTeamView = findViewById(R.id.playing_team_name);
        balledOversView = findViewById(R.id.balled_overs_TextView);
        crrView = findViewById(R.id.crr_textView);
        outPlayersView = findViewById(R.id.out_players_TextView);
        remainingOversView = findViewById(R.id.remaining_overs_TextView);
        remainingBallsView = findViewById(R.id.remaining_balls_TextView);
        wide_button = findViewById(R.id.button_wide);
        scoreDisplayView = findViewById(R.id.score_TextView);
        noBall_button= findViewById(R.id.button_no_ball);
        out_button = findViewById(R.id.button_OUT);
        ok_button = findViewById(R.id.button_OK);
        retire_button = findViewById(R.id.button_Retire);
        endInning_button = findViewById(R.id.button_End_Inning);
        radioGroup = findViewById(R.id.radioGroup);
        targetTextView = findViewById(R.id.target_textView);
        targetScoreView = findViewById(R.id.target_score_TextView);
        rrrView = findViewById(R.id.rrr_textView);
        rrrBox = findViewById(R.id.rrr_Box);
        remainingScoreBox = findViewById(R.id.remainingScore_Box);
        requirementsDisplay = findViewById(R.id.remaining_score);

//-------------------------     Set Data in views      ----------------------------

        remainingBallsView.setText(String.valueOf(remaining_balls));
        balledOversView.setText(String.valueOf(balled_overs));
        updateRemainingOvers();

//-------------------------     If it is second inning then set views respectively      ----------------------------

        if (is_second_inning){
            endInning_button.setText(R.string.end_match);
            targetScoreView.setVisibility(View.VISIBLE);
            targetTextView.setVisibility(View.VISIBLE);
            rrrBox.setVisibility(View.VISIBLE);
            remainingScoreBox.setVisibility(View.VISIBLE);
            battingTeamView.setText(team2Name);
            targetScoreView.setText(String.valueOf(total_target));
            updateRequirements();
            updateRRRdisplay();
        }
        else{
            targetScoreView.setVisibility(View.GONE);
            targetTextView.setVisibility(View.GONE);
            rrrBox.setVisibility(View.GONE);
            remainingScoreBox.setVisibility(View.GONE);
            battingTeamView.setText(team1Name);
        }

        balledOversView.setOnClickListener(view -> showBalledOversDialog());
        scoreDisplayView.setOnClickListener(view -> showCurrentScoreDialog());

//-------------------------     Setting Buttons      ----------------------------

        wide_button.setOnClickListener(view -> {
            if (!isPlayOver()) {
                totalScore += 1;
                totalScore += getSelectedRadioButtonValue();
                updateScoreDisplay();
                clearRadioButtonSelection();
            }
        });
    //----------------------------------------------------------------------
        noBall_button.setOnClickListener(view -> {
            if (!isPlayOver()) {
                totalScore += 1;
                totalScore += getSelectedRadioButtonValue();
                updateScoreDisplay();
                clearRadioButtonSelection();
            }
        });
    //----------------------------------------------------------------------
        out_button.setOnClickListener(view -> {
            if (!isPlayOver()){
                remaining_balls--;
                balled_balls = total_balls - remaining_balls;
                outPlayerCount += 1;
                totalScore += getSelectedRadioButtonValue();
                clearRadioButtonSelection();
                updateOutPlayers();
                updateScoreDisplay();
                updateBalledOversDisplay();
                updateRemainingOvers();
                isPlayOver();
            }
        });
    //----------------------------------------------------------------------
        ok_button.setOnClickListener(view -> {
            if (!isPlayOver()){

                totalScore += getSelectedRadioButtonValue();
                remaining_balls = remaining_balls - 1;
                balled_balls = total_balls - remaining_balls;
                clearRadioButtonSelection();
                updateScoreDisplay();
                updateBalledOversDisplay();
                updateRemainingOvers();
                isPlayOver();
            }
        });
    //----------------------------------------------------------------------
        retire_button.setOnClickListener(view -> {
            if (!isPlayOver()) {
                outPlayerCount += 1;
                updateOutPlayers();
            }
        });
    //----------------------------------------------------------------------
        endInning_button.setOnClickListener(view -> {
            if (is_second_inning) {
                showEndMatchConfirmationDialog();
            }
            else {
                // Show confirmation dialog
                showConfirmationDialog();
            }
        });
    }

//-------------------------     Setting functions      ----------------------------

    //----------------------------------------------------------------------
    private void updateOutPlayers() {
        outPlayersView.setText(String.valueOf(outPlayerCount));
    }
    private void updateScoreDisplay() {
        scoreDisplayView.setText(String.valueOf(totalScore));
        updateCRRdisplay();
        if (is_second_inning){
            updateRRRdisplay();
            updateRequirements();
        }
    }
    private void updateBalledOversDisplay() {
        updateRemainingTotalBallsDisplay();
        balledOversView.setText(getOversInDecimalString(balled_balls));
    }
    private void updateRemainingOvers() {
        remainingOversView.setText(getOversInDecimalString(remaining_balls));
    }
    private void updateRemainingTotalBallsDisplay(){
        remainingBallsView.setText(String.valueOf(remaining_balls));
    }
    private void updateCRRdisplay(){
        if (balled_balls != 0) crrView.setText(String.format("%.2f", (double) (totalScore*BALLS_PER_OVER) / balled_balls));
        else crrView.setText("0.0");

    }
    private void updateRRRdisplay(){

        if (totalScore<=total_target && remaining_balls !=0) rrrView.setText(String.format("%.2f", (double) ((total_target - totalScore) * BALLS_PER_OVER) / remaining_balls));
        else rrrView.setText("0.0");
    }
    private void updateRequirements(){
        remaining_score = total_target - totalScore;
        if (!isPlayOver()) requirementsDisplay.setText(" Need "+remaining_score+" runs in "+remaining_balls+" balls");
        else requirementsDisplay.setText(getWinningTeam());
    }

//----------------------------------------------------------------------

    private int getSelectedRadioButtonValue() {
        int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();

        if (selectedRadioButtonId == R.id.radioButton0) {
            return 0;
        } else if (selectedRadioButtonId == R.id.radioButton1) {
            return 1;
        } else if (selectedRadioButtonId == R.id.radioButton2) {
            return 2;
        } else if (selectedRadioButtonId == R.id.radioButton3) {
            return 3;
        } else if (selectedRadioButtonId == R.id.radioButton4) {
            return 4;
        } else if (selectedRadioButtonId == R.id.radioButton5) {
            return 5;
        } else if (selectedRadioButtonId == R.id.radioButton6) {
            return 6;
        } else {
            return 0;
        }
    }
    private void clearRadioButtonSelection() {
        radioGroup.clearCheck();
    }
    private String getOversInDecimalString(int balls){
        int over = balls/BALLS_PER_OVER;
        int ball = balls%BALLS_PER_OVER;
        return over+"."+ball;
    }
    private void resetStats() {
        // Reset all relevant variables and update UI accordingly
        if (is_second_inning){
            targetScoreView.setVisibility(View.VISIBLE);
            targetTextView.setVisibility(View.VISIBLE);
            rrrBox.setVisibility(View.VISIBLE);
            remainingScoreBox.setVisibility(View.VISIBLE);

            endInning_button.setText(R.string.end_match);
            battingTeamView.setText(team2Name);

            total_target = totalScore + 1;
            totalScore = 0;
            total_balls = total_overs*BALLS_PER_OVER;
            outPlayerCount = 0;
            remaining_balls = total_balls;
            targetScoreView.setText(String.valueOf(total_target));
            balledOversView.setText("0.0");

            updateRemainingOvers();
            updateRemainingTotalBallsDisplay();
            updateOutPlayers();
            updateScoreDisplay();
        }
    }
    private boolean isPlayOver() {
        if (is_second_inning) {
            if (remaining_balls == 0 || totalScore >= total_target) {
                Toast.makeText(this, getWinningTeam(), Toast.LENGTH_SHORT).show();
                return true;
            }
            else return false;
        }
        else {
            if (remaining_balls == 0){
                Toast.makeText(this, "First inning is over.", Toast.LENGTH_SHORT).show();
                is_second_inning=true;
                resetStats();
                return true;
            }
            else return false;
        }


    }
    private String getWinningTeam(){
        if (totalScore >= total_target) winning_team = team2Name;
        else if (totalScore == total_target-1) return "Tie";
        else winning_team = team1Name;
        return winning_team+" won the match";
    }
//----------------------------------------------------------------------
    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScoreBoard.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure to end first inning and move on to second inning?");

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            // User clicked OK, perform your action (resetStats() in this case)
            is_second_inning = true;
            resetStats();
            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User clicked Cancel, dismiss the dialog
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showEndMatchConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ScoreBoard.this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure to end this Match?");

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            Intent intent = new Intent(ScoreBoard.this,MainActivity.class);
            dialog.dismiss();
            startActivity(intent);
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> {
            // User clicked Cancel, dismiss the dialog
            dialog.dismiss();
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showBalledOversDialog() {
        balledOversDialog = new Dialog(this);
        balledOversDialog.setContentView(R.layout.dialog_balled_overs); // Create a layout file for your custom dialog
        balledOversDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText oversEditText = balledOversDialog.findViewById(R.id.editTextBalledOvers);
        EditText ballsEditText = balledOversDialog.findViewById(R.id.editTextBalledBalls);

        // Set up any other dialog configurations or validations as needed

        // Set a click listener for the OK button in the dialog
        Button okButton = balledOversDialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(view -> {
            // Get the entered values from the EditText views
            String oversText = oversEditText.getText().toString();
            String ballsText = ballsEditText.getText().toString();


            // Perform any necessary validations on the input values
            if (oversText.isEmpty() || ballsText.isEmpty()){
                Toast.makeText(this, "Balled Overs and Balled balls of this Over entries are empty", Toast.LENGTH_SHORT).show();
            }
            else{
                int balledOvers = Integer.parseInt(oversText);
                int balledBalls = Integer.parseInt(ballsText);
                if(balledOvers > total_overs){
                    Toast.makeText(this, "Balled Overs can not be greater than total overs of the match", Toast.LENGTH_SHORT).show();
                }
                else if(balledBalls>5){
                    Toast.makeText(this, "Balls can only be in the range of 0 to 5", Toast.LENGTH_SHORT).show();
                }
                else if (balledOvers == total_overs && balledBalls > 0 )
                    Toast.makeText(this, "WRONG ENTRY\nExceeding Overs limit ", Toast.LENGTH_SHORT).show();
                else {
                    // Update your balled overs and balls variables
                    balled_balls = (balledOvers * 6) + balledBalls;
                    remaining_balls = total_balls-balled_balls;
                    updateBalledOversDisplay();
                    updateRemainingOvers();
                    updateRemainingTotalBallsDisplay();
                    updateOutPlayers();
                    updateScoreDisplay();
                    balledOversDialog.dismiss();
                }
            }
        });

        // Set a click listener for the Cancel button in the dialog
        Button cancelButton = balledOversDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            // Dismiss the dialog without saving any changes
            balledOversDialog.dismiss();
        });

        // Show the dialog
        balledOversDialog.show();
    }

    private void showCurrentScoreDialog() {
        CurrentScoreDialog = new Dialog(this);
        CurrentScoreDialog.setContentView(R.layout.dialog_score); // Create a layout file for your custom dialog
        CurrentScoreDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText scoreEditText = CurrentScoreDialog.findViewById(R.id.editText_currentScore);

        // Set up any other dialog configurations or validations as needed

        // Set a click listener for the OK button in the dialog
        Button okButton = CurrentScoreDialog.findViewById(R.id.okButton);
        okButton.setOnClickListener(view -> {
            // Get the entered values from the EditText views
            String currentScoreText = scoreEditText.getText().toString();

            if (currentScoreText.isEmpty()) Toast.makeText(this, "Enter current score of the inning", Toast.LENGTH_SHORT).show();
            else {
                 totalScore = Integer.parseInt(currentScoreText);
                 updateScoreDisplay();
                 CurrentScoreDialog.dismiss();
            }
        });

        // Set a click listener for the Cancel button in the dialog
        Button cancelButton = CurrentScoreDialog.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(view -> {
            // Dismiss the dialog without saving any changes
            CurrentScoreDialog.dismiss();
        });

        // Show the dialog
        CurrentScoreDialog.show();
    }
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - backPressedTime < BACK_PRESS_TIME_INTERVAL) {
            // If the time interval between two back button presses is less than 2 seconds, exit the app
            super.onBackPressed();
        } else {
            // Show a toast indicating the need to press back again to exit
            Toast.makeText(this, "Press back again to start new match", Toast.LENGTH_SHORT).show();
            backPressedTime = System.currentTimeMillis();
        }
    }

}