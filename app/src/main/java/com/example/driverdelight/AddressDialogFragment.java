package com.example.driverdelight;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class AddressDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Get MainActivity as listener
        final OnDialogConfirmListener listener = (OnDialogConfirmListener) getActivity();

        // Create dialog view
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.input_address_fragment, null);

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setTitle(getString(R.string.dialog_title))
                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Send address back to MainActivity
                        EditText address = view.findViewById(R.id.address_input);
                        listener.onDialogConfirm(address.getText().toString());
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Do nothing
                    }
                });

        return builder.create();
    }

    // Listener interface for MainActivity
    interface OnDialogConfirmListener {
        void onDialogConfirm(String address);
    }
}
