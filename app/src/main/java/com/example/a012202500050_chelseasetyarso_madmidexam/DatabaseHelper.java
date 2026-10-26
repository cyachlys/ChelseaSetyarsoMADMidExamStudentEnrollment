
package com.example.a012202500050_chelseasetyarso_madmidexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentDB.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENTS = "students";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "student_name";
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_ENROLLED = "has_enrolled";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_STUDENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_STUDENT_ID + " TEXT NOT NULL UNIQUE, " +
                COL_ENROLLED + " INTEGER NOT NULL DEFAULT 0" +
                ")";

        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db,
            int oldVersion,
            int newVersion
    ) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        onCreate(db);
    }

    // ADD STUDENT
    public long addStudent(
            String name,
            String studentId,
            boolean enrolled
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_ENROLLED, enrolled ? 1 : 0);

        return db.insert(TABLE_STUDENTS, null, values);
    }

    // VIEW STUDENTS
    public Cursor getAllStudents() {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_STUDENTS,
                null,
                null,
                null,
                null,
                null,
                COL_ID + " DESC"
        );
    }

    // GET ONE STUDENT
    public Cursor getStudentById(int id) {
        SQLiteDatabase db = getReadableDatabase();

        return db.query(
                TABLE_STUDENTS,
                null,
                COL_ID + "=?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null
        );
    }

    // EDIT STUDENT
    public int updateStudent(
            int id,
            String name,
            String studentId,
            boolean enrolled
    ) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, name);
        values.put(COL_STUDENT_ID, studentId);
        values.put(COL_ENROLLED, enrolled ? 1 : 0);

        return db.update(
                TABLE_STUDENTS,
                values,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }

    // DELETE STUDENT
    public int deleteStudent(int id) {
        SQLiteDatabase db = getWritableDatabase();

        return db.delete(
                TABLE_STUDENTS,
                COL_ID + "=?",
                new String[]{String.valueOf(id)}
        );
    }
}
