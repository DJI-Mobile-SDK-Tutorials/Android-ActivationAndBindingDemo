package com.dji.activationDemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.realname.AircraftBindingState;
import dji.common.realname.AppActivationState;
import dji.common.useraccount.UserAccountState;
import dji.common.util.CommonCallbacks;
import dji.sdk.realname.AppActivationManager;
import dji.sdk.sdkmanager.DJISDKManager;
import dji.sdk.useraccount.UserAccountManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();
    protected Button loginBtn;
    protected Button logoutBtn;
    protected TextView bindingStateTV;
    protected TextView appActivationStateTV;
    private AppActivationManager appActivationManager;
    private AppActivationState.AppActivationStateListener activationStateListener;
    private AircraftBindingState.AircraftBindingStateListener bindingStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initUI();
        initData();

    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        setUpListener();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        tearDownListener();
        super.onDestroy();
    }

    private void initUI(){

        bindingStateTV = (TextView) findViewById(R.id.tv_binding_state_info);
        appActivationStateTV = (TextView) findViewById(R.id.tv_activation_state_info);
        loginBtn = (Button) findViewById(R.id.btn_login);
        logoutBtn = (Button) findViewById(R.id.btn_logout);
        loginBtn.setOnClickListener(this);
        logoutBtn.setOnClickListener(this);

    }

    private void initData(){
        setUpListener();

        appActivationManager = DJISDKManager.getInstance().getAppActivationManager();

        if (appActivationManager != null) {
            appActivationManager.addAppActivationStateListener(activationStateListener);
            appActivationManager.addAircraftBindingStateListener(bindingStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appActivationStateTV.setText("" + appActivationManager.getAppActivationState());
                    bindingStateTV.setText("" + appActivationManager.getAircraftBindingState());

                }
            });
        }
    }

    private void setUpListener() {
        // Example of Listener
        activationStateListener = new AppActivationState.AppActivationStateListener() {
            @Override
            public void onUpdate(final AppActivationState appActivationState) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        appActivationStateTV.setText("" + appActivationState);
                    }
                });
            }
        };

        bindingStateListener = new AircraftBindingState.AircraftBindingStateListener() {

            @Override
            public void onUpdate(final AircraftBindingState bindingState) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bindingStateTV.setText("" + bindingState);
                    }
                });
            }
        };
    }

    private void tearDownListener() {
        if (activationStateListener != null) {
            appActivationManager.removeAppActivationStateListener(activationStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    appActivationStateTV.setText("Unknown");
                }
            });
        }
        if (bindingStateListener !=null) {
            appActivationManager.removeAircraftBindingStateListener(bindingStateListener);
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bindingStateTV.setText("Unknown");
                }
            });
        }
    }

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_login:{
                loginAccount();
                break;
            }
            case R.id.btn_logout:{
                logoutAccount();
                break;
            }
            default:
                break;
        }
    }

    private void loginAccount(){

        UserAccountManager.getInstance().logIntoDJIUserAccount(this,
                new CommonCallbacks.CompletionCallbackWith<UserAccountState>() {
                    @Override
                    public void onSuccess(final UserAccountState userAccountState) {
                        showToast("Login Success");
                    }
                    @Override
                    public void onFailure(DJIError error) {
                        showToast("Login Error:"
                                + error.getDescription());
                    }
                });

    }

    private void logoutAccount(){
        UserAccountManager.getInstance().logoutOfDJIUserAccount(new CommonCallbacks.CompletionCallback() {
            @Override
            public void onResult(DJIError error) {
                if (null == error) {
                    showToast("Logout Success");
                } else {
                    showToast("Logout Error:"
                            + error.getDescription());
                }
            }
        });
    }

}
