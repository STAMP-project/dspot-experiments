package com.vaadin.tests.server;


import com.vaadin.server.ClientConnector;
import com.vaadin.server.ClientMethodInvocation;
import com.vaadin.server.JsonCodec;
import com.vaadin.ui.JavaScript.JavaScriptCallbackRpc;
import com.vaadin.util.ReflectTools;
import elemental.json.Json;
import elemental.json.JsonArray;
import elemental.json.JsonValue;
import elemental.json.impl.JsonUtil;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.junit.Assert;
import org.junit.Test;


public class ClientMethodSerializationTest {
    private static final Method JAVASCRIPT_CALLBACK_METHOD = ReflectTools.findMethod(JavaScriptCallbackRpc.class, "call", String.class, JsonArray.class);

    private static final Method BASIC_PARAMS_CALL_METHOD = ReflectTools.findMethod(ClientMethodSerializationTest.class, "basicParamsMethodForTesting", String.class, Integer.class);

    private static final Method NO_PARAMS_CALL_METHOD = ReflectTools.findMethod(ClientMethodSerializationTest.class, "noParamsMethodForTesting");

    /**
     * Tests the {@link ClientMethodInvocation} serialization when using
     * {@link JavaScriptCallbackHelper#invokeCallback(String, Object...)}.
     * #12532
     */
    @Test
    public void testClientMethodSerialization_WithJSONArray_ContentStaysSame() throws Exception {
        JsonArray originalArray = Json.createArray();
        originalArray.set(0, "callbackParameter1");
        originalArray.set(1, "callBackParameter2");
        originalArray.set(2, "12345");
        ClientMethodInvocation original = new ClientMethodInvocation(null, "interfaceName", ClientMethodSerializationTest.JAVASCRIPT_CALLBACK_METHOD, new Object[]{ "callBackMethodName", originalArray });
        ClientMethodInvocation copy = ((ClientMethodInvocation) (ClientMethodSerializationTest.serializeAndDeserialize(original)));
        JsonArray copyArray = ((JsonArray) (copy.getParameters()[1]));
        Assert.assertEquals(JsonUtil.stringify(originalArray), JsonUtil.stringify(copyArray));
    }

    @Test
    public void testClientMethodSerialization_WithBasicParams_NoChanges() throws Exception {
        String stringParam = "a string 123";
        Integer integerParam = 1234567890;
        ClientMethodInvocation original = new ClientMethodInvocation(null, "interfaceName", ClientMethodSerializationTest.BASIC_PARAMS_CALL_METHOD, new Serializable[]{ stringParam, integerParam });
        ClientMethodInvocation copy = ((ClientMethodInvocation) (ClientMethodSerializationTest.serializeAndDeserialize(original)));
        String copyString = ((String) (copy.getParameters()[0]));
        Integer copyInteger = ((Integer) (copy.getParameters()[1]));
        Assert.assertEquals(copyString, stringParam);
        Assert.assertEquals(copyInteger, integerParam);
    }

    @Test
    public void testClientMethodSerialization_NoParams_NoExceptions() {
        ClientMethodInvocation original = new ClientMethodInvocation(null, "interfaceName", ClientMethodSerializationTest.NO_PARAMS_CALL_METHOD, null);
        ClientMethodInvocation copy = ((ClientMethodInvocation) (ClientMethodSerializationTest.serializeAndDeserialize(original)));
    }

    @Test
    public void testSerializeTwice() {
        String name = "javascriptFunctionName";
        String[] arguments = new String[]{ "1", "2", "3" };
        JsonArray args = ((JsonArray) (JsonCodec.encode(arguments, null, Object[].class, null).getEncodedValue()));
        ClientConnector connector = null;
        ClientMethodInvocation original = new ClientMethodInvocation(connector, "interfaceName", ClientMethodSerializationTest.JAVASCRIPT_CALLBACK_METHOD, new Object[]{ name, args });
        ClientMethodInvocation copy = ((ClientMethodInvocation) (ClientMethodSerializationTest.serializeAndDeserialize(original)));
        Assert.assertEquals(copy.getMethodName(), original.getMethodName());
        Assert.assertEquals(copy.getParameters().length, original.getParameters().length);
        for (int i = 0; i < (copy.getParameters().length); i++) {
            Object originalParameter = original.getParameters()[i];
            Object copyParameter = copy.getParameters()[i];
            if (originalParameter instanceof JsonValue) {
                Assert.assertEquals(toJson(), toJson());
            } else {
                Assert.assertEquals(originalParameter, copyParameter);
            }
        }
    }
}

