package com.example.shawon.foodies;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private double longitude;
    private double latitude;
    private GoogleApiClient googleApiClient;

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 100;
    private final static int LOCATION_PERMISSION_REQUEST = 101;

    private IGeoCoordinates iGeoCoordinates;

    String orderID,orderNumber;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);

        iGeoCoordinates = CurrentServerUser.getGeoCodeService();

        if(getIntent() != null){
            orderID = getIntent().getStringExtra("OrderID");
            orderNumber = "Order of "+CurrentServerUser.currentRequest.getPhone();
        }



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initializing googleApiClient
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // To get click on the title of the marker...

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(marker.getTitle().toString().equals(orderNumber)){
                    Intent intent = new Intent(TrackingOrder.this,OrderDetailActivityServer.class);
                    intent.putExtra("OrderID",orderID);
                    startActivity(intent);
                }
            }
        });

    }

    //Getting current location
    private void getCurrentLocation() {
        if (mMap != null) mMap.clear();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestRunTimePermission();
        }

        if (checkPlayServices()) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (location != null) {

                //Getting longitude and latitude
                longitude = location.getLongitude();
                latitude = location.getLatitude();

                //moving the map to location
                moveMap(location);

            }
            else{
                Toast.makeText(this,"Can't access the location!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void moveMap(Location location) {

        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Foodies"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.getUiSettings().setZoomControlsEnabled(true);


        // After add Marker for your location...Add Marker for this order...and draw Route...

        drawRoute(location, CurrentServerUser.currentRequest.getAddress());

    }

    private void drawRoute(final Location location, String address) {

        iGeoCoordinates.getGeoCode(address).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                try {

                    JSONObject jsonObject = new JSONObject(response.body().toString());

                    String lat = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lat").toString();

                    String lng = ((JSONArray) jsonObject.get("results"))
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location")
                            .get("lng").toString();

                    LatLng orderLocation = new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));

                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.blue_location);

                    bitmap = CurrentServerUser.scaleBitmap(bitmap,80,80);

                    MarkerOptions marker = new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                            .title("Order of "+CurrentServerUser.currentRequest.getPhone())
                            .position(orderLocation);

                    mMap.addMarker(marker);

                    // draw route...

                    iGeoCoordinates.getDirections(location.getLatitude()+","+location.getLongitude(),
                            orderLocation.latitude+","+orderLocation.longitude)
                            .enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {

                                    new ParserTask().execute(response.body().toString());

                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {

                                }
                            });


                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

    }

    private boolean checkPlayServices() {

        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                Toast.makeText(this, "Your device does not support to perform the action!", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;

    }

    private void requestRunTimePermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        }, LOCATION_PERMISSION_REQUEST);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case LOCATION_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getCurrentLocation();

                }
                break;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

        if (googleApiClient != null) googleApiClient.connect();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (googleApiClient != null) googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient != null)
            googleApiClient.disconnect();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return true;
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>>{

        ProgressDialog mDialog = new ProgressDialog(TrackingOrder.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mDialog.setMessage("Processing");
            mDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... params) {

            JSONObject jsonObject;
            List<List<HashMap<String,String>>> route = null;

            try {

                jsonObject = new JSONObject(params[0]);

                DirectionJsonParser parser = new DirectionJsonParser();

                route = parser.parse(jsonObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return route;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            mDialog.dismiss();
            ArrayList points;
            PolylineOptions polyLineOptions = null;
            for (int i=0; i<lists.size(); i++){

                points = new ArrayList();
                polyLineOptions = new PolylineOptions();

                List<HashMap<String,String>> path = lists.get(i);

                for (int j=0; j<path.size(); j++){

                    HashMap<String,String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));

                    LatLng position = new LatLng(lat,lng);

                    points.add(position);

                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(12);
                polyLineOptions.color(R.color.newCatagory);
                polyLineOptions.geodesic(true);

            }

            if (polyLineOptions!= null) mMap.addPolyline(polyLineOptions);
        }
    }
}