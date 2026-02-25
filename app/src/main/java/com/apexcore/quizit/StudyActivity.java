package com.apexcore.quizit;




import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudyActivity extends AppCompatActivity {
    private Deck currentDeck;
    private int currentIndex = 0;
    private int correctCount = 0;
    private boolean isAnswerVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int deckIndex = getIntent().getIntExtra("deck_index", 0);
        currentDeck = DataManager.allDecks.get(deckIndex);

        CardView flashcardView = findViewById(R.id.flashcardView);
        TextView tvDisplay = findViewById(R.id.tvDisplay);
        ProgressBar studyProgress = findViewById(R.id.studyProgress);
        Button btnCorrect = findViewById(R.id.btnCorrect);
        Button btnWrong = findViewById(R.id.btnWrong);
        Button btnExit = findViewById(R.id.btnExit);

        studyProgress.setMax(currentDeck.getCards().size());
        updateUI(tvDisplay, studyProgress);

        // Flip logic
        flashcardView.setOnClickListener(v -> {
            flashcardView.animate().rotationY(90).setDuration(150).withEndAction(() -> {
                isAnswerVisible = !isAnswerVisible;
                updateUI(tvDisplay, studyProgress);
                flashcardView.setRotationY(-90);
                flashcardView.animate().rotationY(0).setDuration(150).start();
            }).start();
        });

        // "Got it!" logic
        btnCorrect.setOnClickListener(v -> {
            correctCount++;
            moveToNextCard();
        });

        // "Missed" logic
        btnWrong.setOnClickListener(v -> moveToNextCard());

        btnExit.setOnClickListener(v -> showExitConfirmation());
    }

    private void moveToNextCard() {
        if (currentIndex < currentDeck.getCards().size() - 1) {
            currentIndex++;
            isAnswerVisible = false;
            updateUI(findViewById(R.id.tvDisplay), findViewById(R.id.studyProgress));
        } else {
            showFinalScore();
        }
    }

    private void updateUI(TextView tv, ProgressBar pb) {
        Flashcard card = currentDeck.getCards().get(currentIndex);
        pb.setProgress(currentIndex + 1);
        if (isAnswerVisible) {
            tv.setText(card.getAnswer());
            tv.setTextColor(getResources().getColor(R.color.primaryBlue));
        } else {
            tv.setText(card.getQuestion());
            tv.setTextColor(getResources().getColor(R.color.textPrimary));
        }
    }

    private void showFinalScore() {
        int total = currentDeck.getCards().size();
        int percent = (correctCount * 100) / total;

        new AlertDialog.Builder(this)
                .setTitle("Study Session Complete!")
                .setMessage("Score: " + correctCount + "/" + total + " (" + percent + "%)\nGreat job!")
                .setCancelable(false)
                .setPositiveButton("Back to Home", (dialog, which) -> finish())
                .show();
    }

    private void showExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Exit Study?")
                .setMessage("Your progress in this session will not be saved.")
                .setPositiveButton("Exit", (dialog, which) -> finish())
                .setNegativeButton("Stay", null)
                .show();
    }
}