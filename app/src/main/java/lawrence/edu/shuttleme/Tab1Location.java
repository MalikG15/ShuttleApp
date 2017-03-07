package lawrence.edu.shuttleme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.maps.SupportMapFragment;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import java.util.ArrayList;


import static java.lang.Thread.sleep;
import static lawrence.edu.shuttleme.R.id.container;
import static lawrence.edu.shuttleme.R.id.estimated_times;
import static lawrence.edu.shuttleme.R.id.time;

/**
 * Created by elkingarcia on 2/7/17
 * with contributions by Malik Graham.
 */

public class Tab1Location extends Fragment {
    private boolean isCheckedIn = false;

    private String id;
    private ToggleButton toggleButton;
    private TableLayout estimatedTimes;

    public static final String hostName = "143.44.78.173:8080";

    //New Member Variable
    private final boolean running = true;
    // initialize boolean to know tab is visible on screen
    private boolean isFragVisible = true;
    private LinkedHashMap<Integer, List<String>> stops;
    private LinkedHashMap<Integer, Integer> prevTimeEstimate;

    private String driverLat;
    private String driverLong;

    private int index = 0;

    private String API_KEY = GoogleAPI.Google_API_KEY;
    FragmentActivity parent = null;




    // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------ \\
    int simulatedIndex = 30;

    ArrayList<String> simulatedLats;
    ArrayList<String> simulatedLongs;


    // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create a looping function that refreshes ETA information every 30 seconds
            // looping function will call function to begin chain of gathering information

        // initially find closest stop
            // then calculate rolling ETA

        // calculate a "change-in ETA"
                // when checking for closest stop, compare with prev ETAs
                // prev is less then driver is approaching that stop, and shift it to the top
        // if prev ETA (hashmap) is empty then just use smallest value

        // if returned time is less than 2 mins, then shuttle is located there!



        // -> get initial times
            // find lowest and then roll


        // linkedhashmap ids -> timeEstimate
            // array of prevTimeEstimates

        // -------------------------------------------
        // Data Structures: -> HashMap<Integer, String> indexOfArray to Stop Name
                        // -> Array of Time Estimates


        // Array Of PrevTimes is equal to null
                // find the index of the min
                    // if estimates <= 2
                        // say that you are located here

        // Not null
            // find greatest change in prevTime and CurTime
                // use index to iterate over hashmap and creates times
                 // if estimates <= 2
                 // say that you are located here

        simulatedLats = new ArrayList<String>();
        simulatedLongs = new ArrayList<String>();

        simulatedLats.add("44.262359");
        simulatedLongs.add("-88.398098");
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
    }

    // Overriding this method because it happens later in the cycle
    // and therefore the table will not be equal to null.


    @Override
    public void onStart() {
        super.onStart();
        /*new Thread(new Runnable() {
            public void run() {

                try {
                    sleep(3000);

                    while (running) {
                        // do something in the loop
                        if (stops == null) {
                            new Tab1Location.getRoutes().execute();
                        }
                        new Tab1Location.getDriverLoc().execute();

                        if (prevTimeEstimate == null) calculateInitial();

                        calculateETA();
                    }
                }
                catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();*/
        //public void callAsynchronousTask() {

        // get initial size of the routes
                // run the request for the size of the stops

        // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------ \\

        // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------

        if (stops == null) new getRoutes().execute();

    }

    public void updateTable() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------
                            if (simulatedIndex >= simulatedLats.size() || simulatedIndex >= simulatedLongs.size()) return;
                            // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------


                            //parent = (FragmentActivity) getActivity();

                            //else return;
                            //if(isFragVisible==true){
                                new getDriverLoc().execute();
                            //}
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 5000); //execute in every 50000 ms
    }

    public void populateTable() {

        /*int tempIndex = 0;
        int min = Integer.MAX_VALUE;
        LinkedHashMap<Integer, Integer> temp = new LinkedHashMap<Integer, Integer>();
        while (!prevTimeEstimate.isEmpty()) {
            for (int x : prevTimeEstimate.keySet()) {
                if (prevTimeEstimate.get(x) < min) {
                    tempIndex = x;
                    min = prevTimeEstimate.get(x);
                }
            }
            temp.put(tempIndex, min);
            prevTimeEstimate.remove(tempIndex);
            min = Integer.MAX_VALUE;
        }

        prevTimeEstimate = temp;*/


        int runningIndex = index;
        boolean startLoop = false;
        int sum = 0;
        estimatedTimes.removeAllViews();
        System.out.println("Just before we make the tables : " + prevTimeEstimate.get(runningIndex));
        while (index != runningIndex || !startLoop) {
            TableRow tableRow = new TableRow(parent.getApplicationContext());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            TextView name = new TextView(parent.getApplicationContext());
            TextView ETA = new TextView(parent.getApplicationContext());
            //System.out.println("Just before we make the tables : " + prevTimeEstimate.toString() + runningIndex);
            sum += prevTimeEstimate.get(runningIndex);
            if (stops.containsKey(runningIndex)) name.setText(stops.get(runningIndex).get(2));
            if (prevTimeEstimate.containsKey(runningIndex)) {
                if (prevTimeEstimate.get(runningIndex) > 2) ETA.setText(" : about " + String.valueOf(sum) + " mins away.");
                else ETA.setText(" : shuttle is here. ");
            }
            tableRow.addView(name);
            ETA.setTextColor(Color.RED);
            tableRow.addView(ETA);
            estimatedTimes.addView(tableRow);

            if (runningIndex + 1 >= prevTimeEstimate.size()) runningIndex = 0;
            else runningIndex++;

            startLoop = true;
        }
    }


    // get assigned route
    // loop through stops

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.tab1location,container,false);
        id = ((PassengerActivity) getActivity()).getUserID();
        toggleButton = (ToggleButton) rootView.findViewById(R.id.checkInButton) ;
