package com.apexcore.quizit;


import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    public static List<Deck> allDecks = new ArrayList<>();
    private static final String PREFS_NAME = "QuizItPrefs";
    private static final String KEY_DECKS = "decks_list";

    // Save the list of decks to permanent storage
    public static void saveDecks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(allDecks);
        editor.putString(KEY_DECKS, json);
        editor.apply();
    }

    // Load the list of decks from permanent storage
    public static void loadDecks(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(KEY_DECKS, null);
        Type type = new TypeToken<ArrayList<Deck>>() {}.getType();

        List<Deck> loadedDecks = gson.fromJson(json, type);
        if (loadedDecks != null) {
            allDecks = loadedDecks;
        } else {
            allDecks = new ArrayList<>();
        }
    }
}