package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ChatsResult extends AppCompatActivity {

    LinearLayout layout;

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String userType,Uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_chats_result);
        getSupportActionBar().hide();

        layout=findViewById(R.id.layout);

        firestore=FirebaseFirestore.getInstance();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        userType=preferences.getString("UserType","");
        Uid=preferences.getString("Uid","");

        firestore.collection("EndToEnd").document("Chats").collection(Uid).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                ArrayList<String> chats=new ArrayList<>();
                //Toast.makeText(ChatsResult.this, "reached", Toast.LENGTH_SHORT).show();
                for(QueryDocumentSnapshot queryDocumentSnapshot:queryDocumentSnapshots)
                {
                    chats.add(queryDocumentSnapshot.getId());
                    //Toast.makeText(ChatsResult.this, queryDocumentSnapshot.getId(), Toast.LENGTH_SHORT).show();
                }
                show(chats);
            }
        });
    }

    public void show(ArrayList<String> Uids)
    {
        if(Uids.size()==0)
        {
            TextView textView=new TextView(ChatsResult.this);
            textView.setText("No results");
            textView.setTextColor(Color.parseColor("#eeeeee"));
            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);
        }
        for(int i=0;i<Uids.size();i++)
        {
            final String Uid=Uids.get(i);
            final String[] sellerType = new String[1];
            final int finalI = i;
            firestore.collection("Users").document("UserType").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().exists())
                            sellerType[0] = task.getResult().getString(Uid);
                    }
                    LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    final Button button=new Button(ChatsResult.this);
                    button.setId(finalI);
                    button.setBackgroundColor(Color.WHITE);
                    final String s= sellerType[0].equals("Manufacturer")?"EnterpriseName":(sellerType[0].equals("Retailer")?"ShopName":"Firstname");
                    firestore.collection(sellerType[0]).document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                DocumentSnapshot snapshot=task.getResult();
                                if(snapshot.exists())
                                    button.setText(snapshot.getString(s));
                            }
                        }
                    });
                    layout.addView(button);
                    final int id_=button.getId();
                    Button button1=findViewById(id_);
                    button1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent goToChat=new Intent(ChatsResult.this,ChatPage.class);
                            goToChat.putExtra("SellerUid",Uid);
                            goToChat.putExtra("SellerType",sellerType[0]);
                            goToChat.putExtra("Message","");
                            startActivity(goToChat);

                        }
                    });
                    TextView textView=new TextView(ChatsResult.this);
                    textView.setHeight(5);
                    layout.addView(textView);
                }
            });
        }
    }
}