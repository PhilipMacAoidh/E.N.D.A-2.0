package com.nuig.philip.projectenda.Profile;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.support.v7.widget.SearchView;

import com.nuig.philip.projectenda.R;

public class AddFriends extends AppCompatActivity {

    //todo add Search NoItemPage

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends);

        toolbar = findViewById(R.id.addFriendsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EditText searchEditText = findViewById(R.id.searchFriends).findViewById(android.support.v7.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_pressed));
        searchEditText.setHintTextColor(getResources().getColor(R.color.background_material_light));
        SearchView searchBar = findViewById(R.id.searchFriends);
        searchBar.requestFocus();
    }

    public boolean onOptionsItemSelected(MenuItem item){
        AddFriends.this.finish();
        return true;
    }
}
