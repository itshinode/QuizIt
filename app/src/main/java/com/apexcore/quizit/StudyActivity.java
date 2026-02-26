package com.apexcore.quizit;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudyActivity extends AppCompatActivity {
    // 1. Declare variables as MEMBER variables so the whole class sees them
    private Deck currentDeck;
    private int currentIndex = 0;
    private int correctCount = 0;
    private boolean isAnswerVisible = false;

    private TextView tvDisplay;
    private ProgressBar studyProgress;
    private CardView flashcardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int deckIndex = getIntent().getIntExtra("deck_index", 0);
        currentDeck = DataManager.allDecks.get(deckIndex);

        // 2. Initialize the member variables
        flashcardView = findViewById(R.id.flashcardView);
        tvDisplay = findViewById(R.id.tvDisplay);
        studyProgress = findViewById(R.id.studyProgress);
        Button btnCorrect = findViewById(R.id.btnCorrect);
        Button btnWrong = findViewById(R.id.btnWrong);
        Button btnExit = findViewById(R.id.btnExit);

        studyProgress.setMax(currentDeck.getCards().size());
        updateUI(); // No need to pass variables anymore

        flashcardView.setOnClickListener(v -> {
            flashcardView.animate().rotationY(90).setDuration(150).withEndAction(() -> {
                isAnswerVisible = !isAnswerVisible;
                updateUI();
                flashcardView.setRotationY(-90);
                flashcardView.animate().rotationY(0).setDuration(150).start();
            }).start();
        });

        btnCorrect.setOnClickListener(v -> {
            correctCount++;
            moveToNextCard();
        });

        btnWrong.setOnClickListener(v -> moveToNextCard());
        btnExit.setOnClickListener(v -> showExitConfirmation());
    }

    private void moveToNextCard() {
        if (currentIndex < currentDeck.getCards().size() - 1) {
            currentIndex++;
            isAnswerVisible = false;
            updateUI();
        } else {
            showFinalScore();
        }
    }

    private void updateUI() {
        Flashcard card = currentDeck.getCards().get(currentIndex);
        studyProgress.setProgress(currentIndex + 1);

        if (isAnswerVisible) {
            tvDisplay.setText(card.getAnswer());
            tvDisplay.setTextColor(getResources().getColor(R.color.primaryBlue));
        } else {
            tvDisplay.setText(card.getQuestion());
            tvDisplay.setTextColor(getResources().getColor(R.color.textPrimary));
        }
    }

    private void showFinalScore() {
        int total = currentDeck.getCards().size();
        int percent = (correctCount * 100) / total;

        new AlertDialog.Builder(this)
                .setTitle("Study Session Complete!")
                .setMessage("Score: " + correctCount + "/" + total + " (" + percent + "%)")
                .setCancelable(false)
                .setPositiveButton("Back to Home", (dialog, which) -> finish())
                .show();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Study?")
                .setMessage("Your progress will be lost.")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Stay", null)
                .show();
    }
}