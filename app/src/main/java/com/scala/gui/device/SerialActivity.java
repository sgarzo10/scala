package com.scala.gui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scala.R;
import com.scala.bluetooth.BluetoothConnection;

import java.util.Date;

public class SerialActivity  extends AppCompatActivity {

    private EditText testo;
    private LinearLayout output;
    private BluetoothConnection bluetooth;

    BluetoothConnection getBluetooth() { return bluetooth;}
    EditText getTesto() {
        return testo;
    }
    LinearLayout getOutput() { return output; }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("SERIAL_ACTIVITY","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial);
        Button invia = (Button) findViewById(R.id.invia);
        testo = (EditText) findViewById(R.id.mexSeriale);
        output = (LinearLayout) findViewById(R.id.outSeriale);
        bluetooth = new BluetoothConnection();
        AscoltatoreSerialActivity ascoltatore = new AscoltatoreSerialActivity(this);
        invia.setOnClickListener(ascoltatore);
        TextView t = new TextView(this);
        String nome = getIntent().getExtras().getString("nome");
        String mac = getIntent().getExtras().getString("mac");
        boolean connesso = false;
        while (!connesso) {
            if (bluetooth.connetti(mac)) {
                String s = R.string.connected + nome;
                t.setText(s);
                output.addView(t);
                connesso = true;
            } else {
                t.setText(R.string.error);
                output.addView(t);
            }
        }
    }

    @Override
    protected void onResume()
    {
        Log.i("SERIAL_ACTIVITY","onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i("SERIAL_ACTIVITY","onPause");
        while (!bluetooth.disconnetti()) {}
        super.onPause(); }

    @Override
    public void onStop() {
        Log.i("SERIAL_ACTIVITY","onStop");
        super.onStop(); }

    @Override
    protected void onDestroy() {
        Log.i("SERIAL_ACTIVITY","onDestroy");
        super.onDestroy();
    }
}
