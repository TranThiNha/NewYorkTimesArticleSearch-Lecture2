
        package com.example.thinha.newyorktimesarticlesearch.activities;


        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.provider.Settings;
        import android.support.v4.view.MenuItemCompat;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.support.v7.widget.RecyclerView;
        import android.support.v7.widget.SearchView;
        import android.support.v7.widget.StaggeredGridLayoutManager;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import com.example.thinha.newyorktimesarticlesearch.R;
        import com.example.thinha.newyorktimesarticlesearch.Utils.EndlessRecyclerViewScrollListener;
        import com.example.thinha.newyorktimesarticlesearch.adapter.ArticleAdapter;
        import com.example.thinha.newyorktimesarticlesearch.dialog.SortDialog;
        import com.example.thinha.newyorktimesarticlesearch.model.Article;
        import com.example.thinha.newyorktimesarticlesearch.model.Constant;
        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;
        import com.google.gson.JsonArray;
        import com.google.gson.JsonObject;
        import com.loopj.android.http.AsyncHttpClient;
        import com.loopj.android.http.JsonHttpResponseHandler;
        import com.loopj.android.http.RequestParams;
        import com.loopj.android.http.TextHttpResponseHandler;

        import org.json.JSONArray;
        import org.json.JSONException;
        import org.json.JSONObject;

        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Map;

        import butterknife.BindView;
        import butterknife.ButterKnife;
        import cz.msebera.android.httpclient.Header;
        import okhttp3.Response;

        public class MainActivity extends AppCompatActivity {

            private StaggeredGridLayoutManager mLayoutManager;
            @BindView(R.id.rvResult)
            RecyclerView rvResult;

            @BindView(R.id.pbLoading)
            ProgressBar pbLoading;

            @BindView(R.id.pbLoadMore)
            ProgressBar pbLoadingMore;

            ArticleAdapter adapter;

            int pageNumber = 1;
            MenuItem searchItem ,sortItem;
            Map map;

            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_main);
                ButterKnife.bind(this);

                if (!isNetworkAvailable())
                    createCheckNetworkDialog();

                adapter = new ArticleAdapter();
                rvResult.setAdapter(adapter);
                mLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
                rvResult.setLayoutManager(mLayoutManager);
                map = new HashMap();

                load(map);
                rvResult.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount) {
                        map.put("page", String.valueOf(page));
                        loadMore(map);
                    }
                });
            adapter.setListClickListener(new ArticleAdapter.ListClickListener() {
                @Override
                public void onArticleItemClick(Article article) {
                    Intent intent = new Intent(getApplicationContext(),ArticleActivity.class);
                    intent.putExtra("web_url", article.getWebUrl());
                    startActivity(intent);
                }
            });


    }


            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_actionbar, menu);
                searchItem = menu.findItem(R.id.action_search);
                sortItem = menu.findItem(R.id.action_sort);

                final SearchView searchView1 = (SearchView) MenuItemCompat.getActionView(sortItem);
                final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        map.clear();
                        map.put("q", query);
                        map.put("page", "0");
                        load(map);
                        searchView.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return false;
                    }
                });

                return super.onCreateOptionsMenu(menu);

            }



            @Override
            public boolean onOptionsItemSelected(MenuItem item) {
                int id = item.getItemId();

                if ( id == R.id.action_search)
                {

                }
                else if (id == R.id.action_sort)
                {
                    final SortDialog sortDialog = new SortDialog();
                    sortDialog.showDialog(MainActivity.this);
                    ArrayList<Article> articles  =  new ArrayList<>();
                    sortDialog.setOnClickButton(map,sortItem,articles,adapter);
                }

                return true;
            }

            private void createCheckNetworkDialog ()
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Connect to wifi or quit?")
                        .setCancelable(false)
                        .setPositiveButton("Connect to WIFI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        })
                        .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            private void load(Map<String,String> map)
            {
                pbLoading.setVisibility(View.VISIBLE);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                for(String key:map.keySet())
                {
                    params.put(key, map.get(key));
                }
                params.put("api-key", Constant.API_KEY);
                client.get(Constant.URL, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        pbLoading.setVisibility(View.GONE);
                        Gson gson = new GsonBuilder().create();
                        // Define Response class to correspond to the JSON response returned
                        JsonObject root = gson.fromJson(responseString, JsonObject.class);
                        ArrayList<Article> articles = new ArrayList<>();

                        JsonArray jsonArray = root.getAsJsonObject("response").getAsJsonArray("docs");
                        articles.addAll(Article.fromJsonArray(jsonArray));
                        adapter.setArticle(articles);

                    }
                });
            }

            private void loadMore(Map<String, String> map)
            {

                pbLoadingMore.setVisibility(View.VISIBLE);
                AsyncHttpClient client = new AsyncHttpClient();
                RequestParams params = new RequestParams();

                for(String key:map.keySet())
                {
                    params.put(key, map.get(key));
                }
                params.put("api-key", Constant.API_KEY);
                client.get(Constant.URL, params, new TextHttpResponseHandler() {
                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        pbLoadingMore.setVisibility(View.GONE);
                        Gson gson = new GsonBuilder().create();
                        // Define Response class to correspond to the JSON response returned
                        JsonObject root = gson.fromJson(responseString, JsonObject.class);
                        ArrayList<Article> articles = new ArrayList<>();

                        JsonArray jsonArray = root.getAsJsonObject("response").getAsJsonArray("docs");
                        articles.addAll(Article.fromJsonArray(jsonArray));
                        adapter.addArticle(articles);

                    }
                });
            }

            private Boolean isNetworkAvailable() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
            }
}