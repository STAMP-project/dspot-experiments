package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONPOJOBuilder;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest3 extends TestCase {
    public void test_create() throws Exception {
        BuilderTest3.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest3.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest3.VOBuilder.class)
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

    @JSONPOJOBuilder(withPrefix = "kk", buildMethod = "mmm")
    public static class VOBuilder {
        private BuilderTest3.VO vo = new BuilderTest3.VO();

        public BuilderTest3.VO mmm() {
            return vo;
        }

        public BuilderTest3.VOBuilder kkId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest3.VOBuilder kkName(String name) {
            vo.name = name;
            return this;
        }
    }
}

