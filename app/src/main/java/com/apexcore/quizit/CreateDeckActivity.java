package com.apexcore.quizit;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateDeckActivity extends AppCompatActivity {
    Deck temporaryDeck;
    int cardCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        EditText etDeckName = findViewById(R.id.etDeckName);
        EditText etQuestion = findViewById(R.id.etQuestion);
        EditText etAnswer = findViewById(R.id.etAnswer);
        TextView tvCardCount = findViewById(R.id.tvCardCount);
        Button btnAddCard = findViewById(R.id.btnAddCard);
        Button btnSaveDeck = findViewById(R.id.btnSaveDeck);
        Button btnBack = findViewById(R.id.btnBack);

        btnAddCard.setOnClickListener(v -> {
            String q = etQuestion.getText().toString();
            String a = etAnswer.getText().toString();
            String name = etDeckName.getText().toString();

            if (name.isEmpty() || q.isEmpty() || a.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (temporaryDeck == null) temporaryDeck = new Deck(name);

            if (cardCount < 10) {
                temporaryDeck.addCard(new Flashcard(q, a));
                cardCount++;
                tvCardCount.setText("Cards: " + cardCount + "/10");
                etQuestion.setText("");
                etAnswer.setText("");
                etDeckName.setEnabled(false); // Lock name once deck creation starts
                Toast.makeText(this, "Card Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Limit of 10 cards reached!", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveDeck.setOnClickListener(v -> {
            if (temporaryDeck != null && !temporaryDeck.getCards().isEmpty()) {
                DataManager.allDecks.add(temporaryDeck);
                finish();
            } else {
                Toast.makeText(this, "Add at least one card!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}