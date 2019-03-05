package com.nuig.philip.projectenda.Tasks;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.support.v4.view.ViewCompat;
import android.view.View;

public class Animations{

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
}
