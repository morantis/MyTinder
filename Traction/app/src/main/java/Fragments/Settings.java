package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import Models.SaveUserId;
import app.minimize.com.seek_bar_compat.SeekBarCompat;
import bagga.com.traction.R;


public class Settings extends Fragment {



    SeekBarCompat seekBarCompat;
    int progressBar = 10;
    SaveUserId userId;
    TextView distance;
    FloatingActionButton back;


    private OnFragmentInteractionListener mListener;

    public Settings() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v;
        v = inflater.inflate(R.layout.fragment_settings, container, false);
        Toolbar myToolbar = (Toolbar)v.findViewById(R.id.my_toolbar);
        myToolbar.setTitle("Settings");
        ((AppCompatActivity)getActivity()).setSupportActionBar(myToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new BlankFragment();
                android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container1, fragment);
                fragmentTransaction.commit();
            }
        });

        userId = new SaveUserId(getActivity().getApplicationContext());
        TextView txt = (TextView)v.findViewById(R.id.name);
        distance = (TextView)v.findViewById(R.id.distance);
//        Typeface font = Typeface.createFromAsset(getActivity().getAssets(),"Jumping Running.ttf");
//        txt.setTypeface(font);

        seekBarCompat = (SeekBarCompat)v.findViewById(R.id.materialSeekBar);
        seekBarCompat.setThumbColor(Color.RED);
        seekBarCompat.setProgressColor(Color.CYAN);
        seekBarCompat.setProgressBackgroundColor(Color.BLUE);
        seekBarCompat.setThumbAlpha(128);
        back = (FloatingActionButton)v.findViewById(R.id.back);

        seekBarCompat.setProgress(userId.getUserDistance());
        updateUI();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new Bookmarks();
                android.app.FragmentManager fragmentManager = getActivity().getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.fragment_container1, fragment);
                fragmentTransaction.commit();
            }
        });



        seekBarCompat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressBar = progress;
                updateUI();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                userId.saveDistance(seekBar.getProgress());
            }
        });


        return  v;
    }

    private void updateUI() {
        if (progressBar == 0){
            distance.setText(String.valueOf(userId.getUserDistance())+" KM");
        } else {
            distance.setText(String.valueOf(seekBarCompat.getProgress())+" KM");
        }

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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


}
