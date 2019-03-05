package com.nuig.philip.projectenda.Login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nuig.philip.projectenda.Challenge_Page.MainActivity;
import com.nuig.philip.projectenda.Profile.Settings;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.Toasts;
import com.nuig.philip.projectenda.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

import static com.nuig.philip.projectenda.Challenge_Page.Challenge_fragment.view;

public class SignIn extends AppCompatActivity {

    private TextView screenTitle;
    private EditText inputEmail, inputPassword, inputUsername;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSwitch, btnSubmit, btnReset, btnBack;
    private boolean viewLogin = true, viewReset = false;
    private ImageView loginIcon;
    private Uri filePath;
    private FirebaseStorage storage;
    private StorageReference storageReference, ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignIn.this, MainActivity.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        loginIcon = (ImageView) findViewById(R.id.loginIcon);
        loginIcon.setClickable(true);
        screenTitle = (TextView) findViewById(R.id.screenName);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        inputUsername = (EditText) findViewById(R.id.username);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSwitch = (Button) findViewById(R.id.switch_screen_button);
        btnSubmit = (Button) findViewById(R.id.submit_button);
        btnReset = (Button) findViewById(R.id.goto_reset_button);
        btnBack = (Button) findViewById(R.id.back_btn);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        loginIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!viewLogin && !viewReset) {
                    CropImage.activity()
                            .setAspectRatio(1,1)
                            .setFixAspectRatio(true)
                            .setActivityTitle("Image Cropper")
                            .setAutoZoomEnabled(true)
                            .setAllowFlipping(false)
                            .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                            .start(SignIn.this);
                }
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewLogin = !viewLogin;
                if (viewLogin) {
                    openLogin();
                } else {
                    openRegistration();
                }
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReset();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.passwordInput).setVisibility(View.VISIBLE);
                findViewById(R.id.forgot_password_text).setVisibility(View.GONE);
                btnSwitch.setVisibility(View.VISIBLE);
                btnReset.setVisibility(View.VISIBLE);
                btnBack.setVisibility(View.GONE);
                if (viewLogin) {
                    openLogin();
                } else {
                    openRegistration();
                }
                viewReset = false;
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();
                String username = inputUsername.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toasts.failToast("Enter email address!", SignIn.this, Toast.LENGTH_SHORT);
                    return;
                }

                if (!viewReset && TextUtils.isEmpty(password)) {
                    Toasts.failToast("Enter password!", SignIn.this, Toast.LENGTH_SHORT);
                    return;
                }

                if (!viewReset && !viewLogin && password.length() < 6) {
                    Toasts.failToast("Password too short, enter minimum 6 characters!", SignIn.this, Toast.LENGTH_SHORT);
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                if(viewReset) {
                    submitReset();
                }else if(viewLogin) {
                    submitLogin(email, password);
                } else {
                    submitRegistration(email, password, username);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    loginIcon.setImageBitmap(bitmap);
                    loginIcon.setBackgroundResource(R.color.transparent);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                result.getError();
            }
        }
    }

    private void uploadImage() {
        if(filePath != null)
        {
            ref = storageReference.child(auth.getUid().toString()+"/ProfilePictures/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasts.failToast("Error getting image", SignIn.this, Toast.LENGTH_SHORT);
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
                                Log.i("ME", task.getResult().toString());

                                UserProfileChangeRequest addImageURL = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(task.getResult())
                                        .build();
                                auth.getCurrentUser().updateProfile(addImageURL);
                            }
                        }
                    });
        }
    }

    private void openLogin() {
        CardView cardView = findViewById(R.id.inputCard);

        if(viewReset) {
            Animations.bottomToTopFlip(loginIcon, 4);
            Animations.bottomToTopFlip(cardView).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    screenTitle.setText(R.string.login_title);
                    btnSubmit.setText(R.string.login_button);
                    btnSwitch.setText(R.string.login_to_register_btn);
                    inputUsername.setVisibility(View.GONE);
                    inputPassword.setVisibility(View.VISIBLE);
                    loginIcon.setImageResource(R.mipmap.launcher_icon_round);
                    loginIcon.setBackgroundResource(R.color.transparent);
                }
            });
        } else {
            Animations.leftToRightFlip(loginIcon, 4);
            Animations.leftToRightFlip(cardView).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    screenTitle.setText(R.string.login_title);
                    btnSubmit.setText(R.string.login_button);
                    btnSwitch.setText(R.string.login_to_register_btn);
                    inputUsername.setVisibility(View.GONE);
                    inputPassword.setVisibility(View.VISIBLE);
                    loginIcon.setImageResource(R.mipmap.launcher_icon_round);
                    loginIcon.setBackgroundResource(R.color.transparent);
                }
            });
        }
    }

    private void submitLogin(String email, final String password) {
        //authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                inputPassword.setError(getString(R.string.min_password));
                            } else {
                                Toasts.failToast(getString(R.string.failed_login), SignIn.this, Toast.LENGTH_SHORT);
                            }
                        } else {
                            Toasts.successToast("Welcome, "+auth.getCurrentUser().getDisplayName(), SignIn.this, Toast.LENGTH_LONG);
                            Intent intent = new Intent(SignIn.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    private void openRegistration() {
        CardView cardView = findViewById(R.id.inputCard);

        if(viewReset) {
            Animations.bottomToTopFlip(loginIcon, 4);
            Animations.bottomToTopFlip(cardView).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    screenTitle.setText(R.string.register_title);
                    btnSubmit.setText(R.string.register_button);
                    btnSwitch.setText(R.string.registration_to_login_btn);
                    inputUsername.setVisibility(View.VISIBLE);
                    inputPassword.setVisibility(View.VISIBLE);
                    loginIcon.setImageResource(R.drawable.upload_image_icon);
                    loginIcon.setBackgroundResource(R.mipmap.launcher_icon_round);
                }
            });
        } else {
            Animations.rightToLeftFlip(loginIcon, 4);
            Animations.rightToLeftFlip(cardView).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    screenTitle.setText(R.string.register_title);
                    btnSubmit.setText(R.string.register_button);
                    btnSwitch.setText(R.string.registration_to_login_btn);
                    inputUsername.setVisibility(View.VISIBLE);
                    inputPassword.setVisibility(View.VISIBLE);
                    loginIcon.setImageResource(R.drawable.upload_image_icon);
                    loginIcon.setBackgroundResource(R.mipmap.launcher_icon_round);
                }
            });
        }
    }

    private void submitRegistration(String email, final String password, final String username) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignIn.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toasts.failToast(task.getException().getMessage(), SignIn.this, Toast.LENGTH_SHORT);
                        } else {
                            UserProfileChangeRequest profileCreation = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build();
                            uploadImage();
                            auth.getCurrentUser().updateProfile(profileCreation)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressBar.setVisibility(View.GONE);
                                                Toasts.successToast("Account created!", SignIn.this, Toast.LENGTH_LONG);
                                                startActivity(new Intent(SignIn.this, MainActivity.class));
                                                finish();
                                            }
                                        }
                                    });
                        }
                    }
                });
        //todo create new activity for getting name, picture, DoB, gender (Vertical Stepper)
    }

    private void openReset() {
        CardView cardView = findViewById(R.id.inputCard);

        Animations.topToBottomFlip(loginIcon, 4);
        Animations.topToBottomFlip(cardView).addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewReset = true;
                inputPassword.setVisibility(View.GONE);
                inputUsername.setVisibility(View.GONE);
                findViewById(R.id.forgot_password_text).setVisibility(View.VISIBLE);
                btnSwitch.setVisibility(View.GONE);
                btnReset.setVisibility(View.GONE);
                btnBack.setVisibility(View.VISIBLE);
                screenTitle.setText(R.string.forgot_password);
                btnSubmit.setText(R.string.reset_btn);
                loginIcon.setImageResource(R.mipmap.launcher_icon_round);
                loginIcon.setBackgroundResource(R.color.transparent);
            }
        });
    }

    private void submitReset() {

        String email = inputEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toasts.failToast("Enter your registered email id", SignIn.this, Toast.LENGTH_SHORT);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toasts.successToast("We have sent you instructions to reset your password!", SignIn.this, Toast.LENGTH_SHORT);
                        } else {
                            Toasts.failToast("Failed to send reset email!", SignIn.this, Toast.LENGTH_SHORT);
                        }

                        progressBar.setVisibility(View.GONE);
                        }
                    });
    }
}