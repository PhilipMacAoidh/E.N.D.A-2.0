package com.nuig.philip.projectenda.Leaderboards;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.UserLoader;

public class National_Leaderboard extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_national__leaderboard, container, false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference userColl = database.collection("users");
        userColl.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                userColl.orderBy("points", Query.Direction.DESCENDING).whereEqualTo("country", task.getResult().get("country")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        RecyclerView nList = (RecyclerView) view.findViewById(R.id.nationalList);
                        UserLoader adapter = new UserLoader(task.getResult().getDocuments(), UserLoader.rank);
                        nList.setAdapter(adapter);
                        nList.setLayoutManager(new LinearLayoutManager(getContext()));
                    }
                });
            }
        });

        return view;
    }
}