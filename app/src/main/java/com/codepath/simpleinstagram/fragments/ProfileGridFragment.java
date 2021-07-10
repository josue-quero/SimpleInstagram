package com.codepath.simpleinstagram.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.simpleinstagram.EndlessRecyclerViewScrollListener;
import com.codepath.simpleinstagram.Post;
import com.codepath.simpleinstagram.PostsAdapter;
import com.codepath.simpleinstagram.ProfileAdapter;
import com.codepath.simpleinstagram.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfileGridFragment extends Fragment {

    public static final String TAG = "ProfileGridFragment";
    protected EndlessRecyclerViewScrollListener scrollListener;
    private RecyclerView rvMyPosts;
    public static SwipeRefreshLayout swipeContainer;
    private TextView tvName;
    private ImageView ivProfileImage;
    private Button btnEdit;
    protected ProfileAdapter adapter;
    protected List<Post> allPosts;
    View view;

    public ProfileGridFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_grid, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        //viewBinding.myToolbar.inflateMenu(R.menu.menu_main);
        rvMyPosts = view.findViewById(R.id.rvPostsProfile);
        btnEdit = view.findViewById(R.id.btnEdit);
        tvName = view.findViewById(R.id.tvName);
        ivProfileImage = view.findViewById(R.id.ivProfileImage);
        tvName.setText(ParseUser.getCurrentUser().getUsername());
        ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profile_image");
        if (profileImage != null) {
            Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
        }
        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new ProfileAdapter(getContext(), allPosts);
        // set the adapter on the recycler view
        rvMyPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        rvMyPosts.setLayoutManager(gridLayoutManager);
        // query posts from Parstagram
        queryPosts();

        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                allPosts.clear();
                queryPosts();
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(totalItemsCount);
            }
        };
        // Adds the scroll listener to RecyclerView
        rvMyPosts.addOnScrollListener(scrollListener);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                // Compose icon has been selected
                //Navigate to compose activity
                onPickPhoto(v);
            }
        });
    }

    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Bring up gallery to select a photo
            startActivityForResult(intent, PICK_PHOTO_CODE);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();
            Bitmap selectedImage = loadFromUri(photoUri);
            // Load the selected image into a preview
            //ivProfileImage.setImageBitmap(selectedImage);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.PNG, 60, stream);
            byte[] dataImage = stream.toByteArray();

            //convert byte[] to parse file
            ParseFile file = new ParseFile("ProfileImage.png", dataImage);

            saveProfilePhoto(file);
        }
    }


    public void saveProfilePhoto(ParseFile photoFile) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            // Other attributes than "email" will remain unchanged!
            currentUser.put("profile_image", photoFile);
            Log.i(TAG, "Going good");

            // Saves the object.
            currentUser.saveInBackground(e -> {
                if(e==null){
                    //Save successfull
                    Log.i(TAG, "Save Successful");
                    Toast.makeText(getContext(), "Save Successful", Toast.LENGTH_SHORT).show();
                    ParseFile profileImage = ParseUser.getCurrentUser().getParseFile("profile_image");
                    if (profileImage != null) {
                        Glide.with(getContext()).load(profileImage.getUrl()).circleCrop().into(ivProfileImage);
                    }
                }else{
                    // Something went wrong while saving
                    Log.i(TAG, "Save unsuccessful", e);
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadNextDataFromApi(int offset) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // set skip previous posts
        query.setSkip(offset);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data

                // Now we call setRefreshing(false) to signal refresh has finished
                allPosts.addAll(offset, posts);
                adapter.notifyDataSetChanged();
                scrollListener.resetState();
            }
        });
    }

    protected void queryPosts() {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        Log.i(TAG, "KEY_USER: " + ParseUser.getCurrentUser());
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED_AT);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post for profile: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data

                // Now we call setRefreshing(false) to signal refresh has finished
                swipeContainer.setRefreshing(false);
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}