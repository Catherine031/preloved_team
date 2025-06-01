package com.example.preloved;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<CartItem> cartItems;
    private int userId;

    public CartAdapter(Context context, List<CartItem> cartItems, int userId) {
        this.context = context;
        this.cartItems = cartItems;
        this.userId = userId;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText("R" + item.getPrice());
        holder.quantity.setText("Qty: " + item.getQuantity()); // ✅ Set quantity

        Glide.with(context)
                .load("http://10.0.2.2/" + item.getImageUrl())
                .into(holder.image);
    }

    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price, quantity; // ✅ Added quantity

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cart_item_image);
            title = itemView.findViewById(R.id.cart_item_title);
            price = itemView.findViewById(R.id.cart_item_price);
            quantity = itemView.findViewById(R.id.item_quantity); // ✅ Bind quantity view
        }
    }
}
