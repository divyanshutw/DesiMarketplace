package com.example.desi_marketplace;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText editText_username,editText_password;
    TextView textView_forgot,textView_signup;
    Button button_signin;
    ImageButton imageButton_eye;int imageButton_eye_count=0;
    CheckBox checkBox_remember;
    String username="", password="",UserType="";;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    FirebaseAuth auth;
    FirebaseFirestore firestore;

    int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        preferences=getSharedPreferences("LOGIN_INFO",MODE_PRIVATE);
        editor=preferences.edit();

        auth=FirebaseAuth.getInstance();
        firestore=FirebaseFirestore.getInstance();

        editText_username=(EditText)findViewById(R.id.editText_username);
        editText_password=(EditText)findViewById(R.id.editText_password);
        textView_forgot=(TextView)findViewById(R.id.textView_forgot);
        textView_signup=(TextView)findViewById(R.id.textView_signup);
        button_signin=(Button)findViewById(R.id.button_signin);
        checkBox_remember=findViewById(R.id.checkBox_remember);
//startActivity(new Intent(this,MapsActivity.class));


        if(preferences.contains("email") && preferences.contains("password"))
        {
            editText_username.setText(preferences.getString("email",""));
            editText_password.setText(preferences.getString("password",""));
            checkBox_remember.setChecked(true);
        }



        if(preferences.contains("LoggedIn") && preferences.getBoolean("LoggedIn",false)==true) {
            startActivity(new Intent(MainActivity.this, HomePage.class));
            finish();
        }
    }

    public void onClickEye(View v)
    {
        if(imageButton_eye_count%2==0)
        {imageButton_eye_count++;editText_password.setTransformationMethod(null);}
        else
        {imageButton_eye_count++;editText_password.setTransformationMethod(new PasswordTransformationMethod());}
    }
    public void onClickSignin(View v)
    {
        username=editText_username.getText().toString();
        password=editText_password.getText().toString();
        if(username.isEmpty() || password.isEmpty())
            Toast.makeText(this, "Wrong login details", Toast.LENGTH_SHORT).show();
        else
        login(username,password);
    }
    private void login(String email, String password)
    {

        auth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();

                editor.putBoolean("LoggedIn",true);
                editor.putString("Uid",auth.getCurrentUser().getUid());
                DocumentReference doc= firestore.collection("Users").document("UserType");
                doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            DocumentSnapshot snapshot=task.getResult();
                            if(snapshot.exists()) {
                                UserType = task.getResult().getString(auth.getCurrentUser().getUid());
                                editor.putString("UserType",UserType);
                                editor.apply();
                                editor.commit();
                            }
                            else
                                Toast.makeText(MainActivity.this, "Unable to fetch from database", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MainActivity.this, "Unable to link to database", Toast.LENGTH_SHORT).show();
                    }
                });
                flag=1;
                editor.apply();
                editor.commit();
                startActivity(new Intent(MainActivity.this,HomePage.class));
                finish();
                return;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "Wrong login details", Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void onClickForgot(View v)
    {
        username=editText_username.getText().toString();
        if(TextUtils.isEmpty(username))
            Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show();
        else
            forgotPassword(username);
    }
    public void forgotPassword(String email)
    {
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(MainActivity.this, "Check your email to get instructions to reset your password", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Unable to send reset email", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void onClickSignup(View v)
    {
        Intent goToSignup=new Intent();
        goToSignup.setClass(this,Signup.class);
        startActivity(goToSignup);
        //finish();
    }
    public void onClickRemember(View v)     //Check it once again
    {
        username=editText_username.getText().toString();
        password=editText_password.getText().toString();
        boolean checked=checkBox_remember.isChecked();
        if(checked==true) {
            if(username.isEmpty() || password.isEmpty())
            {
                Toast.makeText(this, "Empty credentials", Toast.LENGTH_SHORT).show();
                checkBox_remember.setChecked(false);
            }
            else {
                auth.signInWithEmailAndPassword(username, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        editor.putString("email", username);
                        editor.putString("password", password);
                        editor.commit();
                        return;
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        checkBox_remember.setChecked(false);
                        Toast.makeText(MainActivity.this, "Wrong login details", Toast.LENGTH_SHORT).show();
                    }
                });
            /*if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
                Toast.makeText(this, "Invalid details", Toast.LENGTH_SHORT).show();
            else {
                editor.putString("email", username);
                editor.putString("password", password);
                editor.commit();
            }*/
            }
        }
        else
        {
            editor.remove("email");
            editor.remove("password");
            editor.commit();
        }
    }
}
