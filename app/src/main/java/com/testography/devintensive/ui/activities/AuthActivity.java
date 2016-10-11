package com.testography.devintensive.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.testography.devintensive.R;
import com.testography.devintensive.data.managers.DataManager;
import com.testography.devintensive.data.network.req.UserLoginReq;
import com.testography.devintensive.data.network.res.UserListRes;
import com.testography.devintensive.data.network.res.UserModelRes;
import com.testography.devintensive.data.storage.models.Repository;
import com.testography.devintensive.data.storage.models.RepositoryDao;
import com.testography.devintensive.data.storage.models.User;
import com.testography.devintensive.data.storage.models.UserDao;
import com.testography.devintensive.utils.AppConfig;
import com.testography.devintensive.utils.NetworkStatusChecker;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends BaseActivity implements View.OnClickListener {

    private Button mSignIn;
    private TextView mRememberPassword;
    private EditText mLogin, mPassword;
    private CoordinatorLayout mCoordinatorLayout;

    private DataManager mDataManager;
    private RepositoryDao mRepositoryDao;
    private UserDao mUserDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        mDataManager = DataManager.getInstance();
        mUserDao = mDataManager.getDaoSession().getUserDao();
        mRepositoryDao = mDataManager.getDaoSession().getRepositoryDao();

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_coordinator_container);
        mSignIn = (Button) findViewById(R.id.login_btn);
        mRememberPassword = (TextView) findViewById(R.id.remember_txt);
        mLogin = (EditText) findViewById(R.id.login_email_et);
        mPassword = (EditText) findViewById(R.id.login_password_et);

        mRememberPassword.setOnClickListener(this);
        mSignIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                signIn();
                break;
            case R.id.remember_txt:
                rememberPassword();
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar.make(mCoordinatorLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private void rememberPassword() {
        Intent rememberPassword = new Intent(Intent.ACTION_VIEW, Uri.parse
                ("http://devintensive.softdesign-apps.ru/forgotpass"));
        startActivity(rememberPassword);
    }

    private void loginSuccess(UserModelRes userModel) {
        showSnackbar(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveAuthToken(userModel.getData().getToken());
        mDataManager.getPreferencesManager().saveUserId(userModel.getData()
                .getUser().getId());
        saveUserValues(userModel);
        saveUserInDb();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent loginIntent = new Intent(AuthActivity.this, UserListActivity
                        .class);

                startActivity(loginIntent);
            }
        }, AppConfig.START_DELAY);
    }

    private void signIn() {
        if (NetworkStatusChecker.isNetworkAvailable(this)) {
            Call<UserModelRes> call = mDataManager.loginUser(new UserLoginReq(mLogin.getText().toString(), mPassword.getText().toString()));
            call.enqueue(new Callback<UserModelRes>() {
                @Override
                public void onResponse(Call<UserModelRes> call, Response<UserModelRes> response) {
                    if (response.code() == 200) {
                        loginSuccess(response.body());
                    } else if (response.code() == 404) {
                        showSnackbar("Wrong login or password");
                    } else {
                        showSnackbar("It's a major setback, man!");
                    }
                }

                @Override
                public void onFailure(Call<UserModelRes> call, Throwable t) {
                    // TODO: 30-Sep-16 process the retrofit errors
                }
            });
        } else {
            showSnackbar("The network is not accessible, try again later");
        }
    }

    private void saveUserValues(UserModelRes userModel) {
        int[] userValues = {
                userModel.getData().getUser().getProfileValues().getRating(),
                userModel.getData().getUser().getProfileValues().getLinesCode(),
                userModel.getData().getUser().getProfileValues().getProjects()
        };
        mDataManager.getPreferencesManager().saveUserProfileValues(userValues);
    }

    private void saveUserInDb() {
        Call<UserListRes> call = mDataManager.getUserListFromNetwork();

        call.enqueue(new Callback<UserListRes>() {
            @Override
            public void onResponse(Call<UserListRes> call, Response<UserListRes> response) {
                try {
                    if (response.code() == 200) {

                        List<Repository> allRepositories = new ArrayList<Repository>();
                        List<User> allUsers = new ArrayList<User>();

                        for (UserListRes.UserData userRes : response.body()
                                .getData()) {
                            allRepositories.addAll(getRepoListFromUserRes(userRes));
                            allUsers.add(new User(userRes));
                        }

                        mRepositoryDao.insertOrReplaceInTx((Repository)
                                allRepositories);
                        mUserDao.insertOrReplaceInTx((User) allUsers);

                    } else {
                        showSnackbar("The Users list can't be retrieved");
                        Log.e(TAG, "onResponse: " + String.valueOf(response
                                .errorBody().source()));
                    }
//                    mUsers = response.body().getData();
//                    mUsersAdapter = new UsersAdapter(mUsers, new UsersAdapter.UserViewHolder.CustomClickListener() {
//                        @Override
//                        public void onUserItemClickListener(int position) {
//
//                            UserDTO userDTO = new UserDTO(mUsers.get(position));
//
////                            Intent profileIntent = new Intent(UserListActivity
////                                    .this, ProfileUserActivity.class);
////                            profileIntent.putExtra(ConstantManager
////                                    .PARCELABLE_KEY, userDTO);
//                            startActivity(profileIntent);
//                        }
//                    });
//                    mRecyclerView.setAdapter(mUsersAdapter);
                } catch (NullPointerException e) {
                    e.printStackTrace();
//                    showSnackbar("Something is wrong");
                    // TODO: 07-Oct-16 solve the bug with the Snackbar crush
                }
            }

            @Override
            public void onFailure(Call<UserListRes> call, Throwable t) {
                // TODO: 07-Oct-16 Process the errors
            }
        });
    }

    private List<Repository> getRepoListFromUserRes(UserListRes.UserData userData) {
        final String userId = userData.getId();

        List<Repository> repositories = new ArrayList<>();
        for (UserModelRes.Repo repositoryRes : userData.getRepositories().getRepo()) {
            repositories.add(new Repository(repositoryRes, userId));
        }

        return repositories;
    }
}
