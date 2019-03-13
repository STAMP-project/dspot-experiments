package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_42283905 extends TestCase {
    public void test_0() throws Exception {
        String text;
        {
            List<Bug_for_42283905.Group> groups = new ArrayList<Bug_for_42283905.Group>();
            Bug_for_42283905.Command c0 = new Bug_for_42283905.Command(1);
            Bug_for_42283905.Command c1 = new Bug_for_42283905.Command(2);
            Bug_for_42283905.Command c2 = new Bug_for_42283905.Command(3);
            c1.setPre(c0);
            c2.setPre(c1);
            {
                Bug_for_42283905.Group group = new Bug_for_42283905.Group("g0");
                group.getBattleCommandList().add(c0);
                groups.add(group);
            }
            {
                Bug_for_42283905.Group group = new Bug_for_42283905.Group("g1");
                group.getBattleCommandList().add(c1);
                groups.add(group);
            }
            {
                Bug_for_42283905.Group group = new Bug_for_42283905.Group("g2");
                group.getBattleCommandList().add(c2);
                groups.add(group);
            }
            text = JSON.toJSONString(groups);
        }
        System.out.println(text);
        List<Bug_for_42283905.Group> groups = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Bug_for_42283905.Group>>() {});
        Bug_for_42283905.Group g0 = groups.get(0);
        Bug_for_42283905.Group g1 = groups.get(1);
        System.out.println(JSON.toJSONString(groups));
    }

    public static class Group {
        private String name;

        private List<Bug_for_42283905.Command> battleCommandList = new ArrayList<Bug_for_42283905.Command>();

        public Group() {
        }

        public Group(String name) {
            this.name = name;
        }

        public List<Bug_for_42283905.Command> getBattleCommandList() {
            return battleCommandList;
        }

        public void setBattleCommandList(List<Bug_for_42283905.Command> battleCommandList) {
            this.battleCommandList = battleCommandList;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class Command {
        private int id;

        public Command() {
        }

        public Command(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        private Bug_for_42283905.Command pre;

        public Bug_for_42283905.Command getPre() {
            return pre;
        }

        public void setPre(Bug_for_42283905.Command pre) {
            this.pre = pre;
        }

        public String toString() {
            return ("{id:" + (id)) + "}";
        }
    }
}

