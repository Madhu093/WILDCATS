package com.example.madhukurapati.staysafe.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madhukurapati.staysafe.R;
import com.example.madhukurapati.staysafe.fragment.AllPostedPostsFragment;
import com.example.madhukurapati.staysafe.fragment.MyPostsFragment;
import com.example.madhukurapati.staysafe.fragment.NeedRidesFragment;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

public class MainActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_INVITE = 500;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1500;
    private FirebaseAuth mFirebaseAuth;
    private String TAG = "MainActivity";
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAnalytics mFirebaseAnalytics;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MenuItem mAuthenticateMenuItem, mDonate;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private ImageView mHeaderLogo;
    private TextView mHeaderTitle;
    private TextView mHeaderSubTitle;
    private String mPhotoURL = "";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialize();
        setPager();
        isStoragePermissionGrant();

        FacebookSdk.sdkInitialize(getApplicationContext());
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        if (mFirebaseUser == null) {
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            return;
        }

    }

    public boolean isStoragePermissionGrant() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else { //permission is automatically granted on sdk<23 upon installation
            Log.v(TAG, "Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            //resume tasks needing this permission
        }
    }


    private void setPager() {
        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[]{new NeedRidesFragment(),
                    new AllPostedPostsFragment(),
                    new MyPostsFragment(),
            };
            private final String[] mFragmentNames = new String[]{getString(R.string.Rides),
                    getString(R.string.all_posts),
                    getString(R.string.my_posts)
            };

            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }

            @Override
            public int getCount() {
                return mFragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        populateNavigationView();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initialize() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(R.string.app_name);
                int size = navigationView.getMenu().size();
                for (int i = 0; i < size; i++) {
                    navigationView.getMenu().getItem(i).setChecked(false);
                }
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(R.string.profile);
                supportInvalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View hView = navigationView.getHeaderView(0);
        hView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START);
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));

            }
        });

        Menu menu = navigationView.getMenu();
        mAuthenticateMenuItem = menu.findItem(R.id.logout);

        mHeaderLogo = (ImageView) hView.findViewById(R.id.profile_logo);
        mHeaderTitle = (TextView) hView.findViewById(R.id.profile_name);
        mHeaderSubTitle = (TextView) hView.findViewById(R.id.profile_email);

    }

    private void populateNavigationView() {
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
                        userName = mFirebaseUser.getDisplayName();
                        emailId = mFirebaseUser.getEmail();
                    } else {
                        mPhotoURL = "www.google.com/image/1";
                        userName = usernameFromEmail(mFirebaseUser.getEmail());
                        emailId = mFirebaseUser.getEmail();
                    }
                }
                Picasso.with(getApplicationContext()).load(mPhotoURL).into(mHeaderLogo);
                mHeaderTitle.setText(userName);
                mHeaderSubTitle.setText(emailId);
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


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_story:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent launchMainActivity = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(launchMainActivity);
                return true;
            case R.id.add_ride:
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent launchNewRide = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(launchNewRide);
                return true;
            case R.id.referToAFriend:
                share();
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.settings:
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.app_name);
                builder.setMessage(R.string.confirm_logout);
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            navigationView.getMenu().findItem(R.id.logout).setChecked(false);
                            AsyncTask.execute(new Runnable() {
                                                  @Override
                                                  public void run() {
                                                      mFirebaseAuth.signOut();
                                                      LoginManager.getInstance().logOut();
                                                      Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                                  }
                                              }
                            );
                        } catch (Exception e) {

                        }
                        mFirebaseUser = null;
                        Intent launchSingInActivity = new Intent(MainActivity.this, MainActivity.class);
                        launchSingInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        launchSingInActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(launchSingInActivity);
                    }
                });
                builder.setNegativeButton(android.R.string.no, null);
                builder.show();
                return true;
        }

        return true;
    }

    private void share() {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Download the app to stay in the race ";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "WILD CATS");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), REQUEST_INVITE);
        sendToFirebaseAnalytics();
    }

    private void sendToFirebaseAnalytics() {
        Bundle payload = new Bundle();
        payload.putString(FirebaseAnalytics.Param.VALUE, "sent");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,
                payload);
    }
}
