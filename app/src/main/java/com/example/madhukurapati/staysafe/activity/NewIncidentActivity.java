package com.example.madhukurapati.staysafe.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.madhukurapati.staysafe.R;
import com.example.madhukurapati.staysafe.fragment.ChooserDialogFragment;
import com.example.madhukurapati.staysafe.models.Config;
import com.example.madhukurapati.staysafe.models.LocationResponse;
import com.example.madhukurapati.staysafe.models.Post;
import com.example.madhukurapati.staysafe.models.User;
import com.example.madhukurapati.staysafe.rest.ApiInterface;
import com.example.madhukurapati.staysafe.rest.LocationClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by madhukurapati on 3/7/17.
 */

public class NewIncidentActivity extends BaseActivity {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";
    private static final int REQUEST_IMAGE_SELECT = 111;

    private DatabaseReference mDatabase;

    private EditText mTitleField;
    private EditText mBodyField;
    private ImageView addImage;
    private TextView imageLabel;
    private Button submitButton;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private String imageEncoded;
    private String profileImageEncoded;
    private String profilePhoto = "";
    private String location;

    private Double lat;
    private Double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_incident);
        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mTitleField = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        addImage = (ImageView) findViewById(R.id.uploadImage);
        imageLabel = (TextView) findViewById(R.id.imageLabel);
        submitButton = (Button) findViewById(R.id.submit_button);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();
            }
        });

        getProfileImageAndStoreToFirebase();

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImage();
            }
        });

    }


    private void getProfileImageAndStoreToFirebase() {
        String facebookUserId = "";
        for (UserInfo profile : mFirebaseUser.getProviderData()) {
            if (profile.getProviderId().equals(getString(R.string.facebook_provider_id))) {
                profilePhoto = "https://graph.facebook.com/" + facebookUserId + "/picture?type=large";
            } else {
                if (mFirebaseUser.getPhotoUrl() != null) {
                    profilePhoto = mFirebaseUser.getPhotoUrl().toString();
                } else {
                    //If user logs in using signUp within app
                }
            }
        }
        try {
            URL url = new URL(profilePhoto);
            Bitmap image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            encodeProfileBitmapAndSaveToFirebase(image);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void encodeProfileBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos2);
        profileImageEncoded = Base64.encodeToString(baos2.toByteArray(), Base64.DEFAULT);
    }


    private void addImage() {
        ChooserDialogFragment fragment = new ChooserDialogFragment();
        fragment.show(getSupportFragmentManager(), "ChooseCameraGallery");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    imageLabel.setText("Image Captured");
                    encodeBitmapAndSaveToFirebase(imageBitmap);
                }

                break;
            case 101:
                if (resultCode == RESULT_OK
                        && null != data) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();
                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filePath);
                    imageLabel.setText("Image Selected From Storage");
                    encodeBitmapAndSaveToFirebase(yourSelectedImage);
                } else {
                    Toast.makeText(this, "You haven't picked Image",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
    }

    private void submitPost() {
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
        call.enqueue(new Callback<LocationResponse>() {
            @Override
            public void onResponse(Call<LocationResponse> call, Response<LocationResponse> response) {
                try {
                    LocationResponse lr = response.body();
                    lat = lr.getLocation().getLat();
                    longitude = lr.getLocation().getLng();

                    Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
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
                                                    Toast.makeText(NewIncidentActivity.this,
                                                            "Error: could not fetch user.",
                                                            Toast.LENGTH_SHORT).show();
                                                } else {
                                                    // Write new post
                                                    // Body is required
                                                    if (TextUtils.isEmpty(imageEncoded)) {
                                                        writeNewPost(userId, user.username, title, body, profileImageEncoded, location);
                                                    } else {
                                                        writeNewPostWithImage(userId, user.username, title, body, imageEncoded, profileImageEncoded, location);
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
    private void writeNewPost(String userId, String username, String title, String body, String profileImageEncoded, String location) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body, profileImageEncoded, location);
        Map<String, Object> postValues = post.toMapWithoutimageEncoded();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

    // [START write_fan_out]
    private void writeNewPostWithImage(String userId, String username, String title, String
            body, String image, String profileImageEncoded, String location) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body, image, profileImageEncoded, location);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

}
