package com.apexcore.quizit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

/**
 * MainActivity serves as the hub for subject management.
 * Provides navigation to study sessions and full Edit/Delete capabilities.
 */
public class MainActivity extends AppCompatActivity implements DeckAdapter.OnDeckClickListener {
    private RecyclerView deckRecyclerView;
    private ExtendedFloatingActionButton btnCreateDeck;
    private LinearLayout emptyStateView;
    private DeckAdapter deckAdapter;

    @Override
    protected void onResume() {
        super.onResume();
        DataManager.ensureDecksLoaded(this);
        updateUI();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        setupListeners();
    }

    private void initializeViews() {
        deckRecyclerView = findViewById(R.id.deckRecyclerView);
        btnCreateDeck = findViewById(R.id.btnCreateDeck);
        emptyStateView = findViewById(R.id.emptyStateView);

        deckRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        deckAdapter = new DeckAdapter(DataManager.allDecks, this);
        deckRecyclerView.setAdapter(deckAdapter);
    }

    private void setupListeners() {
        btnCreateDeck.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CreateDeckActivity.class));
        });
    }

    @Override
    public void onDeckClick(int position) {
        if (position >= 0 && position < DataManager.allDecks.size()) {
            Intent intent = new Intent(MainActivity.this, StudyActivity.class);
            intent.putExtra("deck_index", position);
            startActivity(intent);
        }
    }

    @Override
    public void onDeckLongClick(int position) {
        if (position < 0 || position >= DataManager.allDecks.size()) return;

        String[] options = {getString(R.string.edit_deck), getString(R.string.delete_deck)};
        
        new AlertDialog.Builder(this)
                .setTitle(R.string.deck_options)
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        // Edit Subject
                        Intent intent = new Intent(MainActivity.this, CreateDeckActivity.class);
                        intent.putExtra("edit_index", position);
                        startActivity(intent);
                    } else {
                        // Delete Subject
                        showDeleteConfirmation(position);
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_deck_title)
                .setMessage(R.string.delete_deck_message)
                .setPositiveButton(R.string.delete_action, (dialog, which) -> {
                    DataManager.allDecks.remove(position);
                    DataManager.saveDecks(this);
                    updateUI();
                    Toast.makeText(this, R.string.toast_deck_removed, Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void updateUI() {
        if (DataManager.allDecks == null || DataManager.allDecks.isEmpty()) {
            emptyStateView.setVisibility(View.VISIBLE);
            deckRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateView.setVisibility(View.GONE);
            deckRecyclerView.setVisibility(View.VISIBLE);
            deckAdapter.notifyDataSetChanged();
        }
    }
}
