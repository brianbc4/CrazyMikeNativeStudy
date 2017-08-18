package com.crazymike.login;

import android.os.Bundle;
import android.util.Log;

import com.crazymike.api.NetworkService;
import com.crazymike.respositories.CookieRepository;
import com.crazymike.util.EncodeTool;
import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;
import com.crazymike.util.RxUtil;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.gson.Gson;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginPresenter implements LoginContract.Presenter {

    private static final String TAG = LoginPresenter.class.getSimpleName();

    private LoginContract.View mView;
    private String mPrivateKey;

    public LoginPresenter(LoginContract.View view, String privateKey) {
        this.mView = view;
        this.mPrivateKey = privateKey;
    }

    @Override
    public void handleHTML(String html) {
        //<script>appLogin(true, "aKbUI3CaZlB3wKGb10ehFDXHdidJaETtVIlQ59UuVyg.", "crazy", "J4EkzLOftvrOCVsy8ieRX8sjFyotP-4VUKDi3kEf4Bc.");</script>
        if (!html.contains("appLogin")) {
            return;
        }

        String data = html.replace("<script>appLogin(", "").replace(");</script>", "");
        //true, "aKbUI3CaZlB3wKGb10ehFDXHdidJaETtVIlQ59UuVyg.", "crazy", "J4EkzLOftvrOCVsy8ieRX8sjFyotP-4VUKDi3kEf4Bc."

        String success = data.split(",")[0];
        if (!success.equals("true")) {
            mView.onLoginFailure();
            return;
        }

        try {
            String memberId = data.split(",")[1].replace("\"", "").replace(" ", "");
            String type = data.split(",")[2].replace("\"", "").replace(" ", "");
            String user = data.split(",")[3].replace("\"", "").replace(" ", "");

            PreferencesTool.getInstance().put(PreferencesKey.MEMBER_ID, memberId);
            PreferencesTool.getInstance().put(PreferencesKey.LOGIN_TYPE, type);
            PreferencesTool.getInstance().put(PreferencesKey.LOGIN_USER, user);

            CookieRepository.getInstance().setIsLogin(true);
            mView.onLoginSuccess(memberId, type, user);
        } catch (Exception e) {
            e.printStackTrace();
            mView.onLoginFailure();
        }
    }

    @Override
    public void handleLoginGoogleResult(GoogleSignInResult googleSignInResult) {
        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount googleSignInAccount = googleSignInResult.getSignInAccount();
            String strIdToken = googleSignInAccount.getIdToken();
            String strEmail = googleSignInAccount.getEmail();
            int intTime = (int) (DateTime.now().getMillis() / 1000);
            String strCType = LoginActivity.LOGIN_TYPE_GOOGLE;
            String strData = "{\"c_type\":\"" + strCType + "\",\"time\":" + intTime + ",\"token\":\""+ strIdToken +"\",\"user\":\"" + strEmail + "\"}";
            String strSig = EncodeTool.toMD5String(strData + "#" + intTime + "#" + mPrivateKey);

            Log.i(TAG, "email:\t" + strEmail);
            Log.i(TAG, "token:\t" + strIdToken);
            Log.i(TAG, "type:\t" + strCType);
            Log.i(TAG, "time:\t" + intTime);
            Log.i(TAG, "sig:\t" + strSig);
            Log.i(TAG, "data:\t" + strData);

            NetworkService.getInstance().getLoginApi().login(strEmail, strIdToken, strCType, intTime, strSig)
                    .compose(RxUtil.mainAsync())
                    .subscribe(loginResponse -> {
                        String memberId = loginResponse.getRtn().getMember_id();
                        String type = loginResponse.getRtn().getC_type();
                        String user = loginResponse.getRtn().getUser();
                        String sig = loginResponse.getRtn().getSig();
                        String time = loginResponse.getRtn().getTime();
                        String tokenBack = loginResponse.getRtn().getToken();

                        String domain = ".crazymike.tw";
                        CookieRepository.getInstance()
                                .setCookie(domain, "C_type", type)
                                .setCookie(domain, "user", user)
                                .setCookie(domain, "member_id", memberId)
                                .setCookie(domain, "sig", sig)
                                .setCookie(domain, "time", time)
                                .setCookie(domain, "token", tokenBack);

                        PreferencesTool.getInstance().put(PreferencesKey.LOGIN_TYPE, type);
                        PreferencesTool.getInstance().put(PreferencesKey.LOGIN_USER, user);
                        PreferencesTool.getInstance().put(PreferencesKey.MEMBER_ID, memberId);

                        CookieRepository.getInstance().setIsLogin(true);
                        mView.onLoginSuccess(memberId, type, user);
                    }, throwable -> {
                        Log.e(TAG, throwable.toString());
                        Log.e(TAG, "loginFail");
                        mView.onLoginFailure();
                    });
        } else {
            mView.onLoginFailure();
        }
    }

    @Override
    public void handleLoginFaceBookResult(LoginResult loginResult) {

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("LoginActivity", response.toString());
                try {
                    String strIdToken = loginResult.getAccessToken().getToken();
                    String strEmail = object.getString("email");
                    String strCType = LoginActivity.LOGIN_TYPE_FACEBOOK;
                    int intTime = (int) (DateTime.now().getMillis() / 1000);
                    String strData = "{\"c_type\":\"" + strCType + "\",\"time\":" + intTime + ",\"token\":\""+ strIdToken +"\",\"user\":\"" + strEmail + "\"}";
                    String strSig = EncodeTool.toMD5String(strData + "#" + intTime + "#" + mPrivateKey);

                    NetworkService.getInstance().getLoginApi().login(strEmail, strIdToken, strCType, intTime, strSig)
                            .compose(RxUtil.mainAsync())
                            .subscribe(loginResponse -> {
                                String memberId = loginResponse.getRtn().getMember_id();
                                String type = loginResponse.getRtn().getC_type();
                                String user = loginResponse.getRtn().getUser();
                                String sig = loginResponse.getRtn().getSig();
                                String time = loginResponse.getRtn().getTime();
                                String tokenBack = loginResponse.getRtn().getToken();

                                String domain = ".crazymike.tw";
                                CookieRepository.getInstance()
                                        .setCookie(domain, "C_type", type)
                                        .setCookie(domain, "user", user)
                                        .setCookie(domain, "member_id", memberId)
                                        .setCookie(domain, "sig", sig)
                                        .setCookie(domain, "time", time)
                                        .setCookie(domain, "token", tokenBack);

                                PreferencesTool.getInstance().put(PreferencesKey.LOGIN_TYPE, type);
                                PreferencesTool.getInstance().put(PreferencesKey.LOGIN_USER, user);
                                PreferencesTool.getInstance().put(PreferencesKey.MEMBER_ID, memberId);

                                CookieRepository.getInstance().setIsLogin(true);
                                mView.onLoginSuccess(memberId, type, user);
                            }, throwable -> {
                                Log.e(TAG, throwable.toString());
                                mView.onLoginFailure();
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id, first_name, last_name, email, birthday, gender, location"); // Parámetros que pedimos a facebook
        request.setParameters(parameters);
        request.executeAsync();
    }

}
