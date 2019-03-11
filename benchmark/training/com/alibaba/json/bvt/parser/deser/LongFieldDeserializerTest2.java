package com.alibaba.json.bvt.parser.deser;


import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;
import org.junit.Assert;


public class LongFieldDeserializerTest2 extends TestCase {
    public void test_integer() throws Exception {
        String text = "{\"value\":{\"column1\":\"aa\"}}";
        Map<String, LongFieldDeserializerTest2.Entity> map = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<Map<String, LongFieldDeserializerTest2.Entity>>() {});
        Assert.assertNotNull(map);
        Assert.assertNotNull(map.get("value"));
        Assert.assertNotNull("aa", map.get("value").getColumn1());
    }

    public void test_integer_2() throws Exception {
        String text = "[{\"value\":{\"column1\":\"aa\"}}]";
        List<Map<String, LongFieldDeserializerTest2.Entity>> mapList = JSON.parseObject(text, new com.alibaba.fastjson.TypeReference<List<Map<String, LongFieldDeserializerTest2.Entity>>>() {});
        Map<String, LongFieldDeserializerTest2.Entity> map = mapList.get(0);
        Assert.assertNotNull(map);
        Assert.assertNotNull(map.get("value"));
        Assert.assertNotNull("aa", map.get("value").getColumn1());
    }

    public static class Entity implements Serializable {
        private static final long serialVersionUID = 1L;

        private String column1;

        private Long column3;

        public String getColumn1() {
            return column1;
        }

        public void setColumn1(String column1) {
            this.column1 = column1;
        }

        public Long getColumn3() {
            return column3;
        }

        public void setColumn3(Long column3) {
            this.column3 = column3;
        }
    }
}

