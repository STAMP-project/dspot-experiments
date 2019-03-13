package com.alibaba.json.bvt.bug;


import SerializerFeature.PrettyFormat;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_dragoon26_1 extends TestCase {
    public void test_0() throws Exception {
        List<Bug_for_dragoon26_1.MonitorItemAlarmRule> rules = new ArrayList<Bug_for_dragoon26_1.MonitorItemAlarmRule>();
        Bug_for_dragoon26_1.AlarmReceiver receiver1 = new Bug_for_dragoon26_1.AlarmReceiver(1L);
        {
            Bug_for_dragoon26_1.MonitorItemAlarmRule rule = new Bug_for_dragoon26_1.MonitorItemAlarmRule();
            rule.getAlarmReceivers().add(receiver1);
            rules.add(rule);
        }
        {
            Bug_for_dragoon26_1.MonitorItemAlarmRule rule = new Bug_for_dragoon26_1.MonitorItemAlarmRule();
            rule.getAlarmReceivers().add(receiver1);
            rules.add(rule);
        }
        String text = JSON.toJSONString(rules, WriteClassName);
        System.out.println(JSON.toJSONString(rules, WriteClassName, PrettyFormat));
        List<Bug_for_dragoon26_1.MonitorItemAlarmRule> message2 = ((List<Bug_for_dragoon26_1.MonitorItemAlarmRule>) (JSON.parse(text)));
        System.out.println(JSON.toJSONString(message2, WriteClassName, PrettyFormat));
    }

    public static class MonitorItemAlarmRule {
        private List<Bug_for_dragoon26_1.AlarmReceiver> alarmReceivers = new ArrayList<Bug_for_dragoon26_1.AlarmReceiver>();

        public List<Bug_for_dragoon26_1.AlarmReceiver> getAlarmReceivers() {
            return alarmReceivers;
        }

        public void setAlarmReceivers(List<Bug_for_dragoon26_1.AlarmReceiver> alarmReceivers) {
            this.alarmReceivers = alarmReceivers;
        }
    }

    public static class AlarmReceiver {
        private Long id;

        public AlarmReceiver() {
        }

        public AlarmReceiver(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}

