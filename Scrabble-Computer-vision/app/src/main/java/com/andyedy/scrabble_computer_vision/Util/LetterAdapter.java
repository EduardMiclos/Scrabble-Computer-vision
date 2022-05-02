package com.andyedy.scrabble_computer_vision.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andyedy.scrabble_computer_vision.R;

import java.util.List;

public class LetterAdapter extends RecyclerView.Adapter<LetterAdapter.ViewHolder> {

    private List<Letter> letters;
    private LayoutInflater inflater;


    public LetterAdapter(Context context, List<Letter> letterList) {
        letters = letterList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View letterView = inflater.inflate(R.layout.letter_list_item_layout, parent, false);

        ViewHolder viewHolder = new ViewHolder(letterView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Letter l = letters.get(position);
        holder.letter_textview.setText(l.getValue());
        holder.frequency_textview.setText(String.format("x%d", l.getFrequency()));
    }

    @Override
    public int getItemCount() {
        return letters.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView letter_textview;
        public TextView frequency_textview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            letter_textview = (TextView) itemView.findViewById(R.id.letter_text);
            frequency_textview = (TextView) itemView.findViewById(R.id.frequency_text);
        }
    }
}
