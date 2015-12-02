package com.shaiban.geo.geoapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private GoogleMap mMap;
    private Context context;
    ArrayList<MyMarker> data = new ArrayList<MyMarker>();
    private HashMap<Marker, MyMarker> mMarkersHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        mMarkersHashMap = new HashMap<Marker, MyMarker>();
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        Intent intent = getIntent();
        Double lat = intent.getDoubleExtra("lat",0.0);
        Double lng = intent.getDoubleExtra("long",0.0);
        String place = intent.getStringExtra("place");
        showOnMap(lat,lng,place);

    }

    public void showOnMap(Double lat,Double lng,String place){
        MarkerOptions marker = new MarkerOptions().position(new LatLng(lat, lng)).title(""+place);
        mMap.addMarker(marker);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(
                new LatLng(lat, lng)).zoom(12).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        double radiusInMeters = 5000.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(new LatLng(lat,lng)).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mMap.addCircle(circleOptions);

    }

    class GeoAsyncTask extends AsyncTask<Void, Void, String> {
        URL url;
        Context context;

        public GeoAsyncTask(URL url, Context context) {
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
            Log.i("MAPS", "AsyncTask doInBackground() "+stream);
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
                        String name= fname+" "+lname;

                        JSONObject address = object.getJSONObject("address");

                        Double latitude = address.getDouble("latitude");
                        Double longitude = address.getDouble("longitude");

                        Log.i("MAPS", "AsyncTask onPostExecute() "+fname+" "+latitude+" "+longitude);

                        data.add(new MyMarker(context,name, latitude, longitude,""));
                        Log.i("MAPS", "AsyncTask onPostExecute() SUCCESS ADDED");

                    }

                    } catch (JSONException e) {
                    }

            }

            try {
                Log.i("MAPS", "mapsInitialization()");
                initilizeMap();
            } catch (Exception e) {
                Log.i("MAPS", "mapsInitialization() Exception raised");
                e.printStackTrace();
            }  //Loading done
        }

    }

    private void initilizeMap() {
        Log.i("MAPS", "initilizeMap() before if");
     //   if (mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            Log.i("MAPS", "initilizeMap() Inside");
            markLocation();

            // check if map is created successfully or not
            if (mMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }

    //    }

    }

    String name,place;
    int pos1,pos2;

    public void markLocation() {
        Log.i("MAPS", "markLocation() Inside");
        Double lat=0.0,lng=0.0;
    //    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.map_overlay);


        if(data.size() > 0)
        {
            for (final MyMarker myMarker : data)
            {
                Log.i("MAPS", "markLocation() Inside ForLoop");
                // Create user marker with custom icon and other options
                MarkerOptions markerOption = new MarkerOptions().position(new LatLng(myMarker.getLatitude(),
                        myMarker.getLongitude())).title(myMarker.getName() + " is at " + myMarker.getPlace());
                mMap.addMarker(markerOption);

                double radiusInMeters = 5000.0;
                int strokeColor = 0xffff0000; //red outline
                int shadeColor = 0x44ff0000; //opaque red fill

                CircleOptions circleOptions = new CircleOptions().center(new LatLng(myMarker.getLatitude(),
                        myMarker.getLongitude())).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
                mMap.addCircle(circleOptions);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        //    Toast.makeText(context," "+marker.getTitle(),Toast.LENGTH_LONG).show();

                        name=myMarker.getName();
                        place=myMarker.getPlace();

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.chat_dialog);

                        String text=marker.getTitle();
                        pos1= text.indexOf("is at");
                        pos2= text.indexOf("is at");
                        name=text.substring(0, pos1-1);
                        place=text.substring(pos2+6,text.length());

                     //   Toast.makeText(context," "+name+" "+place,Toast.LENGTH_LONG).show();

                        TextView textname = (TextView) dialog.findViewById(R.id.name);
                        textname.setText(""+name);

                        TextView loc = (TextView) dialog.findViewById(R.id.location);
                        loc.setText(""+place);

                        EditText e = (EditText)findViewById(R.id.team1);
                        e.getBackground().clearColorFilter();

                        ImageView image = (ImageView) dialog.findViewById(R.id.send);
                        image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(context,"Message Successfully Sent!!",Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        return false;
                    }
                });
                Marker currentMarker = mMap.addMarker(markerOption);
                mMarkersHashMap.put(currentMarker, myMarker);
            //    mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter());
                lat=myMarker.getLatitude();
                lng=myMarker.getLongitude();
            }
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lng)).zoom(5).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

    }


    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
    {
        public MarkerInfoWindowAdapter()
        {
            Log.i("MAPS", "MarkerInfoWindowAdapter() Inside ForLoop");
        }

        @Override
        public View getInfoWindow(Marker marker)
        {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker)
        {
            View v  = getLayoutInflater().inflate(R.layout.infowindow_layout, null);
            MyMarker myMarker = mMarkersHashMap.get(marker);
            ImageView markerIcon = (ImageView) v.findViewById(R.id.marker_icon);
            TextView markerLabel = (TextView)v.findViewById(R.id.marker_label);
            markerIcon.setImageResource(R.drawable.common_google_signin_btn_icon_dark_normal);
       //     markerIcon.setImageResource(manageMarkerIcon(myMarker.getmIcon()));
            markerLabel.setText(myMarker.getPlace());
            return v;
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

