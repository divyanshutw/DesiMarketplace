package com.example.desi_marketplace;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SearchChoice extends AppCompatActivity {

    Spinner spinner;
    ListView listView;
    Button buttonRating,buttonLocation;
    TextView textView_type;
    CheckBox checkBox_delivery,checkBox_payment;

    String type="",sellerType="Manufacturer",searchBasis;
    boolean delivery,payment;

    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);

        setContentView(R.layout.activity_search_choice);
        getSupportActionBar().hide();

        spinner=findViewById(R.id.spinner);
        listView=findViewById(R.id.listView);
        buttonLocation=findViewById(R.id.button_location);
        buttonRating=findViewById(R.id.button_rating);
        textView_type=findViewById(R.id.textView_type);
        checkBox_delivery=findViewById(R.id.checkBox_delivery);
        checkBox_payment=findViewById(R.id.checkBox_payment);

        firestore=FirebaseFirestore.getInstance();

        String s[]=new String[]{"Manufacturer","Retailer"};
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.row,s);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);

        String list1[]={"Packaging","FoodAndSnacks","Bakery","OtherEatables","Textile","Autoparts"};
        final ArrayAdapter<String> adapter1=new ArrayAdapter<>(this,R.layout.row,list1);
        String list2[]={"GeneralStore","Garments","BeautyProducts","Footwear","WatchStore","MedicalStore"};
        final ArrayAdapter<String> adapter2=new ArrayAdapter<>(this,R.layout.row,list2);
        listView.setAdapter(adapter1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            listView.setNestedScrollingEnabled(true);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                type=adapterView.getItemAtPosition(i).toString();
                textView_type.setText(type);
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0)
                {
                    listView.setAdapter(adapter1);
                    sellerType="Manufacturer";
                }
                else
                {
                    listView.setAdapter(adapter2);
                    sellerType="Retailer";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SearchChoice.this, "Select Manufacturer or Retailer", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void onClickLocation(View v)
    {
        if(type.length()==0)
            Toast.makeText(this, "Select the type", Toast.LENGTH_SHORT).show();
        else {
            searchBasis = "Location";
            delivery=checkBox_delivery.isChecked();
            payment=checkBox_payment.isChecked();
            Intent intent = new Intent(SearchChoice.this, SearchResult.class);
            intent.putExtra("Type", type);
            intent.putExtra("SellerType", sellerType);
            intent.putExtra("Basis", searchBasis);
            intent.putExtra("Delivery",delivery);
            intent.putExtra("Payment",payment);
            startActivity(intent);
        }
    }
    public void onClickRating(View v)
    {

        if(type.length()==0)
            Toast.makeText(this, "Select the type", Toast.LENGTH_SHORT).show();
        else {
            searchBasis = "Rating";
            delivery=checkBox_delivery.isChecked();
            payment=checkBox_payment.isChecked();
            Intent intent = new Intent(SearchChoice.this, SearchResult.class);
            intent.putExtra("Type", type);
            intent.putExtra("SellerType", sellerType);
            intent.putExtra("Basis", searchBasis);
            intent.putExtra("Delivery",delivery);
            intent.putExtra("Payment",payment);
            startActivity(intent);
        }
    }
}