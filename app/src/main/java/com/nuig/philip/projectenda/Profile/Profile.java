package com.nuig.philip.projectenda.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.collect.Lists;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.Leaderboards.Leaderboards;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.HistoryLoader;
import com.nuig.philip.projectenda.Tasks.InternetConnection;
import com.nuig.philip.projectenda.Tasks.Locations;
import java.util.List;
import java.util.Map;

    //todo make this page adaptable to view other profiles

public class Profile extends AppCompatActivity {

    private Toolbar toolbar;
    private InternetConnection broadcastReceiver;
    private FirebaseUser user;
    private int myLastVisiblePos;
    private Integer originalHeights[];
    private Boolean firstRunDown = true, firstRunUp = false, heightGather = true, mostRecent = true;
    private Locations[] historyArray;
    private HistoryLoader locationsAdapter;
    private GridView gridView;
    private String font;
    private List<DocumentSnapshot> history;
    private DocumentReference userDoc;
    private SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Bundle sIS = savedInstanceState;

        toolbar = findViewById(R.id.profileToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(this.getString(R.string.profile_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton settings_button = findViewById(R.id.settingsButton);
        settings_button.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){goto_settings(v);
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();

        getProfilePicture();

        ImageButton add_friends_button = findViewById(R.id.addFriendButton);
        add_friends_button.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){
                goto_add_friends(v);
            }
        });

        TextView profileName = findViewById(R.id.nameText);
        final TextView points_text = findViewById(R.id.profile_points);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        userDoc = database.collection("users").document(user.getUid());
        getUserInfo(profileName, points_text);
        points_text.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){
                goto_leaderboards(v);
            }
        });

        final TextView historyDirection = findViewById(R.id.historyDirection);
        findViewById(R.id.historyLabel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mostRecent) {
                    mostRecent = false;
                    historyDirection.setText("(Oldest First)");
                }
                else if (!mostRecent) {
                    mostRecent = true;
                    historyDirection.setText("(Newest First)");
                }
                history = Lists.reverse(history);
                Animation fade = Animations.fadeOutAnimation(gridView);
                fade.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        gridView.setVisibility(View.INVISIBLE);
                        createGridView();
                        Animations.fadeInAnimation(gridView);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
            }
        });

        userDoc.collection("history").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        history = task.getResult().getDocuments();
                        history = Lists.reverse(history);
                        createGridView();
                    }
                });
        gridView = (GridView)findViewById(R.id.profileHistory);
        myLastVisiblePos = gridView.getFirstVisiblePosition();
        gridView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int currentFirstVisPos = view.getFirstVisiblePosition();
                if(currentFirstVisPos > myLastVisiblePos) {
                    //scroll down
                    if(firstRunDown && heightGather) {
                        originalHeights = Animations.minifyProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.historyLabel), findViewById(R.id.profileHistory));
                        firstRunDown = false;
                        firstRunUp = true;
                        heightGather = false;
                    }
                    else if (firstRunDown) {
                        Animations.minifyProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.historyLabel), findViewById(R.id.profileHistory));
                        firstRunDown = false;
                        firstRunUp = true;
                    }
                }
                if(currentFirstVisPos < myLastVisiblePos) {
                    //scroll up
                    if(firstRunUp && !Animations.animationRunning) {
                        Animations.expandProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.historyLabel), findViewById(R.id.profileHistory), originalHeights[0], originalHeights[1], originalHeights[2]);
                        firstRunUp = false;
                        firstRunDown = true;
                    }
                }
                myLastVisiblePos = currentFirstVisPos;
            }
            @Override
            public void onScrollStateChanged(AbsListView view, int firstVisibleItem) {}
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                Dialog dialog = new Dialog(Profile.this);
                dialog.setContentView(locationsAdapter.getDialog(position, (ViewGroup) view.findViewById(R.id.cardView), dialog.getWindow().getDecorView(), sIS));
                WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
                layoutParams.copyFrom(dialog.getWindow().getAttributes());
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(layoutParams);
            }
        });

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                userDoc.collection("history").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                history = task.getResult().getDocuments();
                                if(mostRecent) {
                                    history = Lists.reverse(history);
                                }
                                createGridView();
                                getProfilePicture();
                                getUserInfo((TextView) findViewById(R.id.nameText), (TextView) findViewById(R.id.profile_points));
                            }
                        });
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getProfilePicture();

        TextView profileName = findViewById(R.id.nameText);
        profileName.setText(user.getDisplayName());

        try {
            broadcastReceiver = new InternetConnection(this, (ImageView) findViewById(R.id.profilePicture), user.getPhotoUrl().toString());
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
            registerReceiver(broadcastReceiver, filter);
        } catch(Exception e){}
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (Exception e){}
    }

    public void getUserInfo(final TextView username, final TextView points_text) {
        userDoc.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                points_text.setText(document.getData().get("points").toString()+" points");
                                username.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                font = document.getData().get("font").toString();
                                if(historyArray != null) {
                                    createGridView();
                                }
                            } else {
                                Log.d("Profile", "No document found with this userID");
                            }
                        } else {
                            Log.d("Profile", "userDoc.get() failed with ", task.getException());
                        }
                    }
                });
    }

    public void createGridView() {
        historyArray = new Locations[history.size()];
        for(int i=0; i<history.size(); i++) {
            Map ref = history.get(i).getData();
            historyArray[i] = new Locations( (String)ref.get("name"), (String)ref.get("date"), (String) ref.get("time"), (String)ref.get("wiki"), (String)ref.get("imgURL"), (Double)ref.get("latitude"), (Double)ref.get("longitude"), (String)ref.get("info"));
            locationsAdapter = new HistoryLoader(Profile.this, historyArray, font);
            gridView.setAdapter(locationsAdapter);
        }
    }

    public void getProfilePicture() {
        try {
            Glide.with(this).load(user.getPhotoUrl().toString())
                    .placeholder(R.drawable.loading_image)
                    .into((ImageView) findViewById(R.id.profilePicture));
        } catch (Exception e){
            ((ImageView) findViewById(R.id.profilePicture)).setImageResource(R.mipmap.launcher_icon_round);
            ((ImageView) findViewById(R.id.profilePicture)).setBackgroundResource(R.color.transparent);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Profile.this.finish();
        return true;
    }

    public void goto_settings(View v) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public void goto_add_friends(View v) {
        Intent intent = new Intent(this, AddFriends.class);
        startActivity(intent);
    }

    public void goto_leaderboards(View v) {
        Intent intent = new Intent(this, Leaderboards.class);
        startActivity(intent);
    }
}
