package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.health.UidHealthStats;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class SearchResult extends AppCompatActivity {

    FirebaseFirestore firestore;
    CollectionReference collectionRef;

    String type,sellerType,basis;
    boolean delivery,payment;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    //final ArrayList<String> Uids=new ArrayList<>();

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_search_result);
        getSupportActionBar().hide();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        Intent intent=getIntent();
        type=intent.getStringExtra("Type");
        sellerType=intent.getStringExtra("SellerType");
        basis=intent.getStringExtra("Basis");
        delivery=intent.getBooleanExtra("Delivery",false);
        payment=intent.getBooleanExtra("Payment",false);

        layout=findViewById(R.id.layout);

        firestore=FirebaseFirestore.getInstance();
        collectionRef=FirebaseFirestore.getInstance().collection("EnterpriseType").document("Type").collection(type);

        if(delivery && payment)
        {
            if(basis.equals("Rating")) {
                collectionRef.orderBy("Rating", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.getBoolean("PaymentMode") && documentSnapshot.getBoolean("Delivery"))
                            {
                                Uids.add(documentSnapshot.getString("Uid"));
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView=new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));
                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if(basis.equals("Location")) {

                collectionRef.whereEqualTo("Delivery", delivery).whereEqualTo("PaymentMode", payment).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.getId().equals("Sample"))
                                Uids.add(documentSnapshot.getString("Uid"));
                        }
                        final double[] userLat = new double[1];
                        final double[] userLong = new double[1];
                        firestore.collection(preferences.getString("UserType", "")).document(preferences.getString("Uid", "")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                userLat[0] = task.getResult().getDouble("Latitude");
                                                userLong[0] = task.getResult().getDouble("Longitude");
                                            }
                                        }
                                    }
                                });
                        double[] distance = new double[Uids.size()];
                        int d = 0, i, j;
                        double a;
                        boolean isSwapped = true;
                        for (i = 0; i < Uids.size(); i++) {
                            final double[] Lat = new double[1];
                            final double[] Long = new double[1];
                            firestore.collection(sellerType).document(Uids.get(i)).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    Lat[0] = task.getResult().getDouble("Latitude");
                                                    Long[0] = task.getResult().getDouble("Longitude");
                                                }
                                            }
                                        }
                                    });
                            distance[d++] = Math.sqrt((userLat[0] - Lat[0]) * (userLat[0] - Lat[0]) + (userLong[0] - Long[0]) * (userLong[0] - Long[0]));
                        }
                        for (i = 0; i < Uids.size() && isSwapped; i++) {
                            isSwapped = false;
                            for (j = i + 1; j < Uids.size(); j++) {
                                if (distance[i] > distance[j])                         //Check for increasing order and decreasing order
                                {
                                    Collections.swap(Uids, i, j);
                                    a = distance[i];
                                    distance[i] = distance[j];
                                    distance[j] = a;
                                    isSwapped = true;
                                }
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView = new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }

        }
        else if(delivery)
        {
            if(basis.equals("Rating")) {
                collectionRef.orderBy("Rating", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.getBoolean("Delivery"))
                            {
                                Uids.add(documentSnapshot.getString("Uid"));
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView=new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if(basis.equals("Location")) {

                collectionRef.whereEqualTo("Delivery", delivery).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.getId().equals("Sample"))
                                Uids.add(documentSnapshot.getString("Uid"));
                        }
                        final double[] userLat = new double[1];
                        final double[] userLong = new double[1];
                        firestore.collection(preferences.getString("UserType", "")).document(preferences.getString("Uid", "")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                userLat[0] = task.getResult().getDouble("Latitude");
                                                userLong[0] = task.getResult().getDouble("Longitude");
                                            }
                                        }
                                    }
                                });
                        double[] distance = new double[Uids.size()];
                        int d = 0, i, j;
                        double a;
                        boolean isSwapped = true;
                        for (i = 0; i < Uids.size(); i++) {
                            final double[] Lat = new double[1];
                            final double[] Long = new double[1];
                            firestore.collection(sellerType).document(Uids.get(i)).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    Lat[0] = task.getResult().getDouble("Latitude");
                                                    Long[0] = task.getResult().getDouble("Longitude");
                                                }
                                            }
                                        }
                                    });
                            distance[d++] = Math.sqrt((userLat[0] - Lat[0]) * (userLat[0] - Lat[0]) + (userLong[0] - Long[0]) * (userLong[0] - Long[0]));
                        }
                        for (i = 0; i < Uids.size() && isSwapped; i++) {
                            isSwapped = false;
                            for (j = i + 1; j < Uids.size(); j++) {
                                if (distance[i] > distance[j])                         //Check for increasing order and decreasing order
                                {
                                    Collections.swap(Uids, i, j);
                                    a = distance[i];
                                    distance[i] = distance[j];
                                    distance[j] = a;
                                    isSwapped = true;
                                }
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView = new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else if(payment)
        {

            if(basis.equals("Rating")) {
                collectionRef.orderBy("Rating", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if(documentSnapshot.getBoolean("PaymentMode"))
                            {
                                Uids.add(documentSnapshot.getString("Uid"));
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView=new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if(basis.equals("Location")) {

                collectionRef.whereEqualTo("PaymentMode", payment).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.getId().equals("Sample"))
                                Uids.add(documentSnapshot.getString("Uid"));
                        }
                        final double[] userLat = new double[1];
                        final double[] userLong = new double[1];
                        firestore.collection(preferences.getString("UserType", "")).document(preferences.getString("Uid", "")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                userLat[0] = task.getResult().getDouble("Latitude");
                                                userLong[0] = task.getResult().getDouble("Longitude");
                                            }
                                        }
                                    }
                                });
                        double[] distance = new double[Uids.size()];
                        int d = 0, i, j;
                        double a;
                        boolean isSwapped = true;
                        for (i = 0; i < Uids.size(); i++) {
                            final double[] Lat = new double[1];
                            final double[] Long = new double[1];
                            firestore.collection(sellerType).document(Uids.get(i)).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    Lat[0] = task.getResult().getDouble("Latitude");
                                                    Long[0] = task.getResult().getDouble("Longitude");
                                                }
                                            }
                                        }
                                    });
                            distance[d++] = Math.sqrt((userLat[0] - Lat[0]) * (userLat[0] - Lat[0]) + (userLong[0] - Long[0]) * (userLong[0] - Long[0]));
                        }
                        for (i = 0; i < Uids.size() && isSwapped; i++) {
                            isSwapped = false;
                            for (j = i + 1; j < Uids.size(); j++) {
                                if (distance[i] > distance[j])                         //Check for increasing order and decreasing order
                                {
                                    Collections.swap(Uids, i, j);
                                    a = distance[i];
                                    distance[i] = distance[j];
                                    distance[j] = a;
                                    isSwapped = true;
                                }
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView = new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
        else
        {
            if(basis.equals("Rating")) {
                collectionRef.orderBy("Rating", Query.Direction.DESCENDING).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Uids.add(documentSnapshot.getString("Uid"));
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView=new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
            else if(basis.equals("Location")) {

                collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        ArrayList<String> Uids = new ArrayList<>();
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            if (!documentSnapshot.getId().equals("Sample"))
                                Uids.add(documentSnapshot.getString("Uid"));
                        }
                        final double[] userLat = new double[1];
                        final double[] userLong = new double[1];
                        firestore.collection(preferences.getString("UserType", "")).document(preferences.getString("Uid", "")).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            if (task.getResult().exists()) {
                                                userLat[0] = task.getResult().getDouble("Latitude");
                                                userLong[0] = task.getResult().getDouble("Longitude");
                                            }
                                        }
                                    }
                                });
                        double[] distance = new double[Uids.size()];
                        int d = 0, i, j;
                        double a;
                        boolean isSwapped = true;
                        for (i = 0; i < Uids.size(); i++) {
                            final double[] Lat = new double[1];
                            final double[] Long = new double[1];
                            firestore.collection(sellerType).document(Uids.get(i)).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                if (task.getResult().exists()) {
                                                    Lat[0] = task.getResult().getDouble("Latitude");
                                                    Long[0] = task.getResult().getDouble("Longitude");
                                                }
                                            }
                                        }
                                    });
                            distance[d++] = Math.sqrt((userLat[0] - Lat[0]) * (userLat[0] - Lat[0]) + (userLong[0] - Long[0]) * (userLong[0] - Long[0]));
                        }
                        for (i = 0; i < Uids.size() && isSwapped; i++) {
                            isSwapped = false;
                            for (j = i + 1; j < Uids.size(); j++) {
                                if (distance[i] > distance[j])                         //Check for increasing order and decreasing order
                                {
                                    Collections.swap(Uids, i, j);
                                    a = distance[i];
                                    distance[i] = distance[j];
                                    distance[j] = a;
                                    isSwapped = true;
                                }
                            }
                        }
                        show(Uids);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        {
                            TextView textView = new TextView(SearchResult.this);
                            textView.setText("No results");
                            textView.setTextColor(Color.parseColor("#eeeeee"));

                            textView.setGravity(Gravity.CENTER);
                            layout.addView(textView);
                            //Toast.makeText(SearchResult.this, "No results", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }
    public void onClickSearch(View v)
    {
        v.setVisibility(View.INVISIBLE);
        /*for(int i=0;i<Uids.size();i++)
        {
            final String Uid=Uids.get(i);

            LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            final Button button=new Button(this);
            button.setId(i);
            button.setBackgroundColor(Color.WHITE);
            final String s=sellerType.equals("Manufacturer")?"EnterpriseName":"ShopName";
            final String name ;
            firestore.collection(sellerType).document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot snapshot=task.getResult();
                            if(snapshot.exists())
                                button.setText(snapshot.getString(s));
                            else
                                Toast.makeText(SearchResult.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(SearchResult.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                });
            layout.addView(button);
            final int id_=button.getId();
            Button button1=findViewById(id_);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(SearchResult.this,SellerPage.class);
                    intent.putExtra("Type",type);
                    intent.putExtra("SellerType",sellerType);
                    intent.putExtra("SellerUid",Uid);
                    startActivity(intent);
                }
            });
        }*/
    }

    public void show(ArrayList<String> Uids)
    {
        if(Uids.size()==0)
        {
            TextView textView=new TextView(SearchResult.this);
            textView.setText("No results");
            textView.setTextColor(Color.parseColor("#eeeeee"));

            textView.setGravity(Gravity.CENTER);
            layout.addView(textView);
        }
        for(int i=0;i<Uids.size();i++)
        {
            final String Uid=Uids.get(i);
            if(!Uid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                final Button button = new Button(this);
                button.setId(i);
                button.setBackgroundColor(Color.WHITE);
                final String s = sellerType.equals("Manufacturer") ? "EnterpriseName" : "ShopName";
                final String name;
                firestore.collection(sellerType).document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot.exists())
                                button.setText(snapshot.getString(s));
                            //else
                            //  Toast.makeText(SearchResult.this, "Snapshot not exists", Toast.LENGTH_SHORT).show();
                        }
                        //else
                        // Toast.makeText(SearchResult.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                });
                layout.addView(button);
                final int id_ = button.getId();
                Button button1 = findViewById(id_);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SearchResult.this, SellerPage.class);
                        intent.putExtra("SellerType", sellerType);
                        intent.putExtra("SellerUid", Uid);
                        intent.putExtra("Type", type);
                        startActivity(intent);
                    }
                });
                TextView textView = new TextView(this);
                textView.setHeight(5);
                layout.addView(textView);
            }
        }
    }
}