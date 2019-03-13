package com.netflix.conductor.core.utils;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Any;
import com.google.protobuf.Struct;
import com.google.protobuf.Value;
import com.netflix.conductor.common.utils.JsonMapperProvider;
import java.io.IOException;
import java.io.StringWriter;
import org.junit.Assert;
import org.junit.Test;


public class JsonMapperProviderTest {
    @Test
    public void testSimpleMapping() throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper m = new JsonMapperProvider().get();
        Assert.assertTrue(m.canSerialize(Any.class));
        Struct struct1 = Struct.newBuilder().putFields("some-key", Value.newBuilder().setStringValue("some-value").build()).build();
        Any source = Any.pack(struct1);
        StringWriter buf = new StringWriter();
        m.writer().writeValue(buf, source);
        Any dest = m.reader().forType(Any.class).readValue(buf.toString());
        Assert.assertEquals(source.getTypeUrl(), dest.getTypeUrl());
        Struct struct2 = dest.unpack(Struct.class);
        Assert.assertTrue(struct2.containsFields("some-key"));
        Assert.assertEquals(struct1.getFieldsOrThrow("some-key").getStringValue(), struct2.getFieldsOrThrow("some-key").getStringValue());
    }
}

