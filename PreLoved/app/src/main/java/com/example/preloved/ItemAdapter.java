package com.example.preloved;

import static android.content.Intent.getIntent;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private List<Item> items;
    private int userId;
    private Context context;

    public ItemAdapter(Context context, List<Item> items, int userId) {
        this.context = context;

        this.items = items;
        this.userId = userId;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(view);
    }



    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText("R" + item.getPrice());
        holder.username.setText("@" + item.getUsername());

        Glide.with(holder.image.getContext())
                .load("http://10.0.2.2/" + item.getImageUrl())
                .into(holder.image);


    }




    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_image);
            title = itemView.findViewById(R.id.item_title);
            price = itemView.findViewById(R.id.item_price);
            username = itemView.findViewById(R.id.item_username);

            title.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Item clickedItem = items.get(position);
                    Context context = itemView.getContext();
                    Intent intent = new Intent(context, ItemDetailsActivity.class);
                    intent.putExtra("title", clickedItem.getTitle());
                    intent.putExtra("user_id", userId); // This must be included!


                    context.startActivity(intent);
                }
            });
        }
        }
    }



