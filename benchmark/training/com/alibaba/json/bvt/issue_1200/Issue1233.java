package com.alibaba.json.bvt.issue_1200;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import java.lang.reflect.Type;
import java.util.List;
import junit.framework.TestCase;


/**
 * Created by wenshao on 30/05/2017.
 */
public class Issue1233 extends TestCase {
    public void test_for_issue() throws Exception {
        ParserConfig.getGlobalInstance().putDeserializer(Issue1233.Area.class, new ObjectDeserializer() {
            public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
                JSONObject jsonObject = ((JSONObject) (parser.parse()));
                String areaType;
                if ((jsonObject.get("type")) instanceof String) {
                    areaType = ((String) (jsonObject.get("type")));
                } else {
                    return null;
                }
                if (Issue1233.Area.TYPE_SECTION.equals(areaType)) {
                    return ((T) (JSON.toJavaObject(jsonObject, Issue1233.Section.class)));
                } else
                    if (Issue1233.Area.TYPE_FLOORV1.equals(areaType)) {
                        return ((T) (JSON.toJavaObject(jsonObject, Issue1233.FloorV1.class)));
                    } else
                        if (Issue1233.Area.TYPE_FLOORV2.equals(areaType)) {
                            return ((T) (JSON.toJavaObject(jsonObject, Issue1233.FloorV2.class)));
                        }


                return null;
            }

            public int getFastMatchToken() {
                return 0;
            }
        });
        JSONObject jsonObject = JSON.parseObject("{\"type\":\"floorV2\",\"templateId\":\"x123\"}");
        Issue1233.FloorV2 floorV2 = ((Issue1233.FloorV2) (jsonObject.toJavaObject(Issue1233.Area.class)));
        TestCase.assertNotNull(floorV2);
        TestCase.assertEquals("x123", floorV2.templateId);
    }

    public interface Area {
        public static final String TYPE_SECTION = "section";

        public static final String TYPE_FLOORV1 = "floorV1";

        public static final String TYPE_FLOORV2 = "floorV2";

        String getName();
    }

    public static class Section implements Issue1233.Area {
        public List<Issue1233.Area> children;

        public String type;

        public String templateId;

        public String getName() {
            return templateId;
        }
    }

    public static class FloorV1 implements Issue1233.Area {
        public String type;

        public String templateId;

        public String getName() {
            return templateId;
        }
    }

    public static class FloorV2 implements Issue1233.Area {
        public List<Issue1233.Area> children;

        public String type;

        public String templateId;

        public String getName() {
            return templateId;
        }
    }
}

