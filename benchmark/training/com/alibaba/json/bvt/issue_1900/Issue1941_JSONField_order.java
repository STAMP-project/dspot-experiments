package com.alibaba.json.bvt.issue_1900;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


public class Issue1941_JSONField_order extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"type\":\"floorV2\",\"templateId\":\"x123\",\"name\":\"floorname2\"}";
        Issue1941_JSONField_order.FloorV2 a = ((Issue1941_JSONField_order.FloorV2) (JSON.parseObject(json, Issue1941_JSONField_order.Area.class)));
        TestCase.assertEquals("floorname2", a.name);
        TestCase.assertEquals("x123", a.templateId);
    }

    @JSONType(seeAlso = { Issue1941_JSONField_order.FloorV2.class }, typeKey = "type")
    public static interface Area {}

    @JSONType(typeName = "floorV2")
    public static class FloorV2 implements Issue1941_JSONField_order.Area {
        @JSONField(ordinal = -1)
        public String type;

        public String templateId;

        public String name;
    }
}

