package com.instagram.common.json.annotation.processor;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.instagram.common.json.annotation.processor.nobodies.NoBodyUUT;
import com.instagram.common.json.annotation.processor.nobodies.NoBodyUUT__JsonHelper;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that disabling method bodies works.
 */
public class NobodiesTest {
    @Test
    public void serializeIsNoOp() throws IOException {
        StringWriter stringWriter = new StringWriter();
        JsonGenerator jsonGenerator = new JsonFactory().createGenerator(stringWriter);
        NoBodyUUT obj = new NoBodyUUT();
        obj.mValue = "some-value";
        NoBodyUUT__JsonHelper.serializeToJson(jsonGenerator, obj, true);
        jsonGenerator.close();
        String serialized = stringWriter.toString();
        Assert.assertEquals("{}", serialized);
    }

    @Test
    public void deserializeIsNoOp() throws IOException {
        JsonParser jp = new JsonFactory().createParser("{\"value\":\"some-value\"}");
        jp.nextToken();
        NoBodyUUT obj = NoBodyUUT__JsonHelper.parseFromJson(jp);
        Assert.assertNull(obj.mValue);
    }
}

