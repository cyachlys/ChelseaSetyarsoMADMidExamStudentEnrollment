package com.example.a012202500050_chelseasetyarso_madmidexam;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etStudentName, etStudentId;
    private CheckBox cbHasEnrolled;

    private Button btnAdd, btnView, btnEdit, btnDelete, btnClear;

    private TextView tvTotalStudents, tvEnrolledStudents;
    private TextView tvEmptyState;
    private LinearLayout studentContainer;

    private DatabaseHelper dbHelper;

    private int selectedDatabaseId = -1;

    private final int blue = Color.rgb(36, 87, 197);
    private final int darkBlue = Color.rgb(23, 43, 77);
    private final int gray = Color.rgb(113, 128, 150);
    private final int green = Color.rgb(32, 128, 93);
    private final int red = Color.rgb(180, 35, 24);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);

        etStudentName = findViewById(R.id.etStudentName);
        etStudentId = findViewById(R.id.etStudentId);
        cbHasEnrolled = findViewById(R.id.cbHasEnrolled);

        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        btnClear = findViewById(R.id.btnClear);

        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvEnrolledStudents = findViewById(R.id.tvEnrolledStudents);
        tvEmptyState = findViewById(R.id.tvEmptyState);

        studentContainer = findViewById(R.id.studentContainer);

        btnAdd.setOnClickListener(v -> addStudent());
        btnView.setOnClickListener(v -> loadStudents());
        btnEdit.setOnClickListener(v -> editStudent());
        btnDelete.setOnClickListener(v -> confirmDelete());
        btnClear.setOnClickListener(v -> clearForm());

        loadStudents();
    }

    // CREATE
    private void addStudent() {
        String name = etStudentName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        boolean enrolled = cbHasEnrolled.isChecked();

        if (!validateInput(name, studentId)) {
            return;
        }

        try {
            long result = dbHelper.addStudent(
                    name, studentId, enrolled
            );

            if (result != -1) {
                showMessage("Student added successfully!");
                clearForm();
                loadStudents();
            } else {
                showMessage("Unable to add student. Check the ID.");
            }

        } catch (SQLiteConstraintException e) {
            showMessage("This Student ID already exists.");
        }
    }

    // READ: LOAD DATA AND UPDATE DASHBOARD
    private void loadStudents() {
        studentContainer.removeAllViews();

        int total = 0;
        int enrolledTotal = 0;

        Cursor cursor = dbHelper.getAllStudents();

        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ID
                        )
                );

                String name = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_NAME
                        )
                );

                String studentId = cursor.getString(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_STUDENT_ID
                        )
                );

                boolean enrolled = cursor.getInt(
                        cursor.getColumnIndexOrThrow(
                                DatabaseHelper.COL_ENROLLED
                        )
                ) == 1;

                total++;

                if (enrolled) {
                    enrolledTotal++;
                }

                addStudentCard(id, name, studentId, enrolled);
            }
        } finally {
            cursor.close();
        }

        tvTotalStudents.setText(String.valueOf(total));
        tvEnrolledStudents.setText(String.valueOf(enrolledTotal));

        tvEmptyState.setVisibility(
                total == 0 ? View.VISIBLE : View.GONE
        );

        showSelectionState(false);
    }

    // CREATE A STUDENT CARD
    private void addStudentCard(
            int id,
            String name,
            String studentId,
            boolean enrolled
    ) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(16), dp(15), dp(16), dp(12));

        GradientDrawable cardBackground = new GradientDrawable();
        cardBackground.setColor(Color.WHITE);
        cardBackground.setCornerRadius(dp(14));
        cardBackground.setStroke(dp(1), Color.rgb(225, 233, 244));
        card.setBackground(cardBackground);
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

        cardParams.bottomMargin = dp(12);
        card.setLayoutParams(cardParams);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);

        TextView avatar = new TextView(this);
        avatar.setText(getInitials(name));
        avatar.setTextColor(blue);
        avatar.setTextSize(16);
        avatar.setTypeface(null, Typeface.BOLD);
        avatar.setGravity(Gravity.CENTER);

        GradientDrawable avatarBackground = new GradientDrawable();
        avatarBackground.setColor(Color.rgb(230, 238, 255));
        avatarBackground.setCornerRadius(dp(12));
        avatar.setBackground(avatarBackground);

        LinearLayout.LayoutParams avatarParams =
                new LinearLayout.LayoutParams(dp(46), dp(46));

        avatarParams.setMarginEnd(dp(12));
        row.addView(avatar, avatarParams);

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams detailsParams =
                new LinearLayout.LayoutParams(
                        0,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        1
                );

        TextView nameView = new TextView(this);
        nameView.setText(name);
        nameView.setTextColor(darkBlue);
        nameView.setTextSize(16);
        nameView.setTypeface(null, Typeface.BOLD);

        TextView idView = new TextView(this);
        idView.setText("Student ID: " + studentId);
        idView.setTextColor(gray);
        idView.setTextSize(13);

        TextView statusView = new TextView(this);
        statusView.setText(
                enrolled ? "●  Enrolled" : "●  Not Enrolled"
        );
        statusView.setTextColor(enrolled ? green : gray);
        statusView.setTextSize(12);
        statusView.setPadding(0, dp(5), 0, 0);

        details.addView(nameView);
        details.addView(idView);
        details.addView(statusView);

        row.addView(details, detailsParams);
        card.addView(row);

        // Select a card to edit or delete its student
        card.setOnClickListener(v -> {
            selectedDatabaseId = id;

            etStudentName.setText(name);
            etStudentId.setText(studentId);
            cbHasEnrolled.setChecked(enrolled);

            showSelectionState(true);
            showMessage("Selected: " + name);
        });

        // Card actions
        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setGravity(Gravity.END);
        actions.setPadding(0, dp(12), 0, 0);

        Button editButton = createSmallButton(
                "EDIT", blue, Color.WHITE
        );

        Button deleteButton = createSmallButton(
                "DELETE", red, Color.WHITE
        );

        LinearLayout.LayoutParams actionParams =
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        dp(38)
                );

        actionParams.setMarginStart(dp(8));

        actions.addView(editButton, actionParams);
        actions.addView(deleteButton, actionParams);

        editButton.setOnClickListener(v -> {
            selectedDatabaseId = id;
            etStudentName.setText(name);
            etStudentId.setText(studentId);
            cbHasEnrolled.setChecked(enrolled);

            showSelectionState(true);
            etStudentName.requestFocus();
            showMessage("Edit the selected student, then tap UPDATE.");
        });

        deleteButton.setOnClickListener(v -> {
            selectedDatabaseId = id;
            etStudentName.setText(name);
            etStudentId.setText(studentId);
            cbHasEnrolled.setChecked(enrolled);
            confirmDelete();
        });

        card.addView(actions);
        studentContainer.addView(card);
    }

    // CREATE SMALL ACTION BUTTONS
    private Button createSmallButton(
            String text,
            int backgroundColor,
            int textColor
    ) {
        Button button = new Button(this);
        button.setText(text);
        button.setTextColor(textColor);
        button.setTextSize(11);
        button.setTypeface(null, Typeface.BOLD);
        button.setAllCaps(false);
        button.setPadding(dp(10), 0, dp(10), 0);
        button.setMinWidth(0);
        button.setMinimumWidth(0);
        button.setMinHeight(0);
        button.setMinimumHeight(0);
        button.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        backgroundColor
                )
        );

        return button;
    }

    // UPDATE
    private void editStudent() {
        if (selectedDatabaseId == -1) {
            showMessage("Select a student card first.");
            return;
        }

        String name = etStudentName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        boolean enrolled = cbHasEnrolled.isChecked();

        if (!validateInput(name, studentId)) {
            return;
        }

        try {
            int result = dbHelper.updateStudent(
                    selectedDatabaseId,
                    name,
                    studentId,
                    enrolled
            );

            if (result > 0) {
                showMessage("Student updated successfully!");
                clearForm();
                loadStudents();
            } else {
                showMessage("No student was updated.");
            }

        } catch (SQLiteConstraintException e) {
            showMessage("Another student already uses that ID.");
        }
    }

    // DELETE CONFIRMATION
    private void confirmDelete() {
        if (selectedDatabaseId == -1) {
            showMessage("Select a student card first.");
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Delete Student")
                .setMessage(
                        "Are you sure you want to delete this student?"
                )
                .setPositiveButton("Delete", (dialog, which) ->
                        deleteStudent()
                )
                .setNegativeButton("Cancel", null)
                .show();
    }

    // DELETE
    private void deleteStudent() {
        int result = dbHelper.deleteStudent(selectedDatabaseId);

        if (result > 0) {
            showMessage("Student deleted successfully!");
            clearForm();
            loadStudents();
        } else {
            showMessage("Unable to delete student.");
        }
    }

    // VALIDATE INPUT
    private boolean validateInput(String name, String studentId) {
        if (name.isEmpty()) {
            etStudentName.setError("Student name is required.");
            etStudentName.requestFocus();
            return false;
        }

        if (studentId.isEmpty()) {
            etStudentId.setError("Student ID is required.");
            etStudentId.requestFocus();
            return false;
        }

        return true;
    }

    // CLEAR FORM
    private void clearForm() {
        etStudentName.setText("");
        etStudentId.setText("");
        cbHasEnrolled.setChecked(false);
        selectedDatabaseId = -1;

        etStudentName.setError(null);
        etStudentId.setError(null);

        showSelectionState(false);
    }

    private void showSelectionState(boolean selected) {
        btnAdd.setEnabled(!selected);
        btnAdd.setAlpha(selected ? 0.5f : 1f);

        btnEdit.setEnabled(selected);
        btnEdit.setAlpha(selected ? 1f : 0.5f);

        btnDelete.setEnabled(selected);
        btnDelete.setAlpha(selected ? 1f : 0.5f);
    }

    private String getInitials(String name) {
        String trimmed = name.trim();

        if (trimmed.isEmpty()) {
            return "?";
        }

        String[] parts = trimmed.split("\\s+");

        if (parts.length == 1) {
            return parts[0].substring(0, 1).toUpperCase();
        }

        return (
                parts[0].substring(0, 1) +
                        parts[parts.length - 1].substring(0, 1)
        ).toUpperCase();
    }

    private int dp(int value) {
        return (int) (value *
                getResources().getDisplayMetrics().density + 0.5f);
    }

    private void showMessage(String message) {
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}
