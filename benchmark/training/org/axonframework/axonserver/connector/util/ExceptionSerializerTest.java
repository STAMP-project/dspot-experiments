package org.axonframework.axonserver.connector.util;


import io.axoniq.axonserver.grpc.ErrorMessage;
import org.junit.Assert;
import org.junit.Test;


/**
 * Author: marc
 */
public class ExceptionSerializerTest {
    @Test
    public void serializeNullClient() {
        ErrorMessage result = ExceptionSerializer.serialize(null, new RuntimeException("Something went wrong"));
        Assert.assertEquals("", result.getLocation());
    }

    @Test
    public void serializeNonNullClient() {
        ErrorMessage result = ExceptionSerializer.serialize("Client", new RuntimeException("Something went wrong"));
        Assert.assertEquals("Client", result.getLocation());
    }
}

