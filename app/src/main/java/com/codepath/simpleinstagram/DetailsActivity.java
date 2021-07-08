package com.codepath.simpleinstagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.Date;

public class DetailsActivity extends AppCompatActivity {

    private final int REQUEST_CODE = 30;
    public static final String EXTRA_MESSAGE = "com.example.Twitter.MESSAGE";

    Post post;
    ImageView ivMedia;
    TextView tvBody;
    TextView tvRelativeTime;
    TextView tvName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));
        tvBody = findViewById(R.id.tvBody);
        tvName = findViewById(R.id.tvName);
        tvRelativeTime = findViewById(R.id.tvRelativeTime);
        ivMedia = findViewById(R.id.ivMedia);

        tvBody.setText(post.getDescription());
        tvName.setText(post.getUser().getUsername());
        // Bind the post data to the view elements
        Date createdAt = post.getCreatedAt();
        String timeAgo = Post.calculateTimeAgo(createdAt);
        tvRelativeTime.setText(timeAgo);

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivMedia);
            ivMedia.setVisibility(View.VISIBLE);
        } else {
            ivMedia.setVisibility(View.GONE);
        }
    }
}