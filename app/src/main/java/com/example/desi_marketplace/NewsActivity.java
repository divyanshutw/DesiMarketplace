package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    //String websiteName[];
   
    SharedPreferences preferences;
    FirebaseFirestore firestore;
    CollectionReference collectionRef;

    SharedPreferences.Editor editor;
    FirebaseAuth auth;
    ListView listView;
    String link="";
    ArrayList<String> links = new ArrayList<>();
    ArrayList<String> link_Titles = new ArrayList<>();
    WebView webV;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_news);

        final TextView top = findViewById(R.id.textViewTop);
        listView=findViewById(R.id.listView);
        webV= findViewById(R.id.webView);


        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        firestore= FirebaseFirestore.getInstance();

        final int[] i = {0};
        firestore.collection("News").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot snapshot : task.getResult()) {
                        if (snapshot.exists()) {

                            if ((String)snapshot.get("News") != null) {
                                links.add((String) snapshot.get("News"));


                            }
                            if ((String)snapshot.get("NewsTitle") != null) {
                                link_Titles.add((String) snapshot.get("NewsTitle"));


                            }



                        //String news = (String) snapshot.getString("News");
                        //Toast.makeText(NewsActivity.this, news, Toast.LENGTH_SHORT).show();




                    }
                        top.setText("News links of some websites, click to view");
                        ArrayAdapter<String> adapter = new ArrayAdapter(NewsActivity.this, R.layout.row, link_Titles );
                        listView.setAdapter(adapter);

                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                link = links.get(i).toString();
                                //Toast.makeText(NewsActivity.this, link, Toast.LENGTH_SHORT).show();

                                webV.loadUrl(link);


                            }
                        });

                    }
                }
            }



        });




        }
}