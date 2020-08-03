package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class FavouriteResult extends AppCompatActivity {

    FirebaseFirestore firestore;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String userType,Uid;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_favourite_result);
        getSupportActionBar().hide();

        layout=findViewById(R.id.layout);

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        userType=preferences.getString("UserType","");
        Uid=preferences.getString("Uid","");

        firestore=FirebaseFirestore.getInstance();

        firestore.collection(userType).document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    DocumentSnapshot snapshot=task.getResult();
                    if(snapshot.exists())
                    {
                        ArrayList<String> favourites=new ArrayList<>();
                        if ((ArrayList<String>) snapshot.get("Favourites") != null) {
                            favourites = (ArrayList<String>) snapshot.get("Favourites");
                            favourites.remove("Sample");
                            show(favourites);
                        }
                    }
                }
            }
        });
    }
    public void show(ArrayList<String> Uids)
    {
        if(Uids.size()==0)
        {
            TextView textView=new TextView(FavouriteResult.this);
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
            final Button button=new Button(FavouriteResult.this);
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
                    Intent intent=new Intent(FavouriteResult.this,SellerPage.class);
                    intent.putExtra("SellerType", sellerType[0]);
                    intent.putExtra("SellerUid",Uid);
                    startActivity(intent);
                }
            });
            TextView textView=new TextView(FavouriteResult.this);
            textView.setHeight(5);
            layout.addView(textView);
                }
            });
        }
    }
}