package com.alibaba.json.bvt.bug;


import com.alibaba.fastjson.JSON;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class Bug_for_wangran1 extends TestCase {
    public void test_0() throws Exception {
        Bug_for_wangran1.Entity entity = new Bug_for_wangran1.Entity();
        entity.setId(11);
        entity.setName("xx");
        Bug_for_wangran1.Queue q = new Bug_for_wangran1.Queue();
        q.setId(55);
        entity.getQueue().put(q.getId(), q);
        String text = JSON.toJSONString(entity);
        System.out.println(text);
        Bug_for_wangran1.Entity entity2 = JSON.parseObject(text, Bug_for_wangran1.Entity.class);
        Assert.assertNotNull(entity2.getQueue());
        Assert.assertEquals(1, entity2.getQueue().size());
        Assert.assertEquals(true, ((entity2.getQueue().values().iterator().next()) instanceof Bug_for_wangran1.Queue));
    }

    public static class Entity {
        private int id;

        private String name;

        private Map<Integer, Bug_for_wangran1.Queue> queue = new HashMap<Integer, Bug_for_wangran1.Queue>();

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<Integer, Bug_for_wangran1.Queue> getQueue() {
            return queue;
        }

        public void setQueue(Map<Integer, Bug_for_wangran1.Queue> queue) {
            this.queue = queue;
        }

        public Map<Integer, Bug_for_wangran1.Queue> getKQueue() {
            return queue;
        }

        public void setKQueue(Map<Integer, Bug_for_wangran1.Queue> queue) {
            this.queue = queue;
        }
    }

    public static class Queue {
        public Queue() {
        }

        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

