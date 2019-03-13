package org.mockserver.serialization.java;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.HttpError;
import org.mockserver.serialization.Base64Converter;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpErrorToJavaSerializerTest {
    private final Base64Converter base64Converter = new Base64Converter();

    @Test
    public void shouldSerializeFullObjectWithForwardAsJava() throws IOException {
        Assert.assertEquals(((((((((((NEW_LINE) + "        error()") + (NEW_LINE)) + "                .withDelay(new Delay(TimeUnit.MILLISECONDS, 100))") + (NEW_LINE)) + "                .withDropConnection(true)") + (NEW_LINE)) + "                .withResponseBytes(new Base64Converter().base64StringToBytes(\"") + (base64Converter.bytesToBase64String("example_bytes".getBytes(StandardCharsets.UTF_8)))) + "\"))"), new HttpErrorToJavaSerializer().serialize(1, new HttpError().withDelay(TimeUnit.MILLISECONDS, 100).withDropConnection(true).withResponseBytes("example_bytes".getBytes(StandardCharsets.UTF_8))));
    }
}

