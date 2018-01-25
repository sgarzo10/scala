package com.giacca.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class BluetoothConnection {
    private BluetoothSocket mmSocket;
    private OutputStream outStream;
    private InputStream input;
    private BluetoothAdapter bluetoothAdapter;
    private MyHandler h;

    public BluetoothConnection (){
        h = new MyHandler();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mmSocket = null;
        outStream = null;
        input = null;
    }

    public boolean connetti(String mac) {
        BluetoothDevice mmDevice = bluetoothAdapter.getRemoteDevice(mac);
        try {
            mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
        } catch (Exception e) {
            return false;
        }
        try {
            mmSocket.connect();
            outStream = mmSocket.getOutputStream();
            input = mmSocket.getInputStream();
            return true;
        } catch (Exception e) {
            try {
                mmSocket.close();
                return false;
            } catch (Exception e1) {
                return false;
            }
        }
    }

    public boolean disconnetti() {
        try {
            mmSocket.close();
            outStream = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean invia(String message) {
        byte[] msgBuffer = message.getBytes();
        try {
            outStream.write(msgBuffer);
            Log.i("SEND","Messaggio inviato "+message);
            return true;
        } catch (IOException e) {
            Log.w("SEND","Messaggio non inviato");
            return false;
        }
    }

    public String ricevi(){
        byte[] buffer = new byte[256];
        int bytes;
        boolean letto = false;
        while (!letto) {
            try {
                bytes = input.read(buffer);
                h.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                letto = true;
            } catch (IOException ignored) {
            }
        }
        return h.getMexRicevuto();
    }
}
