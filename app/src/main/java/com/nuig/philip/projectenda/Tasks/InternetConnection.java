package com.nuig.philip.projectenda.Tasks;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.nuig.philip.projectenda.R;

public class InternetConnection extends BroadcastReceiver {

    private Activity activity;
    private Boolean firstRun = false;
    private ImageView view;
    private String URL;

    public InternetConnection(Activity act, ImageView v, String url) {
        this.activity = act;
        this.view = v;
        this.URL = url;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getExtras()!=null) {
            NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
            if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
                if(firstRun) {
                    Glide.with(activity).load(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()).into(view);
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
