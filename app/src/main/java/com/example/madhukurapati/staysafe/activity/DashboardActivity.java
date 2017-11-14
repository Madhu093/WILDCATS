package com.example.madhukurapati.staysafe.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.madhukurapati.staysafe.R;

/**
 * Created by madhukurapati on 11/12/17.
 */

public class DashboardActivity extends MainActivity {

    Button dashPosts;
    Button dashRides;
    Button dashStudentActivities;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(
                R.layout.activity_dashboard
        );

       dashPosts = (Button)findViewById(R.id.dashboad_button_posts);
       dashRides = (Button)findViewById(R.id.dashboad_button_rides);
       dashStudentActivities = (Button)findViewById(R.id.dashboad_button_student_activities);


       dashPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPosts = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(launchPosts);

            }
        });

        dashRides.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPosts = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(launchPosts);

            }
        });

        dashStudentActivities.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchPosts = new Intent(DashboardActivity.this, MainActivity.class);
                startActivity(launchPosts);

            }
        });


    }

}
