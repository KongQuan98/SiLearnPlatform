package com.example.imagepro;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ItemViewHolder>{

    private List<LibraryModel> modelList;

    public LibraryAdapter(List<LibraryModel> modelList){
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public LibraryAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.library_item, parent, false);
        return new LibraryAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryAdapter.ItemViewHolder holder, int position) {

        LibraryModel model = modelList.get(position);
        holder.textView.setText(model.getItemText());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(v.getContext());
                View popupView = LayoutInflater.from(v.getContext()).inflate(
                        R.layout.library_pop_up, v.findViewById(R.id.library_popup_container));

                GifImageView gifImageView = popupView.findViewById(R.id.gifImage);

                if (model.getItemText().equals("A")){
                    gifImageView.setImageResource(R.drawable.a);
                }else if (model.getItemText().equals("B")){
                    gifImageView.setImageResource(R.drawable.b);
                }else if (model.getItemText().equals("C")){
                    gifImageView.setImageResource(R.drawable.c);
                }else if (model.getItemText().equals("D")){
                    gifImageView.setImageResource(R.drawable.d);
                }else if (model.getItemText().equals("E")){
                    gifImageView.setImageResource(R.drawable.e);
                }else if (model.getItemText().equals("F")){
                    gifImageView.setImageResource(R.drawable.f);
                }else if (model.getItemText().equals("G")){
                    gifImageView.setImageResource(R.drawable.g);
                }else if (model.getItemText().equals("H")){
                    gifImageView.setImageResource(R.drawable.h);
                }else if (model.getItemText().equals("I")){
                    gifImageView.setImageResource(R.drawable.i);
                }else if (model.getItemText().equals("J")){
                    gifImageView.setImageResource(R.drawable.j);
                }else if (model.getItemText().equals("K")){
                    gifImageView.setImageResource(R.drawable.k);
                }else if (model.getItemText().equals("L")){
                    gifImageView.setImageResource(R.drawable.l);
                }else if (model.getItemText().equals("M")){
                    gifImageView.setImageResource(R.drawable.m);
                }else if (model.getItemText().equals("N")){
                    gifImageView.setImageResource(R.drawable.n);
                }else if (model.getItemText().equals("O")){
                    gifImageView.setImageResource(R.drawable.o);
                }else if (model.getItemText().equals("P")){
                    gifImageView.setImageResource(R.drawable.p);
                }else if (model.getItemText().equals("Q")){
                    gifImageView.setImageResource(R.drawable.q);
                }else if (model.getItemText().equals("R")){
                    gifImageView.setImageResource(R.drawable.r);
                }else if (model.getItemText().equals("S")){
                    gifImageView.setImageResource(R.drawable.s);
                }else if (model.getItemText().equals("T")){
                    gifImageView.setImageResource(R.drawable.t);
                }else if (model.getItemText().equals("U")){
                    gifImageView.setImageResource(R.drawable.u);
                }else if (model.getItemText().equals("V")){
                    gifImageView.setImageResource(R.drawable.v);
                }else if (model.getItemText().equals("W")){
                    gifImageView.setImageResource(R.drawable.w);
                }else if (model.getItemText().equals("X")){
                    gifImageView.setImageResource(R.drawable.x);
                }else if (model.getItemText().equals("Y")){
                    gifImageView.setImageResource(R.drawable.y);
                }else if (model.getItemText().equals("Z")){
                    gifImageView.setImageResource(R.drawable.z);
                }else if (model.getItemText().equals("1")){
                    gifImageView.setImageResource(R.drawable.num1);
                }else if (model.getItemText().equals("2")){
                    gifImageView.setImageResource(R.drawable.num2);
                }else if (model.getItemText().equals("3")){
                    gifImageView.setImageResource(R.drawable.num3);
                }else if (model.getItemText().equals("4")){
                    gifImageView.setImageResource(R.drawable.num4);
                }else if (model.getItemText().equals("5")){
                    gifImageView.setImageResource(R.drawable.num5);
                }else if (model.getItemText().equals("6")){
                    gifImageView.setImageResource(R.drawable.num6);
                }else if (model.getItemText().equals("7")){
                    gifImageView.setImageResource(R.drawable.num7);
                }else if (model.getItemText().equals("8")){
                    gifImageView.setImageResource(R.drawable.num8);
                }else if (model.getItemText().equals("9")){
                    gifImageView.setImageResource(R.drawable.num9);
                }else if (model.getItemText().equals("10")){
                    gifImageView.setImageResource(R.drawable.num10);
                }else if (model.getItemText().equals("Hello")){
                    gifImageView.setImageResource(R.drawable.hello);
                }else if (model.getItemText().equals("Good Bye")){
                    gifImageView.setImageResource(R.drawable.goodbye);
                }else if (model.getItemText().equals("Thank You")){
                    gifImageView.setImageResource(R.drawable.thankyou);
                }else if (model.getItemText().equals("Please")){
                    gifImageView.setImageResource(R.drawable.please);
                }else if (model.getItemText().equals("Sorry")){
                    gifImageView.setImageResource(R.drawable.sorry);
                }else{
                    gifImageView.setImageResource(R.drawable.space);
                }

                dialog.setContentView(popupView);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;
        private LinearLayout linearLayout;

        public ItemViewHolder(@NonNull View itemView){
            super(itemView);
            textView = itemView.findViewById(R.id.itemText);
            linearLayout = itemView.findViewById(R.id.Linear_layout);
        }
    }

    //Search function
    public void filterList(List<LibraryModel> filteredList){
        modelList = filteredList;
        notifyDataSetChanged();
    }

}
