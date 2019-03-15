package com.nuig.philip.projectenda.Challenge_Page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.Profile.Profile;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.HistoryLoader;
import com.nuig.philip.projectenda.Tasks.InternetConnection;
import com.nuig.philip.projectenda.Tasks.Locations;
import com.nuig.philip.projectenda.Tasks.Toasts;
import com.nuig.philip.projectenda.R;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Challenge_fragment extends DialogFragment {

    public static final String TAG = Challenge_fragment.class.getSimpleName();
    public static View view;
    private Button challengeHelpBtn;
    private boolean helpOpen = false;
    private FirebaseUser auth;
    private FirebaseFirestore database;
    private InternetConnection broadcastReceiver;
    private String challengeImgUrl, challengeNum;
    private DocumentReference userDoc, challengeDoc;
    private Bundle sIS;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenge, container, false);
        auth = FirebaseAuth.getInstance().getCurrentUser();
        sIS = savedInstanceState;
        refreshDocuments();

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
                        challengeNum = document.getData().get("challenge#").toString();
                        challengeDoc = database.collection("challenges").document("challenge"+challengeNum);
                        challengeDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        challengeImgUrl = document.getData().get("imgUrl").toString();
                                        Glide.with(getActivity()).load(challengeImgUrl)
                                                .centerCrop()
                                                .placeholder(R.drawable.loading_image)
                                                .into((ImageView) view.findViewById(R.id.challenge_image));
                                    } else {
                                        Log.d("data-base", "No such document");
                                    }
                                } else {
                                    Log.d("data-base", "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        Log.d("data-base", "No such document");
                    }
                } else {
                    Log.d("data-base", "get failed with ", task.getException());
                }
            }
        });
    }

    public void onNfcDetected(Ndef ndef, MainActivity main){
        readFromNFC(ndef, main);
    }

    private void readFromNFC(Ndef ndef, MainActivity main) {

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
                                        //todo set card Image dialog to appear with location picture and name when NFC tag confirmed

                                        Map ref = document.getData();
                                        final Locations[] completed = {new Locations( (String)ref.get("name"), new SimpleDateFormat("dd/MM/yyyy").format(new Date()), (String)ref.get("wikiUrl"), (String)ref.get("imgUrl"), (Double)ref.get("lat"), (Double)ref.get("long"), (String)ref.get("extract"))};
                                        final HistoryLoader locationsAdapter = new HistoryLoader(getActivity(), completed);
                                        Dialog dialog = new Dialog(getActivity());
                                        dialog.setContentView(locationsAdapter.getDialog(completed, (ViewGroup) view, dialog.getWindow().getDecorView(), sIS));
                                        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                                        layoutParams.copyFrom(dialog.getWindow().getAttributes());
                                        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                                        dialog.show();
                                        dialog.getWindow().setAttributes(layoutParams);

                                        String newNum = String.valueOf(Integer.parseInt(challengeNum)+1);
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
                                        userDoc.update("challenge#", newNum)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        refreshDocuments();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w("data-base", "Error writing document", e);
                                                    }
                                                });
                                        userDoc.collection("history").add(completed[0]);
                                    }
                                    else {
                                        Toasts.failToast("Tag does not match current challenge", getActivity(), Toast.LENGTH_LONG);
                                    }

                                } else {
                                    Log.d("data-base", "No such document");
                                }
                            } else {
                                Log.d("data-base", "get failed with ", task.getException());
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

    public void  openNFCHelp() {
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

    public void  closeNFCHelp() {
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

