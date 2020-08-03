package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class Signup extends AppCompatActivity {

    Spinner spinner;
    ImageButton eye1,eye2,imageButton_capture;
    EditText editText_firstname,editText_lastname,editText_email,editText_password,editText_confirmPassword;
    Button button_next;

    String accountType,firstname,lastname,email,password;
    int imageButton_eye_count1=0,imageButton_eye_count2=0;
    Boolean flag_spinner=false;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;


    SharedPreferences preferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        spinner=findViewById(R.id.spinner_users);
        eye1=findViewById(R.id.imageButton_eye);
        eye2=findViewById(R.id.imageButton_eye2);
        button_next=findViewById(R.id.button_next);
        editText_firstname=findViewById(R.id.editText_firstname);
        editText_lastname=findViewById(R.id.editText_lastname);
        editText_email=findViewById(R.id.editText_email);
        editText_password=findViewById(R.id.editText_password);
        editText_confirmPassword=findViewById(R.id.editText_confirm);


        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();


        String s[]=new String[]{"Manufacturer","Retailer","Consumer"};

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.row,s);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String user = adapterView.getItemAtPosition(i).toString();
                accountType= user;
                flag_spinner=true;
                //Toast.makeText(adapterView.getContext(),accountType, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {


            }
        });


    }

    public void onClickEye1(View v)
    {
        if(imageButton_eye_count1%2==0)
        {imageButton_eye_count1++;editText_password.setTransformationMethod(null);}
        else
        {imageButton_eye_count1++;editText_password.setTransformationMethod(new PasswordTransformationMethod());}
    }
    public void onClickEye2(View v)
    {
        if(imageButton_eye_count2%2==0)
        {imageButton_eye_count2++;editText_confirmPassword.setTransformationMethod(null);}
        else
        {imageButton_eye_count2++;editText_confirmPassword.setTransformationMethod(new PasswordTransformationMethod());}
    }
    public void onClickNext(View v)
    {
        firstname=editText_firstname.getText().toString();
        lastname=editText_lastname.getText().toString();
        email=editText_email.getText().toString();
        password=editText_password.getText().toString();
        String confirm=editText_confirmPassword.getText().toString();

        if(!flag_spinner)
            Toast.makeText(this, "Select your account type", Toast.LENGTH_SHORT).show();

        else if( firstname.isEmpty() || lastname.isEmpty()
                || email.isEmpty() || password.isEmpty()
                || confirm.isEmpty())
        {
            Toast.makeText(getApplicationContext(), "Please fill all the details!", Toast.LENGTH_SHORT).show();
        }
        else if (!(password.equals(confirm))) {
            Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
        }
        else if (!(email.contains("@")) || !(email.contains(".com")))
        {
            Toast.makeText(getApplicationContext(), "Invalid Email!", Toast.LENGTH_SHORT).show();
        }

        else if( !(firstname.matches("^[a-zA-Z]*"))
                || firstname.equals(" ")
                ||!(lastname.matches("^[a-zA-Z]*"))
                || lastname.equals(" ")

        )
        {
            Toast.makeText(getApplicationContext(), "Name can only contains alphabets", Toast.LENGTH_SHORT).show();
        }
        else
        {
            registerUser(email,password);
            HashMap<String,Object> map=new HashMap<>();
            map.put("firstname",firstname);
            map.put("lastname",lastname);
            map.put("email",email);
            editor.putString("Firstname",firstname);
            editor.putString("Lastname",lastname);
            editor.putString("email",email);
            editor.putString("UserType",accountType);
            editor.commit();
            if(accountType=="Manufacturer")
            {
                /*firestore.collection("Manufacturer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        Log.d("Document saving","saved");
                        else
                            Log.d("Document saving","failed");
                    }
                });*/
                //editor.putString("UserType","Manufacturer");
                //editor.commit();
                Intent goToManu=new Intent(Signup.this,RegisterManuActivity.class);
                goToManu.putExtra("Firstname",firstname);
                goToManu.putExtra("Lastname",lastname);
                goToManu.putExtra("email",email);
                startActivity(goToManu);
            }
            else if(accountType=="Retailer")
            {
                /*firestore.collection("Retailer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Log.d("Document saving","saved");
                        else
                            Log.d("Document saving","failed");
                    }
                });*/
                //editor.putString("UserType","Retailer");
                //editor.commit();
                Intent goToRetail=new Intent(Signup.this,RegisterRetailActivity.class);
                goToRetail.putExtra("Firstname",firstname);
                goToRetail.putExtra("Lastname",lastname);
                goToRetail.putExtra("email",email);
                startActivity(goToRetail);
            }
            else
            {
                /*firestore.collection("Consumer").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Log.d("Document saving","saved");
                        else
                            Log.d("Document saving","failed");
                    }
                });*/
                //editor.putString("UserType","Consumer");
                //editor.commit();
                Intent goToConsu=new Intent(Signup.this,RegisterConsuActivity.class);
                goToConsu.putExtra("Firstname",firstname);
                goToConsu.putExtra("Lastname",lastname);
                goToConsu.putExtra("email",email);
                startActivity(goToConsu);
            }

            finish();
        }
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Signup.this, "Registration successful", Toast.LENGTH_SHORT).show();
                            editor.putBoolean("LoggedIn",true);
                            editor.commit();
                        }
                        else
                            Toast.makeText(Signup.this, "Registration failed", Toast.LENGTH_SHORT).show();

                    }
                }
        );
    }


}