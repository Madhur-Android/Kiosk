package com.example.kiosk.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kiosk.Data.Menu.ShopMenuItem;
import com.example.kiosk.MainActivity;
import com.example.kiosk.OrderSummaryActivity;
import com.example.kiosk.R;
import com.example.kiosk.Room.CartModel;
import com.example.kiosk.Room.DatabaseHelper;
import com.squareup.picasso.Picasso;

import java.nio.FloatBuffer;
import java.util.List;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MyViewHolder> {


    Context context;
    List<ShopMenuItem> data;
    DatabaseHelper databaseHelper;

    public MenuListAdapter(Context context, List<ShopMenuItem> data) {
        this.context = context;
        this.data = data;
        databaseHelper = DatabaseHelper.getInstance(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.menu_list_items, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load("https://segwitz-api.myeongdongtopokki.com/"+data.get(position).getImage()).placeholder(R.drawable.buffering_img).into(holder.image);
        holder.dish_name.setText(data.get(position).getName());
        holder.amount.setText("RM " + data.get(position).getBasePrice().getDineIn().toString());
//            Log.d("hello usfjksdfjksdf", singleUnit.getName() + " " + singleUnit.getImage() + " " + singleUnit.getBasePrice().getDineIn());
        holder.dish_name.setText(data.get(position).getName());
        holder.amount.setText("RM" + data.get(position).getBasePrice().getDineIn().toString());

//        String cartName = holder.dish_name.getText().toString();
////        String cartDisc;
////        if (data.get(position).getMasterMenu().getName() != null) {
////            cartDisc = data.get(position).getMasterMenu().getName();
////        } else {
////            cartDisc = " ";
////        }
//        float price = data.get(position).getBasePrice().getDineIn().floatValue();
//        String imagePath = data.get(position).getImage();
//        ////////////////
        holder.add_to_card.setOnClickListener(v -> {

            if (databaseHelper.cartDao().doesUserExist(data.get(position).getItemId())) {
                Log.d("idididididididid111111", String.valueOf(data.get(position).getItemId()));
                Toast.makeText(context, "Item already exists in the cart", Toast.LENGTH_SHORT).show();
            } else {
                databaseHelper.cartDao().addCart(new CartModel((Integer) data.get(position).getItemId(), data.get(position).getName(), data.get(position).getMasterMenu().getDesc(), data.get(position).getBasePrice().getDineIn().floatValue(), data.get(position).getImage()));
                ((MainActivity) context).showCartCount();
                Toast.makeText(context, "Item successfully added to cart", Toast.LENGTH_SHORT).show();
                Log.d("idididididididid222222", String.valueOf(data.get(position).getItemId()));
            }

//            int itemId = data.get(position).getItemId();
//            int shopId = data.get(position).getShopId();
//            Intent i = new Intent(context, OrderSummaryActivity.class);
//            i.putExtra("itemId", itemId);
//            i.putExtra("shopId", shopId);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        } else {
            return 0;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView dish_name, amount, add_to_card;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            dish_name = itemView.findViewById(R.id.dish_name);
            amount = itemView.findViewById(R.id.amount);
            add_to_card = itemView.findViewById(R.id.add_to_card);
        }
    }
}