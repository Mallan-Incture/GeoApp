package com.shaiban.geo.geoapp;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mohammed on 11/26/2015.
 */
public class TeamMap extends AppCompatActivity {
    private GoogleMap mMap;
    private Context context;
    ArrayList<MyMarker> data = new ArrayList<MyMarker>();
    private HashMap<Marker, MyMarker> mMarkersHashMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.team_map);
        context = this;

//        data.add(new MyMarker(context, "Janani", Double.parseDouble("12.699962"), Double.parseDouble("12.699962"),""));
//        data.add(new MyMarker(context,"Sujith", Double.parseDouble("12.938314"), Double.parseDouble("77.650509"),""));
//        data.add(new MyMarker(context,"Mariyam", Double.parseDouble("17.385044"), Double.parseDouble("78.486671"),""));
//        data.add(new MyMarker(context,"Sharique",Double.parseDouble("12.924595"),Double.parseDouble("77.776337"),""));

      /*  Intent i = getIntent();
        data = (ArrayList<MyMarker>) i.getSerializableExtra("MyClass");*/

           try {
            URL uri = new URL("http://172.31.98.112:8000/users");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                new GeoAsyncTask(uri, context).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            else {
                Log.i("MAPS","Calling Async Task");
                new GeoAsyncTask(uri, context).execute();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        mMarkersHashMap = new HashMap<Marker, MyMarker>();
        try {
             Log.i("MAPS", "mapsInitialization()");
             initilizeMap();
         } catch (Exception e) {
             Log.i("MAPS", "mapsInitialization() Exception raised");
             e.printStackTrace();
        }  //Loading done
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
                        String icon= object.getString("avatarUrl");
                        Log.i("MAPS", "AsyncTask onPostExecute() "+fname+" "+latitude+" "+longitude);

                        data.add(new MyMarker(context, name, latitude, longitude, icon));
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
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.teammap)).getMap();
        Log.i("MAPS", "initilizeMap() Inside");
        markLocation();

        // check if map is created successfully or not
        if (mMap == null) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }
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

                        final Dialog dialog = new Dialog(TeamMap.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.chat_dialog);

                        String text=marker.getTitle();
                        pos1= text.indexOf("is at");
                        pos2= text.indexOf("is at");
                        name=text.substring(0, pos1 - 1);
                        place=text.substring(pos2+6,text.length());

                        EditText e = (EditText)findViewById(R.id.team1);


                        //   Toast.makeText(context," "+name+" "+place,Toast.LENGTH_LONG).show();

                        ImageView image = (ImageView) dialog.findViewById(R.id.dp);
                        if(name.equalsIgnoreCase("Aanchal T"))
                            image.setImageResource(R.drawable.aanchal);
                        else if(name.contains("Mariyam"))
                            image.setImageResource(R.drawable.maryam);
                        else if(name.contains("Prem"))
                            image.setImageResource(R.drawable.prem);
                        else if(name.contains("Shridhar"))
                            image.setImageResource(R.drawable.shrid);
                        else if(name.equalsIgnoreCase("Ravikiran Papthimar"))
                            image.setImageResource(R.drawable.ravi);
                        else if(name.contains("Sarathi"))
                            image.setImageResource(R.drawable.sarathi);
                        else if(name.contains("Vikram"))
                            image.setImageResource(R.drawable.vikram);
                        else
                            image.setImageResource(R.drawable.shaiban);

                        TextView textname = (TextView) dialog.findViewById(R.id.name);
                        textname.setText(""+name);

                        TextView loc = (TextView) dialog.findViewById(R.id.location);
                        loc.setText(""+place);

                        ImageView image1 = (ImageView) dialog.findViewById(R.id.send);
                        image1.setOnClickListener(new View.OnClickListener() {
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

                //    markerOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.currentlocation_icon));
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
