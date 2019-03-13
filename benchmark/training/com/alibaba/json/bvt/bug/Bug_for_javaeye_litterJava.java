package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;


public class Bug_for_javaeye_litterJava extends TestCase {
    public void test_for_bug() throws Exception {
        Bug_for_javaeye_litterJava.Group group = new Bug_for_javaeye_litterJava.Group();
        group.setId(123L);
        group.setName("xxx");
        group.getClzes().add(Bug_for_javaeye_litterJava.Group.class);
        String text = JSON.toJSONString(group);
        JSON.parseObject(text, Bug_for_javaeye_litterJava.Group.class);
    }

    public static class Group {
        private Long id;

        private String name;

        private List<Class> clzes = new ArrayList<Class>();

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Class> getClzes() {
            return clzes;
        }

        public void setClzes(List<Class> clzes) {
            this.clzes = clzes;
        }
    }
}

