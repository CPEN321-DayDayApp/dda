package com.example.daydayapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.daydayapp.fragments.tdl.MyLatLng;
import com.example.daydayapp.MainActivity;
import com.example.daydayapp.model.ToDoModel;
import com.example.daydayapp.R;
import com.example.daydayapp.fragments.tdl.TdlListFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ToDoAdapter extends RecyclerView.Adapter<ToDoAdapter.ViewHolder> {
    private final HashMap<MyLatLng, ArrayList<ToDoModel>> locationTdl;
    private MyLatLng currentStudyLocation;
    private ArrayList<ToDoModel> todoList;
    private final MainActivity main;
    private TdlListFragment tdlListFragment;
    private int next_id;

    public ToDoAdapter(FragmentActivity fragment){
        this.main = (MainActivity) fragment;
        this.locationTdl = new HashMap<>();
    }

    public void setTdlListFragment(TdlListFragment tdlListFragment) {
        this.tdlListFragment = tdlListFragment;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.tdl_list_layout, parent, false);
        return new ViewHolder(itemView);
    }

    public void onBindViewHolder(ViewHolder holder, int position) {
        ToDoModel item = todoList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.textView.setText(item.getDate());
        holder.task.setOnClickListener(v -> {
            if(holder.task.isChecked()){
                item.setStatus(1);
            } else {
                item.setStatus(0);
            }
        });
    }

    public Context getContext() {
        return main;
    }

    public int getItemCount(){
        if (todoList == null) return 0;
        return todoList.size();
    }

    private boolean toBoolean(int n){
        return n != 0;
    }

    public void setTasks() {
        notifyDataSetChanged();
    }

    public void setTasks(LatLng location) {
        this.todoList = locationTdl.get(new MyLatLng(location));
        notifyDataSetChanged();
    }

    public void setTasks(ArrayList<ToDoModel> todoList) {
        this.todoList = todoList;
        Collections.sort(todoList, (t1, t2) -> {
            Integer i1 = dateStringToInt(t1.getDate());
            Integer i2 = dateStringToInt(t2.getDate());
            return i1.compareTo(i2);
        });
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        todoList.remove(position);
        notifyItemRemoved(position);
    }

    public void editItem(int position) {
        ToDoModel task = todoList.get(position);
        tdlListFragment.createNewTaskDialog(task.getTask(), task.getDate(), task.getDuration(), task);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox task;
        TextView textView;
        ViewHolder(View view) {
            super(view);
            task = view.findViewById(R.id.todoCheckBox);
            textView = view.findViewById(R.id.taskDate);
        }
    }

    public LatLng getCurrentStudyLocation() {
        if (currentStudyLocation == null) return null;
        return currentStudyLocation.toLatLng();
    }

    public void setCurrentStudyLocation(LatLng currentStudyLocation) {
        if (currentStudyLocation == null) {
            this.currentStudyLocation = null;
            this.todoList = null;
        } else {
            this.currentStudyLocation = new MyLatLng(currentStudyLocation);
            this.todoList = locationTdl.get(this.currentStudyLocation);
        }
    }

    public boolean isLocationSet(LatLng location) {
        return locationTdl.containsKey(new MyLatLng(location));
    }

    public void addLocation(LatLng location) {
        MyLatLng myLocation = new MyLatLng(location);
        if (!locationTdl.containsKey(myLocation)) {
            locationTdl.put(myLocation, new ArrayList<>());
        }
    }

    public void addLocation(ArrayList<LatLng> locationList) {
        if (locationList != null && locationList.size() > 0) {
            for (LatLng location : locationList) {
                if (!locationTdl.containsKey(new MyLatLng(location)))
                    locationTdl.put(new MyLatLng(location), new ArrayList<>());
            }
            setCurrentStudyLocation(locationList.get(0));
        }
    }

    public void deleteLocation(LatLng location) {
        locationTdl.remove(new MyLatLng(location));
    }

    public void addTask(LatLng location, ToDoModel task) {
        MyLatLng myLocation = new MyLatLng(location);
        if (locationTdl.containsKey(myLocation)) {
            ArrayList<ToDoModel> tdlList = locationTdl.get(myLocation);
            if (tdlList != null) {
                tdlList.add(task);
            }
        }
    }

    public void addTask(ToDoModel task) {
        if (currentStudyLocation != null) {
            todoList.add(task);
        }
    }

    public ArrayList<ToDoModel> getTdl(LatLng location) {
        return locationTdl.get(new MyLatLng(location));
    }

    public ToDoModel getTask(int position) {
        return todoList.get(position);
    }

    public void setLocationTdl(JSONArray taskList) throws JSONException {
        int max_id = 0;
        JSONObject task;
        LatLng location;
        ToDoModel taskToAdd;
        for (int i = 0; i < taskList.length(); i++) {
            task = (JSONObject) taskList.get(i);
            location = new LatLng((double) task.get("lat"), (double) task.get("lng"));
            taskToAdd = new ToDoModel(0, (String) task.get("task"), (String) task.get("date"), String.valueOf((int) task.get("time")), (int) task.get("_id"));

            if (!isLocationSet(location))
                addLocation(location);

            addTask(location, taskToAdd);

            int curr_id = (int) task.get("_id");
            if (curr_id > max_id)
                max_id = curr_id;
        }
        this.next_id = max_id;
    }

    private int dateStringToInt(String date) {
        String[] arrOfStr = date.split(" ");
        String month;
        final String[] months = {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"};
        ArrayList<String> mon = new ArrayList<>(Arrays.asList(months));
        month = String.valueOf(mon.indexOf(arrOfStr[0]) + 1);
        if (mon.indexOf(arrOfStr[0]) < 9) {
            month = "0" + month;
        }
        String dateInt = arrOfStr[2] + month + arrOfStr[1];
        return Integer.parseInt(dateInt);
    }

    public int getNext_id() {
        next_id ++;
        return next_id;
    }
}
