package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest1 extends TestCase {
    public void test_create() throws Exception {
        BuilderTest1.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest1.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest1.VOBuilder.class)
    public static class VO {
        private int id;

        private String name;

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public static class VOBuilder {
        private BuilderTest1.VO vo = new BuilderTest1.VO();

        public BuilderTest1.VO create() {
            return vo;
        }

        public BuilderTest1.VOBuilder withId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest1.VOBuilder withName(String name) {
            vo.name = name;
            return this;
        }
    }
}

