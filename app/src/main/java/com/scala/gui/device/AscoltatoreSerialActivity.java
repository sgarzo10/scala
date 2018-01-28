package com.scala.gui.device;

import android.view.View;
import android.widget.TextView;

import com.scala.R;

class AscoltatoreSerialActivity implements View.OnClickListener{

    private SerialActivity app;

    AscoltatoreSerialActivity(final SerialActivity app) { this.app = app; }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.invia:
                TextView t = new TextView(app);
                if (!app.getBluetooth().invia(app.getTesto().getText().toString())) {
                    t.setText(R.string.error);
                    app.getOutput().addView(t);
                }
                else {
                    String mex = "RX: "+app.getBluetooth().ricevi();
                    t.setText(mex);
                    app.getOutput().addView(t);
                }
                break;
        }
    }
}
