package com.reeman.nvstatusdemo.utils;

import android.util.Log;
import com.reeman.nerves.RobotActionProvider;

/**
 * Created by GJ on 2019/5/28
 */
public class RosSerialPortProtocolUtils {
    private static RosSerialPortProtocolUtils mInstance;

    private RosSerialPortProtocolUtils() { }

    public static RosSerialPortProtocolUtils getInstance(){
        if(mInstance == null){
            synchronized (RosSerialPortProtocolUtils.class){
                if(mInstance == null){
                    mInstance = new RosSerialPortProtocolUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取导航主机ip地址，结果在OnROSListener回调中获得
     */
    public void getRosIP(){
        RobotActionProvider.getInstance().sendRosCom("ip:request");
    }

    /**
     * 获取导航主机版本号，结果在OnROSListener回调中获得
     */
    public void getRosVersion(){
        RobotActionProvider.getInstance().sendRosCom("sys:version");
    }

    /**
     * 导航主机创建wifi，结果在OnROSListener回调中获得
     */
    public void rosCreateWifi(){
        RobotActionProvider.getInstance().sendRosCom("create_wifi");
    }

    /**
     * 重启导航主机
     */
    public void rebootRos(){
        RobotActionProvider.getInstance().sendRosCom("sys:reboot");
    }

    /**
     * 关闭机器
     */
    public void shutDownRobot(){
        RobotActionProvider.getInstance().sendRosCom("sys:shutdown");
    }

    /**
     * 取消充电
     */
    public void cancelCharge(){
        RobotActionProvider.getInstance().sendRosCom("bat:uncharge");
    }

    /**
     * 直接对接充电桩
     */
    public void connectChargeStation(){
        RobotActionProvider.getInstance().sendRosCom("goal:just_charge");
    }

    public void cancelNavigation(){
        RobotActionProvider.getInstance().sendRosCom("cancel_goal");
    }

    /**
     * 导航主机连接wiif,连接结果在OnROSListener回调中获得
     * @param ssidName
     * @param password
     */
    public void rosConnectWifi(String ssidName,String password){
        String requestStr = "wifi[ssid " + ssidName + ";pwd " +password;
        RobotActionProvider.getInstance().sendRosCom(requestStr);
    }

    /**
     * 获取导航主机hostname,结果在OnROSListener回调中获得
     */
    public void getRosHostName(){
        RobotActionProvider.getInstance().sendRosCom("hostname:get");
    }

    /**
     * 打开rosbag 记录导航数据
     */
    public void openRosBag(){
        RobotActionProvider.getInstance().sendRosCom("rosbag[1]");
    }

    /**
     * 结束rosbag 记录导航数据
     */
    public void closeRosBag(){
        RobotActionProvider.getInstance().sendRosCom("rosbag[2]");
    }

    /**
     * 设置导航主机hostName,结果在OnROSListener回调中获得
     * @param hostName
     */
    public void settingRosHostName(String hostName){
        RobotActionProvider.getInstance().sendRosCom("hostname:set[" + hostName + "]");
    }

    /**
     * 根据坐标点导航,导航结果在OnROSListener回调中获得
     * @param x
     * @param y
     * @param angle
     * 取值范围[-180,180]
     */
    public void goNavigateByCoordinate(float x,float y,float angle){
        StringBuffer buffer = new StringBuffer("goal:nav");
        buffer.append("[").append(x).append(",").append(y).append(",").append(angle).append("]");
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 根据坐标字符串导航
     * @param coordinateStr 格式 x,y,yaw
     */
    public void goNavigateByCoordinate(String coordinateStr){
        try {
            String[] point = coordinateStr.split(",");
            float x = Float.valueOf(point[0]);
            float y = Float.valueOf(point[1]);
            float yaw = Float.valueOf(point[2]);
            goNavigateByCoordinate(x,y,yaw);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据坐标点导航并充电,导航结果在OnROSListener回调中获得
     * @param x
     * @param y
     * @param angle
     * 取值范围[-180,180]
     */
    public void goChargeByCoordinate(float x,float y,float angle){
        StringBuffer buffer = new StringBuffer("goal:charge");
        buffer.append("[").append(x).append(",").append(y).append(",").append(angle).append("]");
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 根据坐标字符串导航并充电
     * @param coordinateStr 格式 x,y,yaw
     */
    public void goChargeByCoordinate(String coordinateStr){
        try {
            String[] point = coordinateStr.split(",");
            float x = Float.valueOf(point[0]);
            float y = Float.valueOf(point[1]);
            float yaw = Float.valueOf(point[2]);
            goChargeByCoordinate(x,y,yaw);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 根据坐标点名称导航,导航结果在OnROSListener回调中获得
     * @param pointName  eg:办公室
     */
    public void goNavigateByPointName(String pointName){
        StringBuffer buffer = new StringBuffer();
        buffer.append("point[").append(pointName).append("]");
        Log.i("ggg","发送地点名："+ buffer.toString());
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 根据坐标点名称导航并充电,导航结果在OnROSListener回调中获得
     * @param pointName  eg:充电桩
     */
    public void goChargeByPointName(String pointName){
        StringBuffer buffer = new StringBuffer();
        buffer.append("point_charge[").append(pointName).append("]");
        Log.i("ggg","发送充电地点名："+ buffer.toString());
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 全局重定位
     */
    public void reLocation(){
        RobotActionProvider.getInstance().sendRosCom("nav:reloc");
    }

    /**
     * 在（x,y）坐标附近进行重定位
     */
    public void reLocationByCoordinate(float x,float y){
        StringBuffer buffer = new StringBuffer();
        buffer.append("nav:reloc[").append(x).append(",").append(y).append("]");
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 在（x,y,angle）坐标附近进行重定位
     */
    public void reLocationByCoordinate(float x,float y,float angle){
        StringBuffer buffer = new StringBuffer();
        buffer.append("nav:reloc[").append(x).append(",").append(y).append(",").append(angle).append("]");
        Log.i("ggg","发送重定位坐标：" + buffer.toString());
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 根据坐标进行重定位
     * @param coordinateStr 格式 x,y 或 x,y,yaw
     */
    public void reLocationByCoordinate(String coordinateStr){
        try {
            String[] point = coordinateStr.split(",");
            float x = Float.valueOf(point[0]);
            float y = Float.valueOf(point[1]);
            if (point.length == 3){
                float yaw = Float.valueOf(point[2]);
                reLocationByCoordinate(x,y,yaw);
                return;
            }
            reLocationByCoordinate(x,y);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 给定距离，角度 移动机器人，
     * 移动完成结果在OnROSListener回调中获得
     * move:done:16 直走完成
     * move:done:17 左转完成
     * move:done:18 右转完成
     *
     * @param distance
     * @param angle
     * distance angle 必须有一个值为0，也就是机器只能直走或者转弯
     * [0,0]表示停止移动
     */
    public void moveRobot(int distance,int angle){
        StringBuffer buffer = new StringBuffer();
        buffer.append("move[").append(distance).append(",").append(angle).append("]");
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     * 给定距离，角度，速度移动机器人，
     * 移动完成结果在OnROSListener回调中获得
     * move:done:16 直走完成
     * move:done:17 左转完成
     * move:done:18 右转完成
     *
     * @param distance
     * @param angle
     * @param speech 默认0.3米每秒,小于或等于0时为默认速度
     * distance angle 必须有一个值为0，也就是机器只能直走或者转弯
     * [0,0,0]表示停止移动
     */
    public void moveRobot(int distance,int angle,double speech){
        StringBuffer buffer = new StringBuffer();
        buffer.append("move[").append(distance).append(",").append(angle).append(",").append(speech).append("]");
        RobotActionProvider.getInstance().sendRosCom(buffer.toString());
    }

    /**
     *
     * @param coordinateStr
     */
    public void moveRobot(String coordinateStr){
        try {
            String[] point = coordinateStr.split(",");
            int distance = Integer.valueOf(point[0]);
            int angle = Integer.valueOf(point[1]);
            if (point.length == 3){
                double speech = Double.valueOf(point[2]);
                moveRobot(distance,angle,speech);
            }else if (point.length == 2){
                moveRobot(distance,angle);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
