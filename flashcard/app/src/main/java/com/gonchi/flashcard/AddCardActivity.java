package com.gonchi.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class AddCardActivity extends AppCompatActivity {

    FloatingActionButton cancelBtn;
    FloatingActionButton saveBtn;
    EditText questionET;
    EditText answerET;
    EditText option1ET;
    EditText option2ET;

    List<String> editOptions;

    String editQuestion, editAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        cancelBtn = findViewById(R.id.cancel_btn);
        saveBtn = findViewById(R.id.save_btn);
        questionET = findViewById(R.id.add_question_et);
        answerET = findViewById(R.id.add_answer_et);
        option1ET = findViewById(R.id.add_option1_et);
        option2ET = findViewById(R.id.add_option2_et);

        if (isEditQuestion()) {
            questionET.setText(editQuestion);
            answerET.setText(editAnswer);
            editOptions.remove(editAnswer);

            option1ET.setText(editOptions.get(0));
            option2ET.setText(editOptions.get(1));

        }


        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = questionET.getText().toString();
                String answer = answerET.getText().toString();
                String option1 = option1ET.getText().toString();
                String option2 = option2ET.getText().toString();
                if (question.isEmpty() || answer.isEmpty() || option1.isEmpty() || option2.isEmpty())  {
                    Snackbar.make(findViewById(R.id.add_card_container),
                            "Oops! Make sure you filled out all the text boxes",
                            Snackbar.LENGTH_SHORT)
                            .show();
                    return;
                }
                Intent data = new Intent();
                data.putExtra("question", question);
                data.putExtra("answer", answer);
                data.putExtra("option1", option1);
                data.putExtra("option2", option2);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    private boolean isEditQuestion() {
        editQuestion = getIntent().getStringExtra("editQuestion");
        editAnswer = getIntent().getStringExtra("editAnswer");
        editOptions = getIntent().getStringArrayListExtra("editOptions");
        return editQuestion != null && editAnswer != null && editOptions != null;
    }
    
}
