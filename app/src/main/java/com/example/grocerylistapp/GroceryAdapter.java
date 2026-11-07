package com.example.grocerylistapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class GroceryAdapter extends ArrayAdapter<Grocery> {

    public GroceryAdapter(@NonNull Context context, @NonNull List<Grocery> groceries) {
        super(context, 0, groceries);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_grocery, parent, false);
        }

        Grocery grocery = getItem(position);

        TextView tvName = convertView.findViewById(R.id.tvGroceryName);
        TextView tvAmount = convertView.findViewById(R.id.tvGroceryAmount);
        TextView tvWhoAdded = convertView.findViewById(R.id.tvGroceryWhoAdded);
        ImageView ivImage = convertView.findViewById(R.id.ivGroceryImage);

        if (grocery != null) {
            tvName.setText(grocery.getName());
            tvAmount.setText("כמות: " + grocery.getAmount());
            tvWhoAdded.setText("נוסף על ידי: " + grocery.getWhoAdded());

            String base64Image = grocery.getImgData();
            if (base64Image != null && !base64Image.isEmpty()) {
                Bitmap bitmap = ImageUtils.convertBase64ToBitmap(base64Image);
                ivImage.setImageBitmap(bitmap);
            } else {
                ivImage.setImageResource(android.R.drawable.ic_menu_camera);
            }
        }

        return convertView;
    }
}
