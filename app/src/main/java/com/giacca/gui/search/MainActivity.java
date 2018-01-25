package com.giacca.gui.search;

import android.content.IntentFilter;
import android.os.Bundle;
import android.bluetooth.*;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.giacca.R;

public class MainActivity extends AppCompatActivity {

    private Ricevitore ricevitore;
    private AscoltatoreMainActivity ascoltatore;
    private Button associati;
    private ImageButton cerca;
    private LinearLayout linear;
    private TextView t;
    private ListView lv;
    private ListView lv1;

    public TextView getT() {return t;}
    public LinearLayout getLinear() {return linear;}
    public AscoltatoreMainActivity getAscoltatore() {return ascoltatore;}
    public Button getAssociati() {return associati;}
    public ImageButton getCerca() {return cerca;}
    public ListView getLv() {return lv;}
    public ListView getLv1() {return lv1;}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv= (ListView)findViewById(R.id.listView);
        lv1= (ListView)findViewById(R.id.listView2);
        linear=(LinearLayout) findViewById(R.id.linear);
        ImageButton accendi= (ImageButton)findViewById(R.id.accendi);
        ImageButton spegni= (ImageButton) findViewById(R.id.spegni);
        t = (TextView) findViewById(R.id.textView);
        associati = (Button) findViewById(R.id.associati);
        cerca = (ImageButton) findViewById(R.id.cerca);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        ricevitore = new Ricevitore(this);
        registerReceiver(ricevitore, filter);
        ascoltatore= new AscoltatoreMainActivity(this);
        associati.setOnClickListener(ascoltatore);
        cerca.setOnClickListener(ascoltatore);
        spegni.setOnClickListener(ascoltatore);
        accendi.setOnClickListener(ascoltatore);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(ricevitore);
    }

    @Override
    protected void onStop() {
        super.onStop();
        TextView t= (TextView) findViewById(R.id.textView);
        t.setText(R.string.finish_connection);
    }
}
