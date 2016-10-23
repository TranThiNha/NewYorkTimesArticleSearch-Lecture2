package com.example.thinha.newyorktimesarticlesearch.model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ThiNha on 10/20/2016.
 */
public class Article implements Serializable {

    @SerializedName("web_url")
    String webUrl;
    @SerializedName("snippet")
    String headLine;

    @SerializedName("multimedia")
    JsonArray multimedia;

    public JsonArray getMultimedia() {
        return multimedia;
    }

    public String getThumbNail() {
        if (multimedia.size() == 0)
            return "";
        else
            return "http://www.nytimes.com/" + multimedia.get(0).getAsJsonObject().get("url").getAsString();

    }
    public String getHeadLine() {
        return headLine;
    }

    public String getWebUrl() {
        return webUrl;
    }

    //Dont need
    public Article(JsonObject jsonObject)
    {
        Gson gson = new Gson();
        Article article = gson.fromJson(jsonObject, Article.class);
        this.webUrl = article.getWebUrl();
        this.headLine = article.getHeadLine();
        this.multimedia = article.getMultimedia();
    }

    public static ArrayList<Article> fromJsonArray(JsonArray array)
    {
        ArrayList<Article> results = new ArrayList<>();
        Gson gson = new Gson();
        for (int i=0;i<array.size();i++)
        {
            results.add(gson.fromJson(array.get(i),Article.class));
        }
        return results;
    }
}
