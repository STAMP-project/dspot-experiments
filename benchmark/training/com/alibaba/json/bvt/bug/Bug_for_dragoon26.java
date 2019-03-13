package com.alibaba.json.bvt.bug;


import SerializerFeature.PrettyFormat;
import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


public class Bug_for_dragoon26 extends TestCase {
    public void test_0() throws Exception {
        Bug_for_dragoon26.MonitorConfigMessage message = new Bug_for_dragoon26.MonitorConfigMessage();
        Bug_for_dragoon26.MonitorConfig config = new Bug_for_dragoon26.MonitorConfig();
        message.setContent(config);
        Bug_for_dragoon26.AlarmReceiver receiver1 = new Bug_for_dragoon26.AlarmReceiver(2001L);
        Bug_for_dragoon26.AlarmReceiver receiver2 = new Bug_for_dragoon26.AlarmReceiver(2002L);
        Bug_for_dragoon26.AlarmReceiver receiver3 = new Bug_for_dragoon26.AlarmReceiver(2003L);
        ArrayList<Bug_for_dragoon26.MonitorItem> items = new ArrayList<Bug_for_dragoon26.MonitorItem>();
        {
            Bug_for_dragoon26.MonitorItem item1 = new Bug_for_dragoon26.MonitorItem();
            item1.setId(1001L);
            Bug_for_dragoon26.MonitorItemAlarmRule rule = new Bug_for_dragoon26.MonitorItemAlarmRule();
            rule.getAlarmReceivers().add(receiver1);
            rule.getAlarmReceivers().add(receiver2);
            item1.getRules().add(rule);
            items.add(item1);
        }
        {
            Bug_for_dragoon26.MonitorItem item = new Bug_for_dragoon26.MonitorItem();
            item.setId(1002L);
            Bug_for_dragoon26.MonitorItemAlarmRule rule = new Bug_for_dragoon26.MonitorItemAlarmRule();
            rule.getAlarmReceivers().add(receiver1);
            rule.getAlarmReceivers().add(receiver3);
            item.getRules().add(rule);
            items.add(item);
        }
        {
            Bug_for_dragoon26.MonitorItem item = new Bug_for_dragoon26.MonitorItem();
            item.setId(1003L);
            Bug_for_dragoon26.MonitorItemAlarmRule rule = new Bug_for_dragoon26.MonitorItemAlarmRule();
            rule.getAlarmReceivers().add(receiver2);
            rule.getAlarmReceivers().add(receiver3);
            item.getRules().add(rule);
            items.add(item);
        }
        config.setMonitorItems(items);
        String text = JSON.toJSONString(message, WriteClassName);
        System.out.println(JSON.toJSONString(message, WriteClassName, PrettyFormat));
        Bug_for_dragoon26.MonitorConfigMessage message2 = ((Bug_for_dragoon26.MonitorConfigMessage) (JSON.parse(text)));
        System.out.println(JSON.toJSONString(message2, WriteClassName, PrettyFormat));
    }

    public static class MonitorConfigMessage {
        private Object content;

        public Object getContent() {
            return content;
        }

        public void setContent(Object content) {
            this.content = content;
        }
    }

    public static class MonitorConfig {
        private Map<Long, Bug_for_dragoon26.MonitorItem> monitorItems = new HashMap<Long, Bug_for_dragoon26.MonitorItem>();

        @JSONField(name = "MonitorItems")
        public Collection<Bug_for_dragoon26.MonitorItem> getMonitorItems() {
            return monitorItems.values();
        }

        @JSONField(name = "MonitorItems")
        public void setMonitorItems(Collection<Bug_for_dragoon26.MonitorItem> items) {
            for (Bug_for_dragoon26.MonitorItem item : items) {
                this.monitorItems.put(item.getId(), item);
            }
        }
    }

    public static class MonitorItem extends Bug_for_dragoon26.MonitorItemBase<Bug_for_dragoon26.MonitorItemAlarmRule> {}

    public static class MonitorItemBase<K extends Bug_for_dragoon26.AlarmRuleBase> {
        private Long id;

        private List<K> rules = new ArrayList<K>();

        @JSONField(name = "mid")
        public Long getId() {
            return id;
        }

        @JSONField(name = "mid")
        public void setId(Long id) {
            this.id = id;
        }

        public List<K> getRules() {
            return rules;
        }

        public void setRules(List<K> rules) {
            this.rules = rules;
        }
    }

    public static class AlarmRuleBase {}

    public static class MonitorItemAlarmRule extends Bug_for_dragoon26.AlarmRuleBase {
        private List<Bug_for_dragoon26.AlarmReceiver> alarmReceivers = new ArrayList<Bug_for_dragoon26.AlarmReceiver>();

        public List<Bug_for_dragoon26.AlarmReceiver> getAlarmReceivers() {
            return alarmReceivers;
        }

        public void setAlarmReceivers(List<Bug_for_dragoon26.AlarmReceiver> alarmReceivers) {
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

