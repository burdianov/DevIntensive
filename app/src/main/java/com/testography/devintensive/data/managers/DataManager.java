package com.testography.devintensive.data.managers;

import android.content.Context;

import com.squareup.picasso.Picasso;
import com.testography.devintensive.data.network.PicassoCache;
import com.testography.devintensive.data.network.RestService;
import com.testography.devintensive.data.network.ServiceGenerator;
import com.testography.devintensive.data.network.req.UserLoginReq;
import com.testography.devintensive.data.network.res.UserListRes;
import com.testography.devintensive.data.network.res.UserModelRes;
import com.testography.devintensive.data.storage.models.DaoSession;
import com.testography.devintensive.data.storage.models.User;
import com.testography.devintensive.data.storage.models.UserDao;
import com.testography.devintensive.utils.DevintensiveApplication;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class DataManager {
    private static DataManager INSTANCE = null;
    private Picasso mPicasso;

    private DaoSession mDaoSession;

    private Context mContext;
    private PreferencesManager mPreferencesManager;
    private RestService mRestService;

    private DataManager() {
        mPreferencesManager = new PreferencesManager();
        mContext = DevintensiveApplication.getContext();
        mRestService = ServiceGenerator.createService(RestService.class);
        mPicasso = new PicassoCache(mContext).getPicassoInstance();
        mDaoSession = DevintensiveApplication.getDaoSession();
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

    public Context getContext() {
        return mContext;
    }

    public Picasso getPicasso() {
        return mPicasso;
    }

    //region =============== Network ===============

    public Call<UserModelRes> loginUser(UserLoginReq userLoginReq) {
        return mRestService.loginUser(userLoginReq);
    }

    public Call<UserListRes> getUserListFromNetwork() {
        return mRestService.getUserList();
    }

    //endregion

    //region =============== Database ===============


    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public List<User> getUserListFromDb() {
        List<User> userList = new ArrayList<>();

        try {
            userList = mDaoSession.queryBuilder(User.class)
                    .where(UserDao.Properties.CodeLines.gt(0))
                    .orderDesc(UserDao.Properties.CodeLines)
                    .build()
                    .list();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userList;
    }

    //endregion

}
