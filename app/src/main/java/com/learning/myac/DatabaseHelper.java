package com.learning.myac;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class DatabaseHelper extends SQLiteOpenHelper {

    public Context context;
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Quiz_question.db";

    private static DatabaseHelper instance;

    private SQLiteDatabase db;
    private DatabaseHelper databaseHelper;



    private DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context){
        if (instance == null){
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    public static class CategoriesTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_catagories";
        public static final String COLOUMN_NAME = "name";
    }

    public static class QuestionsTable implements BaseColumns {

        public static final String TABLE_NAME = "Quiz_question_Table";
        public static final String ROW_ID = "_id";
        public static final String Column_Question = "question";
        public static final String Column_Option1 = "option1";
        public static final String Column_Option2 = "option2";
        public static final String Column_Option3 = "option3";
        public static final String Column_Answer_No = "annswer_No";
        public static final String Column_Difficulty = "difficulty";
        public static final String Column_Category_id = "catagory_id";

    }





    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_CATEGORIES_TABLE = "CREATE TABLE " +
                CategoriesTable.TABLE_NAME + "( " +
                CategoriesTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, " +
                CategoriesTable.COLOUMN_NAME + " TEXT " +
                ") ";

         final String CREATE_TABLE = "CREATE TABLE " + QuestionsTable.TABLE_NAME + "("
                + QuestionsTable.ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE NOT NULL, "
                + QuestionsTable.Column_Question + " TEXT , "
                + QuestionsTable.Column_Option1 + " TEXT, "
                + QuestionsTable.Column_Option2 + " TEXT, "
                + QuestionsTable.Column_Option3 + " TEXT," +
                QuestionsTable.Column_Answer_No + " INTEGER, " +
                 QuestionsTable.Column_Difficulty + " TEXT, " +
                 QuestionsTable.Column_Category_id + " INTEGER, " +
                 "FOREIGN KEY(" + QuestionsTable.Column_Category_id + ") REFERENCES " +
                 CategoriesTable.TABLE_NAME + "(" + CategoriesTable._ID + ")" + "ON DELETE CASCADE" +
                ")";

         db.execSQL(SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(CREATE_TABLE);
        fillCategoriesTable();
        fillQuestionTable();


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + CategoriesTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);

    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    private void fillCategoriesTable(){
        Category category1 = new Category("Programming");
        addCategory(category1);
        Category category2 = new Category("Math");
        addCategory(category2);
        Category category3 = new Category("Riddle");
        addCategory(category3);
    }
    private void addCategory(Category category){
        ContentValues contentValues = new ContentValues();
        contentValues.put(CategoriesTable.COLOUMN_NAME,category.getName());
        db.insert(CategoriesTable.TABLE_NAME,null,contentValues);

    }

    public void fillQuestionTable(){
        Question question1 = new Question("Programming,Easy : A is correct",
                "A","B","C",1,Question.DIFFICULTY_EASY,Category.PROGRAMMING);
        insertQuestionIntoDB(question1);
        Question question2 = new Question("Math,Medium : B is correct",
                "A","B","C",2,Question.DIFFICULTY_MEDIUM,Category.MATH);
        insertQuestionIntoDB(question2);
        Question question3 = new Question("Riddle,Hard : C is correct",
                "A","B","C",3,Question.DIFFICULTY_HARD,Category.RIDDLE);
        insertQuestionIntoDB(question3);
        Question question4 = new Question("Programming,Medium : B is correct",
                "A","B","C",2,Question.DIFFICULTY_MEDIUM,Category.PROGRAMMING);
        insertQuestionIntoDB(question4);
        Question question5 = new Question("Math,Medium : C is correct",
                "A","B","C",3,Question.DIFFICULTY_MEDIUM,Category.MATH);
        insertQuestionIntoDB(question5);




    }

    public void insertQuestionIntoDB(Question question){

        ContentValues contentValues = new ContentValues();
        contentValues.put(QuestionsTable.Column_Question, question.getQuestion());
        contentValues.put(QuestionsTable.Column_Option1, question.getOption1());
        contentValues.put(QuestionsTable.Column_Option2, question.getOption2());
        contentValues.put(QuestionsTable.Column_Option3, question.getOption3());
        contentValues.put(QuestionsTable.Column_Answer_No, question.getAnswerNo());
        contentValues.put(QuestionsTable.Column_Difficulty, question.getDifficulty());
        contentValues.put(QuestionsTable.Column_Category_id,question.getCategoryID());

        db.insert(QuestionsTable.TABLE_NAME,null,contentValues);

    }
    public List<Category> getAllCategories(){
        List<Category> categoryList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + CategoriesTable.TABLE_NAME,null);

        if (cursor.moveToFirst()) {
            do {

                    Category category = new Category();
                    category.setId(cursor.getInt(cursor.getColumnIndex(CategoriesTable._ID)));
                    category.setName(cursor.getString(cursor.getColumnIndex(CategoriesTable.COLOUMN_NAME)));
                    categoryList.add(category);
                }while (cursor.moveToNext());
            }
            cursor.close();
            return categoryList;
        }

    public ArrayList<Question> getAllQuestion (){
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Question)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option3)));
                question.setAnswerNo(cursor.getInt(cursor.getColumnIndex(QuestionsTable.Column_Answer_No)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Difficulty)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuestionsTable.Column_Category_id)));
                questionList.add(question);


            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return questionList;
    }
    public ArrayList<Question> getQuestion (int categoriesID,String difficulty){
        ArrayList<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();

        String selection  = QuestionsTable.Column_Category_id + " = ? " +
                " AND " + QuestionsTable.Column_Difficulty + " = ? ";
        String[] selectionArgs = new String[]{String.valueOf(categoriesID),difficulty};

        Cursor cursor =db.query(QuestionsTable.TABLE_NAME,null,selection,selectionArgs,null,null,null);

        if (cursor.moveToFirst()) {
            do {
                Question question = new Question();
                question.setId(cursor.getInt(cursor.getColumnIndex(QuestionsTable._ID)));
                question.setQuestion(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Question)));
                question.setOption1(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option1)));
                question.setOption2(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option2)));
                question.setOption3(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Option3)));
                question.setAnswerNo(cursor.getInt(cursor.getColumnIndex(QuestionsTable.Column_Answer_No)));
                question.setDifficulty(cursor.getString(cursor.getColumnIndex(QuestionsTable.Column_Difficulty)));
                question.setCategoryID(cursor.getInt(cursor.getColumnIndex(QuestionsTable.Column_Category_id)));
                questionList.add(question);


            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return questionList;
    }


}
