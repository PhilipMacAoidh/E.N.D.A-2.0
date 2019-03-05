package com.nuig.philip.projectenda.Leaderboards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuig.philip.projectenda.R;

public class Friends_Leaderboard extends Fragment {

    //todo add the 'NoFirends' tab from NoItemPage

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friends__leaderboard, container, false);
    }
}