package org.javaee7.json.object.reader;


import JSONCompareMode.STRICT;
import java.io.StringReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import org.jboss.arquillian.junit.Arquillian;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class JsonReaderFromReaderTest {
    @Test
    public void testEmptyObject() throws JSONException {
        JsonReader jsonReader = Json.createReader(new StringReader("{}"));
        JsonObject json = jsonReader.readObject();
        Assert.assertNotNull(json);
        Assert.assertTrue(json.isEmpty());
    }

    @Test
    public void testSimpleObjectWithTwoElements() throws JSONException {
        JsonReader jsonReader = Json.createReader(new StringReader(("{" + (("  \"apple\":\"red\"," + "  \"banana\":\"yellow\"") + "}"))));
        JsonObject json = jsonReader.readObject();
        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
        Assert.assertTrue(json.containsKey("apple"));
        Assert.assertEquals("red", json.getString("apple"));
        Assert.assertTrue(json.containsKey("banana"));
        Assert.assertEquals("yellow", json.getString("banana"));
    }

    @Test
    public void testArray() throws JSONException {
        JsonReader jsonReader = Json.createReader(new StringReader(("[" + (("  { \"apple\":\"red\" }," + "  { \"banana\":\"yellow\" }") + "]"))));
        JsonArray jsonArr = jsonReader.readArray();
        Assert.assertNotNull(jsonArr);
        Assert.assertEquals(2, jsonArr.size());
        JSONAssert.assertEquals("{\"apple\":\"red\"}", jsonArr.get(0).toString(), STRICT);
        JSONAssert.assertEquals("{\"banana\":\"yellow\"}", jsonArr.get(1).toString(), STRICT);
    }

    @Test
    public void testNestedStructure() throws JSONException {
        JsonReader jsonReader = Json.createReader(new StringReader(("{" + ((((((("  \"title\":\"The Matrix\"," + "  \"year\":1999,") + "  \"cast\":[") + "    \"Keanu Reaves\",") + "    \"Laurence Fishburne\",") + "    \"Carrie-Anne Moss\"") + "  ]") + "}"))));
        JsonObject json = jsonReader.readObject();
        Assert.assertNotNull(json);
        Assert.assertFalse(json.isEmpty());
        Assert.assertTrue(json.containsKey("title"));
        Assert.assertEquals("The Matrix", json.getString("title"));
        Assert.assertTrue(json.containsKey("year"));
        Assert.assertEquals(1999, json.getInt("year"));
        Assert.assertTrue(json.containsKey("cast"));
        JsonArray jsonArr = json.getJsonArray("cast");
        Assert.assertNotNull(jsonArr);
        Assert.assertEquals(3, jsonArr.size());
        JSONAssert.assertEquals(("[" + ((("    \"Keanu Reaves\"," + "    \"Laurence Fishburne\",") + "    \"Carrie-Anne Moss\"") + "  ]")), jsonArr.toString(), STRICT);
    }
}

