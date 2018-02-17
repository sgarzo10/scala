package com.scala.gui.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import com.scala.R;
import com.scala.bluetooth.BluetoothConnection;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothConnection bluetooth;
    private ImageButton scalaSu;
    private ImageButton scalaGiu;
    private SeekBar luminosita;
    private EditText tempo_OFF;
    private EditText tempo_ON;
    private TextView output;
    private RadioButton radio_WHITE;
    private RadioButton radio_GREEN;
    private RadioButton radio_BLUE;
    private RadioButton radio_RED;
    private String nome;
    private String mac;

    BluetoothConnection getBluetooth() { return bluetooth;}
    TextView getOutput() { return output;}
    EditText getTempo_ON() {
        return tempo_ON;
    }
    EditText getTempo_OFF() {return tempo_OFF; }
    ImageButton getScalaSu() { return scalaSu; }
    ImageButton getScalaGiu() { return scalaGiu; }
    SeekBar getLuminosita() { return luminosita; }
    RadioButton getRadioWHITE() {return radio_WHITE;}
    RadioButton getRadioGREEN() {return radio_GREEN;}
    RadioButton getRadioBLUE() {return radio_BLUE;}
    RadioButton getRadioRED() {return radio_RED;}
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
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        Button setData = (Button) findViewById(R.id.setData);
        Button bt_setTEMPO = (Button) findViewById(R.id.bt_setTEMPO );
        Button bt_setCOLLUM = (Button) findViewById(R.id.bt_setCOLLUM );
        Button seriale = (Button) findViewById(R.id.seriale);
        radio_WHITE = (RadioButton) findViewById(R.id.radio_WHITE);
        radio_GREEN = (RadioButton) findViewById(R.id.radio_GREEN);
        radio_BLUE = (RadioButton) findViewById(R.id.radio_BLUE);
        radio_RED = (RadioButton) findViewById(R.id.radio_RED);
        luminosita = (SeekBar) findViewById(R.id.bar_Luminosita);
        scalaSu = (ImageButton) findViewById(R.id.salita);
        scalaGiu = (ImageButton) findViewById(R.id.discesa);
        tempo_ON = (EditText) findViewById(R.id.edt_tON);
        tempo_OFF = (EditText) findViewById(R.id.edt_tOFF);
        output = (TextView) findViewById(R.id.output);
        nome = getIntent().getExtras().getString("nome");
        mac = getIntent().getExtras().getString("mac");
        setData.setOnClickListener(ascoltatore);
        bt_setTEMPO.setOnClickListener(ascoltatore);
        bt_setCOLLUM.setOnClickListener(ascoltatore);
        radio_WHITE.setOnClickListener(ascoltatore);
        radio_GREEN.setOnClickListener(ascoltatore);
        radio_BLUE.setOnClickListener(ascoltatore);
        radio_RED.setOnClickListener(ascoltatore);
        scalaSu.setOnClickListener(ascoltatore);
        scalaGiu.setOnClickListener(ascoltatore);
        luminosita.setOnClickListener(ascoltatore);
        seriale.setOnClickListener(ascoltatore);
        scalaSu.setAlpha(0.5f);
        scalaGiu.setAlpha(1f);
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


