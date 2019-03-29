package com.nuig.philip.projectenda.Tasks;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.Challenge_Page.Challenge_fragment;
import com.nuig.philip.projectenda.Challenge_Page.Map_fragment;

import java.util.ArrayList;
import java.util.List;

public class ChallengeLoader {

    private DocumentReference userDoc;
    private CollectionReference challengesColl;
    private List<DocumentSnapshot> challengeList = new ArrayList<DocumentSnapshot>(), historyList, skippedList;
    private LatLng currentLocation;
    private long userDistance;
    private static Challenge_fragment fragment;

    public ChallengeLoader(Challenge_fragment fragment, boolean run) {
        if (fragment != null) {
            this.fragment = fragment;
        }
        if(run) {
            userDoc = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
            challengesColl = FirebaseFirestore.getInstance().collection("challenges");
            currentLocation = Map_fragment.getCurrentLocation();
            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    userDistance = (Long) task.getResult().getData().get("distance");
                }
            });
            getChallengeList();
        }
    }

    private void getChallengeList() {
        challengesColl.get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    challengeList = task.getResult().getDocuments();
                    isLocationWithinDistance();
                }
            });
    }

    private void isLocationWithinDistance() {
        challengeList = Maths.getDistance(challengeList, currentLocation, userDistance);
        if(!challengeList.isEmpty()) {
            getSkipped();
        }
        else {
            userDoc.update("challenge#", "VhIzRahC4NTJ2GJFAnKn");
            fragment.refreshDocuments();
        }
    }

    private void getSkipped() {
        userDoc.collection("skipped").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                skippedList = task.getResult().getDocuments();
                if(skippedList!=null) {
                    getHistory();
                }
            }
        });
    }

    private void getHistory() {
        userDoc.collection("history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                historyList = task.getResult().getDocuments();
                if(historyList!=null) {
                    cleanLists();
                }
            }
        });
    }

    private void cleanLists() {
        for (int i=0; i<historyList.size(); i++) {
            LatLng historyPoint = new LatLng((Double)historyList.get(i).getData().get("latitude"), (Double)historyList.get(i).getData().get("longitude"));
            for (int j=0; j<challengeList.size(); j++) {
                LatLng challengePoint = new LatLng((Double)challengeList.get(j).getData().get("lat"), (Double)challengeList.get(j).getData().get("long"));
                if(historyPoint.equals(challengePoint)){
                    challengeList.remove(j);
                }
            }
        }
        for (int i=0; i<skippedList.size(); i++) {
            skippedList.get(i).getId();
            for (int j=0; j<challengeList.size(); j++) {
                if(skippedList.get(i).getId().equals(challengeList.get(j).getId())){
                    challengeList.remove(j);
                }
            }
        }
        setChallenge();
    }

    private void setChallenge() {
        if(challengeList.size() != 0) {
            userDoc.update("challenge#", challengeList.get(0).getId());
        } else {
            userDoc.update("challenge#", "VhIzRahC4NTJ2GJFAnKn");
        }
        fragment.refreshDocuments();
    }
}
