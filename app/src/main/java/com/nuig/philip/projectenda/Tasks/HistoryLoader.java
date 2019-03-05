package com.nuig.philip.projectenda.Tasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nuig.philip.projectenda.R;

public class HistoryLoader extends BaseAdapter {

    private Context Context;
    private Locations[] locations;
    static Boolean frontShowing;

    public HistoryLoader(Context context, Locations[] locations) {
        this.Context = context;
        this.locations = locations;
    }

    @Override
    public int getCount() {
        return locations.length;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Locations loc = locations[position];
        ViewGroup activity = parent;

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(Context);
            convertView = layoutInflater.inflate(R.layout.layout_history_location, null);
        }

        final CardView locationCard = (CardView)convertView.findViewById(R.id.cardView);
        final ImageView locationImage = (ImageView)convertView.findViewById(R.id.location_image);
        final TextView locationName = (TextView)convertView.findViewById(R.id.location_name);
        final TextView date = (TextView)convertView.findViewById(R.id.location_date);

        ViewGroup.MarginLayoutParams cardParams = (ViewGroup.MarginLayoutParams) locationCard.getLayoutParams();
        if(position == 0 || position == 1) {
            cardParams.setMargins(0, 32, 0, 16);
            locationCard.requestLayout();
        }
        if(position == (getCount()-2) || position == (getCount()-1)) {
            cardParams.setMargins(0, 16, 0, 32);
            //todo change name font based on history cards
            //https://developer.android.com/guide/topics/ui/look-and-feel/fonts-in-xml
            locationCard.requestLayout();
        }

        Glide.with(parent).load(loc.getImgURL())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .into(locationImage);
        locationName.setText(loc.getName());
        date.setText(loc.getDate());

        return convertView;
    }

    public View getDialog(int position, ViewGroup parent, View dialog) {
        frontShowing = true;
        final Locations loc = locations[position];
        final View dialogView = dialog;
        final LayoutInflater layoutInflater = LayoutInflater.from(Context);
        final View cardView = layoutInflater.inflate(R.layout.layout_history_location, null);
        final CardView locationCard = (CardView)cardView.findViewById(R.id.cardView);

        final LinearLayout frontCard = cardView.findViewById(R.id.frontCard);
        final ImageView locationImage = (ImageView)cardView.findViewById(R.id.location_image);
        final TextView locationName = (TextView)cardView.findViewById(R.id.location_name);
        final TextView date = (TextView)cardView.findViewById(R.id.location_date);

        final LinearLayout backCard = cardView.findViewById(R.id.backCard);
        //Map googleMap;
        final Double latitude, logitude;
        final TextView locationInfoName = cardView. findViewById(R.id.locationInfoName);
        final TextView synopsis = cardView.findViewById(R.id.synopsis);
//        final Button wikiBtn = cardView.findViewById(R.id.wikiBtn);

        ViewGroup.MarginLayoutParams cardParams = (ViewGroup.MarginLayoutParams) locationCard.getLayoutParams();
        cardParams.setMargins(0, 0, 0, 0);
        //todo change name font based on history cards
        locationCard.requestLayout();

        Glide.with(parent).load(loc.getImgURL())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .into(locationImage);
        locationName.setText(loc.getName());
        locationName.setTextSize(24);
        date.setTextSize(14);
        date.setText(loc.getDate());

        latitude = loc.getLatitude();
        logitude = loc.getLongitude();
        locationInfoName.setText(loc.getName());
        synopsis.setText(loc.getInfo());
//        wikiBtn.setText("www.pissoff.com");

        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(frontShowing) {
                    Animations.rightToLeftFlip(dialogView, 0).addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            showBackCard(frontCard, backCard);
                        }
                    });
                } else {
                    Animations.leftToRightFlip(dialogView, 0).addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            showFrontCard(frontCard, backCard);
                        }
                    });
                }

            }
        });

        return cardView;
    }

    public void showFrontCard(LinearLayout frontCard, LinearLayout backCard) {
        backCard.setVisibility(View.GONE);
        frontCard.setVisibility(View.VISIBLE);
        frontShowing = true;
    }

    public void showBackCard(LinearLayout frontCard, LinearLayout backCard) {
        frontCard.setVisibility(View.GONE);
        backCard.setVisibility(View.VISIBLE);
        frontShowing = false;
    }

}