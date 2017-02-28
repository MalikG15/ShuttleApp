package lawrence.edu.shuttleme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;


public class LocateShuttle extends Fragment implements OnMapReadyCallback {

    private LocateShuttle locateshuttleobject;
    private GoogleMap mGoogleMap = null;
    private Marker shuttleMarker;
    private Double longitude = -88.4154;
    private Double lat = 44.2619;

    // Default zoom level, unless otherwise changed
    private float prevZoomLevel= 16.235184f;

    // initialize boolean to know tab is already loaded or load first time
    private boolean isFragmentLoaded = false;

    // initialize boolean to know marker hasnt been created
    private boolean isFirst = true;

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
        Log.d("Coordinates Received: ", result);
        // Parse data - get lat and long values
        try {
            JSONObject jObject = new JSONObject(result);
                try {
                    // Pulling items from the object
                    String lati = jObject.getString("latitude");
                    String longi = jObject.getString("longitude");

                    // Add a marker for Shuttle, and move the camera.
                    // Move camera position to appleton if we don't receive coordinates
                    if (lati == "" || longi == "" || lati == null || longi == null) {

                        LatLng appleton = new LatLng(lat, longitude);
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appleton, prevZoomLevel));

                    }
                    // Update shuttle marker with new coordinates without changing camera position.
                    else{
                        // If latitude and longitude hasn't change do nothing
                        if(lati.equals(String.valueOf(lat)) && longi.equals( String.valueOf(longitude))){

                        } // Otherwise update
                        else{

                            lat = Double.valueOf(lati);
                            longitude = Double.valueOf(longi);

                            LatLng shuttle = new LatLng(lat, longitude);

                            // Create marker first time around
                            if(isFirst){
                                shuttleMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .title("Shuttle")
                                        .position(shuttle)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shuttle)));
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttle, prevZoomLevel));
                                isFirst = false;
                            }
                            // Else update shuttle marker
                            else{
                                if(mGoogleMap.getCameraPosition().zoom != 16.235184f) {
                                    prevZoomLevel = mGoogleMap.getCameraPosition().zoom;
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttle, prevZoomLevel));
                                }
                                else{
                                    shuttleMarker.setPosition(shuttle);
                                }
                            }
                            Log.d("Updated zoom level:", String.valueOf(prevZoomLevel));

                        }
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