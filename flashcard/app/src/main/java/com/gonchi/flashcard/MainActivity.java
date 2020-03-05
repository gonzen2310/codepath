package com.gonchi.flashcard;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gonchi.flashcard.database.Flashcard;
import com.gonchi.flashcard.database.FlashcardDatabase;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class MainActivity extends AppCompatActivity {

    int ADD_REQUEST_CODE = 2310;
    int EDIT_REQUEST_CODE = 2904;
    int questionNumber = 0;
    FlashcardDatabase flashcardDatabase;

    FloatingActionButton eyeBtn;
    FloatingActionButton addBtn;
    FloatingActionButton editBtn;

    TextView questionTV;
    TextView answerTV;
    TextView questionNumberTV;

    List<TextView> optionsViews;
    List<String> optionsStr;
    ImageButton nextBtn;
    ImageButton prevBtn;
    ImageButton deleteBtn;

    List<Flashcard> allFlashcards;

    boolean isShowingOptions = true;
    boolean isShowingQuestion = true;
    public String answer = "Alaska";
    KonfettiView konfettiView;


    Animation animateIn;
    Animation animateOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animateIn = new AlphaAnimation(0.0f, 1.0f);
        animateIn.setDuration(400);

        animateOut = new AlphaAnimation(1.0f, 0.0f);
        animateOut.setDuration(400);

        questionTV = findViewById(R.id.flashcard_question_tv);
        eyeBtn = findViewById(R.id.eye_btn);
        addBtn = findViewById(R.id.add_btn);
        answerTV = findViewById(R.id.flashcard_answer_tv);
        editBtn = findViewById(R.id.edit_btn);
        konfettiView = findViewById(R.id.viewKonfetti);
        nextBtn = findViewById(R.id.next_btn);
        prevBtn = findViewById(R.id.prev_btn);
        questionNumberTV = findViewById(R.id.question_number_tv);
        deleteBtn = findViewById(R.id.delete_btn);

        optionsViews = new ArrayList<>();
        optionsViews.add((TextView) findViewById(R.id.flashcard_option1_tv));
        optionsViews.add((TextView) findViewById(R.id.flashcard_option2_tv));
        optionsViews.add((TextView) findViewById(R.id.flashcard_option3_tv));

        flashcardDatabase = new FlashcardDatabase(getApplicationContext());
        allFlashcards = flashcardDatabase.getAllCards();

//        String s = "";
//
//        for(Flashcard flashcard: allFlashcards) {
//            s += "QUESTION: " + flashcard.getQuestion() + " ANSWER: " + flashcard.getAnswer() + " " +
//                    flashcard.getWrongAnswer1() +  " " + flashcard.getWrongAnswer2() + "\n";
//        }
//
//        Log.d("MAINACTIVITY", s);

        optionsStr = new ArrayList<>();
        for(TextView option: optionsViews) {
            optionsStr.add(option.getText().toString());
        }

        if (allFlashcards != null && allFlashcards.size() > 0) {
            questionTV.setText(allFlashcards.get(questionNumber).getQuestion());
            answerTV.setText(allFlashcards.get(questionNumber).getAnswer());
            setQuiz(allFlashcards.get(questionNumber).getQuestion(),
                    allFlashcards.get(questionNumber).getAnswer(),
                    allFlashcards.get(questionNumber).getWrongAnswer1(),
                    allFlashcards.get(questionNumber).getWrongAnswer2(),
                    false);
        } else {
            List<String> wrongAnswers = new ArrayList<>();
            for (String str: optionsStr)
                if (!answer.equals(str)) wrongAnswers.add(str);

            flashcardDatabase.insertCard(new Flashcard(questionTV.getText().toString(), answer, wrongAnswers.get(0), wrongAnswers.get(1)));
            allFlashcards = flashcardDatabase.getAllCards();
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
                MainActivity.this.startActivityForResult(intent, ADD_REQUEST_CODE);
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                intent.putStringArrayListExtra("editOptions", (ArrayList<String>)optionsStr);
                intent.putExtra("editQuestion", questionTV.getText().toString());
                intent.putExtra("editAnswer", answerTV.getText().toString());
                MainActivity.this.startActivityForResult(intent, EDIT_REQUEST_CODE);
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuestion();
            }
        });

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeQuestion();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flashcardDatabase.deleteCard(questionTV.getText().toString());
                allFlashcards = flashcardDatabase.getAllCards();
