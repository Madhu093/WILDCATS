package com.example.madhukurapati.staysafe.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.View;

/**
 * Created by madhukurapati on 3/8/17.
 */

public abstract class BaseDialogFragment extends AppCompatDialogFragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
