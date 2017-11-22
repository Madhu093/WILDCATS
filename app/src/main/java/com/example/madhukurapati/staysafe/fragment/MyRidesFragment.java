package com.example.madhukurapati.staysafe.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by madhukurapati on 11/21/17.
 */

public class MyRidesFragment extends RidesListFragment {

    public MyRidesFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-rides")
                .child(getUid());
    }
}

