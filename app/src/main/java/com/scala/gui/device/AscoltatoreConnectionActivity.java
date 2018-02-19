package com.scala.gui.device;

import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;

import com.scala.R;

import java.util.Objects;

class AscoltatoreConnectionActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ConnectionActivity app;
    private String direzione;
    private String luce;
    private String colore;

    void setLuce(String luce) { this.luce = luce; }
    void setDirezione(String direzione) { this.direzione = direzione; }
    void setColore(String colore) {
        this.colore = colore;
        checkRadio();
    }

    AscoltatoreConnectionActivity(final ConnectionActivity app) {
        direzione = "GIU";
        luce = "65";
        colore = "";
        this.app = app;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_setTEMPO:
                String conf_tempi = "setT" + direzione + "ON:" + app.getTempo_ON().getText().toString()+ "OFF:" + app.getTempo_OFF().getText().toString();
                if (!app.getBluetooth().invia(conf_tempi))
                    app.getOutput().setText(R.string.error);
                break;

            case R.id.bt_setCOLLUM:
                String conf_collum = "setC" + colore + "LUM:" + luce;
                if (!app.getBluetooth().invia(conf_collum))
                    app.getOutput().setText(R.string.error);
                break;

            case R.id.bt_setFOTO:
                String conf_foto = "setF";
                if (app.getFotoresistenza().isChecked())
                    conf_foto = conf_foto + "ON";
                else
                    conf_foto = conf_foto + "OFF";
                if (!app.getBluetooth().invia(conf_foto))
                    app.getOutput().setText(R.string.error);
                break;

            case R.id.bt_combo1:
                if (!app.getBluetooth().invia("com1"))
                    app.getOutput().setText(R.string.error);
                break;

            case R.id.bt_combo2:
                if (!app.getBluetooth().invia("com2"))
                    app.getOutput().setText(R.string.error);
                break;

            case R.id.salita:
                direzione = "SU";
                app.getScalaGiu().setAlpha(0.5f);
                app.getScalaSu().setAlpha(1f);
                /*app.getBluetooth().invia("getTSU");
                String tempi = app.getBluetooth().ricevi();
                tempi = tempi.replace("tONs","");
                app.getTempo_ON().setText(tempi.split("tOFFs")[0]);
                app.getTempo_OFF().setText(tempi.split("tOFFs")[1]);*/
                break;

            case R.id.discesa:
                direzione = "GIU";
                app.getScalaSu().setAlpha(0.5f);//  BUTTON 0 INVISIBLE 1 VISIBLE  BACKGROUND  0 INVISIBLE 255 VISIBLE
                app.getScalaGiu().setAlpha(1f);
                /*app.getBluetooth().invia("getTGIU");
                tempi = app.getBluetooth().ricevi();
                tempi = tempi.replace("tONd","");
                app.getTempo_ON().setText(tempi.split("tOFFd")[0]);
                app.getTempo_OFF().setText(tempi.split("tOFFd")[1]);*/
                break;

            case R.id.radio_WHITE :
                colore = "WHT";
                checkRadio();
                break;

            case R.id.radio_GREEN :
                colore = "GRN";
                checkRadio();
                break;

            case R.id.radio_BLUE :
                colore = "BLU";
                checkRadio();
                break;

            case R.id.radio_RED :
                colore = "RED";
                checkRadio();
                break;

            case R.id.seriale:
                Intent openSeriale = new Intent(app,SerialActivity.class);
                openSeriale.putExtra("nome",  app.getNome());
                openSeriale.putExtra("mac", app.getMac());
                app.startActivity(openSeriale);
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int value, boolean b) {
        luce = Integer.toString(value*20 + 65 );
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void checkRadio()
    {
        for(int i=0;i<app.getRadio().size();i++)
        {
            if (Objects.equals(app.getRadio().get(i).getContentDescription().toString(), colore))
                app.getRadio().get(i).setChecked(true);
            else
                app.getRadio().get(i).setChecked(false);
        }
    }
}
