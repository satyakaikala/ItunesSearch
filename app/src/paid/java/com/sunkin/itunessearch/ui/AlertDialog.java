package com.sunkin.itunessearch.ui;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.sunkin.itunessearch.R;

/**
 * Created by kaika on 5/26/2017.
 */

public class AlertDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.fav_found_msg);
        builder.setPositiveButton(R.string.dialog_button_show, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                foundFavs();
            }
        });
        builder.setNegativeButton(R.string.dialog_button_cancel, null);

        Dialog dialog = builder.create();

        Window window = dialog.getWindow();

        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
        return dialog;
    }

    private void foundFavs() {
        Activity parent = getActivity();
        if (parent instanceof MainActivity) {
            ((MainActivity) parent).foundFavs();
        }
        dismiss();
    }
}
