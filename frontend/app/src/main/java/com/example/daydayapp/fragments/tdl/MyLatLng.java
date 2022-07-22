package com.example.daydayapp.fragments.tdl;

import com.google.android.gms.maps.model.LatLng;

public class MyLatLng {
    private int latitude,
                longitude;
    private final int SCALE = 1000000;

    /**
     * This class stores latitude and longitude as integer
     * The latitude and longitude is 1000000 larger than their normal value
     * Function equals and hashcode are overridden for the use in hashmap
     */
    public MyLatLng(LatLng latLng) {
        this.latitude = Double.valueOf(latLng.latitude * SCALE).intValue();
        this.longitude = Double.valueOf(latLng.longitude * SCALE).intValue();
    }

    public int getLatitude() {
        return this.latitude;
    }

    public void setLatitude(int latitude) {
        this.latitude = latitude;
    }

    public int getLongitude() {
        return this.longitude;
    }

    public void setLongitude(int longitude) {
        this.longitude = longitude;
    }

    public LatLng toLatLng() {
        return new LatLng(((double) this.latitude) / SCALE, ((double) this.longitude) / SCALE);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof MyLatLng)) {
            return false;
        }

        MyLatLng latLng = (MyLatLng) o;

        return latLng.longitude == this.longitude &&
                latLng.latitude == latitude;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + this.latitude;
        result = 31 * result + this.longitude;
        return result;
    }
}
