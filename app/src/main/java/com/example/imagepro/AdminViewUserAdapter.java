package com.example.imagepro;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdminViewUserAdapter extends RecyclerView.Adapter<AdminViewUserAdapter.ItemViewHolder> {

    Context context;
    private List<UserModel> modelList;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    public AdminViewUserAdapter(Context context, List<UserModel> modelList) {
        this.context = context;
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_view_user, parent, false);
        return new AdminViewUserAdapter.ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewUserAdapter.ItemViewHolder holder, int position) {

        UserModel model = modelList.get(position);
        holder.name.setText(model.getfName());
        holder.email.setText(model.getEmail());
        holder.org.setText(model.getOrganization());
        holder.userType.setText(model.getUsertype());
        if (!model.getProfileImages().equals("") && model.getProfileImages() != null){
            Picasso.with(context).load(model.getProfileImages()).fit().centerCrop().into(holder.profileImage);
        }else{
            Picasso.with(context).load(R.drawable.ic_baseline_account_circle_24).fit().centerCrop().into(holder.profileImage);
        }

        holder.editMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AdminViewUser.class);
                intent.putExtra("edit", model.getUserID());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout popup;
        private TextView name, email, org, userType, editMenu;
        private ImageView profileImage;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            popup = itemView.findViewById(R.id.popup_container);
            name = itemView.findViewById(R.id.name);
            email = itemView.findViewById(R.id.email);
            org = itemView.findViewById(R.id.org);
            userType = itemView.findViewById(R.id.userType);
            profileImage = itemView.findViewById(R.id.profileImage);
            editMenu = itemView.findViewById(R.id.editMenu);

        }
    }

    //Search function
    public void filterList(List<UserModel> filteredList){
        modelList = filteredList;
        notifyDataSetChanged();
    }
}
