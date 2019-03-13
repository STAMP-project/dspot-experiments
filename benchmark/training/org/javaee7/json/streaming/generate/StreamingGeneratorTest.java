package org.javaee7.json.streaming.generate;


import JSONCompareMode.STRICT;
import java.io.StringWriter;
import javax.json.Json;
import javax.json.stream.JsonGenerator;
import javax.json.stream.JsonGeneratorFactory;
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
public class StreamingGeneratorTest {
    @Test
    public void testEmptyObject() throws JSONException {
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        StringWriter w = new StringWriter();
        JsonGenerator gen = factory.createGenerator(w);
        gen.writeStartObject().writeEnd();
        gen.flush();
        JSONAssert.assertEquals("{}", w.toString(), STRICT);
    }

    @Test
    public void testSimpleObject() throws JSONException {
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        StringWriter w = new StringWriter();
        JsonGenerator gen = factory.createGenerator(w);
        gen.writeStartObject().write("apple", "red").write("banana", "yellow").writeEnd();
        gen.flush();
        JSONAssert.assertEquals("{\"apple\" : \"red\", \"banana\" : \"yellow\" }", w.toString(), STRICT);
    }

    @Test
    public void testArray() throws JSONException {
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        StringWriter w = new StringWriter();
        JsonGenerator gen = factory.createGenerator(w);
        gen.writeStartArray().writeStartObject().write("apple", "red").writeEnd().writeStartObject().write("banana", "yellow").writeEnd().writeEnd();
        gen.flush();
        JSONAssert.assertEquals("[{\"apple\":\"red\"},{\"banana\":\"yellow\"}]", w.toString(), STRICT);
    }

    @Test
    public void testNestedStructure() throws JSONException {
        JsonGeneratorFactory factory = Json.createGeneratorFactory(null);
        StringWriter w = new StringWriter();
        JsonGenerator gen = factory.createGenerator(w);
        gen.writeStartObject().write("title", "The Matrix").write("year", 1999).writeStartArray("cast").write("Keanu Reaves").write("Laurence Fishburne").write("Carrie-Anne Moss").writeEnd().writeEnd();
        gen.flush();
        JSONAssert.assertEquals("{\"title\":\"The Matrix\",\"year\":1999,\"cast\":[\"Keanu Reaves\",\"Laurence Fishburne\",\"Carrie-Anne Moss\"]}", w.toString(), STRICT);
    }
}

