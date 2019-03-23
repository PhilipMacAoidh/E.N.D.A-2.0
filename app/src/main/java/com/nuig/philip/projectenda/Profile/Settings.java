package com.nuig.philip.projectenda.Profile;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

    //Todo update UI

    private Toolbar toolbar;
    private Uri filePath;
    private ImageView changePPBtn;
    private boolean photoChanged = false, usernameChanged = false, distanceChanged = false, fontChanged = false, countryChanged = false, distFirstRun = true, distSecondRun = true, fontFirstRun = true, fontSecondRun = true, countryFirstRun = true, countrySecondRun = true;
    private FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference, ref;
    private InternetConnection broadcastReceiver;
    private Button actionBtn;
    private EditText inputUsername;
    private TextView fontTester;
    private Spinner distanceSpinner, fontSpinner, nationalitySpinner;
    private DocumentReference userDoc;
    private int newDistance;
    private String newFont, newCountry;
    private Typeface defaultFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(getString(R.string.setting_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        user = FirebaseAuth.getInstance().getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        userDoc = database.collection("users").document(user.getUid());

        changePPBtn = findViewById(R.id.changePPBtn);
        inputUsername = (EditText) findViewById(R.id.username);
        distanceSpinner = findViewById(R.id.distanceSpinner);
        fontSpinner = findViewById(R.id.fontSpinner);
        fontTester = findViewById(R.id.fontTester);
        defaultFont = fontTester.getTypeface();
        nationalitySpinner = findViewById(R.id.nationalitySpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.distance_options));
        distanceSpinner.setAdapter(adapter);
        ArrayAdapter<String> fontAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.font_options));
        fontSpinner.setAdapter(fontAdapter);
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.countries));
        nationalitySpinner.setAdapter(nationalityAdapter);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        inputUsername.setText((String) document.getData().get("name"));
                        inputUsername.addTextChangedListener(usernameFilter);

                        switch(Math.round((long) document.getData().get("distance"))) {
                            case 1:
                                distanceSpinner.setSelection(0);
                                distSecondRun = false;
                                break;
                            case 2:
                                distanceSpinner.setSelection(1);
                                break;
                            case 3:
                                distanceSpinner.setSelection(2);
                                break;
                            case 5:
                                distanceSpinner.setSelection(3);
                                break;
                            case 10:
                                distanceSpinner.setSelection(4);
                                break;
                            case 15:
                                distanceSpinner.setSelection(5);
                                break;
                            case 20:
                                distanceSpinner.setSelection(6);
                                break;
                            case 25:
                                distanceSpinner.setSelection(7);
                                break;
                        }

                        switch(document.getData().get("font").toString()) {
                            case "none":
                                fontSpinner.setSelection(0);
                                fontTester.setTypeface(defaultFont);
                                fontSecondRun = false;
                                break;
                            case "friday_vibes":
                                fontSpinner.setSelection(1);
                                fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.friday_vibes));
                                break;
                            case "modista_script":
                                fontSpinner.setSelection(2);
                                fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.modista_script));
                                break;
                            case "queenstown_signature":
                                fontSpinner.setSelection(3);
                                fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.queenstown_signature));
                                break;
                            case "rockness":
                                fontSpinner.setSelection(4);
                                fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.rockness));
                                break;
                            case "simplicity":
                                fontSpinner.setSelection(5);
                                fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.simplicity));
                                break;
                        }

                        switch(document.getData().get("country").toString()) {
                            case "Ireland":
                                nationalitySpinner.setSelection(0);
                                countrySecondRun = false;
                                break;
                            case "America":
                                nationalitySpinner.setSelection(1);
                                break;
                            case "England":
                                nationalitySpinner.setSelection(2);
                                break;
                            case "Japan":
                                nationalitySpinner.setSelection(3);
                                break;
                            case "Scotland":
                                nationalitySpinner.setSelection(4);
                                break;
                            case "Australia":
                                nationalitySpinner.setSelection(5);
                                break;
                        }
                    } else {
                        Log.d("Settings", "No document found with this userID");
                    }
                } else {
                    Log.d("Settings", "get failed with ", task.getException());
                }
            }
        });

        distanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        newDistance = 1;
                        break;
                    case 1:
                        newDistance = 2;
                        break;
                    case 2:
                        newDistance = 3;
                        break;
                    case 3:
                        newDistance = 5;
                        break;
                    case 4:
                        newDistance = 10;
                        break;
                    case 5:
                        newDistance = 15;
                        break;
                    case 6:
                        newDistance = 20;
                        break;
                    case 7:
                        newDistance = 25;
                        break;
                }
                if (!distFirstRun) {
                    if(!distSecondRun) {
                        distanceChanged = true;
                        switchToSave();
                    }
                    distSecondRun = false;
                }else{
                    switchToSignOut();
                }
                distFirstRun = false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        fontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0:
                            newFont = "none";
                            fontTester.setTypeface(defaultFont);
                            break;
                        case 1:
                            newFont = "friday_vibes";
                            fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.friday_vibes));
                            break;
                        case 2:
                            newFont = "modista_script";
                            fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.modista_script));
                            break;
                        case 3:
                            newFont = "queenstown_signature";
                            fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.queenstown_signature));
                            break;
                        case 4:
                            newFont = "rockness";
                            fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.rockness));
                            break;
                        case 5:
                            newFont = "simplicity";
                            fontTester.setTypeface(ResourcesCompat.getFont(Settings.this, R.font.simplicity));
                            break;
                    }
                if (!fontFirstRun) {
                    if(!fontSecondRun) {
                        fontChanged = true;
                        switchToSave();
                    }
                    fontSecondRun = false;
                }else{
                    switchToSignOut();
                }
                fontFirstRun = false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        nationalitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        newCountry = "Ireland";
                        break;
                    case 1:
                        newCountry = "America";
                        break;
                    case 2:
                        newCountry = "England";
                        break;
                    case 3:
                        newCountry = "Japan";
                        break;
                    case 4:
                        newCountry = "Scotland";
                        break;
                    case 5:
                        newCountry = "Australia";
                        break;
                }
                if (!countryFirstRun) {
                    if(!countrySecondRun) {
                        countryChanged = true;
                        switchToSave();
                    }
                    countrySecondRun = false;
                }else{
                    switchToSignOut();
                }
                countryFirstRun = false;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

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
                    userDoc.update("name", inputUsername.getText().toString());
                }
                if(distanceChanged) {
                    userDoc.update("distance", newDistance);
                }
                if(fontChanged) {
                    userDoc.update("font", newFont);
                }
                if(countryChanged) {
                    userDoc.update("country", newCountry);
                }
                if (!usernameChanged && !photoChanged && !distanceChanged && !fontChanged && !countryChanged) {
                    MainActivity.signOut();
                    Toasts.successToast("Goodbye", Settings.this, Toast.LENGTH_SHORT);
                }
                if(photoChanged || usernameChanged || distanceChanged || fontChanged || countryChanged) {
                    Toasts.successToast(getString(R.string.save_toast_text), Settings.this, Toast.LENGTH_SHORT);
                    photoChanged = false; usernameChanged = false; distanceChanged = false; fontChanged = false; countryChanged = false;
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
        registerReceiver(broadcastReceiver, filter);
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
            ref = storageReference.child("Users/"+user.getUid().toString()+"/ProfilePictures/"+ UUID.randomUUID().toString());
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
                                userDoc.update("imgUrl", task.getResult().toString());
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
