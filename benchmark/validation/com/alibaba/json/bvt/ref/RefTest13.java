package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest13 extends TestCase {
    public void test_0() throws Exception {
        RefTest13.Entity entity = new RefTest13.Entity(123, new RefTest13.Child());
        entity.getChild().setParent(entity);
        String text = JSON.toJSONString(entity);
        System.out.println(text);
        RefTest13.Entity entity2 = JSON.parseObject(text, RefTest13.Entity.class);
        Assert.assertEquals(entity2, entity2.getChild().getParent());
        System.out.println(JSON.toJSONString(entity2));
    }

    public static class Entity {
        private final int id;

        private final RefTest13.Child child;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        int id, @JSONField(name = "child")
        RefTest13.Child child) {
            super();
            this.id = id;
            this.child = child;
        }

        public int getId() {
            return id;
        }

        public RefTest13.Child getChild() {
            return child;
        }

        public String toString() {
            return "Model-" + (id);
        }
    }

    public static class Child {
        private RefTest13.Entity parent;

        public Child() {
        }

        public RefTest13.Entity getParent() {
            return parent;
        }

        public void setParent(RefTest13.Entity parent) {
            this.parent = parent;
        }

        public String toString() {
            return "Child";
        }
    }
}

