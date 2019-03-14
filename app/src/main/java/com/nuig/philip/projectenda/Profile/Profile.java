package com.nuig.philip.projectenda.Profile;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nuig.philip.projectenda.Leaderboards.Leaderboards;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.Animations;
import com.nuig.philip.projectenda.Tasks.HistoryLoader;
import com.nuig.philip.projectenda.Tasks.InternetConnection;
import com.nuig.philip.projectenda.Tasks.Locations;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Profile extends AppCompatActivity {

    //todo make the text font customizable

    private Toolbar toolbar;
    private InternetConnection broadcastReceiver;
    private FirebaseUser user;
    private int myLastVisiblePos;
    private Integer originalHeights[];
    private Boolean firstRunDown = true, firstRunUp = false, heightGather = true;

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
        profileName.setText(user.getDisplayName());
        TextView points_text = findViewById(R.id.profile_points);
        String points = String.valueOf(1024) + " " + this.getString(R.string.points);
        points_text.setText(points);
        points_text.setOnClickListener( new View.OnClickListener()
        {
            public void onClick(View v){
                goto_leaderboards(v);
            }
        });

        Locations OB1 = new Locations("Spanish Arch", new SimpleDateFormat("dd/MM/yyyy").format(new Date()), "https://en.wikipedia.org/wiki/Spanish_Arch", "https://upload.wikimedia.org/wikipedia/commons/c/c7/Spanish_Arch.JPG", 53.270260,-9.053810,"The Spanish Arch and the Caoċ Arch in Galway city, Ireland, are two remaining arches on the Ceann an Bhalla. The two arches were part of the extension of the city wall from Martin's Tower to the bank of the River Corrib, as a measure to protect the city's quays, which were in the area once known as the Fish Market.");
        Locations OB2 = new Locations("Galway Cathedral", new SimpleDateFormat("dd/MM/yyyy").format(new Date(118, 0, 10)), "https://en.wikipedia.org/wiki/Cathedral_of_Our_Lady_Assumed_into_Heaven_and_St_Nicholas,_Galway", "https://upload.wikimedia.org/wikipedia/commons/thumb/c/cb/Galway_cathedral.jpg/440px-Galway_cathedral.jpg", 53.275145, -9.057533,"The Cathedral of Our Lady Assumed into Heaven and St Nicholas, commonly known as Galway Cathedral, is a Roman Catholic cathedral in Galway, Ireland, and one of the largest and most impressive buildings in the city. Construction began in 1958 on the site of the old city prison.");
        Locations OB3 = new Locations("Eyre Square", new SimpleDateFormat("dd/MM/yyyy").format(new Date(118, 3, 20)), "https://en.wikipedia.org/wiki/Eyre_Square", "https://upload.wikimedia.org/wikipedia/commons/9/97/Fountain_Galway_01.jpg", 53.274343, -9.049237, "Eyre Square, also known as John F. Kennedy Memorial Park is an inner-city public park in Galway, Ireland. The park is within the city centre, adjoining the nearby shopping area of William Street and Shop Street. Galway railway station is adjacent to Eyre Square.");
        final Locations[] history = {OB1, OB2, OB3, OB1, OB2, OB3, OB1, OB2, OB3, OB1, OB2, OB3};
        final GridView gridView = (GridView)findViewById(R.id.profileHistory);
        final HistoryLoader locationsAdapter = new HistoryLoader(this, history);
        myLastVisiblePos = gridView.getFirstVisiblePosition();
        gridView.setAdapter(locationsAdapter);
        gridView.setOnScrollListener( new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int currentFirstVisPos = view.getFirstVisiblePosition();
                if(currentFirstVisPos > myLastVisiblePos) {
                    //scroll down
                    if(firstRunDown && heightGather) {
                        originalHeights = Animations.minifyProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.profileHistory));
                        firstRunDown = false;
                        firstRunUp = true;
                        heightGather = false;
                    }
                    else if (firstRunDown) {
                        Animations.minifyProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.profileHistory));
                        firstRunDown = false;
                        firstRunUp = true;
                    }
                }
                if(currentFirstVisPos < myLastVisiblePos) {
                    //scroll up
                    if(firstRunUp) {
                        Animations.expandProfileHeader(findViewById(R.id.profileHeader), findViewById(R.id.profileHistory), originalHeights[0], originalHeights[1]);
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

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                refreshData(); // your code
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
