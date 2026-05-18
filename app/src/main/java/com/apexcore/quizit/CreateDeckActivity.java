package com.apexcore.quizit;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

/**
 * CreateDeckActivity handles the creation and editing of study subjects.
 */
public class CreateDeckActivity extends AppCompatActivity {
    private static final String KEY_WORKING_DECK = "working_deck";
    private static final String KEY_IS_MCQ = "is_mcq_mode";

    private Deck workingDeck;
    private int editIndex = -1;

    private TextInputEditText etDeckName, etQuestion, etAnswer;
    private TextInputEditText etOptA, etOptB, etOptC, etOptD;
    private TextView tvCardCount;
    private Spinner spinnerCorrectAnswer;
    private LinearLayout layoutManual, layoutMCQ;
    private MaterialButton btnToggleMCQ;
    private boolean isMCQMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_deck);

        // Crucial: Load existing data into memory immediately to prevent accidental overwrites
        DataManager.ensureDecksLoaded(this);

        initializeViews();
        setupSpinner();

        if (savedInstanceState != null) {
            workingDeck = (Deck) savedInstanceState.getSerializable(KEY_WORKING_DECK);
            isMCQMode = savedInstanceState.getBoolean(KEY_IS_MCQ);
            editIndex = getIntent().getIntExtra("edit_index", -1);
        } else {
            loadInitialData();
        }

        setupListeners();
        applyModeUI();
        updateCardCountDisplay();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_WORKING_DECK, workingDeck);
        outState.putBoolean(KEY_IS_MCQ, isMCQMode);
    }

    private void initializeViews() {
        etDeckName = findViewById(R.id.etDeckName);
        etQuestion = findViewById(R.id.etQuestion);
        etAnswer = findViewById(R.id.etAnswer);
        tvCardCount = findViewById(R.id.tvCardCount);
        layoutManual = findViewById(R.id.layoutManualInput);
        layoutMCQ = findViewById(R.id.layoutMCQInput);
        etOptA = findViewById(R.id.etOptA);
        etOptB = findViewById(R.id.etOptB);
        etOptC = findViewById(R.id.etOptC);
        etOptD = findViewById(R.id.etOptD);
        spinnerCorrectAnswer = findViewById(R.id.spinnerCorrectAnswer);
        btnToggleMCQ = findViewById(R.id.btnToggleMCQ);
    }

    private void setupSpinner() {
        String[] keys = {"A", "B", "C", "D"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, keys);
        spinnerCorrectAnswer.setAdapter(adapter);
    }

    private void loadInitialData() {
        editIndex = getIntent().getIntExtra("edit_index", -1);
        if (editIndex != -1 && editIndex < DataManager.allDecks.size()) {
            // Work on a copy to allow discarding changes if the user cancels
            workingDeck = new Deck(DataManager.allDecks.get(editIndex));
            etDeckName.setText(workingDeck.getDeckName());
        } else {
            workingDeck = new Deck("");
        }
    }

    private void setupListeners() {
        btnToggleMCQ.setOnClickListener(v -> {
            isMCQMode = !isMCQMode;
            applyModeUI();
        });

        findViewById(R.id.btnAddCard).setOnClickListener(v -> attemptAddCard());
        findViewById(R.id.btnSaveDeck).setOnClickListener(v -> saveAndExit());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void applyModeUI() {
        layoutMCQ.setVisibility(isMCQMode ? View.VISIBLE : View.GONE);
        layoutManual.setVisibility(isMCQMode ? View.GONE : View.VISIBLE);
        btnToggleMCQ.setText(isMCQMode ? R.string.switch_to_manual : R.string.switch_to_mcq);
    }

    private void attemptAddCard() {
        String name = etDeckName.getText().toString().trim();
        String q = etQuestion.getText().toString().trim();

        if (name.isEmpty() || q.isEmpty()) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        workingDeck.setDeckName(name);

        if (isMCQMode) {
            addMCQCard(q);
        } else {
            addManualCard(q);
        }
    }

    private void addMCQCard(String question) {
        String a = etOptA.getText().toString().trim();
        String b = etOptB.getText().toString().trim();
        String c = etOptC.getText().toString().trim();
        String d = etOptD.getText().toString().trim();
        String correct = spinnerCorrectAnswer.getSelectedItem().toString();

        if (a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            Toast.makeText(this, R.string.error_mcq_options, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> options = new ArrayList<>();
        options.add(a); options.add(b); options.add(c); options.add(d);
        workingDeck.addCard(new Flashcard(question, correct, options));
        onCardAdded();
    }

    private void addManualCard(String question) {
        String ans = etAnswer.getText().toString().trim();
        if (ans.isEmpty()) {
            Toast.makeText(this, R.string.error_answer_required, Toast.LENGTH_SHORT).show();
            return;
        }
        workingDeck.addCard(new Flashcard(question, ans, Flashcard.TYPE_MANUAL));
        onCardAdded();
    }

    private void onCardAdded() {
        updateCardCountDisplay();
        clearInputs();
        Toast.makeText(this, "Card added!", Toast.LENGTH_SHORT).show();
    }

    private void updateCardCountDisplay() {
        tvCardCount.setText(getString(R.string.cards_added_format, workingDeck.getCards().size()));
    }

    private void clearInputs() {
        etQuestion.setText("");
        etAnswer.setText("");
        etOptA.setText("");
        etOptB.setText("");
        etOptC.setText("");
        etOptD.setText("");
    }

    private void saveAndExit() {
        String name = etDeckName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, R.string.error_required_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        if (workingDeck.getCards().isEmpty()) {
            Toast.makeText(this, R.string.error_add_cards_first, Toast.LENGTH_SHORT).show();
            return;
        }

        workingDeck.setDeckName(name);
        if (editIndex == -1) {
            DataManager.allDecks.add(workingDeck);
        } else {
            DataManager.allDecks.set(editIndex, workingDeck);
        }

        // Save immediately to disk
        DataManager.saveDecks(this);
        finish();
    }
}
