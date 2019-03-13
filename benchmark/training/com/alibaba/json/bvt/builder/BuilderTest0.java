package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest0 extends TestCase {
    public void test_0() throws Exception {
        BuilderTest0.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest0.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest0.VOBuilder.class)
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
        private BuilderTest0.VO vo = new BuilderTest0.VO();

        public BuilderTest0.VO build() {
            return vo;
        }

        public BuilderTest0.VOBuilder withId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest0.VOBuilder withName(String name) {
            vo.name = name;
            return this;
        }
    }
}

