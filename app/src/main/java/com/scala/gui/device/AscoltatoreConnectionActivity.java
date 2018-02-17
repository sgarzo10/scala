package com.scala.gui.device;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import com.scala.R;
import java.util.Date;

class AscoltatoreConnectionActivity implements View.OnClickListener {

    private ConnectionActivity app;
    private String direzione;
    private String luce;
    private String colore;

    AscoltatoreConnectionActivity(final ConnectionActivity app) {
        direzione = "GIU";
        luce = "140";
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
                else {
                    String mex = "RX: "+app.getBluetooth().ricevi();
                    app.getOutput().setText(mex);
                }
                break;

            case R.id.bt_setCOLLUM:
                String conf_collum = "setC" + colore + "LUM:" + luce;
                if (!app.getBluetooth().invia(conf_collum))
                    app.getOutput().setText(R.string.error);
                else {
                    String mex = "RX: "+app.getBluetooth().ricevi();
                    app.getOutput().setText(mex);
                }
                break;

            case R.id.salita:
                direzione = "SU";
                app.getScalaGiu().setAlpha(0.5f);
                app.getScalaSu().setAlpha(1f);
                break;
            case R.id.discesa:
                direzione = "GIU";
                app.getScalaSu().setAlpha(0.5f);//  BUTTON 0 INVISIBLE 1 VISIBLE  BACKGROUND  0 INVISIBLE 255 VISIBLE
                app.getScalaGiu().setAlpha(1f);
                break;

            case R.id.radio_WHITE :
                colore = "WHT";
                app.getRadioWHITE().setChecked(true);
                app.getRadioGREEN().setChecked(false);
                app.getRadioBLUE().setChecked(false);
                app.getRadioRED().setChecked(false);
                break;

            case R.id.radio_GREEN :
                colore = "GRN";
                app.getRadioWHITE().setChecked(false);
                app.getRadioGREEN().setChecked(true);
                app.getRadioBLUE().setChecked(false);
                app.getRadioRED().setChecked(false);
                break;

            case R.id.radio_BLUE :
                colore = "BLU";
                app.getRadioWHITE().setChecked(false);
                app.getRadioGREEN().setChecked(false);
                app.getRadioBLUE().setChecked(true);
                app.getRadioRED().setChecked(false);
                break;

            case R.id.radio_RED :
                colore = "RED";
                app.getRadioWHITE().setChecked(false);
                app.getRadioGREEN().setChecked(false);
                app.getRadioBLUE().setChecked(false);
                app.getRadioRED().setChecked(true);
                break;

            case R.id.bar_Luminosita :
                //luce = Integer.toString(app.getLuminosita().getProgress()*20 + 65 );
                break;

            case R.id.seriale:
                Intent openSeriale = new Intent(app,SerialActivity.class);
                openSeriale.putExtra("nome",  app.getNome());
                openSeriale.putExtra("mac", app.getMac());
                app.startActivity(openSeriale);
                break;

            case R.id.setData:
                Log.i("case","setData");
                if (!app.getBluetooth().invia("data" + new Date().toString()))
                    app.getOutput().setText(R.string.error);
                break;

        }
    }
}
