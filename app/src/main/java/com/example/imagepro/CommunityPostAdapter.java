package com.example.imagepro;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.ItemViewHolder> {

    Context context;
    private List<CommunityModel> modelList;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();
//    private List<String> comments = new ArrayList<>();
//    private List<String> person = new ArrayList<>();

    public CommunityPostAdapter(Context context, List<CommunityModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {


        CommunityModel model = modelList.get(position);
        holder.text.setText(model.getDescription());
        Picasso.with(context).load(model.getPostImages()).fit().into(holder.postedImage);

        DocumentReference profileData = fStore.collection("users").document(model.getUserID());
        profileData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String org = documentSnapshot.getString("organization");
                    String imageUrl = documentSnapshot.getString("profileImages");

                    holder.organization.setText(org);

                    if (imageUrl != null && !imageUrl.equals("")) {
                        Picasso.with(context).load(imageUrl).fit().into(holder.profileImage);
                    }else{
                        Picasso.with(context).load(R.drawable.ic_baseline_account_circle_24).fit().centerCrop().into(holder.profileImage);
                    }
                } else {
                    Toast.makeText(context, "Document does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });

        String currentUserID = fAuth.getCurrentUser().getUid();

        //add comments
        DocumentReference communityData = fStore.collection("community").document(model.getDocumentID());
        holder.sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.commentBox.getText().toString().equals("") && holder.commentBox.getText().toString() != null){
                    String getComment = holder.commentBox.getText().toString();
                    String currentUserID = fAuth.getCurrentUser().getUid();
//                    Map<String, Object> random = new HashMap<>();

//                    random.put("comments", FieldValue.arrayUnion(getComment));
//                    random.put("commentID", FieldValue.arrayUnion(currentUserID));

                    communityData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()){
                                List<String> commentIDArray = (List<String>) documentSnapshot.get("commentID");
                                List<String> commentsArray = (List<String>) documentSnapshot.get("comments");
                                commentIDArray.add(currentUserID);
                                commentsArray.add(getComment);
                                communityData.update("commentID", commentIDArray).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        holder.commentBox.setText("");
                                        holder.viewComments.performClick();
                                    }
                                });
                                communityData.update("comments", commentsArray);

                            }
                        }
                    });
                }else {
                    Toast.makeText(context, "Please comment something before selecting the send button", Toast.LENGTH_SHORT);
                }
            }
        });


        //add likes
        final boolean[] checkedLiked = {false};

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkedLiked[0] == false) {
                    checkedLiked[0] = true;
                    holder.like.setBackgroundResource(R.drawable.red_favourtie_24);
                    communityData.update("likes", FieldValue.arrayUnion(currentUserID));
                } else {
                    checkedLiked[0] = false;
                    holder.like.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    communityData.update("likes", FieldValue.arrayRemove(currentUserID));
                }
            }
        });


        // set nested comments adapter
        communityData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<String> comments = (List<String>) documentSnapshot.get("comments");
                    List<String> commentID = (List<String>) documentSnapshot.get("commentID");

                    // logic for comments
                    if (comments.size() >= 1 && commentID.size() >= 1){
                        boolean isExpandable = model.isExpandable();
                        holder.relativeLayout.setVisibility(isExpandable ? View.VISIBLE : View.GONE);
                        if (isExpandable) {
                            holder.viewComments.setText("Hide Comments");
                        } else {
                            if (comments.size() == 1){
                                holder.viewComments.setText("View " + comments.size() + " Comment");
                            }else {
                                holder.viewComments.setText("View " + comments.size() + " Comments");
                            }
                        }

                        CommunityNestedAdapter adapter = new CommunityNestedAdapter(comments, commentID);
                        holder.recyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
                        holder.recyclerView.setHasFixedSize(true);
                        holder.recyclerView.setAdapter(adapter);
                    }else{
                        holder.viewComments.setText("No Comments");
                        holder.viewComments.setEnabled(false);
                    }

                    //logic for likes
                    List<String> likeList = (List<String>) documentSnapshot.get("likes");
                    String count = likeList.size() + "";
                    holder.likesCount.setText(count);
                    if (likeList.size() >= 1) {
                        for (int i = 0; i < likeList.size(); i++) {
                            if (currentUserID.equals(likeList.get(i))) {
                                holder.like.setBackgroundResource(R.drawable.red_favourtie_24);
                                checkedLiked[0] = true;
                            }
                        }
                    }else{
                        holder.like.setBackgroundResource(R.drawable.ic_baseline_favorite_border_24);
                    }
                }
            }
        });

        holder.viewComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setExpandable(!model.isExpandable());
                notifyItemChanged(holder.getAdapterPosition());
            }
        });

        //enable edit function
        if (currentUserID.equals(model.getUserID())){
            holder.editMenu.setVisibility(View.VISIBLE);
        }else{
            fStore.collection("users").document(currentUserID).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.getString("userType").equals("System Administrator")){
                        holder.editMenu.setVisibility(View.VISIBLE);
                    }else {
                        holder.editMenu.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }

        holder.editMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                View popupView = LayoutInflater.from(context).inflate(
                        R.layout.edit_popup, holder.popup);
                Button editPost = popupView.findViewById(R.id.editPost);
                Button deletePost = popupView.findViewById(R.id.deletePost);

                editPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), CommunityClass.class);
                        intent.putExtra("edit", model.getDocumentID());
                        context.startActivity(intent);
                    }
                });

                deletePost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        communityData.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(v.getContext(), "Post Deleted Successfully", Toast.LENGTH_SHORT);
                            }
                        });
                        Intent intent = new Intent(v.getContext(), CommunityClass.class);
                        context.startActivity(intent);
                    }
                });

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

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout bottomSheet;
        private RelativeLayout relativeLayout, popup;
        private TextView organization, text, likesCount, viewComments;
        private RecyclerView recyclerView;
        private ImageView profileImage, postedImage, like, sendComment, editMenu;
        private EditText commentBox;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.expandable_layout);
            organization = itemView.findViewById(R.id.Organization);
            text = itemView.findViewById(R.id.text);
            recyclerView = itemView.findViewById(R.id.recycleView);
            profileImage = itemView.findViewById(R.id.profileImage);
            postedImage = itemView.findViewById(R.id.postedImage);
            like = itemView.findViewById(R.id.like);
            likesCount = itemView.findViewById(R.id.likesCount);
            viewComments = itemView.findViewById(R.id.viewComments);
            sendComment = itemView.findViewById(R.id.commentSend);
            commentBox = itemView.findViewById(R.id.comementBox);
            editMenu = itemView.findViewById(R.id.editMenu);
            popup = itemView.findViewById(R.id.popup_container);
            bottomSheet = itemView.findViewById(R.id.bottomSheetContainer);

        }
    }
}
