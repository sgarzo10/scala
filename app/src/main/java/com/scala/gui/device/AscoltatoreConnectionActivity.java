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

    AscoltatoreConnectionActivity(final ConnectionActivity app) {
        direzione = "";
        luce = "";
        this.app = app;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.salita:
                direzione = "DIR:SU";
                app.getScalaGiu().setAlpha(0.5f);
                app.getScalaSu().setAlpha(1f);
                break;
            case R.id.discesa:
                direzione = "DIR:GIU";
                app.getScalaSu().setAlpha(0.5f);//  BUTTON 0 INVISIBLE 1 VISIBLE  BACKGROUND  0 INVISIBLE 255 VISIBLE
                app.getScalaGiu().setAlpha(1f);
                break;
            case R.id.scalaOn:
                luce = "LUCE:ON";
                app.getScalaOff().setAlpha(0.5f);
                app.getScalaOn().setAlpha(1f);
                break;
            case R.id.scalaOff:
                luce = "LUCE:OFF";
                app.getScalaOn().setAlpha(0.5f);
                app.getScalaOff().setAlpha(1f);
                break;
            case R.id.seriale:
                Intent openSeriale = new Intent(app,SerialActivity.class);
                openSeriale.putExtra("nome",  app.getNome());
                openSeriale.putExtra("mac", app.getMac());
                app.startActivity(openSeriale);
                break;
            case R.id.configura:
                String configurazioneScala = direzione + luce + "TEMPO:" + app.getTempo().getText().toString();
                if (!app.getBluetooth().invia(configurazioneScala))
                    app.getOutput().setText(R.string.error);
                else {
                    String mex = "RX: "+app.getBluetooth().ricevi();
                    app.getOutput().setText(mex);
                }
                break;
            case R.id.setData:
                Log.i("case","setData");
                if (!app.getBluetooth().invia("data" + new Date().toString()))
                    app.getOutput().setText(R.string.error);
                break;

        }
    }
}