//                Toast.makeText(MainActivity.this, String.valueOf(currentCardDisplayedIndex), Toast.LENGTH_SHORT).show();
                changeQuestion();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String question = data.getExtras().getString("question");
            String answer = data.getExtras().getString("answer");
            String option1 = data.getExtras().getString("option1");
            String option2 = data.getExtras().getString("option2");

            if (requestCode == ADD_REQUEST_CODE) {
                flashcardDatabase.insertCard(new Flashcard(question, answer, option1, option2));
                allFlashcards = flashcardDatabase.getAllCards();
            }
            else if (requestCode == EDIT_REQUEST_CODE) {
                allFlashcards.get(questionNumber).setQuestion(question);
                allFlashcards.get(questionNumber).setAnswer(answer);
                allFlashcards.get(questionNumber).setWrongAnswer1(option1);
                allFlashcards.get(questionNumber).setWrongAnswer2(option2);
                flashcardDatabase.updateCard(allFlashcards.get(questionNumber));
            }
            setQuiz(question, answer, option1, option2, true);
        } else {
            Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private void changeQuestion() {
        questionNumber = getRandomNumber(0, allFlashcards.size()-1);
        Flashcard flashcard = allFlashcards.get(questionNumber);
        setQuiz(flashcard.getQuestion(),
                flashcard.getAnswer(),
                flashcard.getWrongAnswer1(),
                flashcard.getWrongAnswer2(),false);

        // always show question
        if (!isShowingQuestion) {
            questionTV.setVisibility(View.VISIBLE);
            answerTV.setVisibility(View.INVISIBLE);
            isShowingQuestion = true;
        }
        if (!isShowingOptions) toggleOptions();
        clearGame();
    }


    private Animation questionAnimationHandler(final String currQuestion) {
        // Animation
        final Animation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(200);

        final Animation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(200);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                questionTV.setText(currQuestion);
                questionTV.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
        return fadeOut;
    }

    private void toggleQuestionAnswer() {
        if(isShowingQuestion) {
            questionTV.startAnimation(animateOut);
            questionTV.setVisibility(View.INVISIBLE);
            answerTV.startAnimation(animateIn);
            answerTV.setVisibility(View.VISIBLE);
            if (isShowingOptions) toggleOptions();
        } else {
            questionTV.startAnimation(animateIn);
            questionTV.setVisibility(View.VISIBLE);
            answerTV.startAnimation(animateOut);
            answerTV.setVisibility(View.INVISIBLE);
            if (!isShowingOptions) toggleOptions();
        }
        isShowingQuestion = !isShowingQuestion;
        clearGame();
    }

    private void toggleOptions() {
        int visibility = isShowingOptions ? View.INVISIBLE : View.VISIBLE;
        for (TextView options: optionsViews) {
            options.setVisibility(visibility);
            options.startAnimation(isShowingOptions ? animateOut : animateIn);
        }
        eyeBtn.setImageResource(isShowingOptions ?
                R.drawable.ic_visibility_off :
                R.drawable.ic_visibility_on
        );
        isShowingOptions = !isShowingOptions;
    }

    private void setOptionsListeners(final List<TextView> options) {
        for(final TextView option: options) {
//            Toast.makeText(MainActivity.this, "ANSWER " + answerTV.getText().toString(), Toast.LENGTH_SHORT).show();

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
                                .streamFor(300, 500L);
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

    private void setQuiz(String question, String answer, String option1, String option2, boolean isNew) {
        this.answer = answer;
        questionTV.startAnimation(questionAnimationHandler(question));
        answerTV.setText(answer);
        questionNumberTV.setText(getString(R.string.question_number));
        optionsStr.clear();
        optionsStr.add(answer);
        optionsStr.add(option1);
        optionsStr.add(option2);

        shuffleQuestion();

        if (isNew)
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
    }

    public int getRandomNumber(int minNumber, int maxNumber) {
        Random rand = new Random();
        return rand.nextInt((maxNumber - minNumber) + 1) + minNumber;
    }

}