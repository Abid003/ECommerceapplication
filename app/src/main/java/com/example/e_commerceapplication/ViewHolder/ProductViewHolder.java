package com.example.e_commerceapplication.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerceapplication.Interface.ItemClickListener;
import com.example.e_commerceapplication.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView textProductName, textProductDescription,textProductPrice;
    public ImageView imageView;
    public ItemClickListener listener;

    public ProductViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView =(ImageView) itemView.findViewById(R.id.product_image_layout);
        textProductName = itemView.findViewById(R.id.product_name_layout);
        textProductDescription = itemView.findViewById(R.id.product_description_layout);
        textProductPrice= itemView.findViewById(R.id.product_price_layout);

    }

    public void setItemClickListener(ItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        listener.onclick(v, getAdapterPosition(),false);
    }
}
