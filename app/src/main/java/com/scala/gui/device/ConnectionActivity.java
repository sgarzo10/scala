package com.scala.gui.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.scala.R;
import com.scala.bluetooth.BluetoothConnection;
import java.util.Date;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothConnection bluetooth;
    private EditText e;
    private TextView t;

    BluetoothConnection getBluetooth() { return bluetooth;}
    EditText getE() {
        return e;
    }
    TextView getT() {
        return t;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        Button invia = (Button) findViewById(R.id.invia);
        Button setData = (Button) findViewById(R.id.setData);
        e = (EditText) findViewById(R.id.inserisci);
        t = (TextView) findViewById(R.id.txtViewLog);
        String nome = getIntent().getExtras().getString("nome");
        String mac = getIntent().getExtras().getString("mac");
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        bluetooth = new BluetoothConnection();
        invia.setOnClickListener(ascoltatore);
        setData.setOnClickListener(ascoltatore);
        if (bluetooth.connetti(mac)){
            String s = R.string.connected + nome;
            t.setText(s);
            invia.setVisibility(View.VISIBLE);
            e.setVisibility(View.VISIBLE);
            if (!bluetooth.invia("data" + new Date().toString()))
                t.setText(R.string.send_error);
        }
        else
            t.setText(R.string.error);
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
        if (bluetooth.disconnetti())
            t.setText(R.string.disconnected);
        else
            t.setText(R.string.error);
    }
}
