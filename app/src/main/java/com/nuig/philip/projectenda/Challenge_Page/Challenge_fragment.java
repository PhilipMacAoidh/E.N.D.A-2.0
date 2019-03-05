package com.nuig.philip.projectenda.Challenge_Page;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.Toasts;
import com.nuig.philip.projectenda.R;
import java.io.IOException;

public class Challenge_fragment extends DialogFragment {

    public static final String TAG = Challenge_fragment.class.getSimpleName();
    public static View view;
    private Button challengeHelpBtn;
    private boolean helpOpen = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_challenge, container, false);
        Glide.with(getActivity()).load(getResources().getString(R.string.challenge_image_url))
                .placeholder(R.drawable.loading_image)
                .into((ImageView) view.findViewById(R.id.challenge_image));

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


        return view;
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
                    String message = new String(ndefMessage.getRecords()[1].getPayload());
                    Log.d(TAG, "readFromNFC: " + message);
                    Toasts.successToast(message, main, Toast.LENGTH_SHORT);
                    //todo set card Image dialog to appear with location picture and name when NFC tag confirmed
                    //use this for viewing locations of previous challenges in profile
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

