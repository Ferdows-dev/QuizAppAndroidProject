package com.learning.myac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {
    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView tvQuestion;
    private TextView tvScore;
    private TextView tvQuestionCount;
    private TextView tvCategories;
    private TextView tvdifficulty;
    private TextView tvCountDown;
    private RadioGroup radioGroup;
    private RadioButton radioButton1;
    private RadioButton radioButton2;
    private RadioButton radioButton3;
    private Button confirmNext;

    private ColorStateList textColorDefaultRB;
    private ColorStateList getTextColorDefaultCD;

    private CountDownTimer countDownTimer;
    private long timeLeftMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answerd;

    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        tvQuestion = findViewById(R.id.viewQuestionTV);
        tvScore = findViewById(R.id.viewScoreTV);
        tvQuestionCount = findViewById(R.id.viewQuestionCountTV);
        tvCategories = findViewById(R.id.categoryTV);
        tvdifficulty = findViewById(R.id.difficultyTV);
        tvCountDown = findViewById(R.id.viewTextCountdownTV);
        radioGroup = findViewById(R.id.radioGroup);
        radioButton1 = findViewById(R.id.radioBtn1);
        radioButton2 = findViewById(R.id.radioBtn2);
        radioButton3 = findViewById(R.id.radioBtn3);
        confirmNext = findViewById(R.id.confirmBtn);

        textColorDefaultRB = radioButton1.getTextColors();
        getTextColorDefaultCD = tvCountDown.getTextColors();

        Intent intent = getIntent();
        int categoryID =intent.getIntExtra(MainActivity.EXTRA_CATEGORY_ID,0);
        String categoryName = intent.getStringExtra(MainActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(MainActivity.EXTRA_DIFFICULTY);

        tvCategories.setText("Categories : " + categoryName);
        tvdifficulty.setText("Difficulty : " + difficulty);

        if (savedInstanceState == null) {
            DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
            questionList = databaseHelper.getQuestion(categoryID,difficulty);
            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);

            showNextQuestion();
        }else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter );
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answerd = savedInstanceState.getBoolean(KEY_ANSWERED);
        }
        if (!answerd){
            startCountDown();
        }else {
            updateCountDowntext();
            showCorrectAns();
        }

        confirmNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!answerd){
                    if (radioButton1.isChecked() || radioButton2.isChecked() || radioButton3.isChecked()){
                        checkAnswer();
                    }else {
                        Toast.makeText(QuizActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    showNextQuestion();
                }
            }
        });

    }
    private void showNextQuestion(){
        radioButton1.setTextColor(textColorDefaultRB);
        radioButton2.setTextColor(textColorDefaultRB);
        radioButton3.setTextColor(textColorDefaultRB);
        radioGroup.clearCheck();

        if (questionCounter<questionCountTotal){
            currentQuestion = questionList.get(questionCounter);

            tvQuestion.setText(currentQuestion.getQuestion());
            radioButton1.setText(currentQuestion.getOption1());
            radioButton2.setText(currentQuestion.getOption2());
            radioButton3.setText(currentQuestion.getOption3());

            questionCounter++;
            tvQuestionCount.setText("Question : " + questionCounter + "/" + questionCountTotal);
            answerd = false;
            confirmNext.setText("Confirm");

            timeLeftMillis = COUNTDOWN_IN_MILLIS;
            startCountDown();

        }else {
            finishQuiz();
        }

    }
    private void startCountDown(){
        countDownTimer = new CountDownTimer(timeLeftMillis,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftMillis = millisUntilFinished;
                updateCountDowntext();

            }

            @Override
            public void onFinish() {
//                timeLeftMillis = 0;
                updateCountDowntext();
                checkAnswer();

            }
        }.start();
    }
    private void updateCountDowntext(){
        int minutes = (int) ((timeLeftMillis / 1000) / 60);
        int seconds = (int) ((timeLeftMillis / 1000)  % 60);

        String timeFormatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvCountDown.setText(timeFormatted);

        if (timeLeftMillis < 5000){
            tvCountDown.setTextColor(Color.RED);
        }else {
            tvCountDown.setTextColor(getTextColorDefaultCD);
        }
    }

    private void checkAnswer(){
        answerd = true;

        countDownTimer.cancel();


        RadioButton selectedRadiobutton = findViewById(radioGroup.getCheckedRadioButtonId());
        int answerNo = radioGroup.indexOfChild(selectedRadiobutton) + 1;

        if (answerNo == currentQuestion.getAnswerNo()){
            score++;
            tvScore.setText("Score "+ score);

        }
        showCorrectAns();
    }
    private void showCorrectAns(){
        radioButton1.setTextColor(Color.RED);
        radioButton2.setTextColor(Color.RED);
        radioButton3.setTextColor(Color.RED);

        switch (currentQuestion.getAnswerNo()){
            case 1:
                radioButton1.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                radioButton2.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                radioButton3.setTextColor(Color.GREEN);
                tvQuestion.setText("Answer 3 is correct");
                break;
        }
        if (questionCounter < questionCountTotal){
            confirmNext.setText("Next");
        }else {
            confirmNext.setText("Finish");
        }
    }

    private void finishQuiz(){
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE,score);
        setResult(RESULT_OK,resultIntent);
        finish();
    }

    @Override
    public void onBackPressed() {

        if (backPressedTime + 2000 > System.currentTimeMillis()){
            finishQuiz();
        }else {
            Toast.makeText(this, "Please press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt(KEY_SCORE,score);
        outState.putInt(KEY_QUESTION_COUNT,questionCounter);
        outState.putLong(KEY_MILLIS_LEFT,timeLeftMillis);
        outState.putBoolean(KEY_ANSWERED,answerd);
        outState.putParcelableArrayList(KEY_QUESTION_LIST,questionList);
    }
}