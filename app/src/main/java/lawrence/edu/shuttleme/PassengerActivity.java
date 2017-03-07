package lawrence.edu.shuttleme;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.SupportMapFragment;

public class PassengerActivity extends AppCompatActivity {
    String userID;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                userID = null;
            } else {
                userID = extras.getString("USER_ID");
            }
        } else {
            userID = (String) savedInstanceState.getSerializable("USER_ID");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("ShuttleMe");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        // Add listener to tabs
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                if (mSectionsPagerAdapter != null) {
                    int count = mSectionsPagerAdapter.getCount();
                    Fragment fragment = (Fragment)mSectionsPagerAdapter.getItem(position);
                    // Run through every other fragment and set visibility to false;
                    if(fragment != null){
                        for(int i=0; i < count; i++){
                            if(i == position){
                                fragment.setUserVisibleHint(true);
                            }
                            else{
                                Fragment tempFrag = (Fragment)mSectionsPagerAdapter.getItem(i);
                                if(tempFrag != null){
                                    tempFrag.setUserVisibleHint(false);
                                }
                            }
                        }
                    }
                }
            }
        });

        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);


    }

    @Override
    public void onBackPressed() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_passenger, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, LoginActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
            /*
            case R.id.menu_item2:
                // another startActivity, this is for item with id "menu_item2"
                break;
            */
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            //Returning the current tabs
            Tab1Location tab1;
            switch (position){
                case 0:
                    tab1 = new Tab1Location();
                    return tab1;
                case 1:
                    LocateShuttle tab3 = new LocateShuttle();
                    return tab3;
                case 2:
                    Tab2GeneralInformation tab2 = new Tab2GeneralInformation();
                    return tab2;
                default:
                    tab1 = new Tab1Location();
                    return tab1;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Current Location";
                case 1:
                    return "Locate Shuttle";
                case 2:
                    return "General Information";
            }
            return null;
        }
    }

    public String getUserID(){
        return this.userID;
    }

}

