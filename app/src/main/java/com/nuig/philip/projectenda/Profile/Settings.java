package com.nuig.philip.projectenda.Profile;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nuig.philip.projectenda.Challenge_Page.MainActivity;
import com.nuig.philip.projectenda.Tasks.InternetConnection;
import com.nuig.philip.projectenda.Tasks.Toasts;
import com.nuig.philip.projectenda.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.UUID;

public class Settings extends AppCompatActivity {

    //todo change to flat settings

    private Toolbar toolbar;
    private Uri filePath;
    private ImageView changePPBtn;
    private boolean photoChanged = false, usernameChanged = false;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference, ref;
    private InternetConnection broadcastReceiver;
    private Button actionBtn;
    private EditText inputUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.setting_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        changePPBtn = findViewById(R.id.changePPBtn);
        inputUsername = (EditText) findViewById(R.id.username);
        inputUsername.addTextChangedListener(usernameFilter);

        user = FirebaseAuth.getInstance().getCurrentUser();

        try {
            Glide.with(this).load(user.getPhotoUrl().toString()).into(changePPBtn);
        } catch (Exception e){
            changePPBtn.setBackgroundResource(R.mipmap.launcher_icon_round);
        }

        changePPBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            CropImage.activity()
                    .setAspectRatio(1,1)
                    .setFixAspectRatio(true)
                    .setActivityTitle("Image Cropper")
                    .setAutoZoomEnabled(true)
                    .setAllowFlipping(false)
                    .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                    .start(Settings.this);
            }
        });

        actionBtn = findViewById(R.id.sign_out);

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(photoChanged) {
                    uploadImage();
                }
                if(usernameChanged) {
                    user.updateProfile(new UserProfileChangeRequest.Builder()
                            .setDisplayName(inputUsername.getText().toString())
                            .build());
                }
                if (!usernameChanged && !photoChanged) {
                    MainActivity.signOut();
                    Toasts.successToast("Goodbye", Settings.this, Toast.LENGTH_SHORT);
                }

                if(photoChanged || usernameChanged) {
                    Toasts.successToast(getString(R.string.save_toast_text), Settings.this, Toast.LENGTH_SHORT);
                    photoChanged = false; usernameChanged = false;
                    switchToSignOut();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        if(photoChanged || usernameChanged) {
            Toasts.failToast("Your changes haven't been saved!", Settings.this, Toast.LENGTH_LONG);
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        broadcastReceiver = new InternetConnection(this, (ImageView) findViewById(R.id.profilePicture), getString(R.string.profile_picture_url));
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, filter);;
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                photoChanged = true;
                switchToSave();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    changePPBtn.setImageBitmap(bitmap);
                    changePPBtn.setBackgroundResource(R.color.transparent);
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

    public void switchToSave() {
        actionBtn.setText(R.string.save_btn);
        actionBtn.setBackgroundResource(R.color.colorPrimary);
    }

    public void switchToSignOut() {
        actionBtn.setText(R.string.sign_out);
        actionBtn.setBackgroundResource(R.color.colorAccent);
    }

    private void uploadImage() {
        if(filePath != null)
        {
            ref = storageReference.child(user.getUid().toString()+"/ProfilePictures/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasts.failToast("Error getting image", Settings.this, Toast.LENGTH_SHORT);
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

                                UserProfileChangeRequest addImageURL = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(task.getResult())
                                        .build();
                                user.updateProfile(addImageURL);
                            }
                        }
                    });
        }
    }

    private TextWatcher usernameFilter = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(count > 0) {
                usernameChanged = true;
                switchToSave();
            }
            if(count == 0) {
                usernameChanged = false;
                switchToSignOut();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void afterTextChanged(Editable s) {}
    };

    public boolean onOptionsItemSelected(MenuItem item){
        Settings.this.finish();
        return true;
    }
}
