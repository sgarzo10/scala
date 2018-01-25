package com.scala.gui.device;

import android.util.Log;
import android.view.View;
import com.scala.R;
import java.util.Date;

public class AscoltatoreConnectionActivity implements View.OnClickListener {

    private ConnectionActivity app;

    AscoltatoreConnectionActivity(final ConnectionActivity app) {
        this.app = app;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                Log.i("case","invia");
                if (!app.getBluetooth().invia(app.getE().getText().toString()))
                    app.getT().setText(R.string.error);
                else {
                    String mex = "RX: "+app.getBluetooth().ricevi();
                    app.getT().setText(mex);
                }
                break;
            case R.id.setData:
                Log.i("case","setData");
                if (!app.getBluetooth().invia("data" + new Date().toString()))
                    app.getT().setText(R.string.error);
                break;

        }
    }
}
