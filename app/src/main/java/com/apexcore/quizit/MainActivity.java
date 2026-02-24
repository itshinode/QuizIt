package com.apexcore.quizit;




import android.content.Intent;
import android.os.Bundle;
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

        DataManager.loadDecks(this);

        deckListView = findViewById(R.id.deckListView);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);

        updateList();

        // Single Click to Study
        deckListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra("deck_index", position);
            startActivity(intent);
        });

        // Long Click for Options (Edit/Delete)
        deckListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String[] options = {"Edit Deck", "Delete Deck"};

            new AlertDialog.Builder(this)
                    .setTitle("Options")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // EDIT: Send index to CreateDeckActivity
                            Intent intent = new Intent(this, CreateDeckActivity.class);
                            intent.putExtra("edit_index", position);
                            startActivity(intent);
                        } else {
                            // DELETE
                            DataManager.allDecks.remove(position);
                            DataManager.saveDecks(this);
                            updateList();
                            Toast.makeText(this, "Deck Deleted", Toast.LENGTH_SHORT).show();
                        }
                    })
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