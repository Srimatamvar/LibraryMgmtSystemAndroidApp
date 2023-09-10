package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class IssuedBooks extends AppCompatActivity {
    DrawerLayout drawerLayout;
    EditText stname,bname,idate,rdate;
    Button b;
    FirebaseFirestore fs;
    FirebaseAuth fauth;
    Boolean valid;
    String author,cover;
    FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issued_books);
        drawerLayout = findViewById(R.id.drawer_layout);
        stname = findViewById(R.id.studentName);
        bname = findViewById(R.id.bookName);
        idate = findViewById(R.id.issuedDate);
        rdate = findViewById(R.id.returnDate);
        b = findViewById(R.id.issuedBtn);
        fauth = FirebaseAuth.getInstance();
        fs = FirebaseFirestore.getInstance();


        TextView t1 = findViewById(R.id.adminLoginEmail);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser id = fauth.getCurrentUser();
        String email = id.getEmail();
        t1.setText(email);

        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = fs.collection("Users").document(fauth.getCurrentUser().getEmail());
        df3.addSnapshotListener(IssuedBooks.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(IssuedBooks.this).load(ul).into(i1);
                }
            }
        });


        final Switch my = findViewById(R.id.switch1);

        SharedPreferences sharedPreferences
                = getSharedPreferences(
                "sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor
                = sharedPreferences.edit();
        final boolean isDarkModeOn
                = sharedPreferences
                .getBoolean(
                        "isDarkModeOn", false);

        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            my.setChecked(true);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            my.setChecked(false);
        }


        my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isDarkModeOn) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean("isDarkModeOn", false);
                    editor.apply();
                    my.setChecked(false);
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean("isDarkModeOn", true);
                    editor.apply();
                    my.setChecked(true);
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkfield(stname);
                checkfield(bname);
                checkfield(idate);
                checkfield(rdate);
                if(valid){
                    String bookname = bname.getText().toString();
                    takingBookInfo(bookname,stname,idate,rdate,bname);
                }

            }
        });

    }

    private void sendEmailAlet(final String studentEmail, final String bookName, final String issuedDate, final String returnDate,final String author) {
        final String email = "Your Mail address";
        final String password = "Mail Password";
        Properties properties =  new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");
        properties.put("mail.smtp.host","smtp.gmail.com");
        properties.put("mail.smtp.port","587");

        final Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email,password);
            }
        });
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    Message message = new MimeMessage(session);
                    message.setFrom(new InternetAddress(email));
                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(studentEmail));
                    message.setSubject("Book Issued");
                    message.setText("Dear student"+"\n"+"You have issued a "+bookName+" book"+"\n"+"Details are below"+"\n"+"Book Name :- "+bookName+"\n"+"Book Author :- "+author+"\n"+"Issued Date :- "+issuedDate+"\n"+"Return Date :- "+returnDate );
                    Transport.send(message);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });

        thread.start();

    }

    private void quantity(final String bookname) {
        System.out.println(bookname.toString());
        DocumentReference documentReference = fs.collection("Books").document(bookname);
        documentReference.addSnapshotListener(IssuedBooks.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String n = value.getString("quantity");
                Integer i = Integer.parseInt(n)-1;
                System.out.println(i);
                String ne = String.valueOf(i);
                System.out.println(ne);
                DocumentReference df = fs.collection("Books").document(bookname);
                df.update("quantity",ne);
                recreate();
            }

        });

    }

    public void takingBookInfo(final String bname, final EditText stname, final EditText idate, final EditText rdate, final EditText bnam) {
        DocumentReference documentReference = fs.collection("Books").document(bname);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                cover = value.getString("bookcover");
                author = value.getString("author");

                System.out.println(cover+author);
                String path = stname.getText().toString()+bname;
                String user = stname.getText().toString();

                DocumentReference df = fs.collection("IssuedBooks").document(stname.getText().toString()+bname);
                Map<String, Object> issuedBookInfo = new HashMap<>();
                issuedBookInfo.put("studentEmail",stname.getText().toString());
                issuedBookInfo.put("BookName",bname);
                issuedBookInfo.put("issuedDate",idate.getText().toString());
                issuedBookInfo.put("returnDate",rdate.getText().toString());
                issuedBookInfo.put("bookCover",cover);
                issuedBookInfo.put("bookAuthor",author);
                df.set(issuedBookInfo);
                updateDocument(path,user,bname);
                sendEmailAlet(user,bname,idate.getText().toString(),rdate.getText().toString(),author);
                Toast.makeText(IssuedBooks.this, "Issued Book Successfully", Toast.LENGTH_SHORT).show();
                stname.setText("");
                bnam.setText("");
                idate.setText("");
                rdate.setText("");

            }
        });
    }


    private void updateDocument(final String path, String user,final String bookname) {
        DocumentReference documentReference = fs.collection("Users").document(user);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String name = value.getString("username");
                String phone = value.getString("phone");
                DocumentReference df = fs.collection("IssuedBooks").document(path);
                Map<String, Object> issuedBookInfo = new HashMap<>();
                issuedBookInfo.put("username",name);
                issuedBookInfo.put("userphone",phone);
                df.update(issuedBookInfo);
                quantity(bookname);
            }
        });
    }




    public void clickMenu(View view){
        ForAdmin.openDrawer(drawerLayout);
    }

    public void clickLogo(View view){
        ForAdmin.closeDrawer(drawerLayout);
    }
    public void clickHome(View view){
        ForAdmin.redirectActivity(this,ForAdmin.class);
    }
    public void clickDashboard(View view){
        ForAdmin.redirectActivity(this,Dashboard.class);

    }
    public void clickAvailableBooks(View view){
        ForAdmin.redirectActivity(this,AvailableBooks.class);

    }

    public void clickProfile(View view){
        ForAdmin.redirectActivity(this,ProfilePage.class);
    }
    public void clickIssuedBooks(View view){

        recreate();
    }

    public void clickAddUser(View view){
        ForAdmin.redirectActivity(this, AddAUser.class);
    }

    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForAdmin.closeDrawer(drawerLayout);
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
