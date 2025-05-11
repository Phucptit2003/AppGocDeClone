package com.example.app.Adapters.Home;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.app.Activities.ProductInformation.ProductInfoActivity;
import com.example.app.Model.Product;
import com.example.app.R;
import com.example.app.databinding.ItemFavouriteProductBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class FavouriteTechnologyAccessoryAdapter extends RecyclerView.Adapter<FavouriteTechnologyAccessoryAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Product> favouriteLists;
    private String userId;
    private String userName;
    private NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    public FavouriteTechnologyAccessoryAdapter(Context mContext, ArrayList<Product> lists, String id) {
        this.mContext = mContext;
        this.favouriteLists = lists;
        this.userId = id;

        FirebaseDatabase.getInstance().getReference().child("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("userName").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FavouriteTechnologyAccessoryAdapter.ViewHolder(ItemFavouriteProductBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteTechnologyAccessoryAdapter.ViewHolder holder, int position) {
        Product product = favouriteLists.get(position);
        if (product != null) {
            if (position==1) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                int margin = 120;
                layoutParams.setMargins(0, margin, 0, 0);
                holder.itemView.setLayoutParams(layoutParams);
            }
            else if (position== 0) {
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
                int margin = 50;
                layoutParams.setMargins(0, margin, 0, 0);
                holder.itemView.setLayoutParams(layoutParams);
            }

            Glide.with(mContext)
                    .load(product.getProductImage1())
                    .placeholder(R.drawable.image_default)
                    .into(holder.binding.imgFavouriteProduct);

            holder.binding.txtFavouriteProductName.setText(product.getProductName());
            double ratingStar = (double) Math.round(product.getRatingStar() * 10) / 10;
            holder.binding.txtFavouriteRating.setText(ratingStar + "/5.0");
            if (product.getRatingStar()>=5) {
                holder.binding.imgFavouriteRate.setImageResource(R.drawable.rating_star_filled);
            } else if (product.getRatingStar()>=3 && product.getRatingStar()<5) {
                holder.binding.imgFavouriteRate.setImageResource(R.drawable.rating_star_half);
            } else {
                holder.binding.imgFavouriteRate.setImageResource(R.drawable.rating_star_empty);
            }
            holder.binding.txtFavouriteProductPrice.setText(nf.format(product.getProductPrice()));
            holder.binding.parentOfItemInFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ProductInfoActivity.class);
                    intent.putExtra("productId", product.getProductId());
                    intent.putExtra("productName", product.getProductName());
                    intent.putExtra("productPrice", product.getProductPrice());
                    intent.putExtra("productImage1", product.getProductImage1());
                    intent.putExtra("productImage2", product.getProductImage2());
                    intent.putExtra("productImage3", product.getProductImage3());
                    intent.putExtra("productImage4", product.getProductImage4());
                    intent.putExtra("ratingStar", product.getRatingStar());
                    intent.putExtra("productDescription", product.getDescription());
                    intent.putExtra("publisherId", product.getPublisherId());
                    intent.putExtra("sold", product.getSold());
                    intent.putExtra("productType", product.getProductType());
                    intent.putExtra("remainAmount", product.getRemainAmount());
                    intent.putExtra("ratingAmount", product.getRatingAmount());
                    intent.putExtra("state", product.getState());
                    intent.putExtra("userId", userId);
                    intent.putExtra("userName", userName);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return favouriteLists == null ? 0 : favouriteLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavouriteProductBinding binding;

        public ViewHolder(ItemFavouriteProductBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
