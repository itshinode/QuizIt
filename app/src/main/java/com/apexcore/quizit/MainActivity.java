package com.apexcore.quizit;






import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView deckListView;
    private Button btnCreateDeck;
    private LinearLayout emptyStateView;
    private DeckAdapter deckAdapter; // Our new Custom Adapter

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the list whenever returning to the home screen
        updateList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. Install Splash Screen (Must be before super.onCreate)
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Initialize UI
        deckListView = findViewById(R.id.deckListView);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);
        emptyStateView = findViewById(R.id.emptyStateView);

        // 3. Load Persistent Data
        DataManager.loadDecks(this);

        // 4. Initial List Setup
        updateList();

        // 5. Single Click: Study Mode
        deckListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, StudyActivity.class);
            intent.putExtra("deck_index", position);
            startActivity(intent);
        });

        // 6. Long Click: Options Menu (Edit/Delete)
        deckListView.setOnItemLongClickListener((parent, view, position, id) -> {
            String[] options = {"Edit Deck", "Delete Deck"};

            new AlertDialog.Builder(this)
                    .setTitle("Deck Options")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            // EDIT: Navigate to CreateDeckActivity with index
                            Intent intent = new Intent(this, CreateDeckActivity.class);
                            intent.putExtra("edit_index", position);
                            startActivity(intent);
                        } else {
                            // DELETE: Confirm and save changes
                            confirmDelete(position);
                        }
                    })
                    .show();
            return true;
        });

        // 7. Create Button Logic (5 Deck Limit)
        btnCreateDeck.setOnClickListener(v -> {
            if (DataManager.allDecks.size() >= 5) {
                Toast.makeText(this, "Deck limit reached (Max: 5)", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(this, CreateDeckActivity.class));
            }
        });
    }

    private void confirmDelete(int position) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Deck")
                .setMessage("Are you sure? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> {
                    DataManager.allDecks.remove(position);
                    DataManager.saveDecks(this);
                    updateList();
                    Toast.makeText(this, "Deck removed", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Re-renders the list using the Custom DeckAdapter
     */
    private void updateList() {
        if (DataManager.allDecks.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            deckListView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            deckListView.setVisibility(View.VISIBLE);

            // Use our new DeckAdapter instead of simple ArrayAdapter
            deckAdapter = new DeckAdapter(this, DataManager.allDecks);
            deckListView.setAdapter(deckAdapter);
        }
    }
}