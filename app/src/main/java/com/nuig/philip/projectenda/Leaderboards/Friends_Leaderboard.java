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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.UserLoader;

import java.util.List;

public class Friends_Leaderboard extends Fragment {
    private List<DocumentSnapshot> userList, tempList;

    //todo change to only show friends

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_national__leaderboard, container, false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference userColl = database.collection("users");
        userColl.orderBy("points", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override//getting userList in order of points
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                userList = task.getResult().getDocuments();
                tempList = task.getResult().getDocuments();
                tempList.clear();
                userColl.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override//getting friend list
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        final List<DocumentSnapshot> friendList = task.getResult().getDocuments();
                        boolean added = false;
                        for (int i = 0; i < userList.size(); i++) {
                            for (int j = 0; j < friendList.size(); j++) {
                                if (userList.get(i).getId().equals(friendList.get(j).getId())) {
                                    tempList.add(userList.get(i));
                                }
                            }
                            if(!added && userList.get(i).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                tempList.add(userList.get(i));
                                added = true;
                            }
                        }
                        if(tempList != null) {
                            final RecyclerView fList = (RecyclerView) view.findViewById(R.id.nationalList);
                            UserLoader adapter = new UserLoader(tempList, UserLoader.rank);
                            fList.setAdapter(adapter);
                            fList.setLayoutManager(new LinearLayoutManager(getContext()));
                        }
                    }
                });
            }
        });

        return view;
    }
}