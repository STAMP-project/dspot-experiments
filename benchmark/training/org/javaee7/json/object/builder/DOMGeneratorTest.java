package org.javaee7.json.object.builder;


import JSONCompareMode.STRICT;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonWriter;
import org.jboss.arquillian.junit.Arquillian;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Arun Gupta
 */
@RunWith(Arquillian.class)
public class DOMGeneratorTest {
    @Test
    public void testEmptyObject() throws JSONException {
        JsonObject jsonObject = Json.createObjectBuilder().build();
        StringWriter w = new StringWriter();
        try (JsonWriter writer = Json.createWriter(w)) {
            writer.write(jsonObject);
        }
        JSONAssert.assertEquals("{}", w.toString(), STRICT);
    }

    @Test
    public void testSimpleObject() throws JSONException {
        JsonObject jsonObject = Json.createObjectBuilder().add("apple", "red").add("banana", "yellow").build();
        StringWriter w = new StringWriter();
        try (JsonWriter writer = Json.createWriter(w)) {
            writer.write(jsonObject);
        }
        JSONAssert.assertEquals("{\"apple\" : \"red\", \"banana\" : \"yellow\" }", w.toString(), STRICT);
    }

    @Test
    public void testArray() throws JSONException {
        JsonArray jsonArray = Json.createArrayBuilder().add(Json.createObjectBuilder().add("apple", "red")).add(Json.createObjectBuilder().add("banana", "yellow")).build();
        StringWriter w = new StringWriter();
        try (JsonWriter writer = Json.createWriter(w)) {
            writer.write(jsonArray);
        }
        JSONAssert.assertEquals("[{\"apple\":\"red\"},{\"banana\":\"yellow\"}]", w.toString(), STRICT);
    }

    @Test
    public void testNestedStructure() throws JSONException {
        JsonObject jsonObject = Json.createObjectBuilder().add("title", "The Matrix").add("year", 1999).add("cast", Json.createArrayBuilder().add("Keanu Reaves").add("Laurence Fishburne").add("Carrie-Anne Moss")).build();
        StringWriter w = new StringWriter();
        try (JsonWriter writer = Json.createWriter(w)) {
            writer.write(jsonObject);
        }
        JSONAssert.assertEquals("{\"title\":\"The Matrix\",\"year\":1999,\"cast\":[\"Keanu Reaves\",\"Laurence Fishburne\",\"Carrie-Anne Moss\"]}", w.toString(), STRICT);
    }
}

