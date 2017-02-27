package lawrence.edu.shuttleme;

import android.os.Bundle;
import android.app.Fragment;
// import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by elkingarcia on 2/21/17.
 */
public class LocateShuttle extends android.support.v4.app.Fragment implements OnMapReadyCallback {

    View rootView;
    GoogleMap mGoogleMap = null;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        rootView = inflater.inflate(R.layout.activity_locate_shuttle,null);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment mapFragment =(SupportMapFragment) fm.findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        // Add a marker in Plantz Hall, and move the camera.
        LatLng plantzhall = new LatLng(44.262286, -88.398013);
        mGoogleMap.addMarker(new MarkerOptions().position(plantzhall).title("Plantz Hall"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(plantzhall));

        // Add a marker in Walmart, and move the camera.
        LatLng walmart = new LatLng(44.269288, -88.480216);
        mGoogleMap.addMarker(new MarkerOptions().position(walmart).title("Walmart"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(walmart));
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(14));
    }
}
