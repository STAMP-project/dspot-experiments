package com.vip.saturn.job.utils;


import com.google.gson.JsonNull;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import com.vip.saturn.job.SaturnJobReturn;
import com.vip.saturn.job.executor.ExecutorConfig;
import com.vip.saturn.job.trigger.TriggeredData;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class JsonUtilsTest {
    @Test
    public void testGetGson() {
        Assert.assertNotNull(JsonUtils.getGson().fromJson("{}", ExecutorConfig.class));
        try {
            JsonUtils.getGson().fromJson("abc", ExecutorConfig.class);
            Assert.fail("cannot happen");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof JsonParseException));
        }
        Assert.assertNotNull(JsonUtils.getGson().fromJson("{}", new TypeToken<Map<String, String>>() {}.getType()));
        try {
            JsonUtils.getGson().fromJson("abc", new TypeToken<Map<String, String>>() {}.getType());
            Assert.fail("cannot happen");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof JsonParseException));
        }
        Assert.assertNotNull(JsonUtils.getGson().fromJson("{}", SaturnJobReturn.class));
        try {
            JsonUtils.getGson().fromJson("abc", SaturnJobReturn.class);
            Assert.fail("cannot happen");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof JsonParseException));
        }
        Map<String, Object> obj = JsonUtils.getGson().fromJson("{\"a\":true, \"b\":1}", new TypeToken<Map<String, Object>>() {}.getType());
        Assert.assertNotNull(obj);
        Assert.assertTrue(obj.containsKey("a"));
        Assert.assertEquals(true, obj.get("a"));
        Assert.assertTrue(obj.containsKey("b"));
        Assert.assertEquals(1.0, obj.get("b"));
        try {
            JsonUtils.getGson().fromJson("abc", new TypeToken<Map<String, Object>>() {}.getType());
            Assert.fail("cannot happen");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof JsonParseException));
        }
        Assert.assertNull(JsonUtils.getGson().fromJson("", new TypeToken<Map<String, Object>>() {}.getType()));
        String src = null;
        Assert.assertNull(JsonUtils.getGson().fromJson(src, new TypeToken<Map<String, Object>>() {}.getType()));
    }

    @Test
    public void testGetJsonParser() {
        Assert.assertEquals("abc", JsonUtils.getJsonParser().parse("{\"message\":\"abc\"}").getAsJsonObject().get("message").getAsString());
        Assert.assertEquals(JsonNull.INSTANCE, JsonUtils.getJsonParser().parse("{\"message\":null}").getAsJsonObject().get("message"));
        Assert.assertNull(JsonUtils.getJsonParser().parse("{}").getAsJsonObject().get("message"));
    }

    @Test
    public void testToJson1() {
        Assert.assertEquals("null", JsonUtils.toJson(null));
        Assert.assertEquals("{}", JsonUtils.toJson(new TriggeredData()));
        Assert.assertEquals("null", JsonUtils.getGson().toJson(JsonNull.INSTANCE));
        Assert.assertEquals("null", JsonUtils.getGson().toJson(null));
    }

    @Test
    public void testToJson2() {
        Map<String, String> src = null;
        Assert.assertEquals("null", JsonUtils.toJson(src, new TypeToken<Map<String, String>>() {}.getType()));
        src = new HashMap<>();
        src.put("cron", "9 9 9 9 9 ? 2099");
        Assert.assertEquals("{\"cron\":\"9 9 9 9 9 ? 2099\"}", JsonUtils.toJson(src, new TypeToken<Map<String, String>>() {}.getType()));
    }

    @Test
    public void testFromJson1() {
        String customContextStr = null;
        Assert.assertNull(JsonUtils.fromJson(customContextStr, new TypeToken<Map<String, String>>() {}.getType()));
        customContextStr = "abc";
        Assert.assertNull(JsonUtils.fromJson(customContextStr, new TypeToken<Map<String, String>>() {}.getType()));
        customContextStr = "{\"key\":\"value\"}";
        Map<String, String> expected = new HashMap<>();
        expected.put("key", "value");
        Assert.assertEquals(expected, JsonUtils.fromJson(customContextStr, new TypeToken<Map<String, String>>() {}.getType()));
    }

    @Test
    public void testFromJson2() {
        String triggeredDataStr = null;
        Assert.assertNull(JsonUtils.fromJson(triggeredDataStr, TriggeredData.class));
        triggeredDataStr = "null";
        Assert.assertNull(JsonUtils.fromJson(triggeredDataStr, TriggeredData.class));
        triggeredDataStr = "{}";
        Assert.assertNotNull(JsonUtils.fromJson(triggeredDataStr, TriggeredData.class));
        triggeredDataStr = "abc";
        Assert.assertNull(JsonUtils.fromJson(triggeredDataStr, TriggeredData.class));
    }

    @Test
    public void testEscapeHtmlChar() {
        String str = "<>'=";
        Assert.assertEquals((("\"" + str) + "\""), JsonUtils.toJson(str));
    }

    @Test
    public void testDateFormat() {
        Assert.assertTrue(JsonUtils.toJson(new Date()).matches("\"\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\""));
    }
}

