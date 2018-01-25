package com.giacca.bluetooth;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import java.util.Set;

public class Bluetooth {
    private BluetoothAdapter bluetoothAdapter;
    private boolean acceso;

    public boolean getAcceso() {return acceso;}
    public void setAcceso() {this.acceso=true;}

    public Bluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        acceso = bluetoothAdapter.isEnabled();
    }

    public void spegni(){
        bluetoothAdapter.disable();
        acceso = false;
    }

    public void annullaRicerca(){
        bluetoothAdapter.cancelDiscovery();
    }

    public Set<BluetoothDevice> dispositivi_associati(){
        return bluetoothAdapter.getBondedDevices();
    }

    public void cerca(){
        bluetoothAdapter.startDiscovery();
    }

    public BluetoothDevice getDevice(String mac){
        return bluetoothAdapter.getRemoteDevice(mac);
    }
}
