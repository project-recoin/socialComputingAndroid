package io.krumbs.sdk.starter;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Ramine on 21/04/2016.
 */
public class ValidateQuestionDialogBox  extends DialogFragment {

        public String resource;
        public boolean goValidate = false;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Valide Resource?")
                    .setPositiveButton("Let\'s Go!", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                           goValidate = true;
                        }
                    })
                    .setNegativeButton("I\'ll pass", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }


    public boolean isGoValidate() {
        return goValidate;
    }
}
