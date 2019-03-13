package com.vaadin.tests.server;


import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import org.junit.Test;


public class SerializationTest {
    public static class JSE extends AbstractJavaScriptExtension {
        {
            addFunction("foo", new JavaScriptFunction() {
                @Override
                public void call(JsonArray arguments) {
                    System.out.println("Foo called");
                }
            });
        }
    }

    @Test
    public void testJSExtension() throws Exception {
        SerializationTest.serializeAndDeserialize(new SerializationTest.JSE());
    }
}

