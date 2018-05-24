package bagga.com.traction;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main.EnterProfileInfo} interface
 * to handle interaction events.
 */
public class Main extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener {

    private EnterProfileInfo mListener;
    private Button join;
    private EditText userName, location, activity;
    View v;
    private GoogleApiClient mGoogleApiClient;
    private Location mlastLocation;
    LocationRequest mLocationRequest;
    private AddressResultReceiver mResultReceiver;
    static String mAddressOutput;
    private CircleImageView imageView;
    FirebaseStorage storage;
    StorageReference spaceRef;
    private Boolean isImageUrlReceived;
    private String imageUrl;
    private ProgressBar spinner;

    public Main() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_main, container, false);

        //Creating instance of google Api
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        join = (Button) v.findViewById(R.id.join_button);
        userName = (EditText) v.findViewById(R.id.username);
        location = (EditText) v.findViewById(R.id.loaction_field);
        activity = (EditText) v.findViewById(R.id.activity_field);
        imageView = (CircleImageView) v.findViewById(R.id.imageView2);
        spinner = (ProgressBar)v.findViewById(R.id.progressBar1);
        spinner.setVisibility(View.GONE);
        storage = FirebaseStorage.getInstance();

        Calendar c = Calendar.getInstance();
        int seconds = c.get(Calendar.SECOND);
        StorageReference storageRef = storage.getReferenceFromUrl("gs://traction-e5293.appspot.com");
        StorageReference imagesRef = storageRef.child("Activities");
        spaceRef = storageRef.child("images/"+seconds+"space.jpg");
        isImageUrlReceived = false;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxImagePicker.with(getActivity()).requestImage(Sources.CAMERA)
                        .flatMap(new Func1<Uri, Observable<Bitmap>>() {
                            @Override
                            public Observable<Bitmap> call(Uri uri) {
                                imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                imageView.setBackgroundColor(Color.parseColor("#536DFE"));
                                return RxImageConverters.uriToBitmap(getActivity(), uri);
                            }
                        })
                        .subscribe(new Action1<Bitmap>() {
                            @Override
                            public void call(Bitmap bitmap) {
                                imageView.setImageBitmap(bitmap);
                                startUploading(bitmap);
                            }
                        });
            }
        });
        location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                location.setText(mAddressOutput);
            }
        });


        if (v.getContext() instanceof EnterProfileInfo) {
            mListener = (EnterProfileInfo) v.getContext();
        }

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setVisibility(View.VISIBLE);
                if (isImageUrlReceived) {
                    spinner.setVisibility(View.GONE);
                    mListener.onJoinPressed(userName.getText().toString(), location.getText().toString(), activity.getText().toString(),imageUrl);
                }

            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EnterProfileInfo) {
            mListener = (EnterProfileInfo) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement EnterProfileInfo Listener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
        if (mlastLocation != null) {
            createLocationRequest();
            startLocationUpdates();
            startIntentService();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mlastLocation = location;
        updateLocation();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    protected void startLocationUpdates() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //This will start services to get current address of user
    protected void startIntentService() {
        Intent intent = new Intent(getActivity(), BackgroundService.class);
        intent.putExtra("result", mResultReceiver);
        intent.putExtra("location", mlastLocation);
        getActivity().startService(intent);
    }

    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void updateLocation() {
        System.out.println(mlastLocation.getLatitude());
        System.out.println(mlastLocation.getLongitude());
    }

    private void startUploading(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTasks = spaceRef.putBytes(data);
        try {
            uploadTasks.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    isImageUrlReceived = true;
                    imageUrl = taskSnapshot.getDownloadUrl().toString();

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public interface EnterProfileInfo {
        void onJoinPressed(String userName, String location, String activity, String imageUrl);
    }

    @SuppressLint("ParcelCreator")
    public static class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);
            mAddressOutput = resultData.getString("key");
        }
    }
}

