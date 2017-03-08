package com.example.madhukurapati.staysafe.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madhukurapati.staysafe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

/**
 * Created by kurapma on 1/13/17.
 */

public class ProfileActivity extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private TextView mUsername;
    private TextView mUserEmail;
    private ImageView mPhoto;
    private String mPhotoURL = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mUsername = (TextView) findViewById(R.id.tvProfileName);
        mUserEmail = (TextView) findViewById(R.id.tvEmail);
        mPhoto = (ImageView) findViewById(R.id.mPhoto);

        if (mFirebaseUser == null) {

        } else {
            String facebookUserId = "";
            String userName = "";
            String emailId = "";
            for (UserInfo profile : mFirebaseUser.getProviderData()) {
                if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                    facebookUserId = profile.getUid();
                    userName = profile.getDisplayName();
                    emailId = profile.getEmail();
                    mPhotoURL = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
                } else {
                    if (mFirebaseUser.getPhotoUrl() != null) {
                        mPhotoURL = mFirebaseUser.getPhotoUrl().toString();
                        mPhotoURL = mPhotoURL.replace("/s96-c/", "/s300-c/");
                        userName = mFirebaseUser.getDisplayName();
                        emailId = mFirebaseUser.getEmail();
                    } else {
                        mPhotoURL = "www.google.com/image/1";
                        userName = usernameFromEmail(mFirebaseUser.getEmail());
                        emailId = mFirebaseUser.getEmail();
                    }
                }
                Picasso.with(getApplicationContext()).load(mPhotoURL).into(mPhoto);
                mUsername.setText(userName);
                mUserEmail.setText(emailId);
            }
        }
    }

    private String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }
}
