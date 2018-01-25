package com.giacca.bluetooth;


public class MyHandler extends android.os.Handler{

    private StringBuilder sb;
    private String mexRicevuto;

    String getMexRicevuto() {return mexRicevuto;}

    MyHandler() {
        sb = new StringBuilder();
        mexRicevuto = "";
    }

    @Override
    public void handleMessage(android.os.Message msg) {
        switch (msg.what) {
            case 1:
                byte[] readBuf = (byte[]) msg.obj;
                String strIncom = new String(readBuf, 0, msg.arg1);
                sb.append(strIncom);
                int endOfLineIndex = sb.indexOf("\r\n");
                if (endOfLineIndex > 0) {
                    mexRicevuto = sb.substring(0, endOfLineIndex);
                    sb.delete(0, sb.length());
                }
                break;
        }
    }
}
