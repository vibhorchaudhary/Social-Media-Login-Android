package com.vibhor.socialogin.facebook;

import com.facebook.GraphResponse;

/**
 * Created by vibhor on 15, January, 2020
 */

public interface OnFacebookSignInListener {

    void OnFacebookSignInSuccess(GraphResponse graphResponse);

    void OnFacebookSignInError(String errorMessage);
}