package com.nuig.philip.projectenda.Leaderboards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.nuig.philip.projectenda.R;

public class National_Leaderboard extends Fragment {

    //todo read in users location or add option to add it via spinner in Settings

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_national__leaderboard, container, false);
    }
}