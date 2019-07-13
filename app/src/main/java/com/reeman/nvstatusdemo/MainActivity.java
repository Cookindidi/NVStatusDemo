package com.reeman.nvstatusdemo;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.reeman.nerves.RobotActionProvider;
import com.reeman.nvstatusdemo.event.MyEvent;
import com.reeman.nvstatusdemo.receiver.HardReceiver;
import com.reeman.nvstatusdemo.utils.FileUtils;
import com.reeman.nvstatusdemo.utils.RosSerialPortProtocolUtils;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import rx.functions.Action1;

public class MainActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Button btn_nv_wifi_connect,btn_setting_nv_hostname,btn_start_navigation,btn_point_nv,btn_nv_charge;
    private Button btn_point_nv_charge,btn_start_move,btn_start_relocal_nv;
    private Button btn_relocal_nv,btn_cancel_nv,btn_connect_point,btn_cancel_charge,btn_reboot_nv,btn_nv_created_wifi;
    private Button btn_search_ip,btn_search_version,btn_shut_down,btn_open_ros_bag,btn_close_ros_bag,btn_nv_host_name,btn_refresh_all_status;

    private TextView tv_device_id,tv_robot_version,tv_nv_ip,tv_nv_hostname,tv_nv_setting_hostname_status,tv_nv_create_wifi_name,tv_nv_point_stauts;
    private TextView tv_reloc_nv_status,tv_location_cfg,tv_nv_status,tv_switch_stop_status,tv_laser_distance,tv_charge_stauts,tv_charge_type,tv_nv_point_charge_stauts,tv_move_stauts,tv_location_cfg_stauts;

    private EditText ed_nv_wifi_ssid,ed_nv_wifi_pwd,ed_setting_nv_hostname,ed_nv,ed_nv_charge,ed_point_nv,ed_point_nv_charge,ed_relocal_nv;
    private EditText ed_move_value;


    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,intent.getAction());
            String action = intent.getAction();
            switch (action) {
                case "REEMAN_BROADCAST_WAKEUP":
                    int angle = intent.getIntExtra("REEMAN_8MIC_WAY", 0);
//                    RobotActionProvider.getInstance().setBeam(0);
                    break;
                case "REEMAN_LAST_MOVTION":
                    int type = intent.getIntExtra("REEMAN_MOVTION_TYPE", 0);
                    break;
                case "REEMAN_BROADCAST_SCRAMSTATE":
                    int stopState = intent.getIntExtra("SCRAM_STATE", -1);
                    if (stopState == 0){
                        tv_switch_stop_status.setText("急停开关被按下");
                    }else{
                        tv_switch_stop_status.setText("急停开关已打开");
                    }
                    break;
                case "android.net.conn.CONNECTIVITY_CHANGE":
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                    break;
                case "ACTION_POWER_CONNECTE_REEMAN":
                    // 插入充电器
                    int powcon = intent.getIntExtra("POWERCHARGE", 0);
                    break;
                case Intent.ACTION_BATTERY_CHANGED:
                    int level = intent.getIntExtra("level", 0);
                    int plugged = intent.getIntExtra("plugged", 0);
                    // 1 AC 2 USB 4 无线 0 电池
                    Log.d("plugged", "充电类型====" + plugged);
                    String pluggedType = "";
                    if (plugged == 1){
                        pluggedType = "AC";
                        tv_charge_stauts.setText("充电器已连接");
                    }else if (plugged == 2){
                        pluggedType = "USB";
                    }else if (plugged == 3){
                        pluggedType = "无线";
                    }else if (plugged == 0){
                        pluggedType = "电池";
                        tv_charge_stauts.setText("充电器未连接");
                    }
                    tv_charge_type.setText(pluggedType + " |电量：" +level);
                    break;
                case "AUTOCHARGE_ERROR_DOCKNOTFOUND":
                    // 未找到充电桩
                    tv_charge_stauts.setText("未找到充电桩");
                    break;
                case "AUTOCHARGE_ERROR_DOCKINGFAILURE":
                    // 连接充电桩失败
                    tv_charge_stauts.setText("连接充电桩失败");
                    break;
                case Intent.ACTION_POWER_CONNECTED:
                    // 连接充电器
                    Log.d("plugged", "充电器已连接");
                    tv_charge_stauts.setText("正在充电");
                    break;
                case Intent.ACTION_POWER_DISCONNECTED:
                    // 断开充电器
                    Log.d("plugged", "充电器未连接");
                    tv_charge_stauts.setText("未充电");
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        initBroadcastReceiver();
        checkPermission();
        HardReceiver.CreateInstance(getApplication());
        EventBus.getDefault().register(this);
        refreshRobotStatus();
    }

    private void initBroadcastReceiver(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("REEMAN_BROADCAST_WAKEUP");// 唤醒广播
        filter.addAction("REEMAN_LAST_MOVTION"); // 运动结束
        filter.addAction("REEMAN_BROADCAST_SCRAMSTATE"); // 急停开关
        filter.addAction("ACTION_POWER_CONNECTE_REEMAN"); // 充电方式
        filter.addAction("AUTOCHARGE_ERROR_DOCKNOTFOUND");// 找不到充电桩
        filter.addAction("AUTOCHARGE_ERROR_DOCKINGFAILURE");// 连接充电桩失败
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");// 网络连接
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);// 电量
        filter.addAction(Intent.ACTION_POWER_CONNECTED); //连接充电器
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);//断开充电器
        filter.addAction(Intent.ACTION_TIME_TICK);//每分钟变化
        registerReceiver(mBroadcastReceiver, filter);
    }

    /**
     * 检查权限
     */
    private void checkPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE).subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean aBoolean) {
                if (aBoolean) {
                    initView();
                }
            }
        });
    }

    private void initView(){
        btn_nv_wifi_connect = findViewById(R.id.btn_nv_wifi_connect);
        btn_setting_nv_hostname = findViewById(R.id.btn_setting_nv_hostname);
        btn_start_navigation = findViewById(R.id.btn_start_navigation);
        btn_nv_charge = findViewById(R.id.btn_nv_charge);
        btn_point_nv = findViewById(R.id.btn_point_nv);
        btn_point_nv_charge = findViewById(R.id.btn_point_nv_charge);
        btn_start_move = findViewById(R.id.btn_start_move);
        btn_start_relocal_nv = findViewById(R.id.btn_start_relocal_nv);
        btn_relocal_nv = findViewById(R.id.btn_relocal_nv);
        btn_cancel_nv = findViewById(R.id.btn_cancel_nv);
        btn_connect_point = findViewById(R.id.btn_connect_point);
        btn_cancel_charge = findViewById(R.id.btn_cancel_charge);
        btn_reboot_nv = findViewById(R.id.btn_reboot_nv);
        btn_nv_created_wifi = findViewById(R.id.btn_nv_created_wifi);
        btn_search_ip = findViewById(R.id.btn_search_ip);
        btn_search_version = findViewById(R.id.btn_search_version);
        btn_shut_down = findViewById(R.id.btn_shut_down);
        btn_open_ros_bag = findViewById(R.id.btn_open_ros_bag);
        btn_close_ros_bag = findViewById(R.id.btn_close_ros_bag);
        btn_nv_host_name = findViewById(R.id.btn_nv_host_name);
        tv_location_cfg_stauts = findViewById(R.id.tv_location_cfg_stauts);
        tv_device_id = findViewById(R.id.tv_device_id);

        tv_reloc_nv_status = findViewById(R.id.tv_reloc_nv_status);

        tv_robot_version = findViewById(R.id.tv_robot_version);
        tv_nv_ip = findViewById(R.id.tv_nv_ip);
        tv_nv_hostname = findViewById(R.id.tv_nv_hostname);
        tv_nv_setting_hostname_status = findViewById(R.id.tv_nv_setting_hostname_status);
        tv_nv_create_wifi_name = findViewById(R.id.tv_nv_create_wifi_name);
        tv_switch_stop_status = findViewById(R.id.tv_switch_stop_status);
        tv_nv_status = findViewById(R.id.tv_nv_status);
        tv_nv_point_stauts = findViewById(R.id.tv_nv_point_stauts);
        tv_laser_distance = findViewById(R.id.tv_laser_distance);
        tv_charge_stauts = findViewById(R.id.tv_charge_status);
        tv_charge_type = findViewById(R.id.tv_charge_type);
        tv_nv_point_charge_stauts = findViewById(R.id.tv_nv_point_charge_stauts);
        tv_move_stauts = findViewById(R.id.tv_move_stauts);
        tv_location_cfg = findViewById(R.id.tv_location_cfg);

        ed_nv_wifi_ssid = findViewById(R.id.ed_nv_wifi_ssid);
        ed_nv_wifi_pwd = findViewById(R.id.ed_nv_wifi_pwd);
        ed_setting_nv_hostname = findViewById(R.id.ed_setting_nv_hostname);
        ed_nv = findViewById(R.id.ed_nv);
        ed_nv_charge = findViewById(R.id.ed_nv_charge);
        ed_point_nv = findViewById(R.id.ed_point_nv);
        ed_point_nv_charge = findViewById(R.id.ed_point_nv_charge);
        ed_relocal_nv = findViewById(R.id.ed_relocal_nv);
        ed_move_value = findViewById(R.id.ed_move_value);
        btn_refresh_all_status = findViewById(R.id.btn_refresh_all_status);

    }

    private void refreshRobotStatus(){

        //急停开关状态
        int stop = RobotActionProvider.getInstance().getScramState();
        if (stop == 0){
            tv_switch_stop_status.setText("急停开关被按下");
        }else{
            tv_switch_stop_status.setText("急停开关已打开");
        }

        //ROS ip
        RosSerialPortProtocolUtils.getInstance().getRosIP();

        //机器版本号
        RosSerialPortProtocolUtils.getInstance().getRosVersion();

        //配置文件
        String[] locations =  FileUtils.getLocationCfg();
        if (locations.length == 0){
            tv_location_cfg_stauts.setText("配置文件未添加");
        }else {
            tv_location_cfg_stauts.setText("配置文件已添加");
            //配置坐标
            String locationStr = "";
            for (int i =0;i<locations.length;i++){
                locationStr = locationStr  + locations[i] + "; ";
                if (locations.length%2 == 0){
                    locationStr = locationStr + "\n";
                }
            }
            tv_location_cfg.setText(locationStr);
        }

        tv_device_id.setText(RobotActionProvider.getInstance().getRobotID());
        //ROS hostname
        RosSerialPortProtocolUtils.getInstance().getRosHostName();


    }

    private void initListener(){
        btn_nv_wifi_connect.setOnClickListener(this);
        btn_setting_nv_hostname.setOnClickListener(this);
        btn_start_navigation.setOnClickListener(this);
        btn_point_nv.setOnClickListener(this);
        btn_nv_charge.setOnClickListener(this);
        btn_point_nv_charge.setOnClickListener(this);
        btn_start_move.setOnClickListener(this);
        btn_start_relocal_nv.setOnClickListener(this);
        btn_relocal_nv.setOnClickListener(this);
        btn_cancel_nv.setOnClickListener(this);
        btn_connect_point.setOnClickListener(this);
        btn_cancel_charge.setOnClickListener(this);
        btn_reboot_nv.setOnClickListener(this);
        btn_search_ip.setOnClickListener(this);
        btn_shut_down.setOnClickListener(this);
        btn_open_ros_bag.setOnClickListener(this);
        btn_search_version.setOnClickListener(this);
        btn_close_ros_bag.setOnClickListener(this);
        btn_nv_created_wifi.setOnClickListener(this);
        btn_nv_host_name.setOnClickListener(this);
        btn_refresh_all_status.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_nv_wifi_connect:
                if (ed_nv_wifi_ssid.getText().toString() == "" || ed_nv_wifi_pwd.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().rosConnectWifi(ed_nv_wifi_ssid.getText().toString(),ed_nv_wifi_pwd.getText().toString());
                break;
            case R.id.btn_setting_nv_hostname:
                if(ed_setting_nv_hostname.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().settingRosHostName(ed_setting_nv_hostname.getText().toString());
                break;
            case R.id.btn_start_navigation:
                if (ed_nv.getText().toString() == null)
                    return;
                RosSerialPortProtocolUtils.getInstance().goNavigateByCoordinate(ed_nv.getText().toString());
                break;
            case R.id.btn_nv_charge:
                if (ed_nv_charge.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().goChargeByCoordinate(ed_nv_charge.getText().toString());
                break;
            case R.id.btn_point_nv:
                if (ed_point_nv.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().goNavigateByPointName(ed_point_nv.getText().toString());
                break;
            case R.id.btn_point_nv_charge:
                if (ed_point_nv_charge.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().goChargeByPointName(ed_point_nv_charge.getText().toString());
                break;
            case R.id.btn_start_move:
                RosSerialPortProtocolUtils.getInstance().moveRobot(ed_move_value.getText().toString());
                break;
            case R.id.btn_start_relocal_nv:
                if (ed_relocal_nv.getText().toString() == "")
                    return;
                RosSerialPortProtocolUtils.getInstance().reLocationByCoordinate(ed_relocal_nv.getText().toString());
                break;
            case R.id.btn_relocal_nv:
                RosSerialPortProtocolUtils.getInstance().reLocation();
                break;
            case R.id.btn_cancel_nv:
                RosSerialPortProtocolUtils.getInstance().cancelNavigation();
                break;
            case R.id.btn_connect_point:
                RosSerialPortProtocolUtils.getInstance().connectChargeStation();
                break;
            case R.id.btn_cancel_charge:
                RosSerialPortProtocolUtils.getInstance().cancelCharge();
                break;
            case R.id.btn_reboot_nv:
                RosSerialPortProtocolUtils.getInstance().rebootRos();
                break;
            case R.id.btn_search_ip:
                RosSerialPortProtocolUtils.getInstance().getRosIP();
                break;
            case R.id.btn_shut_down:
                RosSerialPortProtocolUtils.getInstance().shutDownRobot();
                break;
            case R.id.btn_open_ros_bag:
                RosSerialPortProtocolUtils.getInstance().openRosBag();
                break;
            case R.id.btn_search_version:
                RosSerialPortProtocolUtils.getInstance().getRosVersion();
                break;
            case R.id.btn_close_ros_bag:
                RosSerialPortProtocolUtils.getInstance().closeRosBag();
                break;
            case R.id.btn_nv_created_wifi:
                RosSerialPortProtocolUtils.getInstance().rosCreateWifi();
                break;

            case R.id.btn_nv_host_name:
                RosSerialPortProtocolUtils.getInstance().getRosHostName();
                break;

            case R.id.btn_refresh_all_status:
                refreshRobotStatus();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        try {
            unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ros 系统数据返回
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void rosResult(MyEvent.RosEvent event) {
        String result = event.result;
        Log.d(TAG, "ros result: " + result);
        if (result.startsWith("laser[")) {
            // 激光雷达测距数据
            tv_laser_distance.setText(result);
        } else if (result.startsWith("pt:[")) {

        } else if (result.equals("wifi:rec")) {

        } else if (result.startsWith("ip:")) {
            //ROS 系统的IP地址
            tv_nv_ip.setText(result);
            Log.d(TAG, "========ros ip: " + result);
        } else if (result.startsWith("move_status:")) {
            // 导航状态
            handleNvStatus(result);
        } else if (result.equals("bat:reached")) {
            // 到达充电桩位置，开始对接
            tv_charge_stauts.setText("到达充电桩位置，开始对接");
        } else if (result.equals("sys:uwb:0")) {
        } else if (result.startsWith("loc[")) {

        } else if (result.equals("nav:reloc")) {
            // 开始重定位操作
            handleReloc(result);
        } else if (result.contains("reloc:status:")) {
            // 重定位状态回调
            handleReloc(result);
        } else if (result.startsWith("ver:")) {
            // 版本号
            tv_robot_version.setText(result);
        }else if (result.contains("wifi:connect")){
            tv_nv_create_wifi_name.setText(result);
        }else if (result.contains("sys:boot:")){
            tv_nv_hostname.setText(result);
        }else if (result.contains("hostname:set")){
            tv_nv_setting_hostname_status.setText(result);
        }else if (result.contains("point:")){
            tv_nv_point_stauts.setText(result);
        }else if (result.contains("point_charge:")){
            tv_nv_point_charge_stauts.setText(result);
        }else if (result.contains("move:done:")){
            tv_move_stauts.setText(result);
        }
    }

    /**
     * 重定位状态回调
     *
     * @param result
     */
    public void handleReloc(String result) {
        switch (result) {
            case "reloc:status:0":
                tv_reloc_nv_status.setText("重定位成功");
                break;
            case "reloc:status:1":
                tv_reloc_nv_status.setText("正在进行重定位操作");
                break;
            case "reloc:status:2":
                tv_reloc_nv_status.setText("还未构建地图，无法进行重定位");
                break;
            case "reloc:status:3":
                tv_reloc_nv_status.setText("当前正在构建地图");
                break;
            case "reloc:status:4":
                tv_reloc_nv_status.setText("锚点信息异常");
                break;
            case "reloc:status:5":
                tv_reloc_nv_status.setText("正在进行地图回环检测");
                break;
            case "nav:reloc":
                tv_reloc_nv_status.setText("开始重定位操作");
                break;
        }
    }

    /**
     * result :  move_status:x
     *
     * @param result x = ?  0 : 静止待命   1 : 上次目标失败，等待新的导航命令   2 : 上次目标完成，等待新的导航命令  
     *               3 : 移动中，正在前往目的地   4 : 前方障碍物   5 : 目的地被遮挡 6：用户取消导航 7：收到新的导航
     */
    public void handleNvStatus(String result) {
        Log.d(TAG, "==========nav: " + result);
        String nvStatus = "";
        if ("move_status:1".equals(result)) {
            // 1 : 上次目标失败，等待新的导航命令
            nvStatus = result + " 导航失败，等待新的导航命令";
        } else if ("move_status:2".equals(result)) {
            // 2 : 上次目标完成，等待新的导航命令 
            nvStatus = result + " 导航完成，等待新的导航命令";
        } else if ("move_status:3".equals(result)) {
            // 3 : 导航中
            nvStatus = result + " 导航中";
        } else if ("move_status:4".equals(result)) {
            // 4 : 前方障碍物 
            nvStatus = result + " 前方障碍物";
        } else if ("move_status:5".equals(result)) {
            // 5 : 目的地被遮挡
            nvStatus = result + " 目的地被遮挡";
        } else if ("move_status:6".equals(result)) {
            // 6：用户取消导航
            nvStatus = result + " 用户取消导航";
        } else if ("move_status:7".equals(result)) {
            // 7：收到新的导航
            nvStatus = result + " 收到新的导航";
        }
        tv_nv_status.setText(nvStatus);
    }
}
