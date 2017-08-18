package com.crazymike.web;

import com.crazymike.api.URL;
import com.crazymike.models.Tag;
import com.crazymike.respositories.CookieRepository;
import com.crazymike.respositories.ProductRepository;

import java.util.ArrayList;
import java.util.List;

class WebViewPresenter implements WebViewContract.Presenter {

    private static final String TAG = WebViewPresenter.class.getSimpleName();

    private WebViewContract.View view;
    private List<String> notNeedCheckUrl;

    WebViewPresenter(WebViewContract.View view) {
        this.view = view;
    }

    @Override
    public boolean isNeedCheck(String url) {
        if (notNeedCheckUrl == null) notNeedCheckUrl = new ArrayList<>();
        for (String u : notNeedCheckUrl) {
            if (url.equals(u)) return false;
        }
        return true;
    }

    @Override
    public boolean checkUrl(String url) {

        String msg = WebRule.isShowAlert(url);

        if (WebRule.isWeb(url)) {
            return false;
        }

        if (WebRule.isClickBack(url)) {
            view.onBackClick();
            return true;
        }

        String itemUrl = WebRule.isProduct(url);
        if (itemUrl != null) {

            showAlert(msg);

            view.toProduct(itemUrl);
            return true;
        }

        String tag = WebRule.isTag(url);
        if (tag != null) {

            showAlert(msg);

            checkTagIsInUpsideMenu(tag, url);
        }

        if (WebRule.isSpecialUrl(url)) {
            view.onToSpecialUrl(url);
            return true;
        }

        String keyWord = WebRule.isSearch(url);
        if (keyWord != null) {

            showAlert(msg);

            view.toSearch(keyWord);
            return true;
        }

        return false;
    }

    private void showAlert(String msg) {
        if (msg != null) {
            view.onShowAlert(msg);
        }
    }

    @Override
    public void checkCookie(String url) {
        CookieRepository.getInstance().checkWebViewCookie(url);
    }

    @Override
    public void checkWebToolBarViewType(String url) {
        List<String> needHideButtonUrls = new ArrayList<>();
        needHideButtonUrls.add(URL.CART);
        needHideButtonUrls.add(URL.GUEST_ORDER_LIST);
        needHideButtonUrls.add(URL.ORDER_LIST);
        needHideButtonUrls.add(URL.BUY);
        needHideButtonUrls.add(URL.BONUS_GIFT);
        needHideButtonUrls.add(URL.BONUS_MEMBER);
        needHideButtonUrls.add(URL.CONTRACT);
        needHideButtonUrls.add(URL.CONTRACT_LIST);
        needHideButtonUrls.add(URL.USER_MENU);

        for(String needHideButtonUrl : needHideButtonUrls){
            if(url.equals(needHideButtonUrl)){
                view.onWebToolBarViewTypeChecked(false, false);
                return;
            }
        }

        if (url.contains("crazymike.tw") || url.contains("activity")) {
            view.onWebToolBarViewTypeChecked(true, false);
        }
    }

    private void checkTagIsInUpsideMenu(String tagId, String url) {

        ProductRepository.getInstance().getSubTag(tagId, new ProductRepository.OnGetSubTagCallback() {
            @Override
            public void getSubTag(Tag tag, int tagPosition, Tag subTag1, int subTag1Position, Tag subTag2, int subTag2Position) {
                view.onToTagPage(tag, tagPosition, subTag1, subTag1Position, subTag2, subTag2Position);
            }

            @Override
            public void getNothing() {
//                notNeedCheckUrl.add(url);
//                view.onToTagPage();
                view.onToHomePage(tagId);
            }
        });
    }

}
