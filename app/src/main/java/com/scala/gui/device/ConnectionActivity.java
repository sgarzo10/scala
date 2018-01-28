package com.scala.gui.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.scala.R;
import com.scala.bluetooth.BluetoothConnection;

import org.w3c.dom.Text;

import java.util.Date;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothConnection bluetooth;
    private Button scalaOn;
    private Button scalaOff;
    private ImageButton scalaSu;
    private ImageButton scalaGiu;
    private EditText tempo;
    private TextView output;
    private String nome;
    private String mac;

    BluetoothConnection getBluetooth() { return bluetooth;}
    TextView getOutput() {
        return output;
    }
    EditText getTempo() {
        return tempo;
    }
    Button getScalaOn() { return scalaOn; }
    Button getScalaOff() { return scalaOff; }
    ImageButton getScalaSu() { return scalaSu; }
    ImageButton getScalaGiu() { return scalaGiu; }
    String getNome() { return nome;}
    String getMac() { return mac;}

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("CONNECTION_ACTIVITY","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        Button setData = (Button) findViewById(R.id.setData);
        Button configura = (Button) findViewById(R.id.configura);
        Button seriale = (Button) findViewById(R.id.seriale);
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        scalaOn = (Button) findViewById(R.id.scalaOn);
        scalaOff = (Button) findViewById(R.id.scalaOff);
        scalaSu = (ImageButton) findViewById(R.id.salita);
        scalaGiu = (ImageButton) findViewById(R.id.discesa);
        tempo = (EditText) findViewById(R.id.tempo);
        output = (TextView) findViewById(R.id.output);
        nome = getIntent().getExtras().getString("nome");
        mac = getIntent().getExtras().getString("mac");
        setData.setOnClickListener(ascoltatore);
        configura.setOnClickListener(ascoltatore);
        scalaOn.setOnClickListener(ascoltatore);
        scalaOff.setOnClickListener(ascoltatore);
        scalaSu.setOnClickListener(ascoltatore);
        scalaGiu.setOnClickListener(ascoltatore);
        seriale.setOnClickListener(ascoltatore);
        scalaGiu.setAlpha(0.5f);
        scalaGiu.setAlpha(0.5f);
        scalaOn.setAlpha(0.5f);
        scalaOff.setAlpha(0.5f);
    }

    @Override
    protected void onResume()
    {
        Log.i("CONNECTION_ACTIVITY","onResume");
        super.onResume();
        bluetooth = new BluetoothConnection();
        boolean connesso = false;
        while (!connesso) {
            if (bluetooth.connetti(mac)) {
                String s = R.string.connected + nome;
                output.setText(s);
                connesso = true;
            } else
                output.setText(R.string.error);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        Log.i("CONNECTION_ACTIVITY","onPause");
        while (!bluetooth.disconnetti()) {}
    }

    @Override
    public void onStop() {
        Log.i("CONNECTION_ACTIVITY","onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("CONNECTION_ACTIVITY","onDestroy");
        while (!bluetooth.disconnetti()) {}
    }
}
