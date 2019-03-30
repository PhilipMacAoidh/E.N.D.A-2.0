package com.nuig.philip.projectenda.Profile;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.support.v7.widget.SearchView;

import com.google.android.gms.common.data.DataHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.R;
import com.nuig.philip.projectenda.Tasks.UserLoader;

import java.util.ArrayList;
import java.util.List;

public class AddFriends extends AppCompatActivity {

    //todo add Search NoItemPage

    private Toolbar toolbar;
    private List<DocumentSnapshot> userList;
    private UserLoader adapter;

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

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final CollectionReference userColl = database.collection("users");
            userColl.orderBy("name", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    final RecyclerView sList = (RecyclerView) findViewById(R.id.friendSearchList);
                    userList = task.getResult().getDocuments();
                    for(int i=0; i<userList.size(); i++){
                        if (userList.get(i).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            userList.remove(i);
                        }
                    }
                    userColl.document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("friends").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            List<DocumentSnapshot> friendList = task.getResult().getDocuments();
                            if(friendList != null && userList!= null) {
                                for (int i = 0; i < userList.size(); i++) {
                                    for (int j = 0; j < friendList.size(); j++) {
                                        if (userList.get(i).getId().equals(friendList.get(j).getId())) {
                                            userList.remove(i);
                                        }
                                    }
                                }
                                adapter = new UserLoader(userList, UserLoader.add);
                                sList.setAdapter(adapter);
                                sList.setLayoutManager(new LinearLayoutManager(AddFriends.this));
                            }
                        }
                    });
                }
            });

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filter(s);
                return false;
            }
        });
    }

    public void filter(String text){
        List<DocumentSnapshot> temp = new ArrayList();
        for(int i=0; i<userList.size(); i++){
            if(userList.get(i).get("name").toString().toLowerCase().contains(text.toLowerCase())) {
                temp.add(userList.get(i));
            }
        }
        //update recyclerview
        updateList(temp);
    }

    public void updateList(List list) {
        RecyclerView sList = (RecyclerView) findViewById(R.id.friendSearchList);
        adapter = new UserLoader(list, UserLoader.add);
        sList.setAdapter(adapter);
        sList.setLayoutManager(new LinearLayoutManager(AddFriends.this));
    }

    public boolean onOptionsItemSelected(MenuItem item){
        AddFriends.this.finish();
        return true;
    }
}
