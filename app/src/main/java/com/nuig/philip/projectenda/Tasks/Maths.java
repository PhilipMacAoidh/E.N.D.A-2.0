package com.nuig.philip.projectenda.Tasks;

import android.content.Context;
import android.util.TypedValue;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Maths {

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static List<DocumentSnapshot> getDistance(List<DocumentSnapshot> tempList, LatLng currentLocation, long userDistance) {
        List<DocumentSnapshot> challengeList = new ArrayList<DocumentSnapshot>();
        for(int i=0; i<tempList.size(); i++){
            double lat = (double) tempList.get(i).getData().get("lat");
            double lng = (double) tempList.get(i).getData().get("long");
            int R = 6371; // Radius of the earth in km
            double tLat = (lat-currentLocation.latitude)*(Math.PI/180);
            double tLng = (lng-currentLocation.longitude)*(Math.PI/180);
            double a = Math.sin(tLat/2) * Math.sin(tLat/2) + Math.cos((currentLocation.latitude)*(Math.PI/180)) * Math.cos(lat*(Math.PI/180)) * Math.sin(tLng/2) * Math.sin(tLng/2);
            double distance = R * (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))); // Distance in km

            if (distance <= userDistance) {
                challengeList.add(tempList.get(i));
            }
        }
        return challengeList;
    }
}
