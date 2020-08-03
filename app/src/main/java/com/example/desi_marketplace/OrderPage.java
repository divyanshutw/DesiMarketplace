package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class OrderPage extends AppCompatActivity {

    TextView textView_info;
    EditText editText_message;
    ImageButton imageButton_send;
    LinearLayout layout;
    ScrollView scrollView;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore firestore;

    String message,currentUid,sellerUid,isOrder,sellerType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_order_page);
        getSupportActionBar().hide();

        textView_info = findViewById(R.id.textView_name);
        editText_message = findViewById(R.id.editText_message);
        imageButton_send = findViewById(R.id.imageButton_send);
        layout=findViewById(R.id.layout);
        scrollView=findViewById(R.id.scrollView);

        preferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        editor = preferences.edit();

        firestore = FirebaseFirestore.getInstance();

        currentUid = preferences.getString("Uid", "");
        Intent intent = getIntent();
        sellerUid = intent.getStringExtra("SellerUid");
        sellerType = intent.getStringExtra("SellerType");
        isOrder = intent.getStringExtra("IsOrder");

        String s;
        if (isOrder.equals("given")) {
            editText_message.setText("Order: \nQuantity: \nOther details: ");
            s="OrderGiven";
            textView_info.setText("You can chat with the seller to finalise the order." +
                    " After the order is confirmed, the seller will send an Invoice/OrderSummary which will be visible in the above area." +
                    " Click on the send button to navigate to the chats");
        } else {
            s="OrderTaken";
            editText_message.setText("Order Summary: \nOrder: \nQuantity: \nOther details: \nAmount: ");
            textView_info.setText("If the order is confirmed, you can send an Order Summary/Invoice to the buyer which will be visible here.");
        }


        firestore.collection("EndToEnd").document(s).collection(currentUid).document(sellerUid).collection("History").orderBy("Time").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for(QueryDocumentSnapshot documentSnapshot:queryDocumentSnapshots)
                            show(documentSnapshot.getString("Message"),documentSnapshot.getString("Time"));

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(OrderPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void show(String message,String time)
    {
        time=time.substring(0,4)+"/"+time.substring(4,6)+"/"+time.substring(6,8)+"  "+time.substring(8,10)+"-"+time.substring(10,12);
        TextView textView=new TextView(OrderPage.this);
        textView.setTextColor(Color.parseColor("#eeeeee"));
        textView.setText(time+"\n"+message);
        textView.setTextSize(15);               // to be checked

        layout.addView(textView);
        TextView textView1=new TextView(OrderPage.this);
        textView1.setHeight(10);
        layout.addView(textView1);
        scrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0,layout.getBottom());
            }
        },100L);
    }

    public void onClickSend(View v)
    {
        message=editText_message.getText().toString();
        if(message.isEmpty())
            Toast.makeText(this, "Enter some message", Toast.LENGTH_SHORT).show();
        else {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
            Date date=new Date();
            String dateTime=dateFormat.format(date);
            if (isOrder.equals("given")) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("Sample","sample");
                firestore.collection("EndToEnd").document("Chats").collection(currentUid).document(sellerUid).set(map, SetOptions.merge());
                firestore.collection("EndToEnd").document("Chats").collection(sellerUid).document(currentUid).set(map,SetOptions.merge());
                HashMap<String,Object> map1=new HashMap<>();
                map1.put("Time",dateTime);
                map1.put("Message","Buyer tried to order");
                firestore.collection("EndToEnd").document("OrderTaken").collection(sellerUid).document(currentUid).collection("History").document().set(map1);
                HashMap<String,Object> map2=new HashMap<>();
                map2.put("Time",dateTime);
                map2.put("Sample","Sample");
                firestore.collection("EndToEnd").document("OrderTaken").collection(sellerUid).document(currentUid).set(map1);
                firestore.collection("EndToEnd").document("OrderGiven").collection(currentUid).document(sellerUid).set(map1);
                String message=editText_message.getText().toString();
                Intent goToChat=new Intent(OrderPage.this,ChatPage.class);
                goToChat.putExtra("SellerUid",sellerUid);
                goToChat.putExtra("SellerType",sellerType);
                goToChat.putExtra("Message",message);
                startActivity(goToChat);
            } else {
                HashMap<String,Object> map1=new HashMap<>();
                map1.put("Time",dateTime);
                map1.put("Message",message);
                firestore.collection("EndToEnd").document("OrderTaken").collection(currentUid).document(sellerUid).collection("History").document().set(map1);
                HashMap<String,Object> map2=new HashMap<>();
                map2.put("Time",dateTime);
                map2.put("Message",message);
                firestore.collection("EndToEnd").document("OrderGiven").collection(sellerUid).document(currentUid).collection("History").document().set(map2);


                show(message,dateTime);
                editText_message.getText().clear();
                editText_message.setText("Order Summary: \nOrder: \nQuantity: \nOther details: \nAmount: ");
            }
        }
    }
}