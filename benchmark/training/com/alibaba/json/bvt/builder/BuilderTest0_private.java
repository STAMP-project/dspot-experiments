package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest0_private extends TestCase {
    public void test_0() throws Exception {
        BuilderTest0_private.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest0_private.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest0_private.VOBuilder.class)
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

    private static class VOBuilder {
        private BuilderTest0_private.VO vo = new BuilderTest0_private.VO();

        public BuilderTest0_private.VO build() {
            return vo;
        }

        public BuilderTest0_private.VOBuilder withId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest0_private.VOBuilder withName(String name) {
            vo.name = name;
            return this;
        }
    }
}

