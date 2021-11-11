package com.example.weather12;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText et;
    TextView tv;
    TextView tempp;
    ImageView imageback;
    ImageView imageView;
    ImageView changeimage;
    String url="api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}";
    String apikey="3f4df31d5354e77ac3b66a0778e111e0";
    ImageView rImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView;
        et = findViewById(R.id.et);
        tv = findViewById(R.id.tv);
        tempp = findViewById(R.id.tempp);
        imageback = findViewById(R.id.imageback);
        changeimage = findViewById(R.id.changeimage);

        // getting ImageView by its id
        rImage = findViewById(R.id.rImage);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new fragment1()).commit();
        //navv
        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                Fragment fragment = null;

                switch (item.getItemId()){
                    case R.id.blankFragment1:
                        fragment= new fragment1();

                        break;
                    case R.id.blankFragment2:
                        fragment= new fragment2();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                return true;
            }
        });



        // we will get the default FirebaseDatabase instance
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance("https://weather12-e1f10-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
        // Here "image" is the child node value we are getting
        // child node data in the getImage variable
        DatabaseReference getImage = databaseReference.child("image");

        getImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // getting a DataSnapshot for the location at the specified
                // relative path and getting in the link variable
                String link = dataSnapshot.getValue(String.class);

                // loading that data into rImage
                // variable which is ImageView
               Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/weather12-e1f10.appspot.com/o/pexels-btgl-3894157.jpg?alt=media&token=d1e1a742-dade-4d9e-852e-1d4681eb7890").into(rImage);
            }

            // this will called when any problem
            // occurs in getting data
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // we are showing that error message in toast
                Toast.makeText(MainActivity.this, "Error Loading Image", Toast.LENGTH_SHORT).show();
            }
        });




    }
    public void getweather(View view){

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherapi myapi=retrofit.create(weatherapi.class);
        Call<Example> examplecall = myapi.getweather(et.getText().toString().trim(),apikey);
        examplecall.enqueue(new Callback<Example>() {

            @Override

            public void onResponse(Call<Example> call, Response<Example> response) {
                if(response.code()==404){

                    Toast.makeText(MainActivity.this,"enter valid city",Toast.LENGTH_LONG).show();

                }
                else if (!(response.isSuccessful())){

                    Toast.makeText(MainActivity.this,response.code(),Toast.LENGTH_LONG).show();

                }
                Example mydata = response.body();
                Main main = mydata.getMain();
                Double temp =main.getTemp();
                Integer temperature= (int)(temp-273.15);
                tv.setText(String.valueOf(temperature)+"C");

                if(temperature > 25 ){
                    tempp.setText("Is hot isnt it.");
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/weather12-e1f10.appspot.com/o/pexels-travis-rupert-1032650.jpg?alt=media&token=4e1e4354-1d7b-444e-829b-6110f3b9a7d0").into(changeimage);
                }else if (temperature > 10 && temperature <25){
                    tempp.setText("I think this is the perfect temperature. ");
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/weather12-e1f10.appspot.com/o/pexels-anfisa-eremina-2183863.jpg?alt=media&token=8a8ad70b-d928-4285-8952-3cb327b7f7af").into(changeimage);
                }else if (temperature > 0 && temperature <10){
                    tempp.setText("Its getting colder. ");
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/weather12-e1f10.appspot.com/o/pexels-artem-saranin-1477199.jpg?alt=media&token=1aa9e7cb-d2d3-4425-912f-ebe3b7810698").into(changeimage);
                }
                else{
                    tempp.setText("Im freeeeezing.");
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/weather12-e1f10.appspot.com/o/pexels-luca-chiandoni-3375674.jpg?alt=media&token=0239d26a-4bf6-40da-9f88-0663dbd975f3").into(changeimage);
                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {

                Toast.makeText(MainActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();

            }
        });

    }
}
