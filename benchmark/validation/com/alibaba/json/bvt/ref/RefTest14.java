package com.alibaba.json.bvt.ref;


import SerializerFeature.PrettyFormat;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class RefTest14 extends TestCase {
    public void test_0() throws Exception {
        RefTest14.Group admin = new RefTest14.Group("admin");
        RefTest14.User jobs = new RefTest14.User("jobs");
        RefTest14.User sager = new RefTest14.User("sager");
        RefTest14.User sdh5724 = new RefTest14.User("sdh5724");
        admin.getMembers().add(jobs);
        jobs.getGroups().add(admin);
        admin.getMembers().add(sager);
        sager.getGroups().add(admin);
        admin.getMembers().add(sdh5724);
        sdh5724.getGroups().add(admin);
        sager.setReportTo(sdh5724);
        jobs.setReportTo(sdh5724);
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.setAsmEnable(false);
        String text = JSON.toJSONString(admin, serializeConfig, PrettyFormat);
        System.out.println(text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        JSON.parseObject(text, RefTest14.Group.class, config, 0);
    }

    public static class Group {
        private String name;

        private List<RefTest14.User> members = new ArrayList<RefTest14.User>();

        public Group() {
        }

        public Group(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<RefTest14.User> getMembers() {
            return members;
        }

        public void setMembers(List<RefTest14.User> members) {
            this.members = members;
        }

        public String toString() {
            return this.name;
        }
    }

    public static class User {
        private String name;

        private List<RefTest14.Group> groups = new ArrayList<RefTest14.Group>();

        private RefTest14.User reportTo;

        public User() {
        }

        public RefTest14.User getReportTo() {
            return reportTo;
        }

        public void setReportTo(RefTest14.User reportTo) {
            this.reportTo = reportTo;
        }

        public User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<RefTest14.Group> getGroups() {
            return groups;
        }

        public void setGroups(List<RefTest14.Group> groups) {
            this.groups = groups;
        }

        public String toString() {
            return this.name;
        }
    }
}

