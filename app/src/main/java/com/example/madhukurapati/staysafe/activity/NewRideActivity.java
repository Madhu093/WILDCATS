package com.example.madhukurapati.staysafe.activity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.madhukurapati.staysafe.R;
import com.example.madhukurapati.staysafe.models.Config;
import com.example.madhukurapati.staysafe.models.LocationResponse;
import com.example.madhukurapati.staysafe.models.Ride;
import com.example.madhukurapati.staysafe.models.User;
import com.example.madhukurapati.staysafe.rest.ApiInterface;
import com.example.madhukurapati.staysafe.rest.LocationClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by madhukurapati on 11/13/17.
 */

public class NewRideActivity extends BaseActivity {

    private static final String TAG = "NewRideActivity";
    private static final String REQUIRED = "Required";

    private DatabaseReference mDatabase;

    private EditText mTitleField;
    private EditText mBodyField;
    private Button submitButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String location;

    private Double lat;
    private Double longitude;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_ride);

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mTitleField = (EditText) findViewById(R.id._ride_field_title);
        mBodyField = (EditText) findViewById(R.id.ride_field_body);
        submitButton = (Button) findViewById(R.id.ride_submit_button);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRide();
            }
        });

    }


    public void submitRide() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        //Location call starts
        ApiInterface locationClient =
                LocationClient.getClient().create(ApiInterface.class);

        Call<LocationResponse> call = locationClient.getLocation(Config.GOOGLE_API_KEY);
        Log.d(TAG, "submitRide: ");
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                try {
                    Log.d(TAG, "onResponse: + inside1");
                    LocationResponse lr = response.body();
                    lat = lr.getLocation().getLat();
                    longitude = lr.getLocation().getLng();

                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
                    Log.d(TAG, "onResponse: inside2");
                    try {
                        if (lat != null & longitude != null) {
                            List<Address> addresses = geoCoder.getFromLocation(lat, longitude, 1);

                            if (addresses.size() > 0) {
                                location = addresses.get(0).getLocality();

                                // [START single_value_read]
                                final String userId = getUid();
                                mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                                        new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // Get user value
                                                User user = dataSnapshot.getValue(User.class);

                                                // [START_EXCLUDE]
                                                if (user == null) {
                                                    // User is null, error out
                                                    Log.e(TAG, "User " + userId + " is unexpectedly null");
                                                    Toast.makeText(NewRideActivity.this,
                                                            "Error: could not fetch user.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Write new post
                                                    // Body is required
                                                    try {
                                                        String capTitle = capitalize(title);
                                                        writeNewRide(userId, user.username, capTitle, body, location);
                                                    }catch (Exception e) {
                                                        writeNewRide(userId, user.username, title, body, location);
                                                    }

                                                }

                                                // Finish this Activity, back to the stream
                                                setEditingEnabled(true);
                                                finish();
                                                // [END_EXCLUDE]
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                                // [START_EXCLUDE]
                                                setEditingEnabled(true);
                                                // [END_EXCLUDE]
                                            }
                                        });
                                // [END single_value_read]
                            } else {

                            }
                        } else {
                            System.out.println("Reverse geocoding failed on lat, long null values");
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(Call<LocationResponse> call, Throwable t) {
                System.out.println("response" + t);
            }
        });
    }

    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            submitButton.setVisibility(View.VISIBLE);
        } else {
            submitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewRide(String userId, String username, String title, String body, String location) {
        // Create new post at /user-rides/$userid/$rideid and at
        // /ride/$rideid simultaneously
        String key = mDatabase.child("rides").push().getKey();
        Ride ride = new Ride(userId, username, title, body, location);
        Map<String, Object> postValues = ride.toMapWithoutimageEncoded();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/rides/" + key, postValues);
        childUpdates.put("/user-rides/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    private String capitalize(String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


}
