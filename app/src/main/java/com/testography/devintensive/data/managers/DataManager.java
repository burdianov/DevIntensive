package com.testography.devintensive.data.managers;

import android.content.Context;

import com.testography.devintensive.data.network.RestService;
import com.testography.devintensive.data.network.ServiceGenerator;
import com.testography.devintensive.data.network.req.UserLoginReq;
import com.testography.devintensive.data.network.res.UserModelRes;
import com.testography.devintensive.utils.DevintensiveApplication;

import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mContext = DevintensiveApplication.getContext();
        mRestService = ServiceGenerator.createService(RestService.class);
    }

    public static DataManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DataManager();
        }
        return INSTANCE;
    }

    public PreferencesManager getPreferencesManager() {
        return mPreferencesManager;
    }

    //region =============== Network ===============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    //endregion

    //region =============== Database ===============

}
