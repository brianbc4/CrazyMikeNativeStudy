package com.crazymike.respositories;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import rx.Observable;
import rx.Subscriber;

public class CookieRepository {

    public static final String TAG = CookieRepository.class.getSimpleName();
    private static CookieRepository INSTANCE = new CookieRepository();
    private List<Cookie> mCookieList;
    private List<String> mMktThreeList;
    private boolean mIsLogin;

    private Observable<CookieRepository> cookieChangeObservable;
    private Subscriber<? super CookieRepository> cookieChangeSubscriber;

    private CookieRepository() {
        mCookieList = new ArrayList<>();
        mMktThreeList = new ArrayList<>();
    }

    public static CookieRepository getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new CookieRepository());
    }

    public void initial(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.createInstance(context);
        }
    }

    public Observable<CookieRepository> getCookieObservable() {
        if (cookieChangeObservable == null) {
            cookieChangeObservable = Observable.create((Observable.OnSubscribe<CookieRepository>) subscriber -> cookieChangeSubscriber = subscriber).share();
        }
        return cookieChangeObservable;
    }

    public CookieJar getCookieJar() {
        return new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                mCookieList.clear();
                mCookieList.add(new Cookie.Builder()
                        .domain("appapi2.crazymike.tw").path("/")
                        .name("channel").value("app")
                        .build());

                String cookiesTemp = CookieManager.getInstance().getCookie(url.host());
                if (cookiesTemp != null) {
                    for (String strCookie : cookiesTemp.split(";")) {
                        Cookie cookie = Cookie.parse(url, strCookie);
                        mCookieList.add(cookie);
                    }
                }

                mIsLogin = checkLogin(mCookieList);
                if (cookieChangeSubscriber != null) cookieChangeSubscriber.onNext(INSTANCE);
            }

            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                mCookieList.clear();
                if (mMktThreeList.size() > 0) addMktThreeCookieForRequest();
                mCookieList.add(new Cookie.Builder()
                        .domain("appapi2.crazymike.tw").path("/")
                        .name("channel").value("app")
                        .build());

                String strCookies = CookieManager.getInstance().getCookie(url.host());
                if (strCookies != null) {
                    for (String strCookie : strCookies.split(";")) {
                        Cookie cookie = Cookie.parse(url, strCookie);
                        mCookieList.add(cookie);
                    }
                }

                return mCookieList;
            }
        };
    }

    private boolean checkLogin(List<Cookie> cookieList) {
        boolean user = false, token = false, sig = false, time = false;

        try {
            for (Cookie cookie : cookieList) {
                if (cookie != null && cookie.name() != null && cookie.name().equals("PHPSESSID") && cookie.value() != null && !cookie.value().equals("")) {
                    PreferencesTool.getInstance().put(PreferencesKey.PHPSESSID, cookie.value());
                }

                if (cookie != null && cookie.name() != null && cookie.name().equals("user") && cookie.value() != null && !cookie.value().equals("")) {
                    user = true;
                    PreferencesTool.getInstance().put(PreferencesKey.LOGIN_USER, cookie.value());
                }

                if (cookie != null && cookie.name() != null && cookie.name().equals("token") && cookie.value() != null && !cookie.value().equals("")) {
                    token = true;
                }

                if (cookie != null && cookie.name() != null && cookie.name().equals("sig") && cookie.value() != null && !cookie.value().equals("")) {
                    sig = true;
                }

                if (cookie != null && cookie.name() != null && cookie.name().equals("time") && cookie.value() != null && !cookie.value().equals("")) {
                    time = true;
                }
                if (cookie != null && cookie.name() != null && cookie.name().equals("C_type") && cookie.value() != null && !cookie.value().equals("")) {
                    PreferencesTool.getInstance().put(PreferencesKey.LOGIN_TYPE, cookie.value());
                }

                if (cookie != null && cookie.name() != null && cookie.name().equals("member_id") && cookie.value() != null && !cookie.value().equals("")) {
                    PreferencesTool.getInstance().put(PreferencesKey.MEMBER_ID, cookie.value());
                }
            }

            return user && token && sig && time;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setIsLogin(boolean isLogin) {
        this.mIsLogin = isLogin;
    }

    public boolean isLogin() {
        return mIsLogin;
    }

    public void clearCookies() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.d(TAG, "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().flush();
            } else {
                Log.d(TAG, "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
                CookieManager cookieManager = CookieManager.getInstance();
                cookieManager.removeAllCookie();
                cookieManager.removeSessionCookie();
                CookieSyncManager.getInstance().sync();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkWebViewCookie(String url) {
        try {
            String cookies = CookieManager.getInstance().getCookie(url);
            String[] cookiesList = cookies.split(";");
            mCookieList.clear();
            for (String c : cookiesList) {
                String[] pair = c.split("=");
                mCookieList.add(new Cookie.Builder()
                        .domain("appapi2.crazymike.tw").path("/")
                        .name(pair[0].trim()).value(pair[1].trim())
                        .build());
            }
            mIsLogin = checkLogin(mCookieList);
            if (cookieChangeSubscriber != null) cookieChangeSubscriber.onNext(INSTANCE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMktThreeCookie(String itemId) {
        for (int i = 0; i < mMktThreeList.size(); i++) {
            if (mMktThreeList.get(i).equals(itemId)) return;
        }

        mMktThreeList.add(itemId);
    }

    private void addMktThreeCookieForRequest() {
        String cookieName = "mkt3";
        String cookieValue = "{\"72\":[";
        for (int i = 0; i < mMktThreeList.size() - 1; i++) {
            cookieValue += "\"" + mMktThreeList.get(i) + "\",";
        }
        cookieValue += "\"" + mMktThreeList.get(mMktThreeList.size() - 1) + "\"]}";

        for (int i = 0; i < mCookieList.size(); ++i) {
            if (mCookieList.get(i).name().equals(cookieName)) mCookieList.remove(i);
        }
        mCookieList.add(new Cookie.Builder()
                .domain("appapi2.crazymike.tw").path("/")
                .name(cookieName).value(cookieValue)
                .build());
    }

    public CookieRepository setCookie(String domain, String cookieName, String cookieValue) {
        CookieManager.getInstance().setCookie(domain, cookieName + "=" + cookieValue);
        return this;
    }
}