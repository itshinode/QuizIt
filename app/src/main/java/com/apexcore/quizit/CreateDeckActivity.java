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
    int editIndex = -1; // -1 means we are creating a NEW deck

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

        // CHECK IF EDITING
        editIndex = getIntent().getIntExtra("edit_index", -1);
        if (editIndex != -1) {
            // Load existing deck
            temporaryDeck = DataManager.allDecks.get(editIndex);
            etDeckName.setText(temporaryDeck.getDeckName());
            cardCount = temporaryDeck.getCards().size();
            tvCardCount.setText("Cards Added: " + cardCount + " / 10");
            btnSaveDeck.setText("UPDATE DECK");
        }

        btnAddCard.setOnClickListener(v -> {
            String name = etDeckName.getText().toString().trim();
            String q = etQuestion.getText().toString().trim();
            String a = etAnswer.getText().toString().trim();

            if (name.isEmpty() || q.isEmpty() || a.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (temporaryDeck == null) temporaryDeck = new Deck(name);

            if (cardCount < 10) {
                temporaryDeck.addCard(new Flashcard(q, a));
                cardCount++;
                tvCardCount.setText("Cards Added: " + cardCount + " / 10");
                etQuestion.setText("");
                etAnswer.setText("");
                Toast.makeText(this, "Card " + cardCount + " Added!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Maximum 10 cards reached.", Toast.LENGTH_SHORT).show();
            }
        });

        btnSaveDeck.setOnClickListener(v -> {
            if (temporaryDeck != null && !temporaryDeck.getCards().isEmpty()) {

                if (editIndex == -1) {
                    // It's a brand new deck, add it to the list
                    DataManager.allDecks.add(temporaryDeck);
                } else {
                    // It's an edit, update the name and replace the old version
                    DataManager.allDecks.set(editIndex, temporaryDeck);
                }

                DataManager.saveDecks(this);
                finish();
            } else {
                Toast.makeText(this, "Add at least one card before saving!", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}