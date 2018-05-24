package Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import bagga.com.traction.Main;

/**
 * Created by Davin12x on 16-07-01.
 */
public class Locations implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {
    private GoogleApiClient mGoogleApiClient;
    private Location mlastLocation;
    LocationRequest mLocationRequest;
    private Main.AddressResultReceiver mResultReceiver;
    Activity activity;
    MyListener ml;

   public Locations(Activity activity) {

       BlankFragment.MyReceiver receiver = new BlankFragment.MyReceiver();
       IntentFilter filter = new IntentFilter("bagga");
       activity.getApplicationContext().registerReceiver(receiver,filter);

       this.activity = activity;
       //Creating instance of google Api
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(activity)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();
        }
    }

    public void setOnEventListener(MyListener listener) {
        ml = listener;
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);


        if (mlastLocation == null) {
            createLocationRequest();
            startLocationUpdates();
        } else {

            if (ml!= null){
                ml.callback(mlastLocation);
                createLocationRequest();
                startLocationUpdates();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Double distance = distanceBetweenTwoLocationsInKm(mlastLocation.getLatitude(),mlastLocation.getLongitude(),
                location.getLatitude(),location.getLongitude());

        if (distance>2) {

            if (ml!= null){
                ml.callback(location);
            }
            mlastLocation = location;
        }

        // broadcastIntent(location);
    }

    public static Double distanceBetweenTwoLocationsInKm(Double latitudeOne, Double longitudeOne, Double latitudeTwo, Double longitudeTwo) {
        Double earthRadius = 6371.0;
        Double diffBetweenLatitudeRadians = Math.toRadians(latitudeTwo - latitudeOne);
        Double diffBetweenLongitudeRadians = Math.toRadians(longitudeTwo - longitudeOne);
        Double latitudeOneInRadians = Math.toRadians(latitudeOne);
        Double latitudeTwoInRadians = Math.toRadians(latitudeTwo);
        Double a = Math.sin(diffBetweenLatitudeRadians/2) * Math.sin(diffBetweenLatitudeRadians/2) + Math.cos(latitudeOneInRadians) * Math.cos(latitudeTwoInRadians) * Math.sin(diffBetweenLongitudeRadians/2)
                * Math.sin(diffBetweenLongitudeRadians/2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return (earthRadius * c);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    public Location getLocation() {
        return mlastLocation;
    }

    public void broadcastIntent(Location location){
        Intent intent = new Intent();
        intent.setAction("bagga");
        intent.putExtra("lat",location.getLatitude());
        intent.putExtra("long",location.getLongitude());
        activity.sendBroadcast(intent);
    }

    public interface MyListener {
        // you can define any parameter as per your requirement
         void callback(Location location);
    }
}
