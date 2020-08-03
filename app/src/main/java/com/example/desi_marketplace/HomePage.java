package com.example.desi_marketplace;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomePage extends AppCompatActivity {

    TextView textView_name;
    ImageButton imageButton_i,imageButton_logout;
    Button button_profile,button_chats,button_favourites,button_orders,button_book,button_news,button_search;

    String name="",UserType,Uid;

    SharedPreferences preferences;
    FirebaseFirestore firestore;
    CollectionReference collectionRef;

    SharedPreferences.Editor editor;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_home_page);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        editor = preferences.edit();

        firestore = FirebaseFirestore.getInstance();

        textView_name = findViewById(R.id.textView_name);
        imageButton_i = findViewById(R.id.imageButton_i);
        imageButton_logout = findViewById(R.id.imageButton_logout);
        button_book = findViewById(R.id.button_book);
        button_chats = findViewById(R.id.button_chats);
        button_favourites = findViewById(R.id.button_favourites);
        button_orders = findViewById(R.id.button_orders);
        button_profile = findViewById(R.id.button_profile);
        button_search = findViewById(R.id.button_search);
        button_news = findViewById(R.id.button_news);

        firestore.collection("Users").document("UserType").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    UserType=documentSnapshot.getString(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.putString("UserType", UserType);
                    editor.commit();firestore.collection(UserType).document(Uid)
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot snapshot = task.getResult();
                                    if (snapshot.exists()) {
                                        name = snapshot.getString("Firstname");
                                        //Toast.makeText(HomePage.this, name, Toast.LENGTH_SHORT).show();
                                        textView_name.setText("Hi " + name + ", welcome!");
                                    }
                                }
                            }
                        });
                }
            }
        });

        UserType = preferences.getString("UserType", "");
        Uid = preferences.getString("Uid", "");
        //Toast.makeText(this, UserType+"  "+Uid, Toast.LENGTH_SHORT).show();


    }
    public void onClickI(View v)
    {
        final Toast mToastToShow;

        // Set the toast and duration
        int toastDurationInMilliSeconds = 10000;
        mToastToShow = Toast.makeText(this, "This app provides a platform to local manufacturers " +
                "and retailers to sell their products. And customers to find peer rated good quality product sellers." +
                " Enjoy the app. ", Toast.LENGTH_LONG);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                mToastToShow.show();
            }
            public void onFinish() {
                mToastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        mToastToShow.show();
        toastCountDown.start();
    }
    public void onClickLogout(View v)
    {
        FirebaseAuth.getInstance().signOut();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        editor.putBoolean("LoggedIn",false);
        editor.commit();

        Intent goToSignin = new Intent();
        goToSignin.setClass(HomePage.this,MainActivity.class);
        goToSignin.putExtra("LoggedIn",false);

        startActivity(goToSignin);
        finish();
    }
    public void onClickProfile(View v)
    {
        startActivity(new Intent(HomePage.this,EditProfile.class));
    }
    public void onClickSearch(View v)
    {
        startActivity(new Intent(HomePage.this,SearchChoice.class));
    }

    public void onClickChats(View v)
    {
        startActivity(new Intent(HomePage.this,ChatsResult.class));
    }
    public void onClickOrders(View v)
    {
        startActivity(new Intent(HomePage.this,OrderResult.class));
    }
    public void onClickBook(View v)
    {
        startActivity(new Intent(HomePage.this,BookResult.class));
    }
    public void onClickFavourites(View v)
    {
        startActivity(new Intent(HomePage.this,FavouriteResult.class));
    }
    public void onClickNews(View v)
    {
        startActivity(new Intent(HomePage.this,NewsActivity.class));
    }
    public void onClickAds(View v)
    {
        startActivity(new Intent(HomePage.this,AdsActivity.class));
    }
/*@Override
public void onBackPressed()
{
    //moveTaskToBack(true);
    //android.os.Process.killProcess(android.os.Process.myPid());
    //finish();
}*/

}