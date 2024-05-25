package com.example.imagepro;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.ContentValues.TAG;

public class LoginFragment extends Fragment {

    EditText email, password;
//    TextView forgetpass;
    Button login;
    float v = 0;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.login_tab, container, false);

        fAuth = FirebaseAuth.getInstance();

        if (fAuth.getCurrentUser() != null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        email =root.findViewById(R.id.email);
        password = root.findViewById(R.id.password);
//        forgetpass = root.findViewById(R.id.forgetPassword);
        login = root.findViewById(R.id.checkSign);

        email.setTranslationY(300);
        email.setAlpha(v);
        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        password.setTranslationY(300);
        password.setAlpha(v);
        password.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

//        forgetpass.setTranslationY(300);
//        forgetpass.setAlpha(v);
//        forgetpass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        login.setTranslationY(300);
        login.setAlpha(v);
        login.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (TextUtils.isEmpty(mail)){
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    password.setError("Password is required");
                    return;
                }

                fAuth.signInWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
//                            Toast.makeText(getContext(), "onComplete: Failed=" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            CollectionReference profileData = fStore.collection("adminRequest");
                            profileData.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                    int check = 0;
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        if (document.getString("email") != null && !document.getString("email").isEmpty()) {
                                            if (document.getString("email").equals(mail)
                                                    && document.getString("request").equals("requested")){
                                                check = 1;
                                                Dialog dialog = new Dialog(getContext());
                                                View popupView = LayoutInflater.from(getContext()).inflate(
                                                        R.layout.community_admin_popup, null);
                                                Button okButton = popupView.findViewById(R.id.ok);

                                                okButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                dialog.setContentView(popupView);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            }else if (document.getString("email").equals(mail)
                                                    && document.getString("request").equals("rejected")){
                                                check = 1;
                                                Dialog dialog = new Dialog(getContext());
                                                View popupView = LayoutInflater.from(getContext()).inflate(
                                                        R.layout.community_admin_popup, null);
                                                TextView text = popupView.findViewById(R.id.text);
                                                Button okButton = popupView.findViewById(R.id.ok);

                                                text.setText("Your application has been rejected. Please contact the system administrator for more details.");

                                                okButton.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        dialog.dismiss();
                                                    }
                                                });

                                                dialog.setContentView(popupView);
                                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                dialog.show();
                                            }else {
                                                if (check == 1){
                                                    check = 0;
                                                }else {
                                                    check = 2;
                                                }
                                            }
                                        }
                                    }
                                    if (check == 2){
                                        Toast.makeText(getContext(), "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        return root;
    }
}
