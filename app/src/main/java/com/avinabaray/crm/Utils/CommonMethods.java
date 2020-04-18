package com.avinabaray.crm.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class CommonMethods {

    private ProgressDialog pd;

    public void createAlert(AlertDialog.Builder alertDialogBuilder, String msg) {
        alertDialogBuilder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void makeSnack(View rootLayout, String msg) {
        final Snackbar snackbar = Snackbar
                .make(rootLayout, msg, Snackbar.LENGTH_LONG);
        snackbar.show();
        snackbar.setAction("Dismiss", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        }).setActionTextColor(Color.parseColor("#FFFFFF"));
    }

    public void loadingDialogStart(Context context) {
        pd = new ProgressDialog(context);
        pd.setMessage("Please wait...");
        pd.show();
        pd.setCanceledOnTouchOutside(false);
    }

    /**
     * This is the docs for the user defined method by Avinaba Ray
     * @param context pass the Context or Activity reference
     * @param message pass the message to be displayed in the dialog
     */
    public void loadingDialogStart(Context context, String message) {
        pd = new ProgressDialog(context);
        pd.setMessage(message);
        pd.show();
        pd.setCanceledOnTouchOutside(false);
    }

    public void loadingDialogStop() {
        pd.dismiss();
    }

}
