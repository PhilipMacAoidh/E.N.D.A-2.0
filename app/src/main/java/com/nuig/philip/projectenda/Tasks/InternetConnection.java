package com.nuig.philip.projectenda.Tasks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class InternetConnection extends BroadcastReceiver {

    private Activity activity;
    private Boolean firstRun = false;
    private ImageView view;
    private String URL;

    public InternetConnection(Activity activity, ImageView view, String url) {
        this.activity = activity;
        this.view = view;
        this.URL = url;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                if(firstRun) {
                    try {
                        Glide.with(activity).load(URL).into(view);
                    } catch(Exception e){}
                }
                firstRun = true;
            }
        }
        if(intent.getExtras().getBoolean(ConnectivityManager.EXTRA_NO_CONNECTIVITY,Boolean.FALSE)) {
            Toasts.failToast("Please connect to internet", activity, Toast.LENGTH_SHORT);
            firstRun=true;
        }
    }
}
