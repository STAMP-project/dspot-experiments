package com.alibaba.json.bvt.issue_1200;


import SerializerFeature.WriteClassName;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class Issue1235 extends TestCase {
    public void test_for_issue() throws Exception {
        String json = "{\"type\":\"floorV2\",\"templateId\":\"x123\"}";
        Issue1235.FloorV2 floorV2 = ((Issue1235.FloorV2) (JSON.parseObject(json, Issue1235.Area.class)));
        TestCase.assertNotNull(floorV2);
        TestCase.assertNotNull(floorV2.templateId);
        TestCase.assertEquals("x123", floorV2.templateId);
        TestCase.assertEquals("floorV2", floorV2.type);
        String json2 = JSON.toJSONString(floorV2, WriteClassName);
        TestCase.assertEquals("{\"type\":\"floorV2\",\"templateId\":\"x123\"}", json2);
    }

    @JSONType(seeAlso = { Issue1235.FloorV2.class }, typeKey = "type")
    public interface Area {
        public static final String TYPE_SECTION = "section";

        public static final String TYPE_FLOORV1 = "floorV1";

        public static final String TYPE_FLOORV2 = "floorV2";
    }

    @JSONType(typeName = "floorV2")
    public static class FloorV2 implements Issue1235.Area {
        public String type;

        public String templateId;
    }
}

