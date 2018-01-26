package com.scala.gui.device;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.scala.R;
import com.scala.bluetooth.BluetoothConnection;

import org.w3c.dom.Text;

import java.util.Date;

public class ConnectionActivity extends AppCompatActivity {

    private BluetoothConnection bluetooth;
    private Button scalaOn;
    private Button scalaOff;
    private Button scalaSu;
    private Button scalaGiu;
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
    Button getScalaSu() { return scalaSu; }
    Button getScalaGiu() { return scalaGiu; }
    String getNome() { return nome;}
    String getMac() { return mac;}

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        Button setData = (Button) findViewById(R.id.setData);
        Button configura = (Button) findViewById(R.id.configura);
        Button seriale = (Button) findViewById(R.id.seriale);
        scalaOn = (Button) findViewById(R.id.scalaOn);
        scalaOff = (Button) findViewById(R.id.scalaOff);
        scalaSu = (Button) findViewById(R.id.salita);
        scalaGiu = (Button) findViewById(R.id.discesa);
        tempo = (EditText) findViewById(R.id.tempo);
        output = (TextView) findViewById(R.id.output);
        nome = getIntent().getExtras().getString("nome");
        mac = getIntent().getExtras().getString("mac");
        AscoltatoreConnectionActivity ascoltatore = new AscoltatoreConnectionActivity(this);
        bluetooth = new BluetoothConnection();
        setData.setOnClickListener(ascoltatore);
        configura.setOnClickListener(ascoltatore);
        scalaOn.setOnClickListener(ascoltatore);
        scalaOff.setOnClickListener(ascoltatore);
        scalaSu.setOnClickListener(ascoltatore);
        scalaGiu.setOnClickListener(ascoltatore);
        seriale.setOnClickListener(ascoltatore);
        if (bluetooth.connetti(mac)){
            String s = R.string.connected + nome;
            output.setText(s);
        }
        else
            output.setText(R.string.error);
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
            output.setText(R.string.disconnected);
        else
            output.setText(R.string.error);
    }
}
