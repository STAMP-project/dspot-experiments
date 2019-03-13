package com.vaadin.server;


import com.vaadin.server.communication.JSONSerializer;
import com.vaadin.ui.ConnectorTracker;
import elemental.json.JsonValue;
import java.lang.reflect.Type;
import org.junit.Assert;
import org.junit.Test;


public class CustomJSONSerializerTest {
    public static class Foo {}

    public static class FooSerializer implements JSONSerializer<CustomJSONSerializerTest.Foo> {
        @Override
        public CustomJSONSerializerTest.Foo deserialize(Type type, JsonValue jsonValue, ConnectorTracker connectorTracker) {
            return null;
        }

        @Override
        public JsonValue serialize(CustomJSONSerializerTest.Foo value, ConnectorTracker connectorTracker) {
            return null;
        }
    }

    @Test
    public void testMultipleRegistration() {
        boolean thrown = false;
        try {
            JsonCodec.setCustomSerializer(CustomJSONSerializerTest.Foo.class, new CustomJSONSerializerTest.FooSerializer());
            JsonCodec.setCustomSerializer(CustomJSONSerializerTest.Foo.class, new CustomJSONSerializerTest.FooSerializer());
        } catch (IllegalStateException ise) {
            thrown = true;
        } finally {
            JsonCodec.setCustomSerializer(CustomJSONSerializerTest.Foo.class, null);
        }
        Assert.assertTrue(("Multiple serializer registrations for one class " + "should throw an IllegalStateException"), thrown);
    }
}

