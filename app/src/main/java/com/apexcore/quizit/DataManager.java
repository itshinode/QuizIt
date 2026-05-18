package com.apexcore.quizit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * DataManager handles persistence of Deck data using SharedPreferences and Gson.
 * It provides a central, static list of decks used across the application.
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static final String PREFS_NAME = "QuizItPrefs";
    private static final String KEY_DECKS = "decks_list";
    private static boolean isLoaded = false;

    /** Global static list of all decks. */
    public static final List<Deck> allDecks = new ArrayList<>();

    /**
     * Saves the current list of decks to persistent storage.
     * Uses commit() to ensure data is written immediately to avoid synchronization issues.
     */
    public static void saveDecks(Context context) {
        if (context == null) return;
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            String json = new Gson().toJson(allDecks);
            editor.putString(KEY_DECKS, json);
            // Use commit() for synchronous write to guarantee data is on disk before activity finishes.
            boolean success = editor.commit(); 
            if (!success) Log.e(TAG, "Failed to commit decks to SharedPreferences");
        } catch (Exception e) {
            Log.e(TAG, "Error saving decks", e);
        }
    }

    /**
     * Loads decks from persistent storage into the global allDecks list.
     */
    public static synchronized void loadDecks(Context context) {
        if (context == null) return;
        // If data is already loaded in this process, we skip disk I/O.
        if (isLoaded && !allDecks.isEmpty()) return;

        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_DECKS, null);
            if (json != null) {
                Type type = new TypeToken<ArrayList<Deck>>() {}.getType();
                List<Deck> loaded = new Gson().fromJson(json, type);
                if (loaded != null) {
                    allDecks.clear();
                    allDecks.addAll(loaded);
                }
            }
            isLoaded = true;
        } catch (JsonSyntaxException e) {
            Log.e(TAG, "Error parsing decks JSON, resetting decks", e);
            allDecks.clear();
            isLoaded = true; 
        } catch (Exception e) {
            Log.e(TAG, "Error loading decks", e);
        }
    }

    /**
     * Ensures decks are loaded into memory. Call this before any operation on allDecks.
     */
    public static void ensureDecksLoaded(Context context) {
        loadDecks(context);
    }
}
