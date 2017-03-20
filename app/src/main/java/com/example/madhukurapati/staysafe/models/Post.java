package com.example.madhukurapati.staysafe.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

// [START post_class]
@IgnoreExtraProperties
public class Post {

    public String uid;
    public String imageEncoded;
    public String author;
    public String title;
    public String body;
    public String profileImageEncoded;
    public int starCount = 0;
    public Map<String, Boolean> stars = new HashMap<>();

    public String cityAndState;

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid, String author, String title, String body, String profileImageEncoded, String cityAndState) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.profileImageEncoded = profileImageEncoded;
        this.cityAndState = cityAndState;
    }

    public Post(String uid, String author, String title, String body, String imageEncoded, String profileImageEncoded, String cityAndState) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.imageEncoded = imageEncoded;
        this.profileImageEncoded = profileImageEncoded;
        this.cityAndState = cityAndState;
    }

    // [START post_to_map]
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("imageEncoded", imageEncoded);
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("profileImage", profileImageEncoded);
        result.put("cityAndState", cityAndState);
        return result;
    }

    @Exclude
    public Map<String, Object> toMapWithoutimageEncoded() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("stars", stars);
        result.put("profileImage", profileImageEncoded);
        result.put("cityAndState", cityAndState);
        return result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageEncoded() {
        return imageEncoded;
    }

    public void setImageEncoded(String imageEncoded) {
        this.imageEncoded = imageEncoded;
    }

    public String getAuthor() {
        return author;
    }

    public String getCityAndState() {
        return cityAndState;
    }

    public void setCityAndState(String cityAndState) {
        this.cityAndState = cityAndState;
    }

    public void setAuthor(String author) {
        this.author = author;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public String getProfileImageEncoded() {
        return profileImageEncoded;
    }

    public void setProfileImageEncoded(String profileImageEncoded) {
        this.profileImageEncoded = profileImageEncoded;
    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }
    // [END post_to_map]

}
// [END post_class]
