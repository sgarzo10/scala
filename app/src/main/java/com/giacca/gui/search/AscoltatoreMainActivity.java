package com.giacca.gui.search;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.giacca.R;
import com.giacca.bluetooth.Bluetooth;
import com.giacca.gui.device.ConnectionActivity;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class AscoltatoreMainActivity implements View.OnClickListener {

    private MainActivity app;
    private boolean ricerca;
    private ArrayList<String> nomi;
    private ArrayList<String> mac;
    private Bluetooth bluetooth;
    void setRicerca() {this.ricerca = false;}
    ArrayList<String> getNomi() {return nomi;}
    ArrayList<String> getMac() {return mac;}

    AscoltatoreMainActivity(MainActivity app)
    {
        nomi = new ArrayList<>();
        mac = new ArrayList<>();
        bluetooth = new Bluetooth();
        this.app=app;
        ricerca= false;
        if (bluetooth.getAcceso()) {
            app.getCerca().setVisibility(View.VISIBLE);
            app.getAssociati().setVisibility(View.VISIBLE);
        }
    }

    private void inRicerca()
    {
        if (ricerca) {
            try {
                bluetooth.annullaRicerca();
                app.getCerca().setImageDrawable(ContextCompat.getDrawable(app, R.drawable.lente));
                ricerca = false;
                app.getT().setText(R.string.search_finish);
            } catch (Exception e) {
                app.getT().setText(R.string.error);
            }
        }
    }

    private void dispositivi_associati()
    {
        inRicerca();
        try
        {
            Set<BluetoothDevice> pairedDevices;
            pairedDevices = bluetooth.dispositivi_associati();
            if (pairedDevices.size()==0)
                app.getT().setText(R.string.no_dispo);
            else
            {
                app.getLinear().removeAllViews();
                mac.clear();
                nomi.clear();
                for (BluetoothDevice bt : pairedDevices) nomi.add(bt.getName());
                for (BluetoothDevice bt : pairedDevices) mac.add(bt.getAddress());
                app.getT().setText(R.string.list);
                ArrayAdapter adapter = new ArrayAdapter(app, android.R.layout.simple_list_item_1, nomi);
                app.getLv().setAdapter(adapter);
                ArrayAdapter adapter1 = new ArrayAdapter(app, android.R.layout.simple_list_item_1, mac);
                app.getLv1().setAdapter(adapter1);
                for (int i=0; i<nomi.size();i++)
                {
                    Button pr= new Button(app);
                    pr.setId(R.id.elimina);
                    pr.setText(R.string.delete);
                    pr.setTag(i);
                    pr.setTextSize(10);
                    pr.setOnClickListener(this);
                    app.getLinear().addView(pr);
                }
            }
        }
        catch (NullPointerException e)
        {app.getT().setText(R.string.no_bluetooth);}
    }

    private void accendi()
    {
        inRicerca();
        try {
            boolean acceso = bluetooth.getAcceso();
            if (!acceso) {
                Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                app.startActivityForResult(turnOn, 0);
                app.getT().setText(R.string.on);
                app.getCerca().setVisibility(View.VISIBLE);
                app.getAssociati().setVisibility(View.VISIBLE);
                bluetooth.setAcceso();
            } else
                app.getT().setText(R.string.now_on);
        }
        catch (NullPointerException e)
        {app.getT().setText(R.string.no_bluetooth);}
    }

    private void spegni()
    {
        inRicerca();
        try {
            bluetooth.spegni();
            app.getCerca().setVisibility(View.INVISIBLE);
            app.getAssociati().setVisibility(View.INVISIBLE);
            app.getT().setText(R.string.off);
            app.getLinear().removeAllViews();
            mac.clear();
            nomi.clear();
        }
        catch (NullPointerException e)
        {app.getT().setText(R.string.no_bluetooth);}
    }

    private void cerca()
    {
        if (!ricerca)
        {
            mac.clear();
            nomi.clear();
            app.getLinear().removeAllViews();
            try {
                bluetooth.cerca();
                app.getCerca().setImageDrawable(ContextCompat.getDrawable(app, R.drawable.stop));
                ricerca = true;
            }
            catch (Exception e)
            {app.getT().setText(R.string.error);}
        }
        else
            inRicerca();
    }

    private void connetti(int i)
    {
        inRicerca();
        Intent openPage1 = new Intent(app,ConnectionActivity.class);
        openPage1.putExtra("nome", nomi.get(i));
        openPage1.putExtra("mac", mac.get(i));
        mac.clear();
        nomi.clear();
        app.getLinear().removeAllViews();
        app.getT().setText(R.string.connection);
        app.startActivity(openPage1);
    }

    private void elimina(int i)
    {
        BluetoothDevice mmDevice=bluetooth.getDevice(mac.get(i));
        try {
            Method m = mmDevice.getClass().getMethod("removeBond", (Class[]) null);
            m.invoke(mmDevice, (Object[]) null);
            app.getT().setText(R.string.dissociato);
            mac.clear();
            nomi.clear();
            app.getLinear().removeAllViews();
        } catch (Exception e) { app.getT().setText(R.string.error); }
    }

    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.accendi:
                accendi();
                break;
            case R.id.spegni:
                spegni();
                break;
            case R.id.associati:
                dispositivi_associati();
                break;
            case R.id.cerca:
                cerca();
                break;
            case R.id.connetti:
                connetti((int) view.getTag());
                break;
            case R.id.elimina:
                elimina((int) view.getTag());
                break;
        }
    }
}
