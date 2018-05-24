package Fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import Adapter.BookmarkAdapter;
import Models.BookmarkModel;
import bagga.com.traction.R;

public class Bookmarks extends Fragment {



    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    FirebaseDatabase database ;
    DatabaseReference myRef;
    BookmarkModel item;
    private ArrayList<BookmarkModel> mGridData;

    public Bookmarks() {
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
        View v ;
        v =  inflater.inflate(R.layout.fragment_bookmarks, container, false);
        database = FirebaseDatabase.getInstance();
        mGridData = new ArrayList<>();
        myRef = database.getReference();
        mRecyclerView = (RecyclerView)v.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        myRef.child("liked").addValueEventListener(new ValueEventListener() {
            ArrayList<String> bookmarkedKeys = new ArrayList<String>();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    //Will get all keys which are saved
                    for (DataSnapshot snap:snapshot.getChildren()) {
                        bookmarkedKeys.add(snap.getKey());
                    }
                    filterLikedUsers(bookmarkedKeys);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return  v;
    }

    private void filterLikedUsers(final ArrayList<String> bookmarkedKeys) {

            myRef.child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                        item = new BookmarkModel();
                        for (int i = 0; i<bookmarkedKeys.size();i++) {
                            if (snapshot.getKey().equals(bookmarkedKeys.get(i))){

                                HashMap<String, Object> data = (HashMap<String, Object>) snapshot.getValue();
                                String imageUrl = (String) data.get("imageUrl");

                                String activity = (String) data.get("activityName");
                                String location = (String) data.get("location");

                                item.setImageUrl(imageUrl);
                                item.setName(activity);
                                item.setAddress(location);
                                mGridData.add(item);
                            }
                        }
                    }
                    BookmarkAdapter adapter = new BookmarkAdapter(mGridData);
                    mRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
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
