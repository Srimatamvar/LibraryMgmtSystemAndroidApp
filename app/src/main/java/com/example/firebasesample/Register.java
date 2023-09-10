package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    Boolean valid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final FirebaseAuth fauth = FirebaseAuth.getInstance();
        final FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        Button b1 = findViewById(R.id.button2);
        final EditText email = findViewById(R.id.fetchemail);
        final EditText pass = findViewById(R.id.password);
        final EditText name = findViewById(R.id.fetchname);
        final EditText phone = findViewById(R.id.fetchphone);
        final CheckBox c1 = findViewById(R.id.teacher);
        final CheckBox c2 = findViewById(R.id.student);
        final TextView login = findViewById(R.id.logintext);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });


        c1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    c2.setChecked(false);
                }
            }
        });

        c2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    c1.setChecked(false);
                }
            }
        });



        b1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                checkfield(email);
                checkfield(pass);
                checkfield(name);
                checkfield(phone);
                if(!(c1.isChecked() || c2.isChecked())){
                    Toast.makeText(Register.this, "select account type", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(valid){
                    fauth.createUserWithEmailAndPassword(email.getText().toString(),pass.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = fauth.getCurrentUser();
                            System.out.println(user.getEmail().toString());
                            DocumentReference df = fstore.collection("Users").document(user.getEmail());
                            Map<String, Object> userinfo = new HashMap<>();
                            userinfo.put("username",name.getText().toString());
                            userinfo.put("phone",phone.getText().toString());
                            userinfo.put("email",email.getText().toString());
                            userinfo.put("profileImage","https://firebasestorage.googleapis.com/v0/b/sample-94bc9.appspot.com/o/images%2Fdownload.png?alt=media&token=52a0c084-1861-4a8e-af27-ab6734ef7efc");
                            if(c1.isChecked()){
                                userinfo.put("isAdmin","1");
                            }
                            if(c2.isChecked()){
                                userinfo.put("isUser","1");
                            }
                            df.set(userinfo);
                            Toast.makeText(Register.this, "account created", Toast.LENGTH_SHORT).show();
                            if(c1.isChecked()){
                                startActivity(new Intent(getApplicationContext(),ForAdmin.class));
                            }
                            if(c2.isChecked()){
                                startActivity(new Intent(getApplicationContext(),ForStudent.class));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "account not created", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }

    public Boolean checkfield(EditText textfield){
        if(textfield.getText().toString().isEmpty()){
            textfield.setError("error");
            Toast.makeText(this, "fill all the entries", Toast.LENGTH_SHORT).show();
            valid = false;
        }else{
            valid = true;
        }
        return valid;
    }
}
