package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ChatPage extends AppCompatActivity {

    TextView textView_name;
    EditText editText_message;
    ImageButton imageButton_send;
    LinearLayout layout;
    ScrollView scrollView;

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String currentUid,sellerUid,sellerType,message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_chat_page);
        getSupportActionBar().hide();

        textView_name=findViewById(R.id.textView_name);
        editText_message=findViewById(R.id.editText_message);
        imageButton_send=findViewById(R.id.imageButton_send);
        layout=findViewById(R.id.layout);
        scrollView=findViewById(R.id.scrollView);

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        firestore=FirebaseFirestore.getInstance();

        currentUid=preferences.getString("Uid","");
        Intent intent=getIntent();
        sellerType=intent.getStringExtra("SellerType");
        sellerUid=intent.getStringExtra("SellerUid");
        message=intent.getStringExtra("Message");

        editText_message.setText(message);

        final String s=sellerType.equals("Manufacturer")?"EnterpriseName":(sellerType.equals("Retailer")?"ShopName":"Firstname");;
        firestore.collection(sellerType).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful() && task.getResult().exists())
                {
                    textView_name.setText(task.getResult().getString(s));
                }
            }
        });

        firestore.collection("EndToEnd").document("Chats").collection(currentUid).document(sellerUid).collection("History").orderBy("Time").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                    show(queryDocumentSnapshot.getString("Sender"),queryDocumentSnapshot.getString("Message"),queryDocumentSnapshot.getString("Time"));
                }
            }
        });


    }

    public void show(String senderUid,String message,String time)
    {
        String sender;
        time=time.substring(0,4)+"/"+time.substring(4,6)+"/"+time.substring(6,8)+"  "+time.substring(8,10)+"-"+time.substring(10,12);
        if(senderUid.equals("me"))
            sender="me";
        else
            sender="Transactor";
        TextView textView=new TextView(ChatPage.this);
        textView.setTextColor(Color.parseColor("#eeeeee"));

        textView.setTextColor(Color.parseColor("#eeeeee"));
        if(sender.equals("me"))
        {
            textView.setText(time+"\n"+sender+":\n"+message);
            textView.setGravity(Gravity.RIGHT);

        }
        else
        {
            textView.setText(time+"\n"+sender+":\n"+message);
            textView.setGravity(Gravity.LEFT);
        }

        layout.addView(textView);
        TextView textView1=new TextView(ChatPage.this);
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
            Toast.makeText(this, "Enter a message", Toast.LENGTH_SHORT).show();
        else
        {
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyyMMddHHmmss");
            Date date=new Date();
            String dateTime=dateFormat.format(date);

            HashMap<String,Object> map1=new HashMap<>();
            map1.put("Sender","me");
            map1.put("Time",dateTime);
            map1.put("Message",message);
            firestore.collection("EndToEnd").document("Chats").collection(currentUid).document(sellerUid).collection("History").document().set(map1);
            HashMap<String,Object> map2=new HashMap<>();
            map2.put("Sender",currentUid);
            map2.put("Time",dateTime);
            map2.put("Message",message);
            firestore.collection("EndToEnd").document("Chats").collection(sellerUid).document(currentUid).collection("History").document().set(map2);

            show("me",message,dateTime);
            editText_message.getText().clear();
        }
    }
}