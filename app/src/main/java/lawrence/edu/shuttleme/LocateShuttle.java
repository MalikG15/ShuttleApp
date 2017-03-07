package lawrence.edu.shuttleme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;


public class LocateShuttle extends Fragment implements OnMapReadyCallback {

    private LocateShuttle locateshuttleobject;
    private GoogleMap mGoogleMap = null;
    private Marker shuttleMarker;
    private Double longitude = -88.397369;
    private Double lat = 44.260795;

    private Double curLong;
    private Double curLat;

    // Default zoom level, unless otherwise changed
    private float prevZoomLevel = 16.235184f;

    // initialize boolean to know tab is visible on screen
    private boolean isFragVisible = false;

    // initialize boolean to know marker hasnt been created
    private boolean isFirst = true;

    // Network URI
    public static final String hostName = "143.44.78.173:8080";

    ArrayList<String> simulatedLats;
    ArrayList<String> simulatedLongs;
    int simulationCount = 0;

    TimerTask timerTask;
    Timer timer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_locate_shuttle, null, false);


        //Create fragment to put map into fragment
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.activity_locate_shuttle);
        mapFragment.getMapAsync(this);


        simulatedLats = new ArrayList<String>();
        simulatedLongs = new ArrayList<String>();

        simulatedLats.add("44.262359");
        simulatedLongs.add("-88.398098");
        simulatedLats.add("44.262939");
        simulatedLongs.add("-88.398272");
        simulatedLats.add("44.262924");
        simulatedLongs.add("-88.399720");
        simulatedLats.add("44.262932");
        simulatedLongs.add("-88.399597");
        simulatedLats.add("44.262382");
        simulatedLongs.add("-88.399611");
        simulatedLats.add("44.261810");
        simulatedLongs.add("-88.399643");
        simulatedLats.add("44.261829");
        simulatedLongs.add("-88.400970");
        simulatedLats.add("44.261805");
        simulatedLongs.add("-88.402821");
        simulatedLats.add("44.261859");
        simulatedLongs.add("-88.404354");
        simulatedLats.add("44.261848");
        simulatedLongs.add("-88.405965");
        simulatedLats.add("44.261825");
        simulatedLongs.add("-88.407242");
        simulatedLats.add("44.261825");
        simulatedLongs.add("-88.408733");
        simulatedLats.add("44.261833");
        simulatedLongs.add("-88.410825");
        simulatedLats.add("44.261795");
        simulatedLongs.add("-88.412091");
        simulatedLats.add("44.261818");
        simulatedLongs.add("-88.413722");
        simulatedLats.add("44.261826");
        simulatedLongs.add("-88.415728");
        simulatedLats.add("44.261826");
        simulatedLongs.add("-88.417466");
        simulatedLats.add("44.261826");
        simulatedLongs.add("-88.419097");
        simulatedLats.add("44.261872");
        simulatedLongs.add("-88.420760");
        simulatedLats.add("44.261849");
        simulatedLongs.add("-88.422423");
        simulatedLats.add("44.261818");
        simulatedLongs.add("-88.424086");
        simulatedLats.add("44.261818");
        simulatedLongs.add("-88.425749");
        simulatedLats.add("44.261856");
        simulatedLongs.add("-88.427122");
        simulatedLats.add("44.261864");
        simulatedLongs.add("-88.428281");
        simulatedLats.add("44.261856");
        simulatedLongs.add("-88.429933");
        simulatedLats.add("44.261887");
        simulatedLongs.add("-88.430866");
        simulatedLats.add("44.261826");
        simulatedLongs.add("-88.432089");
        simulatedLats.add("44.261818");
        simulatedLongs.add("-88.433462");
        simulatedLats.add("44.261849");
        simulatedLongs.add("-88.435490");
        simulatedLats.add("44.261841");
        simulatedLongs.add("-88.438258");
        simulatedLats.add("44.261864");
        simulatedLongs.add("-88.440683");
        simulatedLats.add("44.261833");
        simulatedLongs.add("-88.443397");
        simulatedLats.add("44.261795");
        simulatedLongs.add("-88.445908");
        simulatedLats.add("44.261798");
        simulatedLongs.add("-88.448748");
        simulatedLats.add("44.261829");
        simulatedLongs.add("-88.450904");
        simulatedLats.add("44.261821");
        simulatedLongs.add("-88.452985");
        simulatedLats.add("44.261829");
        simulatedLongs.add("-88.454873");
        simulatedLats.add("44.261821");
        simulatedLongs.add("-88.455871");
        simulatedLats.add("44.261806");
        simulatedLongs.add("-88.458285");
        simulatedLats.add("44.261814");
        simulatedLongs.add("-88.460954");
        simulatedLats.add("44.263103");
        simulatedLongs.add("-88.460992");
        simulatedLats.add("44.264355");
        simulatedLongs.add("-88.460992");
        simulatedLats.add("44.265975");
        simulatedLongs.add("-88.461005");
        simulatedLats.add("44.267085");
        simulatedLongs.add("-88.461145");
        simulatedLats.add("44.267131");
        simulatedLongs.add("-88.461821");
        simulatedLats.add("44.266747");
        simulatedLongs.add("-88.462948");
        simulatedLats.add("44.266893");
        simulatedLongs.add("-88.463613");

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        locateshuttleobject = this;

        LatLng appleton = new LatLng(44.2623821, -88.398101);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(appleton, 16f));

        // Check if shuttleid exist first, if not don't display anything for both ETA and locations
        // Else run simulation

        // Needs to be deleted
        simulateShuttle();


        timerTask = new TimerTask() {
            @Override
            public void run() {
                //CALL YOUR ASSYNC TASK HERE.
                // Needs to be uncommented
                /*if(isFragVisible){
                    //Execute asynctask that will repeatedly retrieve coordinates
                    new RetrieveCoords(locateshuttleobject, "http://" + hostName  + "/shuttle/get?shuttleid=2").execute();
                }*/

            }
        };

        timer = new Timer();


        //DELAY: the time to the first execution
        //PERIODICAL_TIME: the time between each execution of your task.
        //timer.schedule(timerTask, 0, 10000);
    }

    public void simulateShuttle() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if(simulationCount < simulatedLats.size()){
                            curLat = Double.valueOf(simulatedLats.get(simulationCount));
                            curLong = Double.valueOf(simulatedLongs.get(simulationCount));
                            LatLng curShuttle = new LatLng(curLat, curLong);
                            if(simulationCount==0){
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curShuttle, prevZoomLevel));
                                shuttleMarker = mGoogleMap.addMarker(new MarkerOptions()
                                        .title("Shuttle")
                                        .position(curShuttle)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shuttle)));
                            }
                            else{
                                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curShuttle, mGoogleMap.getCameraPosition().zoom));
                                shuttleMarker.setPosition(curShuttle);
                            }


                            simulationCount++;
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
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
                                    shuttleMarker.setPosition(shuttle);
                                }
                                else{
                                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(shuttle, prevZoomLevel));
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
        isFragVisible = isVisibleToUser;
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