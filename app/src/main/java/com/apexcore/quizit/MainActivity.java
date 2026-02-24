package com.apexcore.quizit;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView deckListView;
    Button btnCreateDeck;
    ArrayAdapter<String> adapter;

    @Override
    protected void onResume() {
        super.onResume();
        updateList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deckListView = findViewById(R.id.deckListView);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);

        updateList();

        // Single Click to Study
        deckListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra("deck_index", position);
            startActivity(intent);
        });

        // Long Click to Delete
        deckListView.setOnItemLongClickListener((parent, view, position, id) -> {
            new AlertDialog.Builder(this)
                    .setTitle("Delete Deck")
                    .setMessage("Are you sure you want to delete this deck?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        DataManager.allDecks.remove(position);
                        updateList();
                        Toast.makeText(this, "Deck Deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
            return true;
        });

        btnCreateDeck.setOnClickListener(v -> {
            if (DataManager.allDecks.size() >= 5) {
                Toast.makeText(this, "Maximum 5 decks allowed!", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CreateDeckActivity.class));
            }
        });
    }

    private void updateList() {
        List<String> deckNames = new ArrayList<>();
        for (Deck d : DataManager.allDecks) {
            deckNames.add(d.getDeckName() + " (" + d.getCards().size() + " cards)");
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deckNames);
        deckListView.setAdapter(adapter);
    }
}