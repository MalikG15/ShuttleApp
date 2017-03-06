package lawrence.edu.shuttleme;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


/**
 * Created by malikg on 2/21/17.
 */

public class DriverETA extends AppCompatActivity {
    HashMap<String, List<String>> stopLocList = new HashMap<String, List<String>>();
    ArrayList<String> stopIdList = new ArrayList<String>();
    private String driverLat;
    private String driverLong;
    private String API_KEY = GoogleAPI.Google_API_KEY;
    private int currentStop;
    public Integer timeEstimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        new getDriverLoc().execute();

    }

    // Gets driver location from the ShuttleLocation table
    class getDriverLoc extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                //if (longitude != null && latitude != null) {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet("http://143.44.78.173:8080/shuttle/get?shuttleid=de748d23-d9f6-4f5b-8448-89bf0c5302e7");

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
                driverLat = driverLoc.getString("latitude");
                driverLong = driverLoc.getString("longitude");
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }
            new getRoutes().execute();
        }
    }

    // Gets the given route, and the associated stops
    class getRoutes extends AsyncTask<String, String, String> {

        protected String doInBackground(String... urls) {
            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet("http://143.44.78.173:8080/route/getroute?routeid=1");

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
                for (String s : stopIds.split(",")) {
                    new stopLoc(s).execute();
                }
                currentStop = 0;
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }


        }
    }

    // Gets the stop location
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
                infoContainer.add(stopInfo.getString("address"));
                stopLocList.put(currentStopID, infoContainer);
                stopIdList.add(currentStopID);
                System.out.println("HELLLLLLLOOOOOOO");
            }
            catch (JSONException ex) {
                ex.printStackTrace();
            }

            if (stopLocList.size() >= 3) calculateETA();
        }

    }

    public void calculateETA() {

        /*int x = currentStop + 1;
        int tempStop = currentStop;
        int sumOfTime = 0;
        while (tempStop != x) {
            List<String> stopID1 = new ArrayList<String>();

            if (tempStop < stopIdList.size()) stopID1 = stopLocList.get(stopIdList.get(tempStop));
            if (x >= stopIdList.size()) x = 0;

            List<String> stopID2 = stopLocList.get(stopIdList.get(x));

            // Will contain the coordinates of the prev
            List<String> locationCoordinates = new ArrayList<String>();

            int timeEstimation = new getTimeEstimate(stop).execute();
        }*/

        String prevLat = "";
        String prevLong = "";

        int min = Integer.MAX_VALUE;
        String minId = new String();
        for (String s : stopLocList.keySet()) {
            List<String> comparison = new ArrayList<String>();
            List<String> curStop = stopLocList.get(s);

            // Add driver location to find the closest
            comparison.add(driverLat);
            comparison.add(driverLong);

            // add it to send to the API
            comparison.add(curStop.get(0));

            // Cache the previous latitude
            prevLat = curStop.get(0);

            // add it to send to the API
            comparison.add(curStop.get(1));

            //Cache the previous longitude
            prevLong = curStop.get(1);



            try {
               timeEstimate = new getTimeEstimate(comparison).execute().get();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }

            if (min > timeEstimate) {
                min = timeEstimate;
                minId = s;
                System.out.println(minId + "bam");
            }
        }

        int startIndex = stopIdList.indexOf(minId);

        List<String> curStop1 = stopLocList.get(stopIdList.get(startIndex));

        prevLat = curStop1.get(0);
        prevLong = curStop1.get(1);

        TextView stop = (TextView) findViewById(R.id.stop_1);

        stop.setText((stopLocList.get(stopIdList.get(startIndex))).get(2) + " the shuttle is " + min + " min away.\n");
        timeEstimate = min;

        int timeSum = 0;
        int curIndex = startIndex + 1;
        if (curIndex >= stopIdList.size()) curIndex = 0;
        while (curIndex != startIndex) {
            stop = (TextView) findViewById(R.id.stop_2);
            if (!stop.getText().equals("")) stop = (TextView) findViewById(R.id.stop_3);
            if (!stop.getText().equals("")) stop = (TextView) findViewById(R.id.stop_4);


            List<String> comparison = new ArrayList<String>();
            //if (curIndex + 1 >= stopIdList.size()) curIndex = 0;
            List<String> curStop = stopLocList.get(stopIdList.get(curIndex));
            comparison.add(prevLat);
            comparison.add(prevLong);
            comparison.add(curStop.get(0));
            comparison.add(curStop.get(1));
            try {
                timeEstimate += new getTimeEstimate(comparison).execute().get();
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            prevLat = curStop.get(0);
            prevLong = curStop.get(1);

            System.out.println(timeEstimate + " time is this ");
            //timeSum += timeEstimate;
            stop.setText((stopLocList.get(stopIdList.get(curIndex))).get(2) + " is " + timeEstimate + " min away.\n");

            if (curIndex + 1 >= stopIdList.size()) curIndex = 0;
            else curIndex++;
            //curIndex++;
        }



    }


    //

    //public int findClosestStop(String ) {

    //}

    class getTimeEstimate extends AsyncTask<List<String>, Void, Integer> {

        List<String> locations;

        public getTimeEstimate(List<String> s) {
            locations = s;
        }

        protected Integer doInBackground(List<String>... urls) {
            try {
                int TIMEOUT_MILLISEC = 10000;  // = 10 seconds
                HttpParams httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpConnectionParams.setSoTimeout(httpParams, TIMEOUT_MILLISEC);
                HttpClient client = new DefaultHttpClient(httpParams);

                HttpGet request = new HttpGet("https://maps.googleapis.com/maps/api/distancematrix/json?origins=" + locations.get(0) + "," + locations.get(1) +
                        "&destinations=" + locations.get(2) + "," + locations.get(3) + "&key=" + API_KEY);

                HttpResponse response = client.execute(request);
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

                char[] party = new char[50000];
                int bytesread = reader.read(party, 0, party.length);
                String gResponse = new String(party, 0, bytesread);
                JSONObject test = new JSONObject(gResponse);
                //return test.getJSONArray("rows").getJSONArray("elements").getJSONObject("duration").getString("text");

                JSONArray ETA = test.getJSONArray("rows");

                System.out.print(test.toString());

                String time = ETA.getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("duration").getString("text");

                System.out.println(time);
                return Integer.valueOf(time.split(" ")[0]);

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return -1;
        }

        protected void onPostExecute(Integer result) {
            System.out.println(result + " is HERE");
            timeEstimate = result;
        }



    }




}
