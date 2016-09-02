package com.zhitech.zhilunvrsdksrc;

import android.content.ComponentName;
import android.os.Handler;
import android.os.IBinder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhitech.zhilunvrsdk.Utils.SensorPacketDataObject;
import com.zhitech.zhilunvrsdk.Utils.Utils;
import com.zhitech.zhilunvrsdk.ZhilunVrActivity;

import java.util.Locale;

public class MainActivity extends ZhilunVrActivity implements View.OnClickListener {
    private final static String TAG = "MainActivity";

    private TextView mTextViewResult;
    private Button mBtConnect;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewResult = (TextView) findViewById(R.id.textViewResult);
        mBtConnect = (Button) findViewById(R.id.bt_connect);
        mBtConnect.setOnClickListener(this);
    }

    @Override
    public void OnSensorDataChangedHandler(final SensorPacketDataObject object) {
        Utils.dLog(TAG, String.format(Locale.US, "Header:%c%c%n" +
                        "Gyroscope:%d,%d,%d%n" +
                        "Accelerometer:%d,%d,%d%n" +
                        "Magnetic:%d,%d,%d%n" +
                        "Temperature:%d%n" +
                        "Light:%d%n" +
                        "Proximity:%d%n" +
                        "Timestamp:%d%n",
                object.getPacketHeader()[0], object.getPacketHeader()[1],
                object.getPacketDataGyro()[0], object.getPacketDataGyro()[1], object.getPacketDataGyro()[2],
                object.getPacketDataAccel()[0], object.getPacketDataAccel()[1], object.getPacketDataAccel()[2],
                object.getPacketDataMSensor()[0], object.getPacketDataMSensor()[1], object.getPacketDataMSensor()[2],
                object.getPacketDataTemp(),
                object.getPacketDataLSensor(),
                object.getPacketDataPSensor(),
                object.getPacketDataTimestamp()));

        handler.post(new Runnable() {
            @Override
            public void run() {
                mTextViewResult.setText(String.format(Locale.US, "Header:%c%c%n" +
                                "Gyroscope:%d,%d,%d%n" +
                                "Accelerometer:%d,%d,%d%n" +
                                "Magnetic:%d,%d,%d%n" +
                                "Temperature:%d%n" +
                                "Light:%d%n" +
                                "Proximity:%d%n" +
                                "Timestamp:%d%n",
                        object.getPacketHeader()[0], object.getPacketHeader()[1],
                        object.getPacketDataGyro()[0], object.getPacketDataGyro()[1], object.getPacketDataGyro()[2],
                        object.getPacketDataAccel()[0], object.getPacketDataAccel()[1], object.getPacketDataAccel()[2],
                        object.getPacketDataMSensor()[0], object.getPacketDataMSensor()[1], object.getPacketDataMSensor()[2],
                        object.getPacketDataTemp(),
                        object.getPacketDataLSensor(),
                        object.getPacketDataPSensor(),
                        object.getPacketDataTimestamp()));
            }
        });
    }

    @Override
    public void OnTouchPadActonEventHandler(int[] values) {
        Utils.dLog(TAG, String.format(Locale.US, "values[0]=%d, values[1]=%d", values[0], values[1]));
    }

    @Override
    public void OnCommandResultChangedHandler(int cmd, byte[] data, int length) {
        Utils.dLog(TAG, String.format(Locale.US, "cmd:%#04x, length:%d, data:%#04x,%#04x,%#04x,%#04x,%#04x,%#04x",
                cmd, length, data[0],data[1],data[2],data[3],data[4],data[5]));
        mTextViewResult.setText(String.format(Locale.US, "cmd:%#04x, length:%d, data:%#04x,%#04x,%#04x,%#04x,%#04x,%#04x",
                cmd, length, data[0],data[1],data[2],data[3],data[4],data[5]));
    }

    @Override
    public void OnServiceConnectedHandler(ComponentName componentName, IBinder iBinder) {
        super.OnServiceConnectedHandler(componentName, iBinder);
    }

    @Override
    public void OnServiceDisconnectedHandler(ComponentName componentName) {
        super.OnServiceDisconnectedHandler(componentName);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_connect:
                SetDeviceFilter(0x2d29, 0x1001);
                ConnectToDevice();
                SendCommand(Utils.CMD_RECV_SENSOR_DATA);
                break;
            default:
                break;
        }
    }
}
