package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class RegisterManuActivity extends AppCompatActivity {

    String productType="",productName,enterpriseName,phone,description,social;
    boolean delivery,payment;

    Spinner spinner;
    EditText editText_phone,editText_enterprise,editText_product,editText_description,editText_social;
    Button button_next;
    MapView mapView;
    ListView listView;
    ScrollView scrollView;
    ConstraintLayout constraintLayout;
    TextView textView_productType;
    CheckBox checkBox_delivery,checkBox_payment;
    ImageButton imageButton_capture;
    ImageView imageView;
    ProgressBar progressBar;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseFirestore firestore;
    StorageReference storageRef;

    public Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_manuregister);
        getSupportActionBar().hide();

        editText_phone=findViewById(R.id.editText_phone);
        editText_enterprise=findViewById(R.id.editText_enterprise);
        editText_product=findViewById(R.id.editText_product);
        editText_description=findViewById(R.id.editText_description);
        button_next=findViewById(R.id.button_next);
        listView=findViewById(R.id.listView);
        scrollView=findViewById(R.id.scrollView);
        constraintLayout=findViewById(R.id.constraintLayout);
        editText_social=findViewById(R.id.editText_social);
        textView_productType=findViewById(R.id.textView_productType);
        checkBox_delivery=findViewById(R.id.checkBox_delivery);
        checkBox_payment=findViewById(R.id.checkBox_payment);
        imageButton_capture=findViewById(R.id.imageButton_capture);
        imageView=findViewById(R.id.imageView);
        progressBar=findViewById(R.id.progressBar);

        firestore=FirebaseFirestore.getInstance();
        //storageRef= FirebaseStorage.getInstance().getReference("Images/"+"Manufacturer"+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid());


        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        String list[]={"Packaging","FoodAndSnacks","Bakery","OtherEatables","Textile","Autoparts"};
        ArrayAdapter<String> adapter=new ArrayAdapter<>(this,R.layout.row,list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                productType=adapterView.getItemAtPosition(i).toString();
                textView_productType.setText(productType);
                //Toast.makeText(RegisterManuActivity.this,productType , Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
    }
    public void onClickNext(View v)
    {

        productName=editText_product.getText().toString();
        enterpriseName=editText_enterprise.getText().toString();
        phone=editText_phone.getText().toString();
        description=editText_description.getText().toString();
        social=editText_social.getText().toString();
        delivery=checkBox_delivery.isChecked();
        payment=checkBox_payment.isChecked();

        if(description.isEmpty())
            description="No product description provided";
        if(phone.length()<10)
            Toast.makeText(this, "Enter your valid phone no. by which people will contact you", Toast.LENGTH_SHORT).show();
        else if(enterpriseName.isEmpty())
            Toast.makeText(this, "Enter name of your enterprise", Toast.LENGTH_SHORT).show();
        else if(productName.isEmpty())
            Toast.makeText(this, "Enter name of your product", Toast.LENGTH_SHORT).show();
        else if(productType.isEmpty())
            Toast.makeText(this,"Enter your product type",Toast.LENGTH_SHORT).show();
        else
        {
            editor.putString("Uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.commit();
            Intent intent=getIntent();
            String firstname=intent.getStringExtra("Firstname");//preferences.getString("Firstname","");
            String lastname=intent.getStringExtra("Lastname");//preferences.getString("Lastname","");
            String email=intent.getStringExtra("email");//preferences.getString("email","");

            HashMap<String,Object> map=new HashMap<>();
            map.put("ProductType",productType);
            map.put("Phone",phone);
            map.put("ProductName",productName);
            map.put("ProductDescription",description);
            map.put("Social",social);
            //map.put("Rating",0);
            map.put("Firstname",firstname);
            map.put("Lastname",lastname);
            map.put("email",email);
            map.put("EnterpriseName",enterpriseName);
            //map.put("TotalRatings",0);

            editor.remove("Firstname");
            editor.remove("Lastname");

            HashMap<String,Object> map3=new HashMap<>();
            map3.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),"Manufacturer");
            firestore.collection("Users").document("UserType").set(map3,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(RegisterManuActivity.this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(RegisterManuActivity.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                        }
                    });

            Log.d("Hashmap","Hashmap created");
            HashMap<String,Object> map2=new HashMap<>();
            map2.put(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUid());
            firestore.collection("ManufacturingProducts").document(productType).set(map2,SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Log.d("product","product type saved");
                            else
                                Log.d("product","product type failed to save");
                        }
                    });
            firestore.collection("Manufacturer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //Toast.makeText(RegisterManuActivity.this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(RegisterManuActivity.this,MapsActivity.class));
                            }
                            else
                                Toast.makeText(RegisterManuActivity.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                        }
                    });

            HashMap<String,Object> map4=new HashMap<>();
            map4.put("Uid",FirebaseAuth.getInstance().getCurrentUser().getUid());
            map4.put("Rating",0);
            map4.put("TotalRatings",0);
            map4.put("PaymentMode",payment);
            map4.put("Delivery",delivery);
            firestore.collection("EnterpriseType").document("Type").collection(productType).document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map4, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //Toast.makeText(RegisterManuActivity.this, "Welcome!!!", Toast.LENGTH_SHORT).show();
                                //startActivity(new Intent(RegisterRetailActivity.this,MapsActivity.class));
                            }
                            else
                                Toast.makeText(RegisterManuActivity.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                        }
                    });
            firestore.collection("Manufacturer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("Favourites", FieldValue.arrayUnion("Sample"));
            firestore.collection("Manufacturer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).update("Chats", FieldValue.arrayUnion("Sample"));
            Intent goToMap=new Intent(RegisterManuActivity.this,MapsActivity.class);
            goToMap.putExtra("Type",productType);
            startActivity(goToMap);

        }


    }
    public void onClickCapture(View v)
    {
        Toast.makeText(this, "Select picture from gallery", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent();
        intent.setType("image/'");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            imageUri=data.getData();
            uploadImage();
            imageView.setImageURI(imageUri);
        }
    }
    /*private String getExtension(Uri uri)                        // to get the image extension like jpg, png or jpeg, etc
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }*/
    public void uploadImage()
    {
        //final StorageReference ref=storageRef.child("DisplayPicture"+getExtension(imageUri));
        storageRef= FirebaseStorage.getInstance().getReference("Images/"+"Manufacturer"+"/"+FirebaseAuth.getInstance().getCurrentUser().getUid());
        final StorageReference ref=storageRef.child("DisplayPicture");
        ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                    Toast.makeText(RegisterManuActivity.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(RegisterManuActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
            }
        })
                /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri[] downloadUrl = new Uri[1];
                        Toast.makeText(RegisterManuActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();

                        //Uri downloadUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(RegisterManuActivity.this, "Image not uploaded. Try after sometime", Toast.LENGTH_SHORT).show();
                    }
                })*/
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        int progress=(int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressBar.setProgress(progress);
                    }
                });
    }
}
