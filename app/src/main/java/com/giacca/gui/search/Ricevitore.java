package com.giacca.gui.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import com.giacca.R;

public class Ricevitore extends BroadcastReceiver {

    private MainActivity app;
    private int trovati;

    Ricevitore(MainActivity app) {
        this.app=app;
        trovati=0;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
            app.getT().setText(R.string.do_search);
            trovati=0;
        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            app.getT().setText(R.string.search_finish);
            ImageButton b= (ImageButton) app.findViewById(R.id.cerca);
            b.setImageDrawable(ContextCompat.getDrawable(app, R.drawable.lente));
            app.getAscoltatore().setRicerca();
        } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            app.getAscoltatore().getNomi().add(device.getName());
            app.getAscoltatore().getMac().add(device.getAddress());
            ArrayAdapter adapter = new ArrayAdapter(app,android.R.layout.simple_list_item_1, app.getAscoltatore().getNomi());
            app.getLv().setAdapter(adapter);
            ArrayAdapter adapter1 = new  ArrayAdapter(app,android.R.layout.simple_list_item_1, app.getAscoltatore().getMac());
            app.getLv1().setAdapter(adapter1);
            Button pr= new Button(app);
            pr.setId(R.id.connetti);
            pr.setText(R.string.connect);
            pr.setTextSize(10);
            pr.setTag(trovati);
            pr.setOnClickListener(app.getAscoltatore());
            app.getLinear().addView(pr);
            trovati++;
        }
    }
}
