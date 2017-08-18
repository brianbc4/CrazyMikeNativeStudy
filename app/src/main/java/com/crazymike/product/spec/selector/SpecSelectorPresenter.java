package com.crazymike.product.spec.selector;

import android.util.Log;

import com.crazymike.BuildConfig;
import com.crazymike.api.NetworkService;
import com.crazymike.models.Cssets;
import com.crazymike.models.ItemDetail;
import com.crazymike.models.SWCalendar;
import com.crazymike.respositories.ProductRepository;
import com.crazymike.util.RxUtil;

/**
 * Created by Elliot on 2017/3/27.
 */

public class SpecSelectorPresenter
        implements SpecSelectorContract.Presenter {

    private static final String TAG = SpecSelectorPresenter.class.getSimpleName();

    private SpecSelectorContract.View view;
    private String csServiceStartTime;
    private String csServiceEndTime;
    private Cssets cssets;
    private ItemDetail itemDetail;

    public SpecSelectorPresenter(SpecSelectorContract.View view, ItemDetail itemDetail) {
        this.view = view;
        this.itemDetail = itemDetail;
        NetworkService.getInstance().getProductApi().callSWCalendar("swcalendar", "1", "")
                .compose(RxUtil.mainAsync())
                .compose(RxUtil.bindLifecycle(view))
                .subscribe(swCalendarResponse -> {
                    SWCalendar swCalendar = swCalendarResponse.getSwCalendarList().get(0);
                    csServiceStartTime = swCalendar.getCsServiceStartTime();
                    csServiceEndTime = swCalendar.getCsServiceEndTime();
                    cssets = swCalendar.getCssets();
                }, Throwable::printStackTrace);
    }

    @Override
    public boolean isTravelProduct(String tagId) {
        return  ProductRepository.getInstance().checkIsTravelTag(tagId);
    }

    @Override
    public String getCSServiceStartTime() {
        return csServiceStartTime;
    }

    @Override
    public String getCSServiceEndTime() {
        return csServiceEndTime;
    }

    @Override
    public Cssets getCssets() {
        return cssets;
    }

    @Override
    public void postCSServiceMessage(String name, String title, String phoneNumber, String email, String content) {
        NetworkService.getInstance().getProductApi().postCSServiceMessage("31", itemDetail.getInfo().getDeliver_from(), "true", "consultation", title, name, phoneNumber, email, content)
                .compose(RxUtil.mainAsync())
                .subscribe(csServiceMessageResponse -> {
                    view.onCSServiceMessageResponseGet(!Boolean.valueOf(csServiceMessageResponse.getIsError()), csServiceMessageResponse.getMessage());
                }, throwable -> {
                    Log.e(TAG, throwable.toString());
                });
    }
}
