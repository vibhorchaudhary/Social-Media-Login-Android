package com.vibhor.socialogin.facebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by vibhor on 13, January, 2020
 */

public class FacebookLoginHelper {

    private Collection<String> permissions = Arrays.asList("public_profile", "email", "user_birthday", "user_friends");

    private CallbackManager callbackManager;

    private LoginManager loginManager;

    private Activity activity;

    private Fragment fragment;

    private OnFacebookSignInListener facebookSignInListener;

    public FacebookLoginHelper() {

    }

    public FacebookLoginHelper(Collection<String> permissions) {
        this.permissions = permissions;
    }

    public void initialize(Activity activity, OnFacebookSignInListener facebookSignInListener) {
        this.activity = activity;
        this.facebookSignInListener = facebookSignInListener;
    }

    public void initialize(Fragment fragment, OnFacebookSignInListener fbSignInListener) {
        this.fragment = fragment;
        this.facebookSignInListener = fbSignInListener;
    }

    public void loginFacebook() {
        callbackManager = CallbackManager.Factory.create();
        loginManager = LoginManager.getInstance();
        if (activity != null)
            loginManager.logInWithReadPermissions(activity, permissions);
        else
            loginManager.logInWithReadPermissions(fragment, permissions);
        loginManager.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        if (loginResult != null) {
                            callGraphAPI(loginResult.getAccessToken());
                        }
                    }

                    @Override
                    public void onCancel() {
                        facebookSignInListener.OnFacebookSignInError("User cancelled.");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                        facebookSignInListener.OnFacebookSignInError(exception.getMessage());
                    }
                });

    }

    public void logoutFacebook() {
        loginManager.logOut();
    }

    public void disconnectAppFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    private void callGraphAPI(AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        facebookSignInListener.OnFacebookSignInSuccess(response);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, gender, birthday, location");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }


}
