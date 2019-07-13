package com.reeman.nvstatusdemo.event;


/**
 * Created by gj on 2019/5/28.
 */

public class MyEvent {
    public static class MainEvent {
        public int action;
        public Object data;

        public MainEvent(int action, Object obj) {
            this.action = action;
            this.data = obj;
        }
    }


    public static class WakeUpEvent {
        public int angle;//唤醒角度

        public WakeUpEvent(int angle) {
            this.angle = angle;
        }
    }


    public static class MotionTypeEvent {
        public int type; // 运动类型

        public MotionTypeEvent(int type) {
            this.type = type;
        }

    }

    public static class ScramEvent {
        public int state;

        public ScramEvent(int state) {
            this.state = state;
        }

    }

    public static class PowerUpEvent {
        public int type;

        public PowerUpEvent(int type) {
            this.type = type;
        }
    }

    public static class BatteryEvent {
        public int level;

        public BatteryEvent(int level) {
            this.level = level;
        }
    }

    public static class DockEvent {
        public int type;

        public DockEvent(int type) {
            this.type = type;
        }

    }

    public static class RosEvent {
        public String result;

        public RosEvent(String result) {
            this.result = result;
        }
    }

}
