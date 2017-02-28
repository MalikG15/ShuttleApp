package lawrence.edu.shuttleme;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;
import android.os.AsyncTask;
import android.widget.AdapterView;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class DriverManager extends AppCompatActivity {

    private ListView listView;
    private int currentPos;
    //This arraylist will have data as pulled from server.
    private Map<Integer, String> listOfUsers;
    private List users;
    private boolean isPassengerList = false;
    private boolean haveRetrieved = false;

    private ArrayAdapter<String> adapter;

    private SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_manager);

        listView = (ListView) findViewById(R.id.listview);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        String value = (String) listView.getItemAtPosition(position);
                        currentPos = position;
                        view.setSelected(true);
                    }

                }
        );

        //Establishes search text listeners
        search = (SearchView) findViewById(R.id.searchBar);
        search.setOnQueryTextListener(new OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

    }

    // Pre-Condition: Click on any radio button
    public void getList(View view) {

        //require to import the RadioButton class
        RadioButton pb = (RadioButton) findViewById(R.id.passengerButton);
        RadioButton db = (RadioButton) findViewById(R.id.driverButton);

        //is the current radio button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        //now check which radio button is selected
        //android switch statement
        //either get passenger list or driver list
        switch (view.getId()) {
            case R.id.passengerButton:
                if (checked) {
                   new RetrieveList(this, "http://143.44.78.173:8080/user/passengers").execute();
                    isPassengerList = true;
                    haveRetrieved=true;
                }
                break;
            case R.id.driverButton:
                if (checked) {
                    new RetrieveList(this, "http://143.44.78.173:8080/user/drivers").execute();
                    isPassengerList = false;
                    haveRetrieved=true;
                }
                break;
        }
    } // Post-Condition: Can switch between getting passenger or driver list directly from database


    // Pre-Condition: Click on promote button, has to have item selected, has to be a passenger
    public void promote(View view) {
        // Check that list has been retrieved and that its passenger list
        if(haveRetrieved == false){
            Toast.makeText(getApplicationContext(), "Error: Need to retrieve a list", Toast.LENGTH_SHORT).show();
        }
        else if(isPassengerList == false){
            Toast.makeText(getApplicationContext(), "Error: This person is already a driver ", Toast.LENGTH_SHORT).show();
        }
        // look up user id in map
        else if(isPassengerList == true){

            String id = listOfUsers.get(currentPos);
            new changeRole(this, "http://143.44.78.173:8080/user/changerole?userid="+id).execute();
            Toast.makeText(getApplicationContext(), adapter.getItem(currentPos), Toast.LENGTH_SHORT).show();
        }

    } // Post-Condition: update listview with updated list, update database with new permission

    // Pre-Condition: Click on promote button, has to have item selected, has to be a driver
    public void demote(View view) {
        // Check that list has been retrieved and that its passenger list
        if(haveRetrieved == false){
            Toast.makeText(getApplicationContext(), "Error: Need to retrieve a list", Toast.LENGTH_SHORT).show();
        }
        else if(isPassengerList == true){
            Toast.makeText(getApplicationContext(), "Error: This person is already a passenger ", Toast.LENGTH_SHORT).show();
        }
        // look up user id in map
        else if(isPassengerList == false){

            String id = listOfUsers.get(currentPos);
            new changeRole(this, "http://143.44.78.173:8080/user/changerole?userid="+id).execute();
            Toast.makeText(getApplicationContext(), adapter.getItem(currentPos), Toast.LENGTH_SHORT).show();
        }
    } // Post-Condition: update listview with updated list, update database with new permission

    // Interpret result and show it on listview
    public void onRetrieveListCompleted(String result){
        // Store id and names of users
        listOfUsers = new HashMap<Integer,String>();
        users = new ArrayList<String>();
        // Parse data - get id and names
        try {
            JSONArray jArray = new JSONArray(result);
            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    String id = oneObject.getString("userid");
                    String name = oneObject.getString("name");
                    listOfUsers.put(i,id);
                    users.add(name);
                } catch (JSONException e) {
                    // Oops
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Update adapter with list of drivers, then update listview with adapter
        adapter = new ArrayAdapter<String>(this, R.layout.list_view, R.id.textitem, users);
        listView.setAdapter(adapter);
    } // THROW ALERT IF EMPTY - otherwise show list

    public void onChangeRoleCompleted(String result){
        System.out.print(result);
        if(isPassengerList == true){
            new RetrieveList(this, "http://143.44.78.173:8080/user/passengers").execute();
        }
        else{
            new RetrieveList(this, "http://143.44.78.173:8080/user/drivers").execute();
        }

    }
}


class RetrieveList extends AsyncTask<String, String, String> {

    DriverManager caller;
    String uRL;

    public RetrieveList(DriverManager dm, String url) {
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
        caller.onRetrieveListCompleted(result);
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

}

class changeRole extends AsyncTask<String, String, String> {

    DriverManager caller;
    String uRL;

    public changeRole(DriverManager dm, String url) {
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
        caller.onChangeRoleCompleted(result);
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
