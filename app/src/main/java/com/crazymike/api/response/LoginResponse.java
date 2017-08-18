package com.crazymike.api.response;

import lombok.Getter;

/**
 * Created by Elliot on 2017/5/17.
 */

@Getter
public class LoginResponse extends BaseResponse {

    private Rtn rtn;

    @Getter
    public static class Rtn {

        private String C_type;
        private String user;
        private String token;
        private String sig;
        private String time;
        private String member_id;
    }
}
