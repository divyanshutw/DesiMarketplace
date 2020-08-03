package com.example.desi_marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class AdsActivity extends AppCompatActivity {

    EditText editText_subject,editText_body;
    ImageButton imageButton_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_ads);
        getSupportActionBar().hide();

        editText_body=findViewById(R.id.editText_body);
        editText_subject=findViewById(R.id.editText_subject);
        imageButton_email=findViewById(R.id.imageButton_email);
    }
    public void onClickEmail(View v)
    {
        String subject=editText_subject.getText().toString(),body=editText_body.getText().toString();
        Intent emailIntent=new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL,new String[]{"pproject116@gmail.com"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT,subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT,body);
        emailIntent.setType("message/rfc822");
        startActivity(Intent.createChooser(emailIntent,"Choose an email client:"));
    }
}