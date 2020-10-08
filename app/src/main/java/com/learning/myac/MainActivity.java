package com.learning.myac;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighScore";

    private TextView textViewHighScore;
    int highScore;

    Button startQuizButton;
    Spinner spinnerCategory;
    Spinner spinnerDifficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewHighScore = findViewById(R.id.tvHighScore);
        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);

        loadDifficultyLevels();
        loadCategories();
        loadHighScore();

        startQuizButton = findViewById(R.id.startQuizBtn);

        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }
    private void startQuiz(){
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(MainActivity.this,QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID,categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME,categoryName);
        intent.putExtra(EXTRA_DIFFICULTY,difficulty);
        startActivityForResult(intent,REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ){
            if (resultCode == RESULT_OK){
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE,0);
                if (score > highScore){
                    updateHighSCore(score);
                }
            }
        }
    }
    private void loadCategories(){
        DatabaseHelper databaseHelper = DatabaseHelper.getInstance(this);
        List<Category> categories = databaseHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,categories);
        spinnerCategory.setAdapter(adapterCategories);
    }
    private void loadDifficultyLevels(){
        String[] difficultyLevels = Question.getAllDeficultiesLevels();
        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,difficultyLevels);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }
    private void loadHighScore(){
        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        highScore = preferences.getInt(KEY_HIGHSCORE,0);
        textViewHighScore.setText("HighScore : "+ highScore);
    }
    private void updateHighSCore(int highScoreNew){
        highScore = highScoreNew;
        textViewHighScore.setText("HighScore :" + highScore);

        SharedPreferences preferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_HIGHSCORE,highScore);
        editor.apply();
    }
}