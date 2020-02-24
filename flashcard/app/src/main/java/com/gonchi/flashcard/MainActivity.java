package com.gonchi.flashcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class MainActivity extends AppCompatActivity {

    int REQUEST_CODE = 2310;

    FloatingActionButton eyeBtn;
    FloatingActionButton addBtn;
    FloatingActionButton editBtn;

    TextView questionTV;
    TextView answerTV;
    List<TextView> optionsViews;
    List<String> optionsStr;

    boolean isShowingOptions = true;
    boolean isShowingQuestion = true;
    public String answer = "Alaska";
    KonfettiView konfettiView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTV = findViewById(R.id.flashcard_question_tv);
        eyeBtn = findViewById(R.id.eye_btn);
        addBtn = findViewById(R.id.add_btn);
        answerTV = findViewById(R.id.flashcard_answer_tv);
        editBtn = findViewById(R.id.edit_btn);
        konfettiView = findViewById(R.id.viewKonfetti);
        optionsViews = new ArrayList<>();
        optionsViews.add((TextView) findViewById(R.id.flashcard_option1_tv));
        optionsViews.add((TextView) findViewById(R.id.flashcard_option2_tv));
        optionsViews.add((TextView) findViewById(R.id.flashcard_option3_tv));

        optionsStr = new ArrayList<>();
        for(TextView option: optionsViews) {
            optionsStr.add(option.getText().toString());
        }


        // Toggle answer/question cards
        questionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuestionAnswer();
            }
        });

        answerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuestionAnswer();
            }
        });

        setOptionsListeners(optionsViews);

        eyeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleOptions();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putStringArrayListExtra("editOptions", (ArrayList<String>)optionsStr);
                intent.putExtra("editQuestion", questionTV.getText().toString());
                intent.putExtra("editAnswer", answerTV.getText().toString());
                MainActivity.this.startActivityForResult(intent, REQUEST_CODE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) { // this 100 needs to match the 100 we used when we called startActivityForResult!
            String question = data.getExtras().getString("question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String answer = data.getExtras().getString("answer");
            String option1 = data.getExtras().getString("option1");
            String option2 = data.getExtras().getString("option2");
            setQuiz(question, answer, option1, option2);
        }
    }

    private void toggleQuestionAnswer() {
        if(isShowingQuestion) {
            questionTV.setVisibility(View.INVISIBLE);
            answerTV.setVisibility(View.VISIBLE);
            if (isShowingOptions) toggleOptions();
        } else {
            questionTV.setVisibility(View.VISIBLE);
            answerTV.setVisibility(View.INVISIBLE);
            if (!isShowingOptions) toggleOptions();
        }
        isShowingQuestion = !isShowingQuestion;
        clearGame();
    }

    private void toggleOptions() {
        int visibility = isShowingOptions ? View.INVISIBLE : View.VISIBLE;
        for (TextView options: optionsViews )
            options.setVisibility(visibility);
        eyeBtn.setImageResource(isShowingOptions ?
                R.drawable.ic_visibility_off :
                R.drawable.ic_visibility_on
        );
        isShowingOptions = !isShowingOptions;
    }

    private void setOptionsListeners(final List<TextView> options) {
        for(final TextView option: options) {
            option.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkAnswer(option.getText().toString())) {
                        option.setBackground(getResources().getDrawable(R.drawable.wrong_answer, null));
                    } else {
                        toggleQuestionAnswer();
                        option.setBackground(getResources().getDrawable(R.drawable.correct_answer, null));
                        konfettiView.build()
                                .addColors(Color.YELLOW, Color.GREEN, Color.RED, Color.BLUE)
                                .setDirection(0.0, 359.0)
                                .setSpeed(1f, 5f)
                                .setFadeOutEnabled(true)
                                .setTimeToLive(2000L)
                                .addShapes(Shape.RECT, Shape.CIRCLE)
                                .addSizes(new Size(12, 5))
                                .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                                .streamFor(300, 1000L);
                    }
                }
            });
        }
    }

    private void clearGame() {
        for(final TextView option: optionsViews) {
            option.setBackground(getResources().getDrawable(R.drawable.option_card, null));
            option.setTextColor(getResources().getColor(R.color.textColor, null));
        }
        shuffleQuestion();
    }

    private boolean checkAnswer(String chosenAnswer) {
        return answer.equals(chosenAnswer);
    }

    private void setQuiz(String question, String answer, String option1, String option2) {
        this.answer = answer;
        questionTV.setText(question);
        answerTV.setText(answer);
        optionsStr.clear();
        optionsStr.add(answer);
        optionsStr.add(option1);
        optionsStr.add(option2);

        shuffleQuestion();

        Snackbar.make(findViewById(R.id.quiz_container),
                "Card successfully created",
                Snackbar.LENGTH_SHORT)
                .show();
    }


    private void shuffleQuestion() {
        Collections.shuffle(optionsStr);
        String option;
        for (int i = 0; i < optionsViews.size(); i++) {
            option = optionsStr.get(i);
            optionsViews.get(i).setText(option);
        }
//        Toast.makeText(this, optionsStr.toString(), Toast.LENGTH_SHORT).show();
    }

}