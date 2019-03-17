package com.nuig.philip.projectenda.Tasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.nuig.philip.projectenda.R;

import static android.view.View.SCALE_X;
import static android.view.View.SCALE_Y;
import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;

public class Animations{

    public static boolean animationRunning = false;

    public static ObjectAnimator leftToRightFlip(final View view) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationY", 0, 90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(-90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, 1);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator rightToLeftFlip(final View view) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationY", 0, -90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, 1);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator topToBottomFlip(final View view) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 0, -90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, 1);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator bottomToTopFlip(final View view) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 0, 90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(-90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, 1);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator leftToRightFlip(final View view, final int elevation) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationY", 0, 90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(-90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, elevation);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator rightToLeftFlip(final View view, final int elevation) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationY", 0, -90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, elevation);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator topToBottomFlip(final View view, final int elevation) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 0, -90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, elevation);
                    }
                });
            }
        });
        return rotation;
    }

    public static ObjectAnimator bottomToTopFlip(final View view, final int elevation) {
        ViewCompat.setElevation(view, 0);
        final ObjectAnimator rotation = ObjectAnimator.ofFloat(view, "rotationX", 0, 90);
        rotation.setDuration(350);
        rotation.start();
        rotation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rotation.removeAllListeners();
                rotation.setFloatValues(-90, 0);
                rotation.start();
                rotation.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewCompat.setElevation(view, elevation);
                    }
                });
            }
        });
        return rotation;
    }

    public static Integer[] minifyProfileHeader(final View header, final View divider, final View history){
        //changing heights
        Integer originalHeights[] = {header.getMeasuredHeight(), divider.getMeasuredHeight(), history.getMeasuredHeight()};
        ValueAnimator minifyHeader = ValueAnimator.ofFloat(header.getMeasuredHeight(), Maths.dpToPx(63, header.getContext()));
        ValueAnimator minifyDivider = ValueAnimator.ofFloat(divider.getMeasuredHeight(), 0);
        ValueAnimator expandHistory = ValueAnimator.ofFloat(history.getMeasuredHeight(), history.getMeasuredHeight() + (header.getMeasuredHeight()-Maths.dpToPx(63, header.getContext())));
        //dealing with the profile picture
        header.findViewById(R.id.profilePicture).setPivotY(0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 0.3125f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 0.3125f);
        Double xTranslation = (double) ((header.getMeasuredWidth()/2)*-1) + Maths.dpToPx(44, header.getContext());
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(TRANSLATION_X, xTranslation.floatValue());
        ObjectAnimator scalePhoto = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.profilePicture), scaleX, scaleY, translationX);
        //dealing with name text
        Double yTranslationName = (double) (Maths.dpToPx(135, header.getContext())*-1);
        PropertyValuesHolder translationYName = PropertyValuesHolder.ofFloat(TRANSLATION_Y, yTranslationName.floatValue());
        Double xTranslationName = (double) (Maths.dpToPx(105, header.getContext())*-1);
        PropertyValuesHolder translationXName = PropertyValuesHolder.ofFloat(TRANSLATION_X, xTranslationName.floatValue());
        ObjectAnimator translateName = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.nameText), translationYName, translationXName);
        //dealing with the points text
        Double yTranslationPoints = (double) (Maths.dpToPx(70, header.getContext())*-1);
        PropertyValuesHolder translationYPoints = PropertyValuesHolder.ofFloat(TRANSLATION_Y, yTranslationPoints.floatValue());
        Double xTranslationPoints = (double) (Maths.dpToPx(115, header.getContext())*-1);
        PropertyValuesHolder translationXPoints = PropertyValuesHolder.ofFloat(TRANSLATION_X, xTranslationPoints.floatValue());
        ObjectAnimator translatePoints = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.profile_points), translationYPoints, translationXPoints);


        minifyHeader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                layoutParams.height = (int) val;
                header.setLayoutParams(layoutParams);
            }
        });
        minifyHeader.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animationRunning = false;
            }
        });
        minifyDivider.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = divider.getLayoutParams();
                layoutParams.height = (int) val;
                divider.setLayoutParams(layoutParams);
            }
        });
        expandHistory.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float val = (float) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = history.getLayoutParams();
                layoutParams.height = (int) val;
                history.setLayoutParams(layoutParams);
            }
        });



        minifyHeader.setDuration(800);
        minifyDivider.setDuration(800);
        expandHistory.setDuration(800);
        scalePhoto.setDuration(800);
        translateName.setDuration(800);
        translatePoints.setDuration(800);


        animationRunning = true;
        minifyHeader.start();
        minifyDivider.start();
        expandHistory.start();
        scalePhoto.start();
        translateName.start();
        translatePoints.start();

        return originalHeights;
    }


    public static void expandProfileHeader(final View header, final View divider, final View history, final int originalHeaderHeight, final int originalDividerHeight, final int originalHistoryHeight){
        ValueAnimator expandHeader = ValueAnimator.ofInt(header.getMeasuredHeight(), originalHeaderHeight);
        ValueAnimator expandDivider = ValueAnimator.ofInt(divider.getMeasuredHeight(), originalDividerHeight);
        ValueAnimator minifyHistory = ValueAnimator.ofInt(history.getMeasuredHeight(), originalHistoryHeight);
        header.findViewById(R.id.profilePicture).setPivotY(0f);
        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat(SCALE_X, 1f);
        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat(SCALE_Y, 1f);
        PropertyValuesHolder translationX = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0);
        ObjectAnimator scalePhoto = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.profilePicture), scaleX, scaleY, translationX);
        PropertyValuesHolder translationYName = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);
        PropertyValuesHolder translationXName = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0);
        ObjectAnimator translateName = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.nameText), translationYName, translationXName);
        PropertyValuesHolder translationYPoints = PropertyValuesHolder.ofFloat(TRANSLATION_Y, 0);
        PropertyValuesHolder translationXPoints = PropertyValuesHolder.ofFloat(TRANSLATION_X, 0);
        ObjectAnimator translatePoints = ObjectAnimator.ofPropertyValuesHolder(header.findViewById(R.id.profile_points), translationYPoints, translationXPoints);


        expandHeader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = header.getLayoutParams();
                layoutParams.height = val;
                header.setLayoutParams(layoutParams);
            }
        });
        expandDivider.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = divider.getLayoutParams();
                layoutParams.height = val;
                divider.setLayoutParams(layoutParams);
            }
        });
        minifyHistory.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int val = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = history.getLayoutParams();
                layoutParams.height = val;
                history.setLayoutParams(layoutParams);
            }
        });


        expandHeader.setDuration(800);
        expandDivider.setDuration(800);
        minifyHistory.setDuration(800);
        scalePhoto.setDuration(800);
        translateName.setDuration(800);
        translatePoints.setDuration(800);


        expandHeader.start();
        expandDivider.start();
        minifyHistory.start();
        scalePhoto.start();
        translateName.start();
        translatePoints.start();
    }

    public static void fadeInAnimation(final View view) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(500);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeIn);
    }

    public static Animation fadeOutAnimation(final View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(500);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeOut);

        return fadeOut;
    }

}
