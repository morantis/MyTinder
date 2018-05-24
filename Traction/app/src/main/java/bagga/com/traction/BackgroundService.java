package bagga.com.traction;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by Davin12x on 16-06-25.
 */
public class BackgroundService extends IntentService {

    public String TAG = "";
    public  final String PACKAGE_NAME ;
    public  final String RESULT_DATA_KEY;
    public static int SUCCESS;
    public Main.AddressResultReceiver mReceiver;

    public BackgroundService() {
        super("");

        PACKAGE_NAME =
                "bagga.com.traction";
        RESULT_DATA_KEY = PACKAGE_NAME +
                ".RESULT_DATA_KEY";


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String errorMessage = "";


        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra("location");
        List<Address> addresses = null;


        try {
            addresses = geocoder.getFromLocation(
                    location.getLatitude(),
                    location.getLongitude(),
                    // In this sample, get just a single address.
                    1);
        } catch (IOException i) {
            Log.e(TAG, errorMessage, i);
        }
        Address address = addresses.get(0);
        ArrayList<String> addressFragments = new ArrayList<String>();
        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread.
        for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }


        deliverResultToReceiver(0,
                TextUtils.join(System.getProperty("line.separator"),
                        addressFragments));

    }

    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        mReceiver = new Main.AddressResultReceiver(null);
        bundle.putString("key", message);
        mReceiver.send(resultCode, bundle);
    }


}
