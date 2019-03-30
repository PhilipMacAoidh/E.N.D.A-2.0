package com.nuig.philip.projectenda.Challenge_Page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.ChallengeLoader;
import com.nuig.philip.projectenda.Tasks.HistoryLoader;
import com.nuig.philip.projectenda.Tasks.InternetConnection;
import com.nuig.philip.projectenda.Tasks.Locations;
import com.nuig.philip.projectenda.Tasks.Toasts;
import com.nuig.philip.projectenda.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Challenge_fragment extends DialogFragment {

    public static final String TAG = Challenge_fragment.class.getSimpleName();
    public static View view;
    private Button challengeHelpBtn;
    private boolean helpOpen = false;
    private FirebaseUser auth;
    private FirebaseFirestore database;
    private static InternetConnection broadcastReceiver;
    private String challengeImgUrl, font, locationDocumentPath;
    private static String challengeID;
    private DocumentReference userDoc, challengeDoc;
    private Bundle sIS;
    private HistoryLoader locationsAdapter;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenge, container, false);
        auth = FirebaseAuth.getInstance().getCurrentUser();
        sIS = savedInstanceState;
        new ChallengeLoader(Challenge_fragment.this, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        view.findViewById(R.id.challenge_help).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(helpOpen){
                    closeNFCHelp();
                }else {
                    openNFCHelp();
                }
            }
        });


        view.findViewById(R.id.help_image).setMinimumHeight(view.findViewById(R.id.challenge_image).getMeasuredWidth());
        view.findViewById(R.id.help_image).setMinimumWidth(view.findViewById(R.id.challenge_image).getMeasuredWidth());

        broadcastReceiver = new InternetConnection(getActivity(), (ImageView) getActivity().findViewById(R.id.challenge_image), challengeImgUrl);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getActivity().registerReceiver(broadcastReceiver, filter);

        refreshDocuments();

        view.findViewById(R.id.skip_challenge).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                challengeID = document.getData().get("challenge#").toString();
                                if(challengeID != ""){
                                    userDoc.collection("skipped").document(challengeID).set(new HashMap<>());
                                    new ChallengeLoader(null, true);
                                }else {
                                    Toasts.failToast("No Challenge to skip! Increase distance range", getActivity(), Toast.LENGTH_SHORT);
                                }
                            } else {
                                Log.d(TAG, "No document found with this userID");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        pullToRefresh = view.findViewById(R.id.pullToRefreshChallenge);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userDoc.collection("history").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                new ChallengeLoader(null, true);
                                pullToRefresh.setRefreshing(false);
                            }
                        });
            }
        });

        return view;
    }

    public void refreshDocuments(){
        database = FirebaseFirestore.getInstance();
        userDoc = database.collection("users").document(auth.getUid());
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        font = document.getData().get("font").toString();
                        challengeID = document.getData().get("challenge#").toString();
                        if(challengeID != "") {
                            challengeDoc = database.collection("challenges").document(challengeID);
                            challengeDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            challengeImgUrl = document.getData().get("challengeUrl").toString();
                                            Glide.with(getActivity()).load(challengeImgUrl)
                                                    .centerCrop()
                                                    .placeholder(R.drawable.no_challenge_image)
                                                    .into((ImageView) view.findViewById(R.id.challenge_image));
                                        } else {
                                            Log.d(TAG, "Cannot locate document: " + challengeID);
                                        }
                                    } else {
                                        Log.d(TAG, "get failed with ", task.getException());
                                    }
                                }
                            });
                        } else {
                            Glide.with(getActivity()).load("")
                                    .centerCrop()
                                    .placeholder(R.drawable.no_challenge_image)
                                    .into((ImageView) view.findViewById(R.id.challenge_image));
                        }
                    } else {
                        Log.d(TAG, "No document found with this userID");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void onNfcDetected(Ndef ndef, MainActivity main){

        try {
            ndef.connect();
            NdefMessage ndefMessage = ndef.getNdefMessage();
            if(ndefMessage == null) {
                Toasts.failToast("Tag moved too fast, please scan again..", main, Toast.LENGTH_SHORT);
            }
            else {
                String appUrl = new String(ndefMessage.getRecords()[0].getPayload());
                Log.d(TAG, appUrl);
                if (appUrl.contains("com.nuig.philip.projectenda")) {
                    final String message = new String(ndefMessage.getRecords()[1].getPayload());

                    challengeDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    if (message.equals(document.getData().get("code").toString())) {
                                        Toasts.successToast("Congratulations", getActivity(), Toast.LENGTH_SHORT);
                                        Map ref = document.getData();
                                        final Locations[] completed = {new Locations( (String)ref.get("name"), new SimpleDateFormat("dd/MM/yyyy").format(new Date()), new SimpleDateFormat("HH:mm:ss").format(new Date()), (String)ref.get("wikiUrl"), (String)ref.get("imgUrl"), (Double)ref.get("lat"), (Double)ref.get("long"), (String)ref.get("extract"))};
                                        locationsAdapter = new HistoryLoader(getActivity(), completed, font);
                                        Dialog dialog = new Dialog(getActivity());
                                        dialog.setContentView(locationsAdapter.getLocationCard(completed, getActivity(), getContext(), Challenge_fragment.this, (ViewGroup) view, dialog.getWindow().getDecorView(), sIS));
                                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        dialog.show();
                                        dialog.getWindow().setAttributes(layoutParams);
                                        userDoc.get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            DocumentSnapshot document = task.getResult();
                                                            if (document.exists()) {
                                                                userDoc.update("points", (long) document.getData().get("points")+1);
                                                            }
                                                        }
                                                    }
                                                });
                                        new ChallengeLoader(Challenge_fragment.this, true);
                                        locationDocumentPath = new SimpleDateFormat("yyyy:MM:dd").format(new Date()) + " " +new SimpleDateFormat("HH:mm:ss").format(new Date());
                                        userDoc.collection("history").document(locationDocumentPath).set(completed[0]);
                                    }
                                    else {
                                        Toasts.failToast("Tag does not match current challenge", getActivity(), Toast.LENGTH_LONG);
                                    }

                                }
                            } else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });
                }
                else {
                    Toasts.failToast("Incompatible Tag", main, Toast.LENGTH_SHORT);
                }
            }
            ndef.close();

        } catch (IOException | FormatException e) {
            e.printStackTrace();

        }
    }

    public void setCustomImage(Bitmap bitmap, Uri filePath) {
        locationsAdapter.setCustomImage(bitmap);
        if(filePath != null){
            final StorageReference ref = FirebaseStorage.getInstance().getReference().child("Users/"+auth.getUid()+"/PersonalLocationImages/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasts.failToast("Error uploading image", getActivity(), Toast.LENGTH_SHORT);
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            return ref.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                userDoc.collection("history").document(locationDocumentPath).update("imgURL", task.getResult().toString());
                                Toasts.successToast("Personal picture saved!", getActivity(), Toast.LENGTH_SHORT);
                            }
                        }
                    });
        }
    }

    public void openNFCHelp() {
        CardView cardView = view.findViewById(R.id.cardView);

        Animations.leftToRightFlip(cardView).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                challengeHelpBtn = view.findViewById(R.id.challenge_help);
                challengeHelpBtn.setText(R.string.btn_back);
                view.findViewById(R.id.challenge_image).setVisibility(View.GONE);
                view.findViewById(R.id.help_image).setVisibility(View.VISIBLE);
                helpOpen = true;
            }
        });
    }

    public void closeNFCHelp() {
        CardView cardView = view.findViewById(R.id.cardView);

        Animations.rightToLeftFlip(cardView).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                challengeHelpBtn = view.findViewById(R.id.challenge_help);
                challengeHelpBtn.setText(R.string.challenge_help_text);
                view.findViewById(R.id.challenge_image).setVisibility(View.VISIBLE);
                view.findViewById(R.id.help_image).setVisibility(View.GONE);
                helpOpen = false;
            }
        });

    }

}

