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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class RegisterFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    EditText email, name, org, confirmpass, password;
    Button register;
    Spinner spinner;
    String positionText = "Self-Learner";
    float v = 0;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.register_tab, container, false);

        email =root.findViewById(R.id.email);
        name =root.findViewById(R.id.Name);
        org = root.findViewById(R.id.Organization);
        confirmpass = root.findViewById(R.id.confirm_password);
        password = root.findViewById(R.id.password);
        register = root.findViewById(R.id.register);
        spinner = root.findViewById(R.id.position_spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.positions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        //Firebase Authentication
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        if (fAuth.getCurrentUser() != null){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail = email.getText().toString().trim();
                String pass = password.getText().toString().trim();
                String conpass = confirmpass.getText().toString().trim();
                String nama = name.getText().toString();
                String orggg = "";
                if (org.getText() != null){
                    orggg = org.getText().toString();
                }
                String posi = positionText.toString();
                List<String> checkedWord = new ArrayList<>();
                String profImage = "";

                if (TextUtils.isEmpty(mail)){
                    email.setError("Email is required");
                    return;
                }
                if (TextUtils.isEmpty(pass)){
                    password.setError("Password is required");
                    return;
                }
                if (TextUtils.isEmpty(conpass)){
                    confirmpass.setError("Please repeat the password here");
                    return;
                }
                if (!conpass.equals(pass)){
                    confirmpass.setError("Password is different");
                    return;
                }

                String finalOrggg = orggg;

                if (posi.equals("Community Administrator")){
                    Random random = new Random();
                    int randomNumber = random.nextInt(999999 - 111111) + 65;
                    userID = randomNumber + "";
                    DocumentReference documentReference = fStore.collection("adminRequest").document(userID);
                    Map<String, Object> user = new HashMap<>();
                    user.put("fName", nama);
                    user.put("email", mail);
                    user.put("organization", finalOrggg);
                    user.put("userType", posi);
                    user.put("profileImages", profImage);
                    user.put("request", "requested");
                    user.put("password", pass);
                    user.put("requestID", userID);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            email.setText("");
                            name.setText("");
                            org.setText("");
                            confirmpass.setText("");
                            password.setText("");

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
                        }
                    });
                }else {
                    fAuth.createUserWithEmailAndPassword(mail, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                userID = fAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String, Object> user = new HashMap<>();
                                user.put("fName", nama);
                                user.put("email", mail);
                                user.put("organization", finalOrggg);
                                user.put("userType", posi);
                                user.put("wordsChecked", checkedWord);
                                user.put("profileImages", profImage);
                                user.put("userID", userID);
                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getContext(), "User Created Successful!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onSuccess: user Profile is created for " + userID);
                                    }
                                });

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        //Animation

        email.setTranslationY(300);
        email.setAlpha(v);
        email.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        password.setTranslationY(300);
        password.setAlpha(v);
        password.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        name.setTranslationY(300);
        name.setAlpha(v);
        name.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        org.setTranslationY(300);
        org.setAlpha(v);
        org.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        confirmpass.setTranslationY(300);
        confirmpass.setAlpha(v);
        confirmpass.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        register.setTranslationY(300);
        register.setAlpha(v);
        register.animate().translationY(0).alpha(1).setDuration(1000).setStartDelay(400).start();

        return root;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        positionText = parent.getItemAtPosition(position).toString();
        if (positionText.equals("Community Administrator")){
            org.setVisibility(View.VISIBLE);
        }else {
            org.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
