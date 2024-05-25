package com.example.imagepro;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder>{

    private List<imageSlider> sliderItem;
    private ViewPager2 viewPager2;
    private Context context;
    public View view;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    SliderAdapter(List<imageSlider> sliderItem, ViewPager2 viewPager2) {
        this.sliderItem = sliderItem;
        this.viewPager2 = viewPager2;
    }

    public SliderAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SliderViewHolder(
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.slide_item_container,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {

        holder.setImage(sliderItem.get(position), position);


    }

    @Override
    public int getItemCount() {
        return sliderItem.size();
    }

    class SliderViewHolder extends RecyclerView.ViewHolder{
        private RoundedImageView imageView;
        SliderViewHolder(@NonNull View itemView){
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
        }

        void setImage(imageSlider sliderItem, int position){
            //if get image from internet, put code here using glide or picasso
            imageView.setImageResource(sliderItem.getImage());

            MainActivity1 mainActivity = new MainActivity1();
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // get quiz element and initialize quiz
                    fStore.collection("quiz").document("quizElement")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                List<String> alphaList = (List<String>) documentSnapshot.get("alphabet");
                                List<String> numList = (List<String>) documentSnapshot.get("number");
                                List<String> greetList = (List<String>) documentSnapshot.get("greeting");

                                //shuffle list
                                Collections.shuffle(alphaList);
                                Collections.shuffle(numList);
                                Collections.shuffle(greetList);

                                if (position == 0){
                                    List<String> checkAlph = new ArrayList<>();

                                    checkAlph.add(alphaList.get(0));
                                    checkAlph.add(alphaList.get(6));
                                    checkAlph.add(alphaList.get(12));
                                    checkAlph.add(alphaList.get(18));
                                    checkAlph.add(alphaList.get(20));

                                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                                    intent.putStringArrayListExtra("list", (ArrayList<String>) checkAlph);
                                    v.getContext().startActivity(intent);
                                }else if (position == 1){
                                    List<String> checkNum = new ArrayList<>();
                                    checkNum.add(numList.get(0));
                                    checkNum.add(numList.get(2));
                                    checkNum.add(numList.get(4));
                                    checkNum.add(numList.get(6));
                                    checkNum.add(numList.get(8));
                                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                                    intent.putStringArrayListExtra("list", (ArrayList<String>) checkNum);
                                    v.getContext().startActivity(intent);
                                }else if (position == 2){
                                    List<String> checkGreet = new ArrayList<>();
                                    checkGreet.add(greetList.get(0));
                                    checkGreet.add(greetList.get(1));
                                    checkGreet.add(greetList.get(2));
                                    checkGreet.add(greetList.get(3));
                                    checkGreet.add(greetList.get(4));
                                    Intent intent = new Intent(v.getContext(), CameraActivity.class);
                                    intent.putStringArrayListExtra("list", (ArrayList<String>) checkGreet);
                                    v.getContext().startActivity(intent);
                                }
                            }
                        }
                    });
                }
            });
        }

    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            sliderItem.addAll(sliderItem);
            notifyDataSetChanged();
        }
    };
}
