package com.example.thinha.newyorktimesarticlesearch.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.thinha.newyorktimesarticlesearch.R;
import com.example.thinha.newyorktimesarticlesearch.adapter.ArticleAdapter;
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
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by ThiNha on 10/22/2016.
 */
public class SortDialog  {

    private EditText edtDate;
    private EditText edtMonth;
    private EditText edtYear;
    private Spinner sbnSort;
    private CheckBox cbArts;
    private CheckBox cbFashionStyle;
    private CheckBox cbSports;
    private Button btnSave;
    static boolean isClick = false;


    public boolean isClick() {
        return isClick;
    }

    public Button getBtnSave() {
        return btnSave;
    }

    Dialog dialog;


    public String getBeginDate() {
        String date, month, year;
        if (Integer.valueOf(edtDate.getText().toString()) < 10)
        {
            date = "0" + edtDate.getText().toString();
        }
        else {
            date = edtDate.getText().toString();
        }

        if (Integer.valueOf(edtMonth.getText().toString()) < 10)
        {
            month = "0"+edtMonth.getText().toString();
        }
        else {
            month = edtMonth.getText().toString();
        }

        year = edtYear.getText().toString();
        return year+ month+ date;

    }

    public String getSort() {
        return sbnSort.getSelectedItem().toString();
    }

    public String getNewsDesk() {
        String newsdesk= "(";
        if (cbArts.isChecked())
        {
             newsdesk += "\""+cbArts.getText().toString() + "\"%20" ;
        }
        if (cbFashionStyle.isChecked())
        {
            newsdesk += "\""+cbArts.getText().toString() + "\"%20" ;
        }
        if (cbSports.isChecked())
        {
            newsdesk += "\""+cbArts.getText().toString() + "\"%20" ;
        }
        newsdesk +=")";
        return newsdesk;
    }

     public void showDialog(final Activity activity)
    {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.filter_dialog);

        edtDate = (EditText) dialog.findViewById(R.id.edtDate);
        edtMonth = (EditText) dialog.findViewById(R.id.edtMonth);
        edtYear = (EditText) dialog.findViewById(R.id.edtYear);
        sbnSort = (Spinner) dialog.findViewById(R.id.spnSort);
        cbArts = (CheckBox) dialog.findViewById(R.id.cbArts);
        cbFashionStyle = (CheckBox) dialog.findViewById(R.id.cbFashionStyle);
        cbSports = (CheckBox) dialog.findViewById(R.id.cbSports);
        btnSave = (Button) dialog.findViewById(R.id.btnSave);

        dialog.show();



        dialog.setOnKeyListener(new Dialog.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {

                if (keyCode == KeyEvent.KEYCODE_BACK)
                {
                    dialog.dismiss();
                }

                return false;
            }
        });



    }

    public void setOnClickButton(final Map<String, String> map,final MenuItem sortItem, final List<Article> articles, final ArticleAdapter adapter)
    {

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (edtDate.getText().toString()=="")
                {
                    Toast.makeText(view.getContext(),"Date is not empty",Toast.LENGTH_LONG).show();
                }
                else if (edtMonth.getText().toString()=="")
                {
                    Toast.makeText(view.getContext(),"Month is not empty",Toast.LENGTH_LONG).show();
                }

                else if (edtYear.getText().toString()=="")
                {
                    Toast.makeText(view.getContext(),"Year is not empty",Toast.LENGTH_LONG).show();
                }
                else
                {
                    isClick = true;
                    dialog.cancel();
                    Toast.makeText(view.getContext(),"zo roi nek",Toast.LENGTH_LONG).show();
                    search( map,sortItem,  articles, adapter);

                }

            }
        });

    }

    public void search(Map<String, String> map, MenuItem sortItem, final List<Article> articles, final ArticleAdapter adapter)
    {
        final SearchView searchView1 = (SearchView) MenuItemCompat.getActionView(sortItem);
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        map.put("begin_date",getBeginDate());
        map.put("sort",getSort());
        map.put("fq",getNewsDesk());
        map.put("page", "0");
        for(String key:map.keySet())
        {
            params.put(key, map.get(key));
        }
        params.put("api-key", "e863004d8ddc2900d7111aa358a8bdbb:19:4701790");

        client.get(Constant.URL, params, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
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

}
