package com.crazymike.login;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;
import com.crazymike.R;
import com.crazymike.api.URL;
import com.crazymike.base.BaseActivity;
import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;
import com.crazymike.web.WebRule;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Arrays;

public class LoginActivity extends BaseActivity implements LoginContract.View, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RESULT_CODE_LOGIN_GOOGLE = 1001;
    private static final int RESULT_CODE_LOGIN_FACEBOOK = 1002;
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FACEBOOK = "facebook";
    public static final String LOGIN_TYPE_YAHOO = "yahoo";

    private WebView mWebView;
    private LoginPresenter mPresenter;
    private GoogleApiClient mGoogleApiClient;
    private CallbackManager mFaceBookCallbackManager;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String privateKey = getResources().getString(R.string.privateKey);
        mPresenter = new LoginPresenter(this, privateKey);
        initView();
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getResources().getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();

        mFaceBookCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mFaceBookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mPresenter.handleLoginFaceBookResult(loginResult);
                    }

                    @Override
                    public void onCancel() {
                        Log.d("LoginActivity","Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.d("LoginActivity","exception: "+exception.toString());
                        onLoginFailure();
                    }
                });
    }

    private void initView() {
        setContentView(R.layout.login_activity);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mWebView = (WebView) findViewById(R.id.webView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheEnabled(false);
        mWebView.addJavascriptInterface(new JavaScriptInterface(), "appJavascriptInterface");
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (WebRule.isClickBack(url)) onBackPressed();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                mWebView.loadUrl(WebRule.getShowHtmlJS());
                progressBar.setVisibility(View.GONE);
            }
        });
        mWebView.loadUrl(PreferencesTool.getInstance().get(PreferencesKey.IS_DEVEL, Boolean.class) ? URL.DEVEL_LOGIN : URL.LOGIN);
    }

    @Override
    protected void onDestroy() {
        mWebView.destroy();
        super.onDestroy();
    }

    @Override
    public void onLoginSuccess(String memberId, String type, String user) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onLoginFailure() {
        new MaterialDialog.Builder(this)
                .content(R.string.login_failure)
                .positiveText(R.string.confirm)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_CODE_LOGIN_GOOGLE:
                GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                mPresenter.handleLoginGoogleResult(googleSignInResult);
                break;
            case RESULT_CODE_LOGIN_FACEBOOK:
                break;
        }
        mFaceBookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        onLoginFailure();
    }

    class JavaScriptInterface {
        @JavascriptInterface
        public void showHTML(String html) {
            mPresenter.handleHTML(html);
        }

        @JavascriptInterface
        public void login(String loginType) {
            switch (loginType) {
                case LOGIN_TYPE_GOOGLE:
                    Intent loginIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                    startActivityForResult(loginIntent, RESULT_CODE_LOGIN_GOOGLE);
                    break;
                case LOGIN_TYPE_FACEBOOK:
                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("user_photos", "email", "user_birthday", "public_profile"));
                    break;
            }
        }
    }
}
