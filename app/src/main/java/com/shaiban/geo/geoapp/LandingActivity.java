package com.shaiban.geo.geoapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Mohammed on 11/25/2015.
 */
public class LandingActivity  extends AppCompatActivity {
    ArrayList<MyMarker> data = new ArrayList<MyMarker>();
    RecyclerView rv;
    Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        context = this;

        try {
            URL uri = new URL("http://172.31.98.112:8000/users");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new HomeGeoAsyncTask(uri, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else {
                Log.i("MAPS", "Calling Async Task");
                new HomeGeoAsyncTask(uri, context).execute();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Button b = (Button) findViewById(R.id.team);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context,TeamMap.class);
                startActivity(i);
            }
        });

    }

    class HomeGeoAsyncTask extends AsyncTask<Void, Void, String> {
        URL url;
        Context context;

        public HomeGeoAsyncTask(URL url, Context context) {
            this.url = url;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("MAPS", "AsyncTask onPreExecute()");

        }

        @Override
        protected String doInBackground(Void... voids) {
            String userUniqueId = "564d893c76d02b2f0db99b1d";
            String stream = null;
            String urlString = url.toString();
            HTTPDataHandler hh = new HTTPDataHandler(userUniqueId);
            stream = hh.GetHTTPData(urlString);
            Log.i("MAPS", "AsyncTask doInBackground() " + stream);
            return stream;
        }

        @Override
        protected void onPostExecute(String o) {
            super.onPostExecute(o);

            if (o != null) {
                try {


                    JSONObject reader = new JSONObject(o);
                    JSONArray jarray = reader.getJSONArray("data");

                    for (int i = 0; i < jarray.length(); i++) {

                        JSONObject object = jarray.getJSONObject(i);

                        JSONObject from = object.getJSONObject("personaldata");

                        String lname = from.getString("last name");
                        String fname = from.getString("first name");
                        String name = fname + " " + lname;

                        String icon= object.getString("avatarUrl");

                        JSONObject address = object.getJSONObject("address");

                        Double latitude = address.getDouble("latitude");
                        Double longitude = address.getDouble("longitude");

                        Log.i("MAPS", "AsyncTask onPostExecute() " + fname + " " + latitude + " " + longitude);

                        data.add(new MyMarker(context, name, latitude, longitude,icon));
                        Log.i("MAPS", "AsyncTask onPostExecute() SUCCESS ADDED");

                    }

                } catch (JSONException e) {
                }
            }
            rv = (RecyclerView)findViewById(R.id.list);
            rv.setLayoutManager(new LinearLayoutManager(context));
            HomeAdapter adapter = new HomeAdapter(data, new HomeAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                }
            });
            rv.setAdapter(adapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(context,TeamMap.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
