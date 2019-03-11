package io.protostuff;


import io.protostuff.runtime.RuntimeSchema;
import org.junit.Test;


/**
 * Test for ignoring unknown fields during de-seralization
 */
public class JsonUnknownFieldTest {
    public static final String NORMAL_MESSAGE = "{" + (("\"field1\":42," + "\"field2\":\"testValue\"") + "}");

    public static final String UNKNOWN_SCALAR_FIELD = "{" + ((("\"field1\":42," + "\"unknownField\":42,") + "\"field2\":\"testValue\"") + "}");

    public static final String UNKNOWN_ARRAY_FIELD = "{" + ((((("\"field1\":42," + "\"unknownField\":[") + "{\"x\":1}, ") + "{\"x\":1, \"y\": [1,2,3]}],") + "\"field2\":\"testValue\"") + "}");

    public static final String UNKNOWN_EMPTY_MESSAGE_FIELD = "{" + ((("\"field1\":42," + "\"unknownField\":{},") + "\"field2\":\"testValue\"") + "}");

    public static final String UNKNOWN_NESTED_MESSAGE_FIELD = "{" + (((((((((((((("\"field1\":42," + "\"unknownField\":{") + "\"a\":0,") + "\"field1\":43,") + "\"anotherNestedField\":{") + "\"b\":0,") + "\"c\":[1, 2, 3],") + "\"thirdNestedField\":{") + "\"e\":1,") + "\"f\":\"foobar\"") + "}") + "}") + "},") + "\"field2\":\"testValue\"") + "}");

    public static final Schema<JsonUnknownFieldTest.TestMessage> SCHEMA = RuntimeSchema.getSchema(JsonUnknownFieldTest.TestMessage.class);

    private JsonUnknownFieldTest.TestMessage instance;

    @Test
    public void normalMessage() throws Exception {
        JsonIOUtil.mergeFrom(JsonUnknownFieldTest.NORMAL_MESSAGE.getBytes(), instance, JsonUnknownFieldTest.SCHEMA, false);
        checkKnownFields(instance);
    }

    @Test
    public void unknownScalarField() throws Exception {
        JsonIOUtil.mergeFrom(JsonUnknownFieldTest.UNKNOWN_SCALAR_FIELD.getBytes(), instance, JsonUnknownFieldTest.SCHEMA, false);
        checkKnownFields(instance);
    }

    @Test
    public void unknownArrayField() throws Exception {
        JsonIOUtil.mergeFrom(JsonUnknownFieldTest.UNKNOWN_ARRAY_FIELD.getBytes(), instance, JsonUnknownFieldTest.SCHEMA, false);
        checkKnownFields(instance);
    }

    @Test
    public void unknownEmptyMessageField() throws Exception {
        JsonIOUtil.mergeFrom(JsonUnknownFieldTest.UNKNOWN_EMPTY_MESSAGE_FIELD.getBytes(), instance, JsonUnknownFieldTest.SCHEMA, false);
        checkKnownFields(instance);
    }

    @Test
    public void unknownNestedMessageField() throws Exception {
        JsonIOUtil.mergeFrom(JsonUnknownFieldTest.UNKNOWN_NESTED_MESSAGE_FIELD.getBytes(), instance, JsonUnknownFieldTest.SCHEMA, false);
        checkKnownFields(instance);
    }

    static class TestMessage {
        public int field1;

        public String field2;
    }
}

