package com.example.intellihome;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CardViewObjRecycleView extends RecyclerView.Adapter<CardViewObjRecycleView.ViewHolder> {

    Context context;
    ArrayList<CardViewObj> arrayList = new ArrayList<>();

    public CardViewObjRecycleView(ArrayList<CardViewObj> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        //holder.cardViewImage.setImageResource(arrayList.get(position).getImage());
        holder.cardViewLabel.setText(arrayList.get(position).getName());
        holder.cardViewCoords.setText(arrayList.get(position).getCoords());
        holder.cardViewDescription.setText(arrayList.get(position).getDescription());
        holder.cardViewPrice.setText(arrayList.get(position).getPrice());

        holder.cardViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Something Happend", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView cardViewImage;
        TextView cardViewLabel, cardViewCoords, cardViewDescription, cardViewPrice;
        CardView cardViewItem;

        public ViewHolder(@NonNull View viewCard){
            super(viewCard);

            cardViewImage = itemView.findViewById(R.id.CardView_Image);
            cardViewLabel = itemView.findViewById(R.id.CardView_Label);
            cardViewCoords = itemView.findViewById(R.id.CardView_Ubication);
            cardViewDescription = itemView.findViewById(R.id.CardView_Descrip);
            cardViewPrice = itemView.findViewById(R.id.CardView_Price);
            cardViewItem = itemView.findViewById(R.id.card_view);
        }
    }
}
