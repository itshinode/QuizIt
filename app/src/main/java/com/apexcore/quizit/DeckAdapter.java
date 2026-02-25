package com.apexcore.quizit;




import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class DeckAdapter extends ArrayAdapter<Deck> {

    public DeckAdapter(Context context, List<Deck> decks) {
        super(context, 0, decks);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the data item for this position
        Deck deck = getItem(position);

        // 2. Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_deck, parent, false);
        }

        // 3. Lookup view for data population
        TextView tvName = convertView.findViewById(R.id.tvDeckName);
        TextView tvCount = convertView.findViewById(R.id.tvCardCount);

        // 4. Populate the data into the template view
        if (deck != null) {
            tvName.setText(deck.getDeckName());
            tvCount.setText(deck.getCards().size() + " Cards");
        }

        // 5. Return the completed view to render on screen
        return convertView;
    }
}