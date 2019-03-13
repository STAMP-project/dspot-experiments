package com.alibaba.json.bvt.ref;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.parser.ParserConfig;
import junit.framework.TestCase;
import org.junit.Assert;


public class RefTest12 extends TestCase {
    public void test_0() throws Exception {
        RefTest12.Entity entity = new RefTest12.Entity(123, new RefTest12.Child());
        entity.getChild().setParent(entity);
        String text = JSON.toJSONString(entity);
        System.out.println(text);
        ParserConfig config = new ParserConfig();
        config.setAsmEnable(false);
        RefTest12.Entity entity2 = JSON.parseObject(text, RefTest12.Entity.class, config, 0);
        Assert.assertEquals(entity2, entity2.getChild().getParent());
        System.out.println(JSON.toJSONString(entity2));
    }

    public static class Entity {
        private final int id;

        private final RefTest12.Child child;

        @JSONCreator
        public Entity(@JSONField(name = "id")
        int id, @JSONField(name = "child")
        RefTest12.Child child) {
            super();
            this.id = id;
            this.child = child;
        }

        public int getId() {
            return id;
        }

        public RefTest12.Child getChild() {
            return child;
        }
    }

    public static class Child {
        private RefTest12.Entity parent;

        public RefTest12.Entity getParent() {
            return parent;
        }

        public void setParent(RefTest12.Entity parent) {
            this.parent = parent;
        }
    }
}

