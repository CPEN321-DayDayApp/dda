package com.example.daydayapp.fragments.tdl;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.adapter.ToDoAdapter;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class TdlFragment extends Fragment implements LocationListener {

    private final String TAG = "Tdl";
    private final LatLng VANCOUVER = new LatLng(49.248292, -123.116226);
    private RequestQueue queue;
    private final MainActivity main;
    private GoogleMap googleMap;

    private TdlListFragment tdlListFragment;
    private ToDoAdapter tasksAdapter;
    private ArrayList<LatLng> locationList;
    private AutocompleteSupportFragment autocompleteFragment;

    private SlidingUpPanelLayout tdlPanel;

    public TdlFragment(MainActivity main) {
        this.main = main;
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            TdlFragment.this.googleMap = googleMap;

            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);

                // Change current location button position to bottom right
                View locationButton = ((View) requireView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
                rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                rlp.setMargins(0, 10, 0, 0);

                LocationManager locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, TdlFragment.this);
            }

            getAndMarkAllStudyLocations();

            googleMap.setOnMapClickListener(point -> {
                if (isStudying()) {
                    Toast.makeText(main, "You are studying. Please do not play with the map.", Toast.LENGTH_SHORT).show();
                    return;
                }
                googleMap.clear();
                addMarkers(locationList);
                googleMap.addMarker(new MarkerOptions().position(point)
                        .title(String.valueOf(locationList.size() + 1)));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(point, 15));
            });

            // Show corresponding to-do list when a marker is clicked
            googleMap.setOnMarkerClickListener(marker -> {
                if (isStudying()) {
                    Toast.makeText(main, "You are studying. Please do not change study location.", Toast.LENGTH_SHORT).show();
                    return true;
                }
                marker.showInfoWindow();
                LatLng location = marker.getPosition();
                if (tasksAdapter.isLocationSet(location)) {
                    tasksAdapter.setCurrentStudyLocation(location);
                    tasksAdapter.setTasks();
                    tdlPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(TdlFragment.this.getContext());
                    builder.setTitle("Set as Study Location");
                    builder.setMessage("Are you sure you want to set this place as study location?");
                    builder.setPositiveButton("Confirm", (dialog, which) -> {

                        final String url = "http://13.89.36.134:8000/location";

                        HashMap<String, Double> content = new HashMap<>();
                        content.put("lat", location.latitude);
                        content.put("lng", location.longitude);
                        String stringContent = new JSONObject(content).toString();
                        Log.e(TAG, stringContent);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                            Log.i(TAG, response);
                            locationList.add(location);
                            tasksAdapter.addLocation(location);
                            tasksAdapter.setCurrentStudyLocation(location);
                            tasksAdapter.setTasks();
                            tdlPanel.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                            marker.setTitle(String.valueOf(locationList.size()));
                        }, error ->
                                Log.e(TAG, error.toString()))
                        {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() {
                                return stringContent.getBytes(StandardCharsets.UTF_8);
                            }

                            @Override
                            public Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("Authorization", main.getAccount().getIdToken());
                                return headers;
                            }
                        };

                        queue.add(stringRequest);

                    });
                    builder.setNegativeButton("Cancel", (dialog, which) -> marker.remove());
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return true;
            });

            enableAutocomplete(googleMap);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.tasksAdapter = new ToDoAdapter(requireActivity());
        this.tdlListFragment = new TdlListFragment(this.tasksAdapter, main);
        this.locationList = new ArrayList<>();
        this.queue = Volley.newRequestQueue(requireActivity());
        tasksAdapter.setTdlListFragment(this.tdlListFragment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tdl, container, false);
        requireFragmentManager().beginTransaction().add(R.id.list, tdlListFragment, "TDL").commit();
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
        tdlPanel = requireActivity().findViewById(R.id.tdlPanel);
    }

    private void enableAutocomplete(GoogleMap googleMap) {
        if (!Places.isInitialized()) {
            try {
                String API_KEY = (String) requireActivity().getPackageManager()
                        .getApplicationInfo(requireActivity().getPackageName(), PackageManager.GET_META_DATA)
                        .metaData.get("google_place_api_key");
                Places.initialize(requireActivity().getApplicationContext(), API_KEY, Locale.CANADA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        assert autocompleteFragment != null;
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Optimize autocomplete result and set initial bias to near Vancouver
        autocompleteFragment.setCountries("CA", "US");
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(48.246292, -124.116226),
                new LatLng(50.246292, -122.116226)));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                LatLng latLng = place.getLatLng();
                if (latLng != null) {
                    googleMap.addMarker(new MarkerOptions().position(latLng).title(place.getName()));
                    // Animate camera to that position.
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                    tasksAdapter.setCurrentStudyLocation(null);
                }
            }

            @Override
            public void onError(@NonNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    private void addMarkers(ArrayList<LatLng> locationList) {
        for (int i = 0; i < locationList.size(); i++)
            googleMap.addMarker(new MarkerOptions().position(locationList.get(i)).title(String.valueOf(i + 1)));
    }

    private void getAndMarkAllStudyLocations() {
        // Get all study locations
        final String url = "http://13.89.36.134:8000/location";
        HashMap<String, String> content = new HashMap<>();
        JSONObject jsonContent = new JSONObject(content);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                response -> {
                    Log.d(TAG, "Successful");
                    try {
                        JSONArray locations = (JSONArray) response.get("location");
                        for (int i = 0; i < locations.length(); i++) {
                            JSONObject location = (JSONObject) locations.get(i);
                            locationList.add(new LatLng((double) location.get("lat"), (double) location.get("lng")));
                        }
                        Log.d(TAG, "LocationList: " + locationList.size());
                        tasksAdapter.addLocation(locationList);
                        addMarkers(locationList);
                        if (locationList.size() > 0)
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationList.get(0), 15));
                        else
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(VANCOUVER, 15));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> Log.d(TAG, error.toString())) {
            /**
             * Passing some request headers
             * Set API Key
             */
            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", main.getAccount().getIdToken());
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonRequest);
    }

    private boolean isStudying() {
        return tdlListFragment.getStatus();
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        LatLng current = new LatLng(location.getLatitude(), location.getLongitude());
        // Adjust autocomplete bias based on current location
        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(current.latitude - 1, current.longitude - 1),
                new LatLng(current.latitude + 1, current.longitude + 1)));
    }


}