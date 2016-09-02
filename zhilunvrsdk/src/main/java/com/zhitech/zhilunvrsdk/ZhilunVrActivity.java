package com.zhitech.zhilunvrsdk;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;

import com.zhitech.zhilunvrsdk.Utils.SensorPacketDataObject;
import com.zhitech.zhilunvrsdk.Utils.Utils;

/**
 * Created by taipp on 7/8/2016.
 */
public class ZhilunVrActivity extends AppCompatActivity {

    private static final String TAG = "ZhilunVrActivity";

    private static IZhilunVrAidl iZhilunVrAidl = null;

    private IPacketDataAidl.Stub iPacketDataAidlStub = new IPacketDataAidl.Stub() {
        @Override
        public void OnSensorDataChanged(SensorPacketDataObject object) throws RemoteException {
            OnSensorDataChangedHandler(object);
        }

        @Override
        public void OnTouchPadActonEvent(int[] values) throws RemoteException {
            OnTouchPadActonEventHandler(values);
        }
    };

    private ICommandResultAidl.Stub iCommandResultAidlStub = new ICommandResultAidl.Stub() {
        @Override
        public void OnCommandResultChanged(int cmd, byte[] data, int length) throws RemoteException {
            OnCommandResultChangedHandler(cmd, data, length);
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            OnServiceConnectedHandler(componentName, iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            OnServiceDisconnectedHandler(componentName);
        }
    };

    //public method for aidl
    //set device pid vid
    public static void SetDeviceFilter(int vendorId, int productId) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetDeviceFilter(vendorId, productId);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SetBulkTransferTimeout(int timeout) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetBulkTransferTimeout(timeout);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SetReceiveDataGapTime(int gaptime) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetReceiveDataGapTime(gaptime);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void SetMainActivityClassName(String className) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteSetMainActivityClassName(className);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static String GetDeviceInfo() {
        if (iZhilunVrAidl != null) {
            try {
                return iZhilunVrAidl.RemoteGetDeviceInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    //connect to the device
    public static void ConnectToDevice() {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteConnectToDevice();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }
    //send command to device
    public static int SendCommand(int cmd) {
        int retValue = -1;
        if (iZhilunVrAidl != null) {
            try {
                retValue = iZhilunVrAidl.RemoteSendCommand(cmd);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retValue;
    }

    public static int SendCommandWithData(int cmd, byte[] data) {
        int retValue = -1;
        if (iZhilunVrAidl != null) {
            try {
                retValue = iZhilunVrAidl.RemoteSendCommandWithData(cmd, data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return retValue;
    }
    //daemo thread control
    public static boolean CheckServiceStatus() {
        boolean flag = false;
        try {
            flag = iZhilunVrAidl.RemoteCheckServiceListenerThreadIsRunnig();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return flag;
    }
    public static void RestartService() {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteRestartServiceListenerThread();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    //public method for override
    protected void OnSensorDataChangedHandler(SensorPacketDataObject object) {

    }

    protected void OnTouchPadActonEventHandler(int[] values) {

    }

    protected void OnCommandResultChangedHandler(int cmd, byte[] data, int length) {

    }

    public void OnServiceConnectedHandler(ComponentName componentName, IBinder iBinder) {
        iZhilunVrAidl = IZhilunVrAidl.Stub.asInterface(iBinder);
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteRegisterOnPacketDataListener(iPacketDataAidlStub);
                iZhilunVrAidl.RemoteRegisterOnCommandResultListener(iCommandResultAidlStub);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void OnServiceDisconnectedHandler(ComponentName componentName) {
        if (iZhilunVrAidl != null) {
            try {
                iZhilunVrAidl.RemoteUnregisterOnPacketDataListener(iPacketDataAidlStub);
                iZhilunVrAidl.RemoteUnregisterOnCommandResultListener(iCommandResultAidlStub);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        iZhilunVrAidl = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.dLog(TAG);
        super.onCreate(savedInstanceState);
        //start service
        startService(new Intent(this, ZhilunVrService.class));
        //bind remote service
        Intent binderIntent = new Intent(this, ZhilunVrService.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("create_flag", true);
        binderIntent.putExtras(bundle);
        bindService(binderIntent, connection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        Utils.dLog(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Utils.dLog(TAG);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Utils.dLog(TAG);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Utils.dLog(TAG);
        super.onDestroy();
        unbindService(connection);
        //stopService(new Intent(this, ZhilunVrService.class));
    }
}
