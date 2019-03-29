package com.nuig.philip.projectenda.Tasks;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nuig.philip.projectenda.Challenge_Page.Challenge_fragment;
import com.nuig.philip.projectenda.R;

import java.io.File;

public class HistoryLoader extends BaseAdapter {

    private Context Context;
    private Locations[] locations;
    private String font;
    static Boolean frontShowing;
    private GoogleMap googleMap;
    private View cardView;

    public HistoryLoader(Context context, Locations[] locations, String font) {
        this.Context = context;
        this.locations = locations;
        if (font == null){
            this.font = "none";
        }else{
            this.font = font;
        }
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
            locationCard.requestLayout();
        }

        int fontRef;
        switch (font) {
            case "friday_vibes":
                fontRef = R.font.friday_vibes;
                break;
            case "modista_script":
                fontRef = R.font.modista_script;
                break;
            case "queenstown_signature":
                fontRef = R.font.queenstown_signature;
                break;
            case "rockness":
                fontRef = R.font.rockness;
                break;
            case "simplicity":
                fontRef = R.font.simplicity;
                break;
            default:
                fontRef = 0;
                break;
        }
        if (fontRef != 0) {
            locationName.setTypeface(ResourcesCompat.getFont(parent.getContext(), fontRef));
        }

        Glide.with(parent).load(loc.getImgURL())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .into(locationImage);
        locationName.setText(loc.getName());
        date.setText(loc.getDate());

        return convertView;
    }

    public View getDialog(int position, ViewGroup parent, View dialog, final Bundle savedInstanceState) {
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
        final MapView mMapView = (MapView) cardView.findViewById(R.id.infoMap);
        final TextView locationInfoName = cardView. findViewById(R.id.locationInfoName);
        final TextView synopsis = cardView.findViewById(R.id.synopsis);
        final TextView wikiBtn = cardView.findViewById(R.id.urlBtn);

        ViewGroup.MarginLayoutParams cardParams = (ViewGroup.MarginLayoutParams) locationCard.getLayoutParams();
        cardParams.setMargins(0, 0, 0, 0);
        locationCard.requestLayout();

        int fontRef;
        switch (font) {
            case "friday_vibes":
                fontRef = R.font.friday_vibes;
                break;
            case "modista_script":
                fontRef = R.font.modista_script;
                break;
            case "queenstown_signature":
                fontRef = R.font.queenstown_signature;
                break;
            case "rockness":
                fontRef = R.font.rockness;
                break;
            case "simplicity":
                fontRef = R.font.simplicity;
                break;
            default:
                fontRef = 0;
                break;
        }
        if (fontRef != 0) {
            locationName.setTypeface(ResourcesCompat.getFont(parent.getContext(), fontRef));
        }

        Glide.with(parent).load(loc.getImgURL())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .into(locationImage);
        locationName.setText(loc.getName());
        locationName.setTextSize(28);
        date.setTextSize(14);
        date.setText(loc.getDate());

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                MarkerOptions markerObject = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                googleMap = mMap;
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16));
                googleMap.addMarker(markerObject);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        cardFlipAnimation(dialogView, frontCard, backCard);
                    }
                });
            }
        });
        locationInfoName.setText(loc.getName());
        synopsis.setText(loc.getInfo());

        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });
        locationInfoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });
        synopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });

        wikiBtn.setText(loc.getWiki().substring(0, Math.min(loc.getWiki().length(), 46)) + "....");
        wikiBtn.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loc.getWiki()));
                Context.startActivity(browserIntent);
            }
        });

        return cardView;
    }

    public View getLocationCard(Locations[] position, final Activity activity, final Context context, final Challenge_fragment challenge_fragment, final ViewGroup parent, View dialog, final Bundle savedInstanceState) {
        frontShowing = true;
        final Locations loc = position[0];
        final View dialogView = dialog;
        final LayoutInflater layoutInflater = LayoutInflater.from(Context);
        cardView = layoutInflater.inflate(R.layout.layout_history_location, null);
        final CardView locationCard = (CardView)cardView.findViewById(R.id.cardView);

        final LinearLayout frontCard = cardView.findViewById(R.id.frontCard);
        final ImageView locationImage = (ImageView)cardView.findViewById(R.id.location_image);
        final FloatingActionButton customImageBtn = cardView.findViewById(R.id.custom_image_btn);
        final TextView locationName = (TextView)cardView.findViewById(R.id.location_name);
        final TextView date = (TextView)cardView.findViewById(R.id.location_date);

        final LinearLayout backCard = cardView.findViewById(R.id.backCard);
        final MapView mMapView = (MapView) cardView.findViewById(R.id.infoMap);
        final TextView locationInfoName = cardView. findViewById(R.id.locationInfoName);
        final TextView synopsis = cardView.findViewById(R.id.synopsis);
        final TextView wikiBtn = cardView.findViewById(R.id.urlBtn);

        ViewGroup.MarginLayoutParams cardParams = (ViewGroup.MarginLayoutParams) locationCard.getLayoutParams();
        cardParams.setMargins(0, 0, 0, 0);
        locationCard.requestLayout();

        int fontRef;
        switch (font) {
            case "friday_vibes":
                fontRef = R.font.friday_vibes;
                break;
            case "modista_script":
                fontRef = R.font.modista_script;
                break;
            case "queenstown_signature":
                fontRef = R.font.queenstown_signature;
                break;
            case "rockness":
                fontRef = R.font.rockness;
                break;
            case "simplicity":
                fontRef = R.font.simplicity;
                break;
            default:
                fontRef = 0;
                break;
        }
        if (fontRef != 0) {
            locationName.setTypeface(ResourcesCompat.getFont(parent.getContext(), fontRef));
        }

        Glide.with(parent).load(loc.getImgURL())
                .centerCrop()
                .placeholder(R.drawable.loading_image)
                .into(locationImage);
        customImageBtn.show();
        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
            customImageBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri file = Uri.fromFile(new File("storage/emulated/0/Android/data/com.nuig.philip.projectenda/cache/pickImageResult.jpeg"));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, file);
                    activity.startActivityForResult(intent, 7593);
                }
            });
        }
        locationName.setText(loc.getName());
        locationName.setTextSize(28);
        date.setTextSize(14);
        date.setText(loc.getDate());

        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                MarkerOptions markerObject = new MarkerOptions().position(new LatLng(loc.getLatitude(), loc.getLongitude()));
                googleMap = mMap;
                googleMap.getUiSettings().setAllGesturesEnabled(false);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16));
                googleMap.addMarker(markerObject);
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    public void onMapClick(LatLng point) {
                        cardFlipAnimation(dialogView, frontCard, backCard);
                    }
                });
            }
        });
        locationInfoName.setText(loc.getName());
        synopsis.setText(loc.getInfo());

        locationCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });
        locationInfoName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });
        synopsis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardFlipAnimation(dialogView, frontCard, backCard);
            }
        });

        wikiBtn.setText(loc.getWiki().substring(0, Math.min(loc.getWiki().length(), 46)) + "....");
        wikiBtn.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(loc.getWiki()));
                Context.startActivity(browserIntent);
            }
        });

        return cardView;
    }

    public void cardFlipAnimation(View dialogView, final LinearLayout frontCard, final LinearLayout backCard) {
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

    public void setCustomImage(Bitmap bitmap) {
        ImageView locationImage = cardView.findViewById(R.id.location_image);
        locationImage.setImageBitmap(bitmap);
    }

}