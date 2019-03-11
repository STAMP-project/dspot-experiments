package io.searchbox.core;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DocTest {
    @Test
    public void testToMapWithOnlyRequiredParameters() {
        String index = "idx0";
        String type = "typo";
        String id = "00001_AE";
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        expectedMap.put("_index", index);
        expectedMap.put("_type", type);
        expectedMap.put("_id", id);
        Doc doc = new Doc(index, type, id);
        Map<String, Object> actualMap = doc.toMap();
        Assert.assertEquals(3, actualMap.size());
        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testToMapWithFieldsParameter() {
        String index = "idx0";
        String type = "typo";
        String id = "00001_AE";
        List<String> fields = Arrays.asList("user", "location");
        Doc doc = new Doc(index, type, id);
        doc.addFields(fields);
        Map<String, Object> actualMap = doc.toMap();
        Assert.assertEquals(4, actualMap.size());
        Assert.assertEquals(index, actualMap.get("_index"));
        Assert.assertEquals(type, actualMap.get("_type"));
        Assert.assertEquals(id, actualMap.get("_id"));
        Assert.assertEquals(fields, actualMap.get("fields"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullIndex() {
        new Doc(null, "type", "id");
        Assert.fail("Constructor should have thrown an exception when index was null");
    }

    @Test
    public void testConstructionWithNullType() {
        String index = "idx0";
        String id = "00001_AE";
        Map<String, Object> expectedMap = new HashMap<String, Object>();
        expectedMap.put("_index", index);
        expectedMap.put("_id", id);
        Doc doc = new Doc(index, id);
        Map<String, Object> actualMap = doc.toMap();
        Assert.assertEquals(2, actualMap.size());
        Assert.assertEquals(expectedMap, actualMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructionWithNullId() {
        new Doc("idx", "type", null);
        Assert.fail("Constructor should have thrown an exception when id was null");
    }
}

