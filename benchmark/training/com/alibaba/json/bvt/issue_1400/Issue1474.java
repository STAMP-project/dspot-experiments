package com.alibaba.json.bvt.issue_1400;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;


public class Issue1474 extends TestCase {
    public void test_for_issue() throws Exception {
        Map<String, Object> extraData = new HashMap<String, Object>();
        extraData.put("ext_1", null);
        extraData.put("ext_2", null);
        Issue1474.People p = new Issue1474.People();
        p.setId("001");
        p.setName("??");
        p.setExtraData(extraData);
        TestCase.assertEquals("{\"id\":\"001\",\"name\":\"\u987e\u5ba2\"}", JSON.toJSONString(p));
    }

    @JSONType(asm = false)
    static class People {
        private String name;

        private String id;

        @JSONField(unwrapped = true)
        private Object extraData;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getExtraData() {
            return extraData;
        }

        public void setExtraData(Object extraData) {
            this.extraData = extraData;
        }
    }
}

