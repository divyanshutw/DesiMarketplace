package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.DecimalFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SellerPage extends AppCompatActivity {



    ImageButton imageButton_map,imageButton_call,imageButton_favourite,imageButton_send,imageButton_email;
    RatingBar ratingBar;
    EditText editText_message;
    TextView textView_enterpriseName,textView_ownerName,textView_productType,textView_rating,textView_productName
            ,textView_productDescription,textView_social,textView_product,textView_overallRating;
    Button button_book,button_order;
    ImageView imageView_enterpriseImage;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    String sellerType,sellerUid,type;
    String sellerPhone,currentUid,currentUserType,email;
    double sellerLatitude,sellerLongitude;
    boolean isFavourite,isRatingGiven=false,isBookPresent=false;
    float rating=0;

    FirebaseFirestore firestore;
    StorageReference storageref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_seller_page);
        getSupportActionBar().hide();

        preferences = getSharedPreferences("LOGIN_INFO", MODE_PRIVATE);
        editor = preferences.edit();

        currentUid = preferences.getString("Uid", "");
        currentUserType = preferences.getString("UserType", "");
        //Toast.makeText(this, currentUserType+"  "+currentUid, Toast.LENGTH_SHORT).show();
        final Intent intent = getIntent();
        sellerType = intent.getStringExtra("SellerType");
        sellerUid = intent.getStringExtra("SellerUid");
        //type = intent.getStringExtra("Type");

        firestore = FirebaseFirestore.getInstance();
        storageref=FirebaseStorage.getInstance().getReference("/Images/"+sellerType+"/"+sellerUid);
        //Toast.makeText(this, "Images/"+sellerType+"/"+sellerUid, Toast.LENGTH_SHORT).show();

        editText_message = findViewById(R.id.editText_message);
        ratingBar = findViewById(R.id.ratingBar);
        imageButton_call = findViewById(R.id.imageButton_call);
        imageButton_favourite = findViewById(R.id.imageButton_favourite);
        imageButton_map = findViewById(R.id.imageButton_map);
        imageButton_send = findViewById(R.id.imageButton_send);
        button_book = findViewById(R.id.button_book);
        button_order = findViewById(R.id.button_order);
        textView_enterpriseName = findViewById(R.id.textView_enterpriseName);
        textView_ownerName = findViewById(R.id.textView_ownerName);
        textView_productDescription = findViewById(R.id.textView_description);
        textView_productName = findViewById(R.id.textView_productName);
        textView_productType = findViewById(R.id.textView_productType);
        textView_rating = findViewById(R.id.textView_rating);
        textView_product = findViewById(R.id.textView_product);
        textView_overallRating = findViewById(R.id.textView_OvallRating);
        textView_social = findViewById(R.id.textView_social);
        imageButton_email = findViewById(R.id.imageButton_email);
        imageView_enterpriseImage=findViewById(R.id.imageView_enterpriseImage);
        //Toast.makeText(this, currentUserType+"  "+currentUid, Toast.LENGTH_SHORT).show();
        fillTexts();
        downloadImage();


        firestore.collection(sellerType).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        sellerPhone = snapshot.getString("Phone");
                        sellerLatitude = snapshot.getDouble("Latitude");
                        sellerLongitude = snapshot.getDouble("Longitude");
                        email = snapshot.getString("email");
                        type=snapshot.getString("ProductType");
                    } else {
                        Toast.makeText(SellerPage.this, "Unable to fetch data from database", Toast.LENGTH_SHORT).show();
                    }
                } else
                    Toast.makeText(SellerPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
            }
        });

        firestore.collection(currentUserType).document(currentUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                ArrayList<String> favourites = new ArrayList<>();
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        if ((ArrayList<String>) snapshot.get("Favourites") != null) {
                            favourites = (ArrayList<String>) snapshot.get("Favourites");
                            if (favourites.contains(sellerUid))
                                imageButton_favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_on));
                            else
                                imageButton_favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), android.R.drawable.btn_star_big_off));

                        }
                    }
                }
            }
        });

        firestore.collection(currentUserType).document(currentUid).collection("RatingsGiven").document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {
                    isRatingGiven = true;
                    rating = Float.parseFloat(Double.toString(task.getResult().getDouble("RatingGiven")));         //bug rating not stored in variable
                    ratingBar.setRating(Float.parseFloat(Double.toString(task.getResult().getDouble("RatingGiven"))));
                }
            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(final RatingBar ratingBar, final float v, boolean b) {
                firestore.collection(currentUserType).document(currentUid).collection("RatingsGiven").document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        rating = 0;
                        isRatingGiven = false;
                        if (task.isSuccessful() && task.getResult().exists()) {
                            isRatingGiven = true;
                            rating = Float.parseFloat(Double.toString(task.getResult().getDouble("RatingGiven")));         //bug rating not stored in variable
                        }
                        //setRating(v,rating);
                        final float[] oldRating = new float[1], totalRatings = new float[1];
                        final float val = v;
                        final float[] t = new float[1];
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("RatingGiven", val);
                        map.put("Uid", sellerUid);
                        firestore.collection(currentUserType).document(currentUid).collection("RatingsGiven").document(sellerUid).set(map);

                        firestore.collection("EnterpriseType").document("Type").collection(type).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().exists()) {
                                        oldRating[0] = Float.parseFloat(Double.toString(task.getResult().getDouble("Rating")));
                                        totalRatings[0] = Float.parseFloat(Double.toString(task.getResult().getDouble("TotalRatings")));
                                        //Toast.makeText(SellerPage.this, oldRating[0] + "" + totalRatings[0], Toast.LENGTH_SHORT).show();


                                        t[0] = totalRatings[0];
                                        totalRatings[0] = isRatingGiven ? totalRatings[0] : totalRatings[0] + 1;
                                        rating = (oldRating[0] * t[0] - rating + val) / totalRatings[0];
                                        HashMap<String, Object> map1 = new HashMap<>();
                                        map1.put("Rating", rating);
                                        map1.put("TotalRatings", Float.valueOf(totalRatings[0]));
                                        //firestore.collection("Rating").document(sellerUid).set(map1,SetOptions.merge());
                                        firestore.collection("EnterpriseType").document("Type").collection("Packaging").document(sellerUid).set(map1, SetOptions.merge());
                                    } else
                                        Toast.makeText(SellerPage.this, "Unable to fetch from database", Toast.LENGTH_SHORT).show();
                                } else
                                    Toast.makeText(SellerPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                            }                          //Rating and total rating not updating. tasks getting failed
                        });
                    }
                });
            }
        });

        firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists())
                        isBookPresent = true;
                    else
                        isBookPresent = false;
                } else
                    isBookPresent = false;
                button_book.setText(isBookPresent ? "Open Bahi Khata" : "Add to Bahi Khata");
                button_book.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Book(isBookPresent);
                        if (!isBookPresent)
                            isBookPresent = true;
                    }
                });
            }
        });
    }
    public void fillTexts()
    {
        if(sellerType.equals("Retailer"))
        {
            textView_product.setVisibility(View.INVISIBLE);
            textView_productName.setVisibility(View.INVISIBLE);
        }
        firestore.collection(sellerType).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        textView_ownerName.setText(snapshot.getString("Firstname") +" "+ snapshot.getString("Lastname"));
                        textView_social.setText(snapshot.getString("Social"));
                        textView_productDescription.setText(snapshot.getString("ProductDescription")); //for retailer, it contains shop description
                        if (sellerType.equals("Manufacturer")) {
                            textView_enterpriseName.setText(snapshot.getString("EnterpriseName"));
                            textView_productType.setText(snapshot.getString("ProductType"));
                            textView_productName.setText(snapshot.getString("ProductName"));
                            type=snapshot.getString("ProductType");
                        } else {
                            textView_enterpriseName.setText(snapshot.getString("ShopName"));
                            textView_productType.setText(snapshot.getString("ShopType"));
                            type=snapshot.getString("ShopType");
                        }

                        //Toast.makeText(SellerPage.this, type, Toast.LENGTH_SHORT).show();
                        firestore.collection("EnterpriseType").document("Type").collection(type).document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful() && task.getResult().exists()) {
                                    if (task.getResult().exists()) {
                                        String rat = String.format("%.1f", task.getResult().getDouble("Rating"));
                                        textView_rating.setText(rat);
                                    }
                                    else
                                        ;//Toast.makeText(SellerPage.this, "Task failed", Toast.LENGTH_SHORT).show();
                                }
                                else
                                    Toast.makeText(SellerPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        ;//Toast.makeText(SellerPage.this, "Task failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Toast.makeText(SellerPage.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void downloadImage()
    {
        StorageReference ref=storageref.child("DisplayPicture");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageView_enterpriseImage.setImageURI(uri);
            }
        });
        Glide.with(this).load(ref).into(imageView_enterpriseImage);
        final long TWO_MEGABYTE = 2*1024 * 1024;
        ref.getBytes(TWO_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView_enterpriseImage.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                ;//Toast.makeText(getApplicationContext(), exception.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void onClickFavourite(View v)
    {
        if(isFavourite)
            firestore.collection(currentUserType).document(currentUid).update("Favourites",FieldValue.arrayRemove(sellerUid));
        else
            firestore.collection(currentUserType).document(currentUid).update("Favourites",FieldValue.arrayUnion(sellerUid));
        isFavourite=!isFavourite;
        if(isFavourite)
            imageButton_favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_on));
        else
            imageButton_favourite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),android.R.drawable.btn_star_big_off));
    }
    public void onClickMap(View v)
    {
        Intent intent=new Intent(SellerPage.this,SellerMapActivity.class);
        intent.putExtra("Latitude",sellerLatitude);
        intent.putExtra("Longitude",sellerLongitude);
        startActivity(intent);
    }
    public void onClickCall(View v)
    {
        Intent callIntent=new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:"+sellerPhone.trim()));
        startActivity(callIntent);
    }
    public void onClickSend(View v)
    {
        /*firestore.collection(currentUserType).document(currentUid).update("Chats",FieldValue.arrayUnion(sellerUid));
        String message=editText_message.getText().toString();
        Uri destination = Uri.parse("smsto:"+sellerPhone);
        Intent smsIntent=new Intent(Intent.ACTION_SENDTO,destination);
        smsIntent.putExtra("sms_body",message);
        startActivity(smsIntent);*/
        HashMap<String,Object> map=new HashMap<>();
        map.put("Sample","sample");
        firestore.collection("EndToEnd").document("Chats").collection(currentUid).document(sellerUid).set(map,SetOptions.merge());
        firestore.collection("EndToEnd").document("Chats").collection(sellerUid).document(currentUid).set(map,SetOptions.merge());
        String message=editText_message.getText().toString();
        Intent goToChat=new Intent(SellerPage.this,ChatPage.class);
        goToChat.putExtra("SellerUid",sellerUid);
        goToChat.putExtra("SellerType",sellerType);
        goToChat.putExtra("Message",message);
        startActivity(Intent.createChooser(goToChat,"Choose a messaging client:"));
    }

    public void Book(boolean b)
    {
        if(!b) {
            //Toast.makeText(SellerPage.this, sellerUid, Toast.LENGTH_SHORT).show();
            button_book.setText("Open Bahi Khata");
            HashMap<String,Object> map1=new HashMap<>();
            map1.put("Uid",sellerUid);
            map1.put("Balance",0);
            firestore.collection("EndToEnd").document("Book").collection(currentUid).document(sellerUid).set(map1);
            HashMap<String,Object> map2=new HashMap<>();
            map2.put("Uid",sellerUid);
            map2.put("Balance",0);
            firestore.collection("EndToEnd").document("Book").collection(sellerUid).document(currentUid).set(map2);
        }
        else
        {
            Intent goToBookPage=new Intent(SellerPage.this,BookPage.class);
            goToBookPage.putExtra("SellerType",sellerType);
            goToBookPage.putExtra("SellerUid",sellerUid);
            startActivity(goToBookPage);
        }
    }
    public void onClickOrder(View v)
    {
        Intent goToOrderPage=new Intent(SellerPage.this,OrderPage.class);
        goToOrderPage.putExtra("SellerType",sellerType);
        //goToOrderPage.putExtra("Type",type);
        goToOrderPage.putExtra("SellerUid",sellerUid);
        goToOrderPage.putExtra("IsOrder","given");
        startActivity(goToOrderPage);
    }

    public void setRating(float v, float rat)
    {
        final float[] oldRating = new float[1], totalRatings = new float[1];
        final float val=v;
        final float[] rating = {rat};
        firestore.collection("Rating").document(sellerUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult().exists()) {

                    oldRating[0] = Float.parseFloat(Double.toString(task.getResult().getDouble("Rating")));
                    totalRatings[0] = Float.parseFloat(Double.toString(task.getResult().getDouble("TotalRatings")));
                    //Toast.makeText(SellerPage.this, oldRating[0] + "" + totalRatings[0], Toast.LENGTH_SHORT).show();

                    HashMap<String, Object> map = new HashMap<>();
                    totalRatings[0] = isRatingGiven ? totalRatings[0] : totalRatings[0] + 1;
                    rating[0] = (oldRating[0] * totalRatings[0] - rating[0] + val) / totalRatings[0];
                    map.put("RatingGiven", val);
                    map.put("Uid", sellerUid);
                    firestore.collection(currentUserType).document(currentUid).collection("RatingsGiven").document(sellerUid).set(map);
                    HashMap<String,Object> map1=new HashMap<>();
                    map1.put("Rating", rating[0]);
                    map1.put("TotalRatings",totalRatings[0]);
                    //firestore.collection("Rating").document(sellerUid).set(map1,SetOptions.merge());
                    firestore.collection("EnterpriseType").document("Type").collection(sellerType).document(sellerUid).update("Rating", rating[0]);
                }
            }                          //Rating and total rating not updating. tasks getting failed
        });
    }
    public void onClickEmail(View v)
    {
        String subject="Desi Marketplace customer",msg="Type your email";
        Intent emailIntent=new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{email});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,msg);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Choose an email client:"));
    }
}