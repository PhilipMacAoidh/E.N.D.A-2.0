package com.nuig.philip.projectenda.Leaderboards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query.Direction;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.UserLoader;

public class Worldwide_Leaderboard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_worldwide__leaderboard, container, false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference userColl = database.collection("users");
        userColl.orderBy("points", Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                RecyclerView wwList = (RecyclerView) view.findViewById(R.id.worldwideList);
                UserLoader adapter = new UserLoader(task.getResult().getDocuments(), UserLoader.rank);
                wwList.setAdapter(adapter);
                wwList.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });

        return view;
    }
}