package com.apexcore.quizit;



import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class StudyActivity extends AppCompatActivity {
    Deck currentDeck;
    int currentIndex = 0;
    boolean isAnswerVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        int deckIndex = getIntent().getIntExtra("deck_index", 0);
        currentDeck = DataManager.allDecks.get(deckIndex);

        CardView flashcardView = findViewById(R.id.flashcardView);
        TextView tvDisplay = findViewById(R.id.tvDisplay);
        ProgressBar studyProgress = findViewById(R.id.studyProgress);
        TextView tvProgressText = findViewById(R.id.tvProgressText);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnExit = findViewById(R.id.btnExit);

        // Set initial progress
        studyProgress.setMax(currentDeck.getCards().size());
        updateUI(tvDisplay, studyProgress, tvProgressText);

        flashcardView.setOnClickListener(v -> {
            // Flip Animation
            flashcardView.animate().withLayer().rotationY(90).setDuration(150).withEndAction(() -> {
                isAnswerVisible = !isAnswerVisible;
                updateUI(tvDisplay, studyProgress, tvProgressText);
                flashcardView.setRotationY(-90);
                flashcardView.animate().withLayer().rotationY(0).setDuration(150).start();
            }).start();
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < currentDeck.getCards().size() - 1) {
                currentIndex++;
                isAnswerVisible = false;
                updateUI(tvDisplay, studyProgress, tvProgressText);
            } else {
                Toast.makeText(this, "Deck Completed!", Toast.LENGTH_SHORT).show();
            }
        });

        btnExit.setOnClickListener(v -> finish());
    }

    private void updateUI(TextView tv, ProgressBar pb, TextView progressText) {
        Flashcard card = currentDeck.getCards().get(currentIndex);
        pb.setProgress(currentIndex + 1);
        progressText.setText("Card " + (currentIndex + 1) + " of " + currentDeck.getCards().size());

        if (isAnswerVisible) {
            tv.setText(card.getAnswer());
            tv.setTextColor(getResources().getColor(R.color.primaryBlue));
        } else {
            tv.setText(card.getQuestion());
            tv.setTextColor(getResources().getColor(R.color.textPrimary));
        }
    }
}