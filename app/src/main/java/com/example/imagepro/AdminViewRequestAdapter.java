package com.example.imagepro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class AdminViewRequestAdapter extends RecyclerView.Adapter<AdminViewRequestAdapter.ItemViewHolder> {

    Context context;
    private List<RequestModel> modelList;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseAuth mAuth2;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public AdminViewRequestAdapter(Context context, List<RequestModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_view_request, parent, false);
        return new AdminViewRequestAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewRequestAdapter.ItemViewHolder holder, int position) {

        RequestModel model = modelList.get(position);
        holder.name.setText(model.getfName());
        holder.email.setText(model.getEmail());
        holder.org.setText(model.getOrganization());
        holder.userType.setText(model.getUserType());
        if (model.getRequest().equals("accepted")) {
            holder.request.setText("Accepted");
            holder.request.setTextColor(Color.WHITE);
            holder.request.setBackgroundResource(R.drawable.button_bg);
            holder.accepted.setVisibility(View.INVISIBLE);
            holder.rejected.setVisibility(View.INVISIBLE);
        } else if (model.getRequest().equals("rejected")) {
            holder.request.setText("Rejected");
            holder.request.setTextColor(Color.WHITE);
            holder.request.setBackgroundResource(R.drawable.red_button_bg);
            holder.accepted.setVisibility(View.INVISIBLE);
            holder.rejected.setVisibility(View.INVISIBLE);
        } else {
            holder.request.setText("Pending");
            holder.request.setTextColor(Color.WHITE);
            holder.request.setBackgroundResource(R.drawable.yellow_button_bg);
            holder.accepted.setVisibility(View.VISIBLE);
            holder.rejected.setVisibility(View.VISIBLE);
        }

        holder.accepted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mail = model.getEmail();
                String pass = model.getPassword();

                FirebaseOptions firebaseOptions = new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyA1lYlFN-X2q6ixqQOsFGmie-1VpnwuNgo")
                        .setApplicationId("slts-a26e9").build();

                try {
                    FirebaseApp myApp = FirebaseApp.initializeApp(v.getContext().getApplicationContext(), firebaseOptions, "SLTS");
                    mAuth2 = FirebaseAuth.getInstance(myApp);
                } catch (IllegalStateException e) {
                    mAuth2 = FirebaseAuth.getInstance(FirebaseApp.getInstance("SLTS"));
                }

                mAuth2.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            List<String> checkedWord = new ArrayList<>();
                            String profImage = "";

                            String userID = mAuth2.getCurrentUser().getUid();
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", model.getfName());
                            user.put("email", model.getEmail());
                            user.put("organization", model.getOrganization());
                            user.put("userType", model.getUserType());
                            user.put("wordsChecked", checkedWord);
                            user.put("profileImages", profImage);
                            user.put("userID", userID);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(v.getContext(), "User Created Successful!", Toast.LENGTH_SHORT).show();
                                    Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                }
                            });

                        } else {
                            Toast.makeText(v.getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                mAuth2.signOut();

                DocumentReference profileData = fStore.collection("adminRequest").document(model.getRequestID());
                Map<String, Object> random = new HashMap<>();
                random.put("request", "accepted");

                profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Toast.makeText(v.getContext(), "Community Administrator Accepted", Toast.LENGTH_SHORT);
                        holder.request.setText("Accepted");
                        holder.request.setTextColor(Color.WHITE);
                        holder.request.setBackgroundResource(R.drawable.button_bg);
                        holder.accepted.setVisibility(View.INVISIBLE);
                        holder.rejected.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), "Fail to Accept User", Toast.LENGTH_SHORT);
                    }
                });
            }
        });

        holder.rejected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentReference profileData = fStore.collection("adminRequest").document(model.getRequestID());
                Map<String, Object> random = new HashMap<>();
                random.put("request", "rejected");

                profileData.update(random).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(v.getContext(), "Community Administrator Rejected", Toast.LENGTH_SHORT);
                        holder.request.setText("Rejected");
                        holder.request.setTextColor(Color.WHITE);
                        holder.request.setBackgroundResource(R.drawable.red_button_bg);
                        holder.accepted.setVisibility(View.INVISIBLE);
                        holder.rejected.setVisibility(View.INVISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(v.getContext(), "Fail to Reject User", Toast.LENGTH_SHORT);
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView name, email, org, userType, request;
        private Button accepted, rejected;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            org = itemView.findViewById(R.id.org);
            userType = itemView.findViewById(R.id.userType);
            request = itemView.findViewById(R.id.request);
            accepted = itemView.findViewById(R.id.accepted);
            rejected = itemView.findViewById(R.id.rejected);

        }
    }

    //Search function
    public void filterList(List<RequestModel> filteredList) {
        modelList = filteredList;
        notifyDataSetChanged();
    }
}
