package com.brigade.rockit.database;

import android.content.Context;
import android.widget.Toast;

import com.brigade.rockit.R;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ExceptionManager {
    public static void showError(Exception e, Context context) {
        if (e instanceof FirebaseNetworkException) {
            Toast.makeText(context, context.getString(R.string.network_error),
                    Toast.LENGTH_LONG).show();
        } else if (e instanceof FirebaseAuthInvalidCredentialsException) {
            Toast.makeText(context, context.getString(R.string.invalid_password),
                    Toast.LENGTH_LONG).show();
        } else if (e instanceof FirebaseAuthInvalidUserException)
            Toast.makeText(context, context.getString(R.string.account_not_found),
                    Toast.LENGTH_LONG).show();
        else {
            Toast.makeText(context, context.getString(R.string.unexpected_error),
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }
}
