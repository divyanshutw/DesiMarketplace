package com.example.desi_marketplace;

import androidx.annotation.ColorLong;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

public class BookPage extends AppCompatActivity {

    EditText editText_description,editText_amount;
    TextView textView_balance;
    Button button_gave,button_took,button_notify;
    LinearLayout layout;
    ScrollView scrollView;

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String currentUid,sellerUid,sellerType;

    double balance=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_book_page);
        getSupportActionBar().hide();

        editText_description=findViewById(R.id.editText_description);
        editText_amount=findViewById(R.id.editText_amount);
        button_gave=findViewById(R.id.button_gave);
        button_took=findViewById(R.id.button_took);
        layout=findViewById(R.id.layout);
        textView_balance=findViewById(R.id.textView_balance);
        scrollView=findViewById(R.id.scrollView);
        button_notify=findViewById(R.id.button_notify);

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        firestore=FirebaseFirestore.getInstance();

        currentUid=preferences.getString("Uid","");
        Intent intent=getIntent();
        sellerType=intent.getStringExtra("SellerType");
        sellerUid=intent.getStringExtra("SellerUid");

       firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
           @Override
           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
               if (task.isSuccessful() && task.getResult().exists())
               {
                   BookPage.this.balance = task.getResult().getDouble("Balance");
               textView_balance.setText(Double.toString(balance));
               if (balance < 0) {
                   textView_balance.setTextColor(Color.argb(100, 255, 0, 0));
                   button_notify.setVisibility(View.INVISIBLE);
               }
               else {
                   textView_balance.setTextColor(Color.argb(100, 34, 255, 85));
                   button_notify.setVisibility(View.VISIBLE);
               }
           }
               //else
                 //  Toast.makeText(BookPage.this,task.getException().toString() , Toast.LENGTH_SHORT).show();
           }
       });
       // Toast.makeText(this, Double.toString(balance), Toast.LENGTH_SHORT).show();

        firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).collection("History").orderBy("Time").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                            show(documentSnapshot.getString("Sender"),documentSnapshot.getString("Message"),documentSnapshot.getDouble("Amount"),documentSnapshot.getString("Time"));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BookPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
            }
        });


    }

    /*@Override
    protected void onStart()
    {
        super.onStart();
        //realtimeUpdateHistory();

        firestore.collection("EndToEnd").document("Book").collection(currentUid).addSnapshotListener(this,new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if(e!=null) {
                    Toast.makeText(BookPage.this, "Error while loading book history", Toast.LENGTH_SHORT).show();
                    return;
                }if(queryDocumentSnapshots!=null)
                {
                    List<DocumentChange> documentChangeArrayList=queryDocumentSnapshots.getDocumentChanges();
                    for(DocumentChange documentChange:documentChangeArrayList)
                    {
                        show(documentChange.getDocument().getString("Sender"),documentChange.getDocument().getString("Message"),documentChange.getDocument().getDouble("Amount"));
                    }
                }
            }
        });
    }*/

    public void show(String senderUid,String message,double amount,String time)
    {
        String sender;

        time=time.substring(0,4)+"/"+time.substring(4,6)+"/"+time.substring(6,8)+"  "+time.substring(8,10)+"-"+time.substring(10,12);
        if(senderUid.equals("me"))
            sender="me";
        else
            sender="Transactor";
        TextView textView=new TextView(BookPage.this);
        textView.setTextColor(Color.parseColor("#eeeeee"));

        if(sender.equals("me"))
        {
            textView.setText(time+"\n"+sender+":    \n"+"Amount: "+amount+"    \n"+message+"    ");
            textView.setGravity(Gravity.RIGHT);
        }
        else
        {
            textView.setText(time+"\n"+"    "+sender+":\n"+"    Amount: "+amount+"\n    "+message);
            textView.setGravity(Gravity.LEFT);
        }

        layout.addView(textView);
        TextView textView1=new TextView(BookPage.this);
        textView1.setHeight(10);
        layout.addView(textView1);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,layout.getBottom());
            }
        },100L);
    }

    public void onClickGave(View v)
    {
        String message=editText_description.getText().toString();
        double amount=0;

        if(editText_amount.getText().toString().isEmpty())
            Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
        else if(message.isEmpty())
            Toast.makeText(this, "Write a description for the above amount", Toast.LENGTH_SHORT).show();
        else
        {
            amount=Double.parseDouble(editText_amount.getText().toString());
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
            Date date=new Date();
            String dateTime=dateFormat.format(date);

            HashMap<String,Object> map1=new HashMap<>();
            map1.put("Message",message);
            map1.put("Amount",amount);
            map1.put("Sender","me");
            map1.put("Time",dateTime);
            firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).collection("History").document().set(map1);
            HashMap<String,Object> map2=new HashMap<>();
            map2.put("Message",message);
            map2.put("Amount",-amount);
            map2.put("Sender",currentUid);
            map2.put("Time",dateTime);
            firestore.collection("EndToEnd").document("Book").collection(sellerUid).document(currentUid).collection("History").document().set(map2);

            final double finalAmount = amount;
            firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists()) {
                        balance = task.getResult().getDouble("Balance");
                        HashMap<String, Object> map3 = new HashMap<>();
                        map3.put("Balance", balance + finalAmount);
                        firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).set(map3, SetOptions.merge());
                        HashMap<String, Object> map4 = new HashMap<>();
                        map4.put("Balance", -balance - finalAmount);
                        firestore.collection("EndToEnd").document("Book").collection(sellerUid).document(currentUid).set(map4, SetOptions.merge());
                        balance += finalAmount;
                        textView_balance.setText(Double.toString(balance));
                        if (balance < 0) {
                            textView_balance.setTextColor(Color.argb(100, 255, 0, 0));
                            button_notify.setVisibility(View.INVISIBLE);
                        }
                        else {
                            textView_balance.setTextColor(Color.argb(100, 34, 255, 85));
                            button_notify.setVisibility(View.VISIBLE);
                        }
                    }
                    //else
                      //  Toast.makeText(BookPage.this,task.getException().toString() , Toast.LENGTH_SHORT).show();
                }
            });

            /*firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).collection("History").orderBy("Time", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                                show(documentSnapshot.getString("Sender"),documentSnapshot.getString("Message"),documentSnapshot.getDouble("Amount"));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BookPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                }
            });*/
            show("me",message,amount,dateTime);
            editText_amount.getText().clear();
            editText_description.getText().clear();
            editText_amount.setHint("Enter amount");
            editText_description.setHint("Enter description");
        }
    }
    public void onClickTook(View v)
    {
        String message=editText_description.getText().toString();
        double amount=0;
        if(editText_amount.getText().toString().isEmpty())
        Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show();
        else if(message.isEmpty())
            Toast.makeText(this, "Write a description for the above amount", Toast.LENGTH_SHORT).show();

        else
        {
            amount=Double.parseDouble(editText_amount.getText().toString());
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
            Date date=new Date();
            String dateTime=dateFormat.format(date);

            amount=-amount;
            HashMap<String,Object> map1=new HashMap<>();
            map1.put("Message",message);
            map1.put("Amount",amount);
            map1.put("Sender","me");
            map1.put("Time",dateTime);
            firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).collection("History").document().set(map1);
            HashMap<String,Object> map2=new HashMap<>();
            map2.put("Message",message);
            map2.put("Amount",-amount);
            map2.put("Sender",currentUid);
            map2.put("Time",dateTime);
            firestore.collection("EndToEnd").document("Book").collection(sellerUid).document(currentUid).collection("History").document().set(map2);

            final double finalAmount = amount;
            firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful() && task.getResult().exists()) {
                        balance = task.getResult().getDouble("Balance");
                        HashMap<String, Object> map3 = new HashMap<>();
                        map3.put("Balance", balance + finalAmount);
                        firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).set(map3, SetOptions.merge());
                        HashMap<String, Object> map4 = new HashMap<>();
                        map4.put("Balance", -balance - finalAmount);
                        firestore.collection("EndToEnd").document("Book").collection(sellerUid).document(currentUid).set(map4, SetOptions.merge());
                        balance += finalAmount;
                        textView_balance.setText(Double.toString(balance));
                        if (balance < 0) {
                            textView_balance.setTextColor(Color.argb(100, 255, 0, 0));
                            button_notify.setVisibility(View.INVISIBLE);
                        }
                        else {
                            textView_balance.setTextColor(Color.argb(100, 34, 255, 85));
                            button_notify.setVisibility(View.VISIBLE);
                        }
                    }
                    //else
                     //   Toast.makeText(BookPage.this,task.getException().toString() , Toast.LENGTH_SHORT).show();
                }
            });

            /*firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).collection("History").orderBy("Time", Query.Direction.DESCENDING).get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                                show(documentSnapshot.getString("Sender"),documentSnapshot.getString("Message"),documentSnapshot.getDouble("Amount"));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(BookPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                }
            });*/
            show("me",message,amount,dateTime);
            editText_amount.getText().clear();
            editText_description.getText().clear();
            editText_amount.setHint("Enter amount");
            editText_description.setHint("Enter description");
        }
    }
    public void onClickNotify(View v)
    {

    }
}