package com.crazymike.login;

import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONObject;

public interface LoginContract {

    interface View{

        void onLoginSuccess(String memberId, String type, String user);

        void onLoginFailure();
    }
    interface Presenter{

        void handleHTML(String html);

        void handleLoginGoogleResult(GoogleSignInResult googleSignInResult);

        void handleLoginFaceBookResult(LoginResult loginResult);
    }
}
