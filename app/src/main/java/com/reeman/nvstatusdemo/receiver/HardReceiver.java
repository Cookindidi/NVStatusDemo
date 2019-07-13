package com.reeman.nvstatusdemo.receiver;

import android.app.Application;
import android.util.Log;

import com.reeman.nvstatusdemo.event.MyEvent;
import com.rsc.impl.OnROSListener;
import com.rsc.impl.RscServiceConnectionImpl;
import com.rsc.reemanclient.ConnectServer;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by GJ on 2019/5/27
 */
public class HardReceiver {

    private static final String TAG = "HardReceiver";
    private ConnectServer cs;
    private static HardReceiver instance;

    public HardReceiver(Application context) {
        cs = ConnectServer.getInstance(context, impl);
        registerRos();
        instance = this;
    }

    /**
     * 创建SDK实例
     *
     * @param application
     */
    public static HardReceiver CreateInstance(Application application) {
        return new HardReceiver(application);
    }

    public void registerRos() {
        if (cs == null)
            return;
        cs.registerROSListener(mRosListener);
    }

    /**
     * ROS接口
     */
    private OnROSListener mRosListener = new OnROSListener() {
        @Override
        public void onResult(String result) {
            if (result != null)
                EventBus.getDefault().post(new MyEvent.RosEvent(result));
        }
    };


    private RscServiceConnectionImpl impl = new RscServiceConnectionImpl() {

        @Override
        public void onServiceConnected(int name) {
            super.onServiceConnected(name);
            Log.i(TAG, "onServiceConnected: ");
        }

        @Override
        public void onServiceDisconnected(int name) {
            super.onServiceDisconnected(name);
            Log.i(TAG, "onServiceDisconnected: ");
        }
    };

}
