package org.mockserver.serialization.java;


import HttpForward.Scheme.HTTPS;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.HttpForward;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpForwardToJavaSerializerTest {
    @Test
    public void shouldSerializeFullObjectWithForwardAsJava() throws IOException {
        Assert.assertEquals(((((((((NEW_LINE) + "        forward()") + (NEW_LINE)) + "                .withHost(\"some_host\")") + (NEW_LINE)) + "                .withPort(9090)") + (NEW_LINE)) + "                .withScheme(HttpForward.Scheme.HTTPS)"), new HttpForwardToJavaSerializer().serialize(1, new HttpForward().withHost("some_host").withPort(9090).withScheme(HTTPS)));
    }
}

