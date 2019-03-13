package com.alibaba.json.bvt.parser.creator;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import junit.framework.TestCase;


public class JSONCreatorTest10 extends TestCase {
    public void test_for_yk() throws Exception {
        String jsonString = "{\"link\":\"http://lqgzs.org/fsqhwlnf\",\"text\":\"\u4e50\u52a8\u529b\u4e13\u4eab\"}";
        JSONObject headerJSON = JSONObject.parseObject(jsonString);
        JSONCreatorTest10.HeaderDTO headerDTO = headerJSON.toJavaObject(JSONCreatorTest10.HeaderDTO.class);
        TestCase.assertEquals("http://lqgzs.org/fsqhwlnf", headerDTO.link);
        TestCase.assertEquals("?????", headerDTO.title);
    }

    public static class HeaderDTO {
        private String title;

        private String link;

        @JSONCreator
        public HeaderDTO(@JSONField(name = "text")
        String title, @JSONField(name = "link")
        String link) {
            this.title = title;
            this.link = link;
        }
    }
}