/*        toggleButton.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton toggleButton, boolean isChecked) {

            }
        });*/
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInNOut();
            }
        });
        // Will constantly update location
        /*if(isCheckedIn == true){
            final Handler h = new Handler();
            final int delay = 10000; //milliseconds

            h.postDelayed(new Runnable(){
                public void run(){
                    //do something
                    new sendLocation(this, "http://143.44.78.173:8080/user/checkout?userid="+id).execute();
                    h.postDelayed(this, delay);
                }
            }, delay);
        }*/

        // Getting the estimated times, and using this table info in onStart since
        // it's called later in the activity life cycle.
        estimatedTimes = (TableLayout) rootView.findViewById(R.id.estimated_times);
        parent = (FragmentActivity) getActivity();


        //Check checked-in status
        new getCheckInStatus(this, "http://" + hostName  + "/clipboard/status?userid="+id).execute();
        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isFragVisible = isVisibleToUser;
        //if (isFragVisible)
    }

    public void checkInNOut(){
         if(isCheckedIn==false){
                new checkIn(this, "http://" + hostName  + "/clipboard/checkin", id).execute();
         }
         else{
             new checkOut(this, "http://" + hostName + "/clipboard/checkout?userid=" + id).execute();
             isCheckedIn = false;
         }
    }

    // Update toggle button with necessary option
    public void onCheckOutCompleted(Integer result){
        Log.d("Check out Activity: ", String.valueOf(result));
        Log.d("ID: ", id);
        if (Integer.valueOf(result) == -1){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You were not able to check out", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(true);
        }
        if (Integer.valueOf(result) == 0){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You have successfully checked out", Toast.LENGTH_SHORT).show();
            isCheckedIn = false;
        }
    }

    // Update toggle button with necessary action
    public void onCheckInCompleted(Integer result){
        Log.d("Check In Attempt: ", String.valueOf(result));
        if(Integer.valueOf(result) == 0){
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You were not able to check in", Toast.LENGTH_SHORT).show();
            toggleButton.setChecked(false);

        }
        else{
            Toast.makeText(((PassengerActivity) getActivity()).getApplicationContext(), "You have successfully checked in", Toast.LENGTH_SHORT).show();
            isCheckedIn = true;
        }
    }


    public void onGetCheckedInStatusCompleted(String result){
        int res = Integer.valueOf(result);
        //Fail
        if(res == 0){
            // Not checked in
        }//Success - checked in so toggle button to true;
        if(res == 1){
            toggleButton.setChecked(true);
            isCheckedIn = true;
        }

    }

    class cascadeETA extends AsyncTask<Integer, Void, LinkedHashMap<Integer, Integer>> {

        Integer indexForCascade;

        public cascadeETA(Integer s) {
            indexForCascade = s;
        }

        protected LinkedHashMap<Integer, Integer> doInBackground(Integer... urls) {
            LinkedHashMap<Integer, Integer> timeEstimates = new LinkedHashMap<Integer, Integer>();

            try {
                String prevLat = driverLat;
                String prevLong = driverLong;
                boolean startLoop = false;
                int runningIndex = indexForCascade;
                int sum = 0;
                while (indexForCascade != runningIndex || !startLoop) {
                    int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                    HttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                    HttpClient client = new DefaultHttpClient(httpParams);


                    HttpGet request = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + prevLat + "," + prevLong +
                            "&destinations=" + stops.get(runningIndex).get(0) + "," + stops.get(runningIndex).get(1) + "&key=" + API_KEY);

                    HttpResponse response = client.execute(request);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                    char[] party = new char[50000];
                    int bytesread = reader.read(party, 0, party.length);
                    String gResponse = new String(party, 0, bytesread);
                    JSONObject test = new JSONObject(gResponse);
                    //return test.getJSONArray("rows").getJSONArray("elements").getJSONObject("duration").getString("text");

                    JSONArray ETA = test.getJSONArray("rows");

                    System.out.print(test.toString() + " cascade");

                    String time = ETA.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");

                    //System.out.println(time);
                    //return Integer.valueOf(time.split(" ")[0]);
                    //int testIndex = Integer.valueOf(time.split(" ")[0]);
                    sum = Integer.valueOf(time.split(" ")[0]);
                    timeEstimates.put(runningIndex, sum);

                    prevLat = stops.get(runningIndex).get(0);
                    prevLong = stops.get(runningIndex).get(1);

                    if (runningIndex + 1 >= stops.size()) runningIndex = 0;
                    else runningIndex++;

                    startLoop = true;
                    System.out.println(runningIndex);
                }

                System.out.println(indexForCascade.toString() + " before we go into OnPost");
                return timeEstimates;
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(LinkedHashMap<Integer, Integer> result) {
            int highestChangeInETA = Integer.MIN_VALUE;
            int newIndex = -1;
            if (prevTimeEstimate == null)  prevTimeEstimate = result;
            else {
                if (result != null) {
                    for (int s : result.keySet()) {
                        if (prevTimeEstimate != null && prevTimeEstimate.containsKey(s)) {
                            System.out.println(Math.abs(prevTimeEstimate.get(s) - result.get(s)) + " at this place " + stops.get(s).get(2));
                            if (prevTimeEstimate.get(s) - result.get(s) > 0 && prevTimeEstimate.get(s) - result.get(s) > highestChangeInETA) {
                                highestChangeInETA = prevTimeEstimate.get(s) - result.get(s);
                                newIndex = s;
                                System.out.println("This is it yall : " + newIndex);
                                //break;
                            }
                        }
                    }
                    System.out.println("1 is this !!!" + prevTimeEstimate.toString());
                    System.out.println("2 is this !!!" + result.toString());
                    System.out.println(driverLat + " -------- " + driverLong);
                    prevTimeEstimate = result;
                    //index = newIndex;
                }
            }

            if (newIndex != -1) index = newIndex;
            else index = indexForCascade;

            System.out.println("index is this " + index);
            System.out.println("prev is this !!!" + prevTimeEstimate.toString());
            //index = indexForCascade;
            populateTable();
        }

    }

    class getLeastTimeIndex extends AsyncTask<List<String>, Void, Integer> {

        List<String> locations;

        public getLeastTimeIndex(List<String> s) {
            locations = s;
        }

        protected Integer doInBackground(List<String>... urls) {
            int least = Integer.MAX_VALUE;
            int leastIndex = 0;
            try {
                    for (int s : stops.keySet()) {
                        int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                        HttpParams httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                        HttpClient client = new DefaultHttpClient(httpParams);

                        HttpGet request = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + locations.get(0) + "," + locations.get(1) +
                                "&destinations=" + stops.get(s).get(0) + "," + stops.get(s).get(1) + "&key=" + API_KEY);

                        HttpResponse response = client.execute(request);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                        char[] party = new char[50000];
                        int bytesread = reader.read(party, 0, party.length);
                        String gResponse = new String(party, 0, bytesread);
                        JSONObject test = new JSONObject(gResponse);
                        //return test.getJSONArray("rows").getJSONArray("elements").getJSONObject("duration").getString("text");

                        JSONArray ETA = test.getJSONArray("rows");

                        System.out.print(test.toString() + " least time");

                        String time = ETA.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");

                        System.out.println(time);
                        //return Integer.valueOf(time.split(" ")[0]);
                        int testTime = Integer.valueOf(time.split(" ")[0]);
                        if (testTime < least) {
                            least = testTime;
                            leastIndex = s;
                        }

                    }
                return leastIndex;

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return -1;
        }

        protected void onPostExecute(Integer result) {
            System.out.println(result + " is HERE");
            //prevTimeEstimate = new LinkedHashMap<>();
            if (result != -1) new cascadeETA(result).execute();

           System.out.print("Getting least did not work bro!");
        }

    }

    class getDriverLoc extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                //if (longitude != null && latitude != null) {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet("http://143.44.78.173:8080/shuttle/get?shuttleid=2");

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                //JSONObject root = new JSONObject(reader.readLine());
                return reader.readLine();
                //}
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(String result) {
            System.out.println("Getting driver location");
            System.out.println(result);
            if (result == null) {
                System.out.println("Receiving driver location did not work");
                return;
            }
            try {
                JSONObject driverLoc = new JSONObject(result);
                // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------ \\

                driverLat = simulatedLats.get(simulatedIndex);
                driverLong = simulatedLongs.get(simulatedIndex++);
                System.out.println(simulatedIndex + " is the location of simulation out of " + simulatedLats.size());
                //driverLat = driverLoc.getString("latitude"); <--- CODE TO BE UNCOMMENTED
                //driverLong = driverLoc.getString("longitude"); <-----  CODE TO BE UNCOMMENTED
                // -----> TO BE DELETED (FOR SIMULATION PURPOSES ONLY) <------ \\
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
            List<String> driverInfo = new ArrayList<String>();
            driverInfo.add(driverLat);
            driverInfo.add(driverLong);

            // if prev time estimates is equal to null
            if (prevTimeEstimate == null) new getLeastTimeIndex(driverInfo).execute();
            else {
                new cascadeETA(index).execute();
                System.out.println("index is pleassseee" + index);
            }
            System.out.println(index);
        }
    }

    class getRoutes extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet("http://143.44.78.173:8080/route/getassigned");

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                //JSONObject root = new JSONObject(reader.readLine());
                return reader.readLine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        // OnPostExecute grabs the stop ids and then
        // places them in the list object.
        protected void onPostExecute(String result) {
            System.out.println(result + "HAHHAHAHAHAHA");
            if (result == null) {
                System.out.println("Getting the stopid's did not work");
            }
            try {
                JSONObject jsonStop = new JSONObject(result);
                String stopIds = jsonStop.getString("stops");
                stops = new LinkedHashMap<Integer, List<String>>();
                for (String s : stopIds.split(",")) {
                    new Tab1Location.stopLoc(s).execute();
                }
                //currentStop = 0;
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

            updateTable();

        }
    }

    class stopLoc extends AsyncTask<String, Void, String> {

        String currentStopID;

        public stopLoc(String s) {
            currentStopID = s;
        }

        protected String doInBackground(String... urls) {
            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);
                HttpGet request = new HttpGet("http://143.44.78.173:8080/stop/location?stopid=" + currentStopID);

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                //JSONObject root = new JSONObject(reader.readLine());
                return reader.readLine();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            return null;
        }

        // This retrieves the essential information for a
        // particular stop and then adds it to the global stop list
        protected void onPostExecute(String result) {
            System.out.println(result);
            if (result == null) {
                System.out.println("Getting the stop location information did not work");
            }
            try {
                JSONObject stopInfo = new JSONObject(result);
                List<String> infoContainer = new ArrayList<String>();
                infoContainer.add(stopInfo.getString("latitude"));
                infoContainer.add(stopInfo.getString("longitude"));
                infoContainer.add(stopInfo.getString("name"));
                stops.put(index++, infoContainer);

                //stopIdList.add(currentStopID);
                System.out.println(stops.toString());
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

            //----> important line of code
            //updateTable();

        }



}

// Sends check in/out information to server
class checkIn extends AsyncTask<String, String, Integer> {

    Tab1Location caller;
    String uRL;
    String json;

    public checkIn(Tab1Location t1l, String url, String id) {
        super();
        this.caller=t1l;
        this.uRL = url;
        json = "{\"userid\":" + id + ",\"latitude\":" + "\"44\"" + ",\"longitude\":" + "\"-88\"" + ",\"clipboardsessionid\":" + "\"1\"}";
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(String... params) {
        int return_value = -1;

        try {
            int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
            HttpClient client = new DefaultHttpClient(httpParams);

            HttpPost request = new HttpPost(uRL);
            request.setEntity(new ByteArrayEntity(
                    json.toString().getBytes("UTF8")));
            HttpResponse response = client.execute(request);
            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
            String result = reader.readLine();
            return_value = Integer.valueOf(result);

            return return_value;

        } catch(Exception ex) {
            Log.d("Check In Activity","Exception in doPost:" + ex.toString());
        }
        return return_value;
    }



    protected void onPostExecute(final Integer response) {
        caller.onCheckInCompleted(response);
    }

}

class checkOut extends AsyncTask<String, String, Integer> {

    Tab1Location caller;
    String uRL;

    public checkOut(Tab1Location t1l, String url) {
        super();
        this.caller=t1l;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }

    @Override
    protected Integer doInBackground(String... params) {
        int return_value = -1;

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
                return_value = Integer.valueOf(response);
                return return_value;
            }
            else if(responseCode == HttpURLConnection.HTTP_CONFLICT){
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                while ((line=br.readLine()) != null) {
                    responseError+=line;
                }
                return_value = Integer.valueOf(responseError);
                return return_value;
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
        return return_value;
    }

    protected void onPostExecute(final Integer response) {
        caller.onCheckOutCompleted(response);
    }


}class getCheckInStatus extends AsyncTask<String, String, String> {

    Tab1Location caller;
    String uRL;

    public getCheckInStatus(Tab1Location dm, String url) {
        super();
        this.caller=dm;
        this.uRL = url;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        caller.onGetCheckedInStatusCompleted(result);
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

        List<String> listOfPassengers = new ArrayList<String>();

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


}}