package lawrence.edu.shuttleme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;

import javax.net.ssl.HttpsURLConnection;


public class LocateShuttle extends Fragment implements OnMapReadyCallback {

    LocateShuttle locateshuttleobject;
    View rootView;
    GoogleMap mGoogleMap = null;
    private Double longitude = -88.4154;
    private Double lat = 44.2619;
    float prevZoomLevel= 16.235184f;
    // initialize boolean to know tab is already loaded or load first time
    private boolean isFragmentLoaded=false;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_locate_shuttle, null, false);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        locateshuttleobject = this;

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //CALL YOUR ASSYNC TASK HERE.
                Log.d("Timer Task:", "Hello moto");
                //Execute asynctask that will repeatedly retrieve coordinates
                new RetrieveCoords(locateshuttleobject, "http://" + hostName  + "/shuttle/get?shuttleid=dbc8b0c3-a879-4848-a227-b763be16582c").execute();
            }
        };

        Timer timer = new Timer();

        //DELAY: the time to the first execution
        //PERIODICAL_TIME: the time between each execution of your task.
        timer.schedule(timerTask, 1000, 10000);


    }

    // Interpret lat and long and show it on listview
    public void onRetrieveCoordinatesCompleted(String result) {
        Log.d("New Coords Received: ", result);
        // Parse data - get lat and long values
        try {
            JSONObject jObject = new JSONObject(result);
                try {
                    // Pulling items from the object
                    String lati = jObject.getString("latitude");
                    String longi = jObject.getString("longitude");

                    // Add a marker for Shuttle, and move the camera.
                    if (lati == "" || longi == "" || lati == null || longi == null) {

                        LatLng appleton = new LatLng(lat, longitude);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appleton, prevZoomLevel));

                    }//Throw alert that coordinates were not retrieved
                    else{
                        lat = Double.valueOf(lati);
                        longitude = Double.valueOf(longi);
                        LatLng shuttle = new LatLng(lat, longitude);
                        prevZoomLevel = mGoogleMap.getCameraPosition().zoom;

                        Log.d("Updated zoom level:", String.valueOf(prevZoomLevel));
                        mGoogleMap.addMarker(new MarkerOptions()
                                .title("Shuttle")
                                .position(shuttle)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shuttle)));
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttle, prevZoomLevel));
                        System.out.print(lat+longitude);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    } // THROW ALERT IF EMPTY - otherwise show coords

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && !isFragmentLoaded ) {
            // Load your data here or do network operations here


            //Create fragment to put map into fragment
            FragmentManager fm = getChildFragmentManager();
            SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.activity_locate_shuttle);
            mapFragment.getMapAsync(this);

            isFragmentLoaded = true;
        }
    }
public class RetrieveCoords extends AsyncTask<String, String, String> {

    LocateShuttle caller;
    String uRL;

    public RetrieveCoords(LocateShuttle ls, String url) {
        super();
        this.caller=ls;
        uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        caller.onRetrieveCoordinatesCompleted(result);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected String doInBackground(String... params) {

        String response = "";
        String responseError = "";
        HttpURLConnection conn = null;
        try{
            //Connect to URL
            URL url = new URL(uRL);

            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode=conn.getResponseCode();

            //HTTP_OK --> 200
            //HTTP_CONFLICT --> 409
            if (responseCode == HttpsURLConnection.HTTP_OK ) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                return response;
            }
            else if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    responseError+=line;
                }
                System.out.print(responseError);
                return responseError;
            }
            else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null){
                conn.disconnect();
            }

        }
        return response;
    }

    }

}