package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class EditProfile extends AppCompatActivity {

    EditText editText_firstname,editText_lastname,editText_email,editText_phoneNo, editText_shopName;
    TextView  textView_shopType,textView_shopType0;
    ListView listView;
    ImageButton imageButton_capture;
    Button button_next;
    ProgressBar progressBar;
    ImageView imageView;


    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    StorageReference storageRef;

    public Uri imageUri;

    String userType,firstname,lastname,email,phoneNo,shopName, shopType,Uid,productType,enterpriseName;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_edit_profile);
        getSupportActionBar().hide();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();
        userType=preferences.getString("UserType","");
        Uid=preferences.getString("Uid","");

        editText_firstname=findViewById(R.id.editText_firstname);
        editText_lastname=findViewById(R.id.editText_lastname);
        //editText_email=findViewById(R.id.editText_email);
        editText_phoneNo = findViewById(R.id.editText_password);
        editText_shopName = findViewById(R.id.editText_shopName);
        //textView_shopType =  findViewById(R.id.textView_shopType);
        //textView_shopType0 = findViewById(R.id.textView7);
        //listView = findViewById(R.id.listView);
        imageButton_capture=findViewById(R.id.imageButton_capture);
        progressBar=findViewById(R.id.progressBar);
        imageView=findViewById(R.id.imageView5);

        storageRef= FirebaseStorage.getInstance().getReference("Images/"+userType+"/"+Uid);
        firestore = FirebaseFirestore.getInstance();
        downloadImage();

        firestore.collection("Users").document("UserType").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists())
                        userType = task.getResult().getString(Uid);

                    if(userType.equals("Manufacturer"))
                    {
                        firestore.collection("Manufacturer").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists())
                                {
                                    firstname= task.getResult().getString("Firstname");
                                    lastname= task.getResult().getString("Lastname");
                                    email= task.getResult().getString("email");
                                    phoneNo = task.getResult().getString("Phone");
                                    enterpriseName = task.getResult().getString("EnterpriseName");
                                    productType = task.getResult().getString("ProductType");

                                    editText_firstname.setText(firstname);
                                    editText_lastname.setText(lastname);
                                    //editText_email.setText(email);
                                    editText_phoneNo.setText(phoneNo);
                                    editText_shopName.setText(enterpriseName);
                                    //textView_shopType0.setText("Product Type: ");
                                    //textView_shopType.setText(productType);

                                    /*String list[]={"Packaging","FoodAndSnacks","Bakery","OtherEatables","Textile","Autoparts"};
                                    ArrayAdapter<String> adapter=new ArrayAdapter(EditProfile.this,R.layout.row,list);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            productType=adapterView.getItemAtPosition(i).toString();

                                            textView_shopType.setText(productType);
                                            Toast.makeText(EditProfile.this,productType , Toast.LENGTH_SHORT).show();
                                        }
                                    });*/

                                    //listView.setNestedScrollingEnabled(true);


                                }
                            }
                        });
                    }

                    else if(userType.equals("Retailer"))
                    {
                        firestore.collection("Retailer").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists())
                                {
                                    firstname= task.getResult().getString("Firstname");
                                    lastname= task.getResult().getString("Lastname");
                                    email= task.getResult().getString("email");
                                    phoneNo = task.getResult().getString("Phone");
                                    shopName = task.getResult().getString("ShopName");
                                    shopType = task.getResult().getString("ShopType");

                                    editText_firstname.setText(firstname);
                                    editText_lastname.setText(lastname);
                                    //editText_email.setText(email);
                                    editText_phoneNo.setText(phoneNo);
                                    editText_shopName.setText(shopName);
                                    //textView_shopType0.setText("Shop Type: ");
                                    //textView_shopType.setText(productType);

                                    /*String list[]={"GeneralStore","Garments","BeautyProducts","Footwear","WatchStore","ToyShop"};
                                    ArrayAdapter<String> adapter=new ArrayAdapter(EditProfile.this,R.layout.row,list);
                                    listView.setAdapter(adapter);
                                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                            productType=adapterView.getItemAtPosition(i).toString();

                                            textView_shopType.setText(productType);
                                            Toast.makeText(EditProfile.this,productType , Toast.LENGTH_SHORT).show();
                                        }
                                    });*/



                                }
                            }
                        });
                    }

                    else if(userType.equals("Consumer"))
                    {
                        firestore.collection("Consumer").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @SuppressLint("WrongConstant")
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists())
                                {
                                    firstname= task.getResult().getString("Firstname");
                                    lastname= task.getResult().getString("Lastname");
                                    email= task.getResult().getString("email");
                                    phoneNo = task.getResult().getString("Phone");


                                    editText_firstname.setText(firstname);
                                    editText_lastname.setText(lastname);
                                    //editText_email.setText(email);
                                    editText_phoneNo.setText(phoneNo);
                                    editText_shopName.setText("shopName");
                                    //textView_shopType0.setText("productType");
                                    editText_shopName.setVisibility(View.INVISIBLE);
                                    //textView_shopType0.setVisibility(View.INVISIBLE);


                                }
                            }
                        });
                    }


                }
            }

        });









    }

    public void onClickSave(View v)
    {

        if( firstname.isEmpty() || lastname.isEmpty() || phoneNo.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please fill all the details!", Toast.LENGTH_SHORT).show();
        }

       /*else if (!(email.contains("@")) || !(email.contains(".com")))
       {
        Toast.makeText(getApplicationContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
       }*/
        else if( phoneNo.length()<10)
        {
            Toast.makeText(getApplicationContext(), "Invalid Phone no.", Toast.LENGTH_SHORT).show();
        }

        else if( !(firstname.matches("^[a-zA-Z ]*"))
                || firstname.equals(" ")
                ||!(lastname.matches("^[a-zA-Z ]*"))
                || lastname.equals(" ")

        )
        {
            Toast.makeText(getApplicationContext(), "Name can only contains alphabets", Toast.LENGTH_SHORT).show();
        }
        else
        {



            if(userType.equals("Manufacturer"))
            {

                firstname= editText_firstname.getText().toString();
                lastname= editText_lastname.getText().toString();
                //email=  editText_email.getText().toString();
                phoneNo = editText_phoneNo.getText().toString();
                enterpriseName = editText_shopName.getText().toString();
                //productType = textView_shopType.getText().toString();


                HashMap<String,Object> map=new HashMap<>();
                map.put("Firstname",firstname);
                map.put("Lastname",lastname);
                //map.put("email",email);
                map.put("Phone",phoneNo);
                map.put("EnterpriseName",enterpriseName);
                //map.put("ProductType",productType);


                firestore.collection("Manufacturer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(EditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(EditProfile.this, "Unable to link to database \n Try after some time", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                    }
                });



            }



            else if(userType.equals("Retailer"))
            {

                firstname= editText_firstname.getText().toString();
                lastname= editText_lastname.getText().toString();
                //email=  editText_email.getText().toString();
                phoneNo = editText_phoneNo.getText().toString();
                shopName = editText_shopName.getText().toString();
                //shopType = textView_shopType.getText().toString();


                HashMap<String,Object> map=new HashMap<>();
                map.put("Firstname",firstname);
                map.put("Lastname",lastname);
                //map.put("email",email);
                map.put("Phone",phoneNo);
                map.put("ShopName",shopName);
                //map.put("ShopType",shopType);


                firestore.collection("Retailer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(EditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(EditProfile.this, "Unable to link to database \n Try after some time", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                    }
                });



            }


            else if(userType.equals("Consumer"))
            {

                firstname= editText_firstname.getText().toString();
                lastname= editText_lastname.getText().toString();
                //email=  editText_email.getText().toString();
                phoneNo = editText_phoneNo.getText().toString();
                //enterpriseName = editText_shopName.getText().toString();
                //productType = textView_shopType.getText().toString();


                HashMap<String,Object> map=new HashMap<>();
                map.put("Firstname",firstname);
                map.put("Lastname",lastname);
                //map.put("email",email);
                map.put("Phone",phoneNo);



                firestore.collection("Consumer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(EditProfile.this, "Saved", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                        else
                        {
                            Toast.makeText(EditProfile.this, "Unable to link to database \n Try after some time", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProfile.this, HomePage.class));
                            finish();
                        }
                    }
                });
            }
        }
    }
    public void downloadImage()
    {
        StorageReference ref=storageRef.child("DisplayPicture");
        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imageView.setImageURI(uri);
            }
        });
        Glide.with(this).load(ref).into(imageView);
        final long ONE_MEGABYTE = 1024 * 1024;
        ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bmp);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Toast.makeText(getApplicationContext(), "No Such file or Path found!!", Toast.LENGTH_LONG).show();
            }
        });
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
    private String getExtension(Uri uri)                        // to get the image extension like jpg, png or jpeg, etc
    {
        ContentResolver cr=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }
    public void uploadImage()
    {
        //final StorageReference ref=storageRef.child("DisplayPicture"+getExtension(imageUri));
        final StorageReference ref=storageRef.child("DisplayPicture");
        ref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful())
                    Toast.makeText(EditProfile.this, "Image uploaded", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(EditProfile.this, "Image not uploaded. Try after sometime", Toast.LENGTH_SHORT).show();
            }
        })
                /*.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Uri[] downloadUrl = new Uri[1];
                        Toast.makeText(EditProfile.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUrl[0] =uri;
                            }
                        });
                        //Uri downloadUrl=taskSnapshot.getMetadata().getReference().getDownloadUrl();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(EditProfile.this, "Image not uploaded. Try after sometime", Toast.LENGTH_SHORT).show();
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
