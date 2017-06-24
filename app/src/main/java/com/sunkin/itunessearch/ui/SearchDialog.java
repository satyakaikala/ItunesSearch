package com.sunkin.itunessearch.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.sunkin.itunessearch.R;
import com.sunkin.itunessearch.Utility;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sunkin.itunessearch.ui.MainActivity.SEARCH_TEXT_FAB;

/**
 * Created by kaika on 5/26/2017.
 */

public class SearchDialog extends DialogFragment {

    @BindView(R.id.search_text)
    EditText searchTextView;
    @BindView(R.id.entity_spinner)
    Spinner entitySpinner;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        @SuppressLint("InflateParms")View customView = inflater.inflate(R.layout.search_advanced, null);

        ButterKnife.bind(this, customView);
        setupSpinner();
        Bundle intent = getArguments();
        String searchQuery = intent.getString(SEARCH_TEXT_FAB);

        searchTextView.setText(searchQuery);
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                saveSearchKeyword();
                return true;
            }
        });

        builder.setView(customView);
        builder.setMessage(R.string.dialog_title);
        builder.setPositiveButton(R.string.dialog_button_search, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveSearchKeyword();
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.entity_selection_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        entitySpinner.setAdapter(adapter);

    }

    private void saveSearchKeyword() {
        Activity parent = getActivity();
        if (parent instanceof MainActivity) {
            Utility.saveSearchKeyword(getActivity(),searchTextView.getText().toString());
            Utility.saveSearchEntity(getActivity(), entitySpinner.getSelectedItem().toString());
            ((MainActivity) parent).doSearch();
        }
        dismiss();
    }
}
