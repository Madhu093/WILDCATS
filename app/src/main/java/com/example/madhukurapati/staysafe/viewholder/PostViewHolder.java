package com.example.madhukurapati.staysafe.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.madhukurapati.staysafe.R;
import com.example.madhukurapati.staysafe.activity.IncidentDetailActivity;
import com.example.madhukurapati.staysafe.models.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PostViewHolder extends RecyclerView.ViewHolder {

    public TextView titleView;
    public TextView authorView;
    public ImageView starView;
    public TextView numStarsView;
    public TextView bodyView;
    public TextView location;
    public de.hdodenhof.circleimageview.CircleImageView authorImageView;

    public PostViewHolder(View itemView) {
        super(itemView);

        titleView = (TextView) itemView.findViewById(R.id.post_title);
        authorView = (TextView) itemView.findViewById(R.id.post_author);
        location = (TextView) itemView.findViewById(R.id.location_name);
        starView = (ImageView) itemView.findViewById(R.id.star);
        numStarsView = (TextView) itemView.findViewById(R.id.post_num_stars);
        bodyView = (TextView) itemView.findViewById(R.id.post_body);
        authorImageView = (de.hdodenhof.circleimageview.CircleImageView) itemView.findViewById(R.id.post_author_photo);
    }

    public void bindToPost(Post post, View.OnClickListener starClickListener) {
        titleView.setText(post.title);
        authorView.setText(post.author);
        numStarsView.setText(String.valueOf(post.starCount));
        bodyView.setText(post.body);
        location.setText(post.cityAndState);
        starView.setOnClickListener(starClickListener);
    }
}
