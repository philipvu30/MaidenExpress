package com.maidenexpress;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CardAdapter extends ArrayAdapter<String> {

    Context context;

    public CardAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String text = getItem(position);
        CardVH holder;
        if (convertView != null) {
            holder = (CardVH) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_card_layout, parent, false);
            holder = new CardVH(convertView);
            convertView.setTag(holder);
            holder.tvCardNumber.setText(text);
            switch (text.charAt(0)){
                case '4':{
                    holder.imgCardInfo.setImageResource(R.drawable.ic_visa);
                    break;
                }
                case '5':{
                    holder.imgCardInfo.setImageResource(R.drawable.ic_master_card);
                    break;
                }
                default:break;
            }

        }

        return convertView;
    }




    class CardVH{
        TextView tvCardNumber;
        ImageView imgCardInfo;
        public CardVH(@NonNull View itemView) {
           tvCardNumber = itemView.findViewById(R.id.tvCardNumber);
            imgCardInfo = itemView.findViewById(R.id.imgCardInfo);
        }
    }
}
