package com.example.imagepro;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class CommunityNestedAdapter extends RecyclerView.Adapter<CommunityNestedAdapter.NestedViewHolder> {

    private List<String> comments;
    private List<String> commentID;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public CommunityNestedAdapter(List<String> comments, List<String> commentID) {
        this.comments = comments;
        this.commentID = commentID;
    }

    @NonNull
    @Override
    public NestedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.post_nested_item, parent, false);
        return new NestedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NestedViewHolder holder, int position) {
        holder.comments.setText(comments.get(position));

        DocumentReference documentReference = fStore.collection("users").document(commentID.get(position));
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    String userName = documentSnapshot.getString("fName");
                    holder.person.setText("- " + userName);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class NestedViewHolder extends RecyclerView.ViewHolder {
        private TextView comments, person;

        public NestedViewHolder(@NonNull View itemView) {
            super(itemView);
            comments = itemView.findViewById(R.id.comments);
            person = itemView.findViewById(R.id.person);

        }
    }
}
