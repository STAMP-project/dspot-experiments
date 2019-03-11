package com.alibaba.json.bvt;


import com.alibaba.fastjson.JSON;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest extends TestCase {
    public void test_builder() throws Exception {
        BuilderTest.RainbowStats rainbowStats = JSON.parseObject("{\"id\":33}", BuilderTest.RainbowStats.class);
        Assert.assertEquals(33, rainbowStats.getId());
    }

    private static class RainbowStats {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public BuilderTest.RainbowStats setId(int id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public BuilderTest.RainbowStats setName(String name) {
            this.name = name;
            return this;
        }
    }
}

