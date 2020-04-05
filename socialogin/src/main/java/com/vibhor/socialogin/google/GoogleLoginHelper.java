package com.vibhor.socialogin.google;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Created by vibhor on 13, January, 2020
 */

public class GoogleLoginHelper {

    private final static int GOOGLE_SIGN_IN_RESPONSE_CODE = 1001;

    private static GoogleSignInOptions googleSignInOptions;

    private GoogleApiClient googleApiClient;

    private OnGoogleSignInListener googleLoginResultCallBack;

    private ResultCallback<Status> googleLogoutResultCallback;

    public GoogleLoginHelper() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
    }


    public void initialize(FragmentActivity activity, OnGoogleSignInListener onGoogleSignInListener) {
        googleLoginResultCallBack = onGoogleSignInListener;
        googleApiClient = new GoogleApiClient.Builder(activity)
                .enableAutoManage(activity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.e("onConnectionFailed: ", connectionResult.toString());
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        googleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i("onConnected", "onConnected");
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.i("onConnectionSuspended", "onConnectionSuspended");
            }
        });

        googleApiClient.registerConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Log.i("onConnectionFailed", "onConnectionFailed");
            }
        });
    }

    public boolean isConnected() {
        boolean isConnected = googleApiClient.isConnected();
        Log.i("isConnected()", isConnected + "");
        return isConnected;
    }

    public void signIn(Activity activity) {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        activity.startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RESPONSE_CODE);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (googleLogoutResultCallback != null) {
                            googleLogoutResultCallback.onResult(status);
                        }
                    }
                });
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("handleSignInResult:", result.isSuccess() + "");
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();
            if (googleLoginResultCallBack != null) {
                googleLoginResultCallBack.OnGoogleSignInSuccess(acct);
            }
        } else {
            if (googleLoginResultCallBack != null) {
                googleLoginResultCallBack.OnGoogleSignInError(result);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GOOGLE_SIGN_IN_RESPONSE_CODE) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void setLogoutResultCallback(ResultCallback<Status> callback) {
        googleLogoutResultCallback = callback;
    }

}