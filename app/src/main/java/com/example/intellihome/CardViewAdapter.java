package com.example.intellihome;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> { // Cambiado a CardViewAdapter.ViewHolder
    private List<PropertyModule> properties;
    private LayoutInflater layoutInflater;
    private Context context;
    final CardViewAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(PropertyModule obj);
    }

    public CardViewAdapter(List<PropertyModule> itemList, Context context, CardViewAdapter.OnItemClickListener listener) {
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.properties = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.card_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.bindData(properties.get(position));
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public void setItems(List<PropertyModule> items) {
        properties = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView photo;
        TextView label, tags, money;

        ViewHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.card_view_image);
            label = itemView.findViewById(R.id.card_view_label);
            tags = itemView.findViewById(R.id.card_view_tag);
            money = itemView.findViewById(R.id.card_view_money);
        }

        void bindData(final PropertyModule item) {
            label.setText(item.getTitle());
            tags.setText(item.getType());
            money.setText(item.getMoney());

            List<Uri> imageUris = item.getImageUris();
            if (imageUris != null && !imageUris.isEmpty()) {
                Uri firstImageUri = imageUris.get(0);
                photo.setImageURI(firstImageUri);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }
    }
}
