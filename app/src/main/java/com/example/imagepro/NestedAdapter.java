package com.example.imagepro;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NestedAdapter extends RecyclerView.Adapter<NestedAdapter.NestedViewHolder> {

    private List<String> mList;
    private List<String> mListAll;
    private List<String> videoList;
    private String itemName;
    private String checkedAlph;
    private Context context;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public NestedAdapter(List<String> mList, List<String> videoList, String itemName, String checkedAlph) {
        this.mList = mList;
        this.mListAll = new ArrayList<>(mList);
        this.videoList = videoList;
        this.itemName = itemName;
        this.checkedAlph = checkedAlph;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nested_item, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        JSONObject keys = new JSONObject();
        try {
            keys.put("Genre", itemName);
            keys.put("Title", mList.get(position));
            keys.put("VideoKey", videoList.get(position));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String userID = fAuth.getCurrentUser().getUid();
        DocumentReference learning_progress = fStore.collection("users").document(userID);
        learning_progress.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> alphabet = (List<String>) documentSnapshot.get("wordsChecked");
                    for (int i = 0; i < alphabet.size(); i++) {
                        if (alphabet.get(i) != null) {
                            if (alphabet.get(i).equals(mList.get(position))) {
                                holder.tick.setImageResource(R.drawable.ic_baseline_check_circle_24);
                            }
                        }
                    }
                }
            }
        });


        holder.mTv.setText(mList.get(position));
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext().getApplicationContext(), LearningVideo.class);
                intent.putExtra("keys", keys.toString());
                v.getContext().startActivity(intent);
            }
        });

        //        Uri uri = Uri.parse(videoList.get(position));
//        holder.mVV.setVideoURI(uri);
//        holder.mVV.start();
//
//        MediaController mediaController = new MediaController(holder.itemView.getContext());
//        holder.mVV.setMediaController(mediaController);
//
//        holder.mVV.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                mediaController.setAnchorView(holder.mVV);
//                mp.setLooping(true);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        //        private VideoView mVV;
        private ImageView tick;
        private TextView mTv;
        private ConstraintLayout constraintLayout;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
//            mVV = itemView.findViewById(R.id.nestedVideo);
            mTv = itemView.findViewById(R.id.nestedItem);
            tick = itemView.findViewById(R.id.tick);
            constraintLayout = itemView.findViewById(R.id.constraint_layout);
        }
    }
}
