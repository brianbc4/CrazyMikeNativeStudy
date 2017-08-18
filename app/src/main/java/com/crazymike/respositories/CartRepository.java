package com.crazymike.respositories;

import android.util.Log;

import com.crazymike.api.NetworkService;
import com.crazymike.models.Cart;
import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;
import com.crazymike.util.RxUtil;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class CartRepository {

    public static final String TAG = "CartRepository";
    private static CartRepository INSTANCE = null;
    private Observable<Map<String, Cart>> cartChangeObservable;
    private Subscriber<? super Map<String, Cart>> cartChangeSubscriber;

    private Observable<Integer> cartCountChangeObservable;
    private Subscriber<? super Integer> cartCountChangeSubscriber;

    private Map<String, Cart> cartMap;

    private CartRepository() {
        cartMap = new HashMap<>();
    }

    public static CartRepository getInstance() {
        return INSTANCE != null ? INSTANCE : (INSTANCE = new CartRepository());
    }

    public Map<String, Cart> getCartMap() {
        return cartMap;
    }

    public Observable<Map<String, Cart>> getCartObservable() {
        if (cartChangeObservable == null) {
            cartChangeObservable = Observable.create((Observable.OnSubscribe<Map<String, Cart>>) subscriber -> cartChangeSubscriber = subscriber).share();
        }
        return cartChangeObservable;
    }

    public Observable<Integer> getCartCountObservable() {
        if (cartCountChangeObservable == null) {
            cartCountChangeObservable = Observable.create((Observable.OnSubscribe<Integer>) subscriber -> cartCountChangeSubscriber = subscriber).share();
        }
        return cartCountChangeObservable;
    }

    public void addCart(String itemId, int qty, String jsonSpecList) {
        boolean isDevel = PreferencesTool.getInstance().get(PreferencesKey.IS_DEVEL, Boolean.class);
        if (!isDevel) {
            NetworkService.getInstance().getCartApi().addCart(itemId, qty, jsonSpecList)
                    .compose(RxUtil.mainAsync())
                    .subscribe(s -> {
                        cartCountChangeSubscriber.onNext(Integer.valueOf(s));
                    }, throwable -> cartCountChangeSubscriber.onError(throwable));
        } else {
            NetworkService.getInstance().getCartApi().addDevelCart(itemId, qty, jsonSpecList)
                    .compose(RxUtil.mainAsync())
                    .subscribe(s -> {
                        cartCountChangeSubscriber.onNext(Integer.valueOf(s));
                    }, throwable -> cartCountChangeSubscriber.onError(throwable));
        }
    }

    public void callCartMap() {
        boolean isDevel = PreferencesTool.getInstance().get(PreferencesKey.IS_DEVEL, Boolean.class);
        if (!isDevel) {
            NetworkService.getInstance().getCartApi().getCartList()
                    .compose(RxUtil.mainAsync())
                    .subscribe(cartMap -> {
                        if (cartMap == null) this.cartMap = new HashMap<>();
                        else this.cartMap = cartMap;
                        if (cartChangeSubscriber != null) cartChangeSubscriber.onNext(this.cartMap);
                    }, throwable -> cartChangeSubscriber.onError(throwable));
        } else {
            NetworkService.getInstance().getCartApi().getDevelCartList()
                    .compose(RxUtil.mainAsync())
                    .subscribe(cartMap -> {
                        if (cartMap == null) this.cartMap = new HashMap<>();
                        else this.cartMap = cartMap;
                        if (cartChangeSubscriber != null) cartChangeSubscriber.onNext(this.cartMap);
                    }, throwable -> cartChangeSubscriber.onError(throwable));
        }
    }
}
