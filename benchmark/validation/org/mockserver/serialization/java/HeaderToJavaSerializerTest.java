package org.mockserver.serialization.java;


import java.io.IOException;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Header;


public class HeaderToJavaSerializerTest {
    @Test
    public void shouldSerializeHeader() throws IOException {
        Assert.assertEquals(((NEW_LINE) + "        new Header(\"requestHeaderNameOne\", \"requestHeaderValueOneOne\", \"requestHeaderValueOneTwo\")"), new HeaderToJavaSerializer().serialize(1, new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo")));
    }

    @Test
    public void shouldSerializeMultipleHeaders() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Header(\"requestHeaderNameOne\", \"requestHeaderValueOneOne\", \"requestHeaderValueOneTwo\"),") + (NEW_LINE)) + "        new Header(\"requestHeaderNameTwo\", \"requestHeaderValueTwo\")"), new HeaderToJavaSerializer().serializeAsJava(1, new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo")));
    }

    @Test
    public void shouldSerializeListOfHeaders() throws IOException {
        Assert.assertEquals(((((NEW_LINE) + "        new Header(\"requestHeaderNameOne\", \"requestHeaderValueOneOne\", \"requestHeaderValueOneTwo\"),") + (NEW_LINE)) + "        new Header(\"requestHeaderNameTwo\", \"requestHeaderValueTwo\")"), new HeaderToJavaSerializer().serializeAsJava(1, Arrays.asList(new Header("requestHeaderNameOne", "requestHeaderValueOneOne", "requestHeaderValueOneTwo"), new Header("requestHeaderNameTwo", "requestHeaderValueTwo"))));
    }
}

