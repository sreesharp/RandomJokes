package com.sreesharp.randomjokes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private final int DELAY = 5000;
    private final String BASE_URL ="http://api.icndb.com/jokes/random";

    private TextView tvJoke;
    private Handler handler = new Handler();
    private AsyncHttpClient client = new AsyncHttpClient();
    private RequestParams params = new RequestParams();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvJoke = (TextView)findViewById(R.id.tvJoke);

        StrictMode.enableDefaults();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Utility.isNetworkAvailable(this))
            runnable.run();
        else
            Toast.makeText(this,R.string.netwoork_error, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    //Set the given joke to the text view
    private void setJokeText(final String jokeText){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvJoke.setText(Html.fromHtml(jokeText));
            }
        });
    }

    //Thread to fetch the joke from the server
    private Runnable runnable = new Runnable()
    {
        public void run()
        {
            client.get(BASE_URL, params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        setJokeText( response.getJSONObject("value").getString("joke"));
                    } catch (JSONException e) {
                        Log.d("JSON_FAILED", e.getMessage());
                    }
                }
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                    Log.d("HTTP_FAILED", errorResponse.toString());
                }
            } );
            handler.postDelayed(this, DELAY);
        }
    };



}
