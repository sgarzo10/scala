package com.giacca.gui.device;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import com.giacca.R;
import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MyLocationListener implements LocationListener {

    private ConnectionActivity app;

    MyLocationListener(ConnectionActivity app){
        this.app=app;
    }

    @Override
    public void onLocationChanged(Location loc) {
        Log.i("myApp", "location change");
        String longitude = "Longitude: " + loc.getLongitude();
        String latitude = "Latitude: " + loc.getLatitude();
        String cityName = null;
        Geocoder gcd = new Geocoder(app.getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(loc.getLatitude(),
                    loc.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n" + "My Current City is:" + cityName;
        app.getT().setText(s);
        app.getTLa().setText(Double.toString(loc.getLatitude()));
        app.getTLo().setText(Double.toString(loc.getLongitude()));
        if (!app.getBluetooth().invia("pos LA:" + Double.toString(loc.getLatitude()) + "LO:" + Double.toString(loc.getLongitude()) + "CITY:" + cityName))
            app.getT().setText(R.string.error);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

}

