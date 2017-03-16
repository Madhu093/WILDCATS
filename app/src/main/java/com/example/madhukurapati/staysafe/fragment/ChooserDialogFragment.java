package com.example.madhukurapati.staysafe.fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

import com.example.madhukurapati.staysafe.R;

/**
 * Created by madhukurapati on 3/8/17.
 */

public class ChooserDialogFragment extends BaseDialogFragment {


    private static final int REQUEST_PIC_CAPTURE = 100;
    private static final int REQUEST_PIC_SELECT = 101;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.select_or_take_picture))
                .setItems(R.array.chooser_options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent;
                        if (which == 0) {
                            intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            getActivity().startActivityForResult(intent, REQUEST_PIC_CAPTURE);
                        } else {
                            if (Build.VERSION.SDK_INT >= 23) {
                                if ((getActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        == PackageManager.PERMISSION_GRANTED)) {
                                    startGallery();
                                } else {
                                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }
                            } else {
                                startGallery();
                            }
                        }
                    }
                })
                .create();
    }

    private void startGallery() {
        Intent intent;
        intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getActivity().startActivityForResult(intent, REQUEST_PIC_SELECT);
    }
}
