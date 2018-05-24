package Fragments;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import Adapter.GridViewAdapter;
import Models.GridItem;
import Models.SaveUserId;
import bagga.com.traction.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BlankFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BlankFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BlankFragment extends Fragment {

    View v;

    ArrayList picturesArrayList;
    private OnFragmentInteractionListener mListener;
    public CardContainer mCardContainer;
    private DatabaseReference mDatabase;
    private ArrayList<String> _user;
    private ArrayList<String> _imageUrl;
    private ArrayList<String> _activity;
    private ArrayList<String> _location;
    CardModel cardModel;
    SimpleCardStackAdapter adapter;
    ArrayList<String> al;
    ArrayAdapter arrayAdapter;
    GridItem item;
    private ArrayList<GridItem> mGridData;
    private ArrayList<GridItem> loopData;

    private GridViewAdapter gridAdapter;
    SwipeFlingAdapterView flingContainer;
    static SaveUserId userId ;
    private final double EARTH_RADIUS = 6378.137;
    Locations locations;
    ArrayList<String> closedUsers;
    int i = 0;
    boolean adapterHasDataFlag = false;
    FloatingActionButton button;
    TextView like,nope;
    Animation in,out;
    ArrayList<String> otherUsersKey;

    public BlankFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        v=  inflater.inflate(R.layout.fragment_blank, container, false);
        Toolbar myToolbar = (Toolbar)v.findViewById(R.id.my_toolbar);
        myToolbar.setTitle(Html.fromHtml("<font color='#FFFFFF'>TRACTION </font>"));
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        userId = new SaveUserId(getActivity().getApplicationContext());
        final FloatingActionButton heart = (FloatingActionButton)v.findViewById(R.id.heart);
        FloatingActionButton star = (FloatingActionButton)v.findViewById(R.id.star);
        like = (TextView)v.findViewById(R.id.like);
        nope = (TextView)v.findViewById(R.id.nope);


        button = (FloatingActionButton)v.findViewById(R.id.fab);
        flingContainer = (SwipeFlingAdapterView)v.findViewById(R.id.frame);
        locations = new Locations(getActivity());
        locations.setOnEventListener(new Locations.MyListener() {
            @Override
            public void callback(Location location) {
                updateLocation(location);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectLeft();
                nope.setVisibility(View.VISIBLE);
                in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(2000);
                nope.startAnimation(in);

            }
        });

        heart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
                like.setVisibility(View.VISIBLE);
                in = new AlphaAnimation(0.0f, 1.0f);
                in.setDuration(2000);
                like.startAnimation(in);
            }
        });

        star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flingContainer.getTopCardListener().selectRight();
            }
        });





        dragListener();
        item = new GridItem();
        mGridData = new ArrayList<GridItem>();
        loopData = new ArrayList<GridItem>();
        //item.setName("Football");
        //item.setImage("http://www.planwallpaper.com/static/images/Football-players-final-render-7.jpg");
        //mGridData.add(item);

        picturesArrayList = new ArrayList();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        _activity = new ArrayList<>();
        _imageUrl = new ArrayList<>();
        _user = new ArrayList<>();
        _location = new ArrayList<>();

        return v;
    }

    public void dragListener(){

        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
            }

            @Override
            public void onLeftCardExit(Object o) {

                out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(2000);
                nope.startAnimation(out);
                nope.setVisibility(View.INVISIBLE);
                mGridData.remove(0);

                gridAdapter.notifyDataSetChanged();

            }

            @Override
            public void onRightCardExit(Object o) {

                bookmarkKey(otherUsersKey.get(0));

                otherUsersKey.remove(0);
                out = new AlphaAnimation(1.0f, 0.0f);
                out.setDuration(2000);
                like.startAnimation(out);
                like.setVisibility(View.INVISIBLE);
                mGridData.remove(0);
                gridAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int i) {
                if (i <=1) {
                    for (int j= 0; j<loopData.size();j++) {
                        gridAdapter.add(loopData.get(j));
                    }
                    gridAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(float v) {

            }
        });
    }


    private void bookmarkKey(String key) {
        mDatabase.child("liked").child(userId.getUserId()).child(key).setValue(true);
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);

        }
    }

    private void updateLocation(Location location){

        GeoFire geoFire = new GeoFire(new Firebase("https://traction-e5293.firebaseio.com/locs"));
        geoFire.setLocation(userId.getUserId(), new GeoLocation(location.getLatitude(), location.getLongitude()), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, FirebaseError error) {
                if (error != null) {

                    System.err.println("There was an error saving the location to GeoFire: " + error);
                } else {

                }
            }
        });

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()),userId.getUserDistance());
        closedUsers = new ArrayList<>();
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                closedUsers.add(key);


            }

            @Override
            public void onKeyExited(String key) {
                closedUsers.remove(key);
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println("Moving "+key+"   "+ location.latitude);
            }

            @Override
            public void onGeoQueryReady() {
                showOnlyClosedUser();
            }

            @Override
            public void onGeoQueryError(FirebaseError error) {
                System.out.println(error);
            }
        });
    }

    public static void updateLocation(Double lat, Double longs) {
        //Method used with broadCast receiver

    }

    @Override
    public void onResume() {
        super.onResume();
        new BlankFragment();
    }

    private void showOnlyClosedUser() {
        otherUsersKey = new ArrayList<>();
        mDatabase.child("users").orderByKey().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (adapterHasDataFlag) {
                    gridAdapter.clear();
                }

                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {

                    item = new GridItem();
                    for (int j= 0;j<closedUsers.size();j++) {
                        if (snapshot.getKey().equals(closedUsers.get(j))) {

                            otherUsersKey.add(snapshot.getKey());
                            HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                            String imageUrl = (String) data.get("imageUrl");
                            String name = (String) data.get("userName");
                            String activity = (String) data.get("activityName");
                            String location = (String) data.get("location");

                            item.setImage(imageUrl);
                            item.setName(name);
                            item.setLocation(location);
                            item.setActivity(activity);
                            mGridData.add(item);
                            loopData.add(item);
                        }
                    }
                    i++;
                }
                gridAdapter = new GridViewAdapter(getActivity(),R.layout.std_card_inner, R.id.titleCard,mGridData);
                flingContainer.setAdapter(gridAdapter);
                adapterHasDataFlag = true;
                gridAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Fragment fragment = new Settings();
        android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.fragment_container1, fragment);
        fragmentTransaction.commit();
        return super.onOptionsItemSelected(item);
    }

    public static class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            intent.getAction();
            double lat = intent.getDoubleExtra("lat",0);
            double longs = intent.getDoubleExtra("long",0);
            updateLocation(lat,longs);

        }
    }



}
