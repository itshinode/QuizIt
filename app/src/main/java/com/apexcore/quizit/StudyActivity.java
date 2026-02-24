package com.apexcore.quizit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

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

        TextView tvDisplay = findViewById(R.id.tvDisplay);
        Button btnNext = findViewById(R.id.btnNext);
        Button btnExit = findViewById(R.id.btnExit);

        updateCardUI(tvDisplay);

        tvDisplay.setOnClickListener(v -> {
            isAnswerVisible = !isAnswerVisible;
            updateCardUI(tvDisplay);
        });

        btnNext.setOnClickListener(v -> {
            if (currentIndex < currentDeck.getCards().size() - 1) {
                currentIndex++;
                isAnswerVisible = false;
                updateCardUI(tvDisplay);
            } else {
                Toast.makeText(this, "End of Deck!", Toast.LENGTH_SHORT).show();
            }
        });

        btnExit.setOnClickListener(v -> finish());
    }

    private void updateCardUI(TextView tv) {
        Flashcard card = currentDeck.getCards().get(currentIndex);
        if (isAnswerVisible) {
            tv.setText("Question:\n" + card.getQuestion() + "\n\nAnswer:\n" + card.getAnswer());
        } else {
            tv.setText("[ QUESTION ]\n\n" + card.getQuestion() + "\n\n(Tap to Reveal)");
        }
    }
}