package com.gonchi.flashcard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {


    ImageButton eyeBtn;
    TextView questionTV;
    TextView answerTV;
    List<TextView> options;
    boolean isShowingOptions = true;
    static final String answer = "Alaska";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTV = findViewById(R.id.flashcard_question_tv);
        eyeBtn = findViewById(R.id.eye_btn);
        answerTV = findViewById(R.id.flashcard_answer_tv);
        options = new ArrayList<>();
        options.add((TextView) findViewById(R.id.flashcard_option1_tv));
        options.add((TextView) findViewById(R.id.flashcard_option2_tv));
        options.add((TextView) findViewById(R.id.flashcard_option3_tv));

        questionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuestionAnswer(questionTV.getVisibility());
            }
        });

        answerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuestionAnswer(questionTV.getVisibility());
            }
        });

        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eyeBtn.setImageResource(isShowingOptions ?
                       R.drawable.ic_visibility_off :
                        R.drawable.ic_visibility_on
                        );
                toggleOptions(isShowingOptions);
                isShowingOptions = !isShowingOptions;
            }
        });

        setOptionsListeners(options);
    }

    private void toggleQuestionAnswer(int questionVisibility) {
        if(questionVisibility == View.VISIBLE) {
            questionTV.setVisibility(View.INVISIBLE);
            answerTV.setVisibility(View.VISIBLE);
        } else {
            questionTV.setVisibility(View.VISIBLE);
            answerTV.setVisibility(View.INVISIBLE);
        }
        clearGame();
    }

        private void toggleOptions(boolean show) {
        int visibility = show ? View.INVISIBLE : View.VISIBLE;
        for (TextView options: options ) {
            options.setVisibility(visibility);
        }
    }

    private void setOptionsListeners(final List<TextView> options) {
        for(final TextView option: options) {
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkAnswer(option.getText().toString())) {
                        option.setBackground(getResources().getDrawable(R.drawable.wrong_answer, null));
                    } else {
                        toggleQuestionAnswer(questionTV.getVisibility());
                        option.setBackground(getResources().getDrawable(R.drawable.correct_answer, null));
                    }
                    option.setTextColor(getResources().getColor(R.color.white, null));
                }
            });
        }
    }

    private void clearGame() {
        for(final TextView option: options) {
            option.setBackground(getResources().getDrawable(R.drawable.option_card, null));
            option.setTextColor(getResources().getColor(R.color.textColor, null));
        }
    }

    private boolean checkAnswer(String chosenAnswer) {
        return answer.equals(chosenAnswer);
    }
}