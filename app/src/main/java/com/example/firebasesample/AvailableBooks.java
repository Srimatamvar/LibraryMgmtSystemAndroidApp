package com.example.firebasesample;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class AvailableBooks extends AppCompatActivity {
    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    FirebaseFirestore firebaseFirestore;

    FirestoreRecyclerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_books);
        drawerLayout = findViewById(R.id.drawer_layout);
        recyclerView = findViewById(R.id.recylelist);
        firebaseFirestore = FirebaseFirestore.getInstance();
        TextView t1 = findViewById(R.id.adminLoginEmail);
        FirebaseAuth fauth = FirebaseAuth.getInstance();
        FirebaseUser id = fauth.getCurrentUser();
        String email = id.getEmail();
        t1.setText(email);


        final ImageView i1 = findViewById(R.id.logoImage);
        DocumentReference df3 = firebaseFirestore.collection("Users").document(fauth.getCurrentUser().getEmail());
        df3.addSnapshotListener(AvailableBooks.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value!=null){
                    String ul = value.getString("profileImage");
                    Glide.with(AvailableBooks.this).load(ul).into(i1);

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


        Query query = firebaseFirestore.collection("IssuedBooks");
        FirestoreRecyclerOptions<AllIssuedBook> options = new FirestoreRecyclerOptions.Builder<AllIssuedBook>().setQuery(query,AllIssuedBook.class).build();
        adapter = new FirestoreRecyclerAdapter<AllIssuedBook, AllIssuedBookViewHolder>(options) {
            @NonNull
            @Override
            public AllIssuedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_issued_book_layout,parent,false);
                return new AvailableBooks.AllIssuedBookViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull AllIssuedBookViewHolder holder, int position, @NonNull final AllIssuedBook model) {
                holder.name.setText(model.getBookName());
                holder.author.setText(model.getBookAuthor());
                holder.issueDate.setText(model.getIssuedDate());
                holder.returnDate.setText(model.getReturnDate());
                holder.takenby.setText(model.getUsername());
                Glide.with(AvailableBooks.this).load(model.getBookCover().toString()).into(holder.imageView);
                holder.alert.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AvailableBooks.this);
                        builder.setTitle("Sending Alert Message");
                        builder.setMessage("Are you sure you want to send alert message to "+model.getUsername());
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SmsManager smsManager = SmsManager.getDefault();
                                String longMessage = "Dear "+model.getUsername()+"\n"+"Last date of submit the "+model.getBookName()+" book at library is "+ model.getReturnDate() + "\n" + "So please kindly submit the book.";
                                ArrayList<String> parts = smsManager.divideMessage(longMessage);
                                smsManager.sendMultipartTextMessage(model.getUserphone(),null, parts,null,null);
                                sendEmailAlet(model.getStudentEmail(),model.getBookName(),model.getIssuedDate(),model.getReturnDate());
                                Toast.makeText(AvailableBooks.this, "message successfully send", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alert11 = builder.create();
                        alert11.show();

                    }
                });
                holder.submitted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(AvailableBooks.this);
                        builder1.setTitle("Book Submission");
                        builder1.setMessage("Are you sure want to submit "+model.getBookName()+" book");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                DocumentReference df = firebaseFirestore.collection("IssuedBooks").document(model.getStudentEmail()+model.getBookName());
                                df.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(AvailableBooks.this, "Book Successfully Submitted", Toast.LENGTH_SHORT).show();
                                        updateBook(model.getBookName());
                                        dialogInterface.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AvailableBooks.this, "Book not submitted! please try again later.", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                            }
                        });
                        builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                            }
                        });
                        AlertDialog alert12 = builder1.create();
                        alert12.show();

                    }
                });

            }
        };
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void sendEmailAlet(final String studentEmail, final String bookName, final String issuedDate, final String returnDate) {
        final String email = "your mail address";
        final String password = "mail password";
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
                    message.setSubject("regarding book submission");
                    message.setText("Dear student"+"\n"+returnDate+" date to submit the "+bookName+" book"+"\n"+"Please Kindly submit the book at library"+"\n"+"Your issued date of book is "+issuedDate);
                    Transport.send(message);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });

        thread.start();

    }

    private void updateBook(final String bookname) {
        System.out.println(bookname.toString());
        DocumentReference documentReference = firebaseFirestore.collection("Books").document(bookname);
        documentReference.addSnapshotListener(AvailableBooks.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                String n = value.getString("quantity");
                Integer i = Integer.parseInt(n)+1;
                System.out.println(i);
                String ne = String.valueOf(i);
                System.out.println(ne);
                DocumentReference df = firebaseFirestore.collection("Books").document(bookname);
                df.update("quantity",ne);
                recreate();
            }
        });
    }


    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }


    class AllIssuedBookViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView author;
        private TextView returnDate;
        private TextView issueDate;
        private TextView takenby;
        private ImageView imageView;
        private Button alert;
        private Button submitted;

        public AllIssuedBookViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.issuedBookName);
            author = itemView.findViewById(R.id.issuedBookAuthorName);
            issueDate = itemView.findViewById(R.id.issuedBookDate);
            returnDate = itemView.findViewById(R.id.issuedBookReturnDate);
            takenby = itemView.findViewById(R.id.takenBy);
            imageView = itemView.findViewById(R.id.bookimage);
            alert = itemView.findViewById(R.id.alert);
            submitted = itemView.findViewById(R.id.submitted);
        }
    }








    public void clickMenu(View view){
        ForAdmin.openDrawer(drawerLayout);
    }

    public void clickProfile(View view){
        ForAdmin.redirectActivity(this,ProfilePage.class);
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
        recreate();
    }
    public void clickIssuedBooks(View view){
        ForAdmin.redirectActivity(this,IssuedBooks.class);
    }
    public void clickLogout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),SplashScreen.class));
    }

    public void clickAddUser(View view){
        ForAdmin.redirectActivity(this, AddAUser.class);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ForAdmin.closeDrawer(drawerLayout);
    }
}
