package com.example.madhukurapati.staysafe.fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by madhukurapati on 3/6/17.
 */

public class IncidentsCloseToYouFragment extends IncidentListFragment {

    public IncidentsCloseToYouFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }
}
