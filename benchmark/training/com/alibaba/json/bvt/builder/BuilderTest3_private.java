package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest3_private extends TestCase {
    public void test_create() throws Exception {
        BuilderTest3_private.VO vo = JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest3_private.VO.class);
        Assert.assertEquals(12304, vo.getId());
        Assert.assertEquals("ljw", vo.getName());
    }

    @JSONType(builder = BuilderTest3_private.VOBuilder.class)
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
        private BuilderTest3_private.VO vo = new BuilderTest3_private.VO();

        public BuilderTest3_private.VO create() {
            return vo;
        }

        @JSONField(name = "id")
        public BuilderTest3_private.VOBuilder kkId(int id) {
            vo.id = id;
            return this;
        }

        @JSONField(name = "name")
        public BuilderTest3_private.VOBuilder kkName(String name) {
            vo.name = name;
            return this;
        }
    }
}

