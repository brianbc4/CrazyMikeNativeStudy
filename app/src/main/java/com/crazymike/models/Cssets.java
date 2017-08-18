package com.crazymike.models;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * Created by ChaoJen on 2017/4/14.
 */

@Getter
public class Cssets {

    @SerializedName("H")
    private H h;
    @SerializedName("W")
    private W w;

    @Getter
    public class H {
        @SerializedName("cs_wtype")
        String csWType;
        @SerializedName("is_cm_horder_tel")
        String isCmHorderTel;
        @SerializedName("is_cm_horder_contact")
        String isCmHorderContact;
        @SerializedName("is_tr_horder_tel")
        String isTrHorderTel;
        @SerializedName("is_tr_horder_contact")
        String isTrHorderContact;
        @SerializedName("lang_cs_wtype")
        String langCsWType;
    }

    @Getter
    public class W {
        @SerializedName("cs_wtype")
        String csWType;
        @SerializedName("is_cm_horder_tel")
        String isCmHorderTel;
        @SerializedName("is_cm_horder_contact")
        String isCmHorderContact;
        @SerializedName("is_tr_horder_tel")
        String isTrHorderTel;
        @SerializedName("is_tr_horder_contact")
        String isTrHorderContact;
        @SerializedName("lang_cs_wtype")
        String langCsWType;
    }
}
