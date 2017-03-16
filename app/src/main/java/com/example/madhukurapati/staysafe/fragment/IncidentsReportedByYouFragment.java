package com.example.madhukurapati.staysafe.fragment;

import android.widget.LinearLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by madhukurapati on 3/6/17.
 */

public class IncidentsReportedByYouFragment extends IncidentListFragment {

    public IncidentsReportedByYouFragment() {}

    @Override
    public Query getQuery(DatabaseReference databaseReference) {
        // All my posts
        return databaseReference.child("user-posts")
                .child(getUid());
    }
}
