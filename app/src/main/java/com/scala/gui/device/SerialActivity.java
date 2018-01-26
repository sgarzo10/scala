package com.scala.gui.device;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
        super.onCreate(savedInstanceState);
        Button invia = (Button) findViewById(R.id.invia);
        testo = (EditText) findViewById(R.id.mexSeriale);
        output = (LinearLayout) findViewById(R.id.outSeriale);
        bluetooth = new BluetoothConnection();
        AscoltatoreSerialActivity ascoltatore = new AscoltatoreSerialActivity(this);
        invia.setOnClickListener(ascoltatore);
        TextView t = new TextView(this);
        String nome = getIntent().getExtras().getString("nome");
        String mac = getIntent().getExtras().getString("mac");
        if (bluetooth.connetti(mac)){
            String s = R.string.connected + nome;
            t.setText(s);
            output.addView(t);
        }
        else{
            t.setText(R.string.error);
            output.addView(t);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop() { super.onStop(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
