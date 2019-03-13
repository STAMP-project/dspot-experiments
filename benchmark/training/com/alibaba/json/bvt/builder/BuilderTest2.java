package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest2 extends TestCase {
    public void test_create() throws Exception {
        BuilderTest2.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest2.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest2.VOBuilder.class)
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

    @JSONPOJOBuilder(buildMethod = "xxx")
    public static class VOBuilder {
        private BuilderTest2.VO vo = new BuilderTest2.VO();

        public BuilderTest2.VO xxx() {
            return vo;
        }

        public BuilderTest2.VOBuilder withId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest2.VOBuilder withName(String name) {
            vo.name = name;
            return this;
        }
    }
}

