package bagga.com.traction;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.firebase.client.Firebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Fragments.BlankFragment;
import Models.CreateUser;
import Models.SaveUserId;

public class MainActivity extends AppCompatActivity implements Main.EnterProfileInfo {

    private DatabaseReference mDatabase;
    SaveUserId userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
       // getSupportActionBar().hide();
         userId = new SaveUserId(getApplicationContext());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (userId.getUserId()== null){
            Fragment mainFragment = new Main();
            fragmentTransaction.add(R.id.fragment_container1, mainFragment);
            fragmentTransaction.commit();
        } else {
            Fragment cardFragment = new BlankFragment();
            fragmentTransaction.add(R.id.fragment_container1,cardFragment);
            fragmentTransaction.commit();
        }


    }

    @Override
    public void onJoinPressed(String userName, String location, String activity, String imageUrl) {
        CreateUser createUser = new CreateUser(userName,imageUrl,activity,location);

        String key = mDatabase.push().getKey();
        mDatabase.child("users").child(key).setValue(createUser);
        userId.saveUserId(key);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        BlankFragment mainFragment = new BlankFragment();
        fragmentTransaction.add(R.id.fragment_container1, mainFragment);
        fragmentTransaction.commit();
    }


}
