package org.mockserver.serialization.java;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.HttpClassCallback;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpClassCallbackToJavaSerializerTest {
    @Test
    public void shouldSerializeFullObjectWithCallbackAsJava() {
        Assert.assertEquals(((((NEW_LINE) + "        callback()") + (NEW_LINE)) + "                .withCallbackClass(\"some_class\")"), new HttpClassCallbackToJavaSerializer().serialize(1, new HttpClassCallback().withCallbackClass("some_class")));
    }
}

