package com.alibaba.json.bvt.builder;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;
import org.junit.Assert;


public class BuilderTest_error_private extends TestCase {
    public void test_0() throws Exception {
        Exception error = null;
        try {
            JSON.parseObject("{\"id\":12304,\"name\":\"ljw\"}", BuilderTest_error_private.VO.class);
        } catch (JSONException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
    }

    @JSONType(builder = BuilderTest_error_private.VOBuilder.class)
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
        private BuilderTest_error_private.VO vo = new BuilderTest_error_private.VO();

        public BuilderTest_error_private.VO build() {
            throw new IllegalStateException();
        }

        public BuilderTest_error_private.VOBuilder withId(int id) {
            vo.id = id;
            return this;
        }

        public BuilderTest_error_private.VOBuilder withName(String name) {
            vo.name = name;
            return this;
        }
    }
}

