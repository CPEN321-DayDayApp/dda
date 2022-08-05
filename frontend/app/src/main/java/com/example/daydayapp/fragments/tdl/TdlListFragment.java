package com.example.daydayapp.fragments.tdl;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.R;
import com.example.daydayapp.adapter.ToDoAdapter;
import com.example.daydayapp.model.ToDoModel;
import com.example.daydayapp.recycleritemtouchhepler.TdlRecyclerItemTouchHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TdlListFragment extends Fragment implements LocationListener {
    private static final String TAG = "TdlList";
    private final MainActivity main;
    private final TdlFragment tdlFragment;
    private final ToDoAdapter tasksAdapter;
    private Location currLocation;

    private AlertDialog dialog;
    private EditText newTaskPopup_title;
    private Button newTaskPopup_select_date_button;
    private Button newTaskPopup_select_duration_button;
    private ToggleButton start_study_button;
    private DatePickerDialog datePickerDialog;
    private int score = 0;

    public TdlListFragment(ToDoAdapter tasksAdapter, MainActivity main, TdlFragment tdlFragment) {
        // Required empty public constructor
        this.tasksAdapter = tasksAdapter;
        this.main = main;
        this.tdlFragment = tdlFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationManager locationManager = (LocationManager) main.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, TdlListFragment.this);
        } else {
            currLocation = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tdl_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView listRecyclerView = requireView().findViewById(R.id.listRecyclerView);
        listRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        listRecyclerView.setAdapter(tasksAdapter);
        ImageButton addTaskButton = view.findViewById(R.id.add_task_button);
        start_study_button = view.findViewById(R.id.start_study_button);
        Button delete_location_button = view.findViewById(R.id.delete_location_button);

        addTaskButton.setOnClickListener(v -> {
            if (tasksAdapter.getCurrentStudyLocation() == null)
                Toast.makeText(getActivity(), "Please select a study location first.", Toast.LENGTH_SHORT).show();
            else
                createNewTaskDialog("", "JAN 01 2022", "15", null);
        });

        start_study_button.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Check if there are tasks in the to-do list
                if (tasksAdapter.getItemCount() == 0) {
                    start_study_button.setChecked(false);
                    Toast.makeText(main, "You don't have tasks to work on!", Toast.LENGTH_LONG).show();
                    return;
                }
                // Check if user location is within 100 meters from the current study location
                if (currLocation != null) {
                    LatLng currStudyLatLng = tasksAdapter.getCurrentStudyLocation();
                    Location currStudyLocation = new Location("currStudyLocation");
                    currStudyLocation.setLatitude(currStudyLatLng.latitude);
                    currStudyLocation.setLongitude(currStudyLatLng.longitude);

                    if (currLocation.distanceTo(currStudyLocation) > 100) {
                        start_study_button.setChecked(false);
                        Toast.makeText(main, "You are not near you study location.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                Toast.makeText(getActivity(), "Start study", Toast.LENGTH_SHORT).show();

                RequestQueue queue = Volley.newRequestQueue(main);
                final String urlChangeStatus = "http://13.89.36.134:8000/user/status";
                HashMap<String, Boolean> content = new HashMap<>();
                content.put("status", true);
                JSONObject jsonContent = new JSONObject(content);
                final String mRequestBody = jsonContent.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, urlChangeStatus,
                        response -> Log.i(TAG, response), error -> Log.e(TAG, error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        return headers;
                    }
                };

                queue.add(stringRequest);

            } else {
                Toast.makeText(getActivity(), "End study", Toast.LENGTH_SHORT).show();

                // update user status
                RequestQueue queue = Volley.newRequestQueue(main);
                final String urlChangeStatus = "http://13.89.36.134:8000/user/status";
                HashMap<String, Boolean> content = new HashMap<>();
                content.put("status", false);
                JSONObject jsonContent = new JSONObject(content);
                final String mRequestBody = jsonContent.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, urlChangeStatus,
                        response -> Log.i(TAG, response), error -> Log.e(TAG, error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return mRequestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        return headers;
                    }
                };
                queue.add(stringRequest);

                ArrayList<ToDoModel> tdl = tasksAdapter.getTdl(tasksAdapter.getCurrentStudyLocation());
                // calculate score
                ArrayList<ToDoModel> taskToDelete = new ArrayList<>();
                for (ToDoModel task : tdl) {
                    if (task.getStatus() == 1) {
                        score += Integer.parseInt(task.getDuration()) / 15;
                        taskToDelete.add(task);
                    }
                }

                // Get user score
                final String urlGetScore = "http://13.89.36.134:8000/user";
                HashMap<String, Integer> contentGetScore = new HashMap<>();
                JSONObject jsonContentGetScore = new JSONObject(contentGetScore);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, urlGetScore, jsonContentGetScore,
                        response -> {
                            try {
                                score += (int) response.get("score");

                                // update user score
                                final String urlChangeScore = "http://13.89.36.134:8000/user/score";
                                HashMap<String, Integer> contentScore = new HashMap<>();
                                contentScore.put("score", score);
                                main.getMyScoreTab().setScore(score);
                                score = 0;      // reset score value for next calculation
                                JSONObject jsonContentScore = new JSONObject(contentScore);
                                final String myRequestBody = jsonContentScore.toString();
                                StringRequest myStringRequest = new StringRequest(Request.Method.PUT,
                                        urlChangeScore, res -> Log.i(TAG, res), error -> Log.e(TAG, error.toString())) {
                                    @Override
                                    public String getBodyContentType() {
                                        return "application/json; charset=utf-8";
                                    }

                                    @Override
                                    public byte[] getBody() {
                                        return myRequestBody.getBytes(StandardCharsets.UTF_8);
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() {
                                        HashMap<String, String> headers = new HashMap<>();
                                        headers.put("Authorization", main.getAccount().getIdToken());
                                        return headers;
                                    }
                                };
                                queue.add(myStringRequest);
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

                // delete completed task
                for (ToDoModel task : taskToDelete) {
                    int taskId = task.getId();
                    tasksAdapter.deleteItem(tdl.indexOf(task));
                    final String urlDeleteTask = "http://13.89.36.134:8000/tdl/" + taskId;
                    StringRequest deleteStringRequest = new StringRequest(Request.Method.DELETE, urlDeleteTask,
                            response -> Log.i(TAG, response), error -> Log.e(TAG, error.toString())) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<>();
                            headers.put("Authorization", main.getAccount().getIdToken());
                            return headers;
                        }
                    };
                    queue.add(deleteStringRequest);
                }
            }
        });

        delete_location_button.setOnClickListener(v -> {
            if (tasksAdapter.getCurrentStudyLocation() == null) {
                Toast.makeText(getActivity(), "You do not have a study location", Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(TdlListFragment.this.getContext());
            builder.setTitle("Delete this study location");
            builder.setMessage("Are you sure you want to delete this study location?");
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                RequestQueue queue = Volley.newRequestQueue(main);
                final String urlDeleteLocation = "http://13.89.36.134:8000/location";
                HashMap<String, Double> deleteContent = new HashMap<>();
                deleteContent.put("lat", tasksAdapter.getCurrentStudyLocation().latitude);
                deleteContent.put("lng", tasksAdapter.getCurrentStudyLocation().longitude);
                JSONObject jsonContent = new JSONObject(deleteContent);
                final String deleteLocationRequestBody = jsonContent.toString();
                StringRequest deleteLocationRequest = new StringRequest(Request.Method.DELETE, urlDeleteLocation,
                        response -> {
                            tdlFragment.refreshMarker();
                            tasksAdapter.deleteLocation(tasksAdapter.getCurrentStudyLocation());
                        }, error -> Log.e(TAG, error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return deleteLocationRequestBody.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        headers.put("lat", String.valueOf(tasksAdapter.getCurrentStudyLocation().latitude));
                        headers.put("lng", String.valueOf(tasksAdapter.getCurrentStudyLocation().longitude));
                        return headers;
                    }
                };

                queue.add(deleteLocationRequest);

            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TdlRecyclerItemTouchHelper(tasksAdapter, main));
        itemTouchHelper.attachToRecyclerView(listRecyclerView);

        // Get all tasks
        RequestQueue queue = Volley.newRequestQueue(requireActivity());

        final String url = "http://13.89.36.134:8000/tdl";
        HashMap<String, String> content = new HashMap<>();
        JSONObject jsonContent = new JSONObject(content);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, jsonContent,
                response -> {
                    Log.d(TAG, "Successful");
                    try {
                        Thread.sleep(500);
                        JSONArray tdlInfo = (JSONArray) response.get("tasklist");
                        tasksAdapter.setLocationTdl(tdlInfo);
                        tasksAdapter.setTasks();
                    } catch (JSONException | InterruptedException e) {
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

    public void createNewTaskDialog(String title, String date, String duration, ToDoModel task) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        final View newTaskPopupView = getLayoutInflater().inflate(R.layout.new_task_popup, null);
        newTaskPopup_title = newTaskPopupView.findViewById(R.id.newTaskPopup_title);
        Button newTaskPopup_saveButton = newTaskPopupView.findViewById(R.id.newTaskPopup_saveButton);
        Button newTaskPopup_cancelButton = newTaskPopupView.findViewById(R.id.newTaskPopup_cancelButton);
        newTaskPopup_select_date_button = newTaskPopupView.findViewById(R.id.newTaskPopup_select_date_button);
        newTaskPopup_select_duration_button = newTaskPopupView.findViewById(R.id.newTaskPopup_select_duration_button);

        initDatePicker(newTaskPopup_select_date_button, title, date, duration);

        dialogBuilder.setView(newTaskPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        newTaskPopup_select_date_button.setOnClickListener(v -> datePickerDialog.show());

        newTaskPopup_select_duration_button.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            final View view = TdlListFragment.this.getLayoutInflater().inflate(R.layout.number_picker_dialog, null);
            builder.setView(view);
            builder.setTitle("Choose your estimated time duration:");
            final NumberPicker picker = view.findViewById(R.id.picker);

            NumberPicker.Formatter formatter = value -> String.valueOf(value * 15);

            picker.setMinValue(1);
            picker.setMaxValue(32);
            picker.setValue(1);
            picker.setFormatter(formatter);

            builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {
                //positive button action
                newTaskPopup_select_duration_button.setText(String.valueOf(picker.getValue() * 15));
            }).setNegativeButton(android.R.string.cancel, (dialog, id) -> {
                //negative button action
            });

            builder.create().show();
        });

        newTaskPopup_saveButton.setOnClickListener(v -> {
            // Check if all fields are filled
            if (newTaskPopup_title.getText().toString().trim().isEmpty()
                    || newTaskPopup_select_date_button.getText().toString().isEmpty()
                    || newTaskPopup_select_duration_button.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
            } else if (task == null) {
                RequestQueue queue = Volley.newRequestQueue(requireActivity());
                final String url = "http://13.89.36.134:8000/tdl";
                int id = tasksAdapter.getNext_id();
                String taskToAdd = generateTaskJson(id, tasksAdapter.getCurrentStudyLocation().latitude, tasksAdapter.getCurrentStudyLocation().longitude, newTaskPopup_title.getText().toString(), Integer.parseInt(newTaskPopup_select_duration_button.getText().toString()), newTaskPopup_select_date_button.getText().toString());
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
                    Log.i(TAG, response);
                    tasksAdapter.addTask(new ToDoModel(0, newTaskPopup_title.getText().toString(),
                            newTaskPopup_select_date_button.getText().toString(),
                            newTaskPopup_select_duration_button.getText().toString(), id));
                    tasksAdapter.setTasks();
                    dialog.dismiss();
                }, error -> Log.e(TAG, error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return taskToAdd.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        return headers;
                    }
                };
                queue.add(stringRequest);
                dialog.dismiss();
            } else {
                RequestQueue queue = Volley.newRequestQueue(requireActivity());
                final String url = "http://13.89.36.134:8000/tdl/" + task.getId();
                String taskToEdit = generateTaskJson(task.getId(), tasksAdapter.getCurrentStudyLocation().latitude, tasksAdapter.getCurrentStudyLocation().longitude, newTaskPopup_title.getText().toString(), Integer.parseInt(newTaskPopup_select_duration_button.getText().toString()), newTaskPopup_select_date_button.getText().toString());
                StringRequest stringRequest = new StringRequest(Request.Method.PUT, url, response -> {
                    Log.i(TAG, response);
                    task.setTask(newTaskPopup_title.getText().toString());
                    task.setStatus(0);
                    task.setDuration(newTaskPopup_select_duration_button.getText().toString());
                    task.setDate(newTaskPopup_select_date_button.getText().toString());
                    tasksAdapter.setTasks();
                    dialog.dismiss();
                }, error -> Log.e(TAG, error.toString())) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public byte[] getBody() {
                        return taskToEdit.getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", main.getAccount().getIdToken());
                        return headers;
                    }
                };
                queue.add(stringRequest);
                dialog.dismiss();
            }
        });

        newTaskPopup_cancelButton.setOnClickListener(v -> {
            tasksAdapter.setTasks();
            dialog.dismiss();
        });
    }

    private void initDatePicker(Button newTaskPopup_select_date_button, String title, String date, String duration)
    {
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, month, day) -> {
            String date1 = makeDateString(day, month, year);
            newTaskPopup_select_date_button.setText(date1);
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(getActivity(), style, dateSetListener, year, month, day);

        if (!Objects.equals(title, "")){
            newTaskPopup_select_date_button.setText(date);
            newTaskPopup_title.setText(title);
            newTaskPopup_select_duration_button.setText(duration);
        } else {
            newTaskPopup_select_date_button.setText(makeDateString(day, month, year));
        }
    }

    private String makeDateString(int day, int month, int year)
    {
        if (month < 0 || month > 11)
            return "JAN";

        final String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};

        return months[month] + " " + day + " " + year;
    }

    private String generateTaskJson(int taskId, double lat, double lng, String taskName, int time, String date) {

        return "{\"taskId\": " + taskId +
                ", \"lat\":" + lat +
                ", \"lng\":" + lng +
                ", \"task\":" + '"' + taskName + '"' +
                ", \"time\":" + time +
                ", \"date\":" + '"' + date + '"' + '}';
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currLocation = location;
    }

    public boolean getStatus() {
        if (start_study_button == null)
            return false;
        else
            return start_study_button.isChecked();
    }
}