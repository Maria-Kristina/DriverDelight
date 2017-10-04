package com.example.driverdelight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Edward on 01/10/2017.
 */

public class AddressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final OnDialogConfirmListener listener = (OnDialogConfirmListener) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.input_address_fragment, null);

        builder.setView(view)
                .setTitle(getString(R.string.dialog_title))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Confirm
                        EditText address = view.findViewById(R.id.address_input);
                        listener.onDialogConfirm(address.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Cancel
                    }
                });

        return builder.create();
    }

    interface OnDialogConfirmListener {
        void onDialogConfirm(String address);
    }
}
