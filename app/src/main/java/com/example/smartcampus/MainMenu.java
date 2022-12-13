package com.example.smartcampus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainMenu extends AppCompatActivity {

    Button signOut, qr, park;
    TextView textView, tv2;
    String name, k, l, m;
    Double lat1, lon1, lat, lon, lat2, lon2, dist=99.000000, d, late, lane;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference reference = database.getReference().child("parking").child("nmit");
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        signOut = findViewById(R.id.signout);
        qr = findViewById(R.id.qr);
        park = findViewById(R.id.parkme);
        textView = findViewById(R.id.tv);
        tv2 = findViewById(R.id.lon);

        Intent r = getIntent();
        String mail = r.getStringExtra("key_mail");
        //String n = r.getStringExtra("yes");
        try{
            //String mails = mail.replace("//.",",");
        }
        finally {
            //textView.setText(mail);
        }


        qr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainMenu.this,displayqr.class);
                i.putExtra("key_mail",mail);
                startActivity(i);
                finish();
            }
        });

        park.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        lat1 = 13.1369289;
                        lon1 = 77.5963159;
                        //Compute one with the lowest distance
                        for(int i=1; i<3; i++)
                        {
                            k=String.valueOf(i);
                            l = (String) snapshot.child(k).child("lat").getValue();
                            m = (String) snapshot.child(k).child("lon").getValue();
                            lat2 = Double.valueOf(l);
                            lon2 = Double.valueOf(m);
                            //textView.setText(String.valueOf(lat2));
                            late = lat2;
                            lane = lon2;
                            lon1 = Math.toRadians(lon1);
                            lon2 = Math.toRadians(lon2);
                            lat1 = Math.toRadians(lat1);
                            lat2 = Math.toRadians(lat2);

                            double dlon = lon2 - lon1;
                            double dlat = lat2 - lat1;
                            double a = Math.pow(Math.sin(dlat / 2), 2)
                                    + Math.cos(lat1) * Math.cos(lat2)
                                    * Math.pow(Math.sin(dlon / 2),2);

                            double c = 2 * Math.asin(Math.sqrt(a));
                            double r = 6371;
                            d = c*r;
                            if(d<=dist)
                            {
                                dist = d;
                                lat = late;
                                lon = lane;
                            }

                        }
                        textView.setText(String.valueOf(dist));
                        tv2.setText(String.valueOf(lat));
                        Uri gmmIntentUri = Uri.parse("google.navigation:q=lat,88.435534");
                        //String gmmIntentUri = "https://www.google.com/maps/dir/?api=1&destination=" + lat + "," + lon + "&travelmode=driving";
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);



                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent i = new Intent(MainMenu.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}