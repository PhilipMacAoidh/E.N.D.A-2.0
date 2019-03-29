package com.nuig.philip.projectenda.Tasks;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nuig.philip.projectenda.R;

import java.util.HashMap;
import java.util.List;

public class UserLoader extends RecyclerView.Adapter<UserLoader.ViewHolder> {
    public final static int rank = 0;
    public final static int add = 1;

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public TextView userName;
        public TextView userPoints;
        public ImageView userImg;
        public TextView ranking;
        public ImageButton addFriendButton;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            userName = (TextView) itemView.findViewById(R.id.userName);
            userPoints = (TextView) itemView.findViewById(R.id.userPoints);
            userImg = (ImageView) itemView.findViewById(R.id.userImg);
            ranking = (TextView) itemView.findViewById(R.id.ranking);
            addFriendButton = (ImageButton) itemView.findViewById(R.id.addFriendBtn);
        }
    }

    private List<DocumentSnapshot> userList;
    private int version;

    public UserLoader(List<DocumentSnapshot> userList, int version){
        this.userList = userList;
        this.version = version;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public UserLoader.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View userView = inflater.inflate(R.layout.list_item, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(userView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(final UserLoader.ViewHolder viewHolder, final int position) {
        // Set item views based on your views and data model
        TextView userName = viewHolder.userName;
        userName.setText((String) userList.get(position).get("name"));
        if(userList.get(position).getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            userName.setTextColor(0xFFFFCA45);
        }
        TextView userPoints = viewHolder.userPoints;
        userPoints.setText((String) userList.get(position).get("points").toString()+" points");
        Glide.with(viewHolder.userImg).load(userList.get(position).get("imgUrl"))
                .placeholder(R.mipmap.launcher_icon_round)
                .into(viewHolder.userImg);
        final ImageButton addFreindBtn = viewHolder.addFriendButton;
        TextView ranking = viewHolder.ranking;
        ranking.setText(String.valueOf(position+1));

        if(version == rank) {
            addFreindBtn.setVisibility(View.GONE);
            ranking.setVisibility(View.VISIBLE);
        }
        else if(version == add) {
            ranking.setVisibility(View.GONE);
            addFreindBtn.setVisibility(View.VISIBLE);
            addFreindBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    final CollectionReference friendColl = database.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("friends");
                    friendColl.document(userList.get(position).getId()).set(new HashMap<>());
                    addFreindBtn.setBackgroundColor(addFreindBtn.getContext().getResources().getColor(R.color.greyText));
                    addFreindBtn.setEnabled(false);
                }
            });
        }

    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return userList.size();
    }
}
