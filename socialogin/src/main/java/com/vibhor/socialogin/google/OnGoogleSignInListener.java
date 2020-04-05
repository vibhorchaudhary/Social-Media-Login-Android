package com.vibhor.socialogin.google;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * Created by vibhor on 15, January, 2020
 */

public interface OnGoogleSignInListener {

    void OnGoogleSignInSuccess(GoogleSignInAccount googleSignInAccount);

    void OnGoogleSignInError(GoogleSignInResult errorMessage);
}