package org.baeldung.gson.primitives;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import junit.framework.TestCase;
import org.junit.Test;


// @formatter:on
public class PrimitiveValuesUnitTest {
    @Test
    public void whenSerializingToJSON_thenShouldCreateJSON() {
        PrimitiveBundle primitiveBundle = new PrimitiveBundle();
        // @formatter:off
        primitiveBundle.byteValue = ((byte) (4369));
        primitiveBundle.shortValue = ((short) (3));
        primitiveBundle.intValue = 3;
        primitiveBundle.longValue = 3;
        primitiveBundle.floatValue = 3.5F;
        primitiveBundle.doubleValue = 3.5;
        primitiveBundle.booleanValue = true;
        primitiveBundle.charValue = 'a';
        // @formatter:on
        Gson gson = new Gson();
        String expected = "{\"byteValue\":17,\"shortValue\":3,\"intValue\":3," + (("\"longValue\":3,\"floatValue\":3.5" + ",\"doubleValue\":3.5") + ",\"booleanValue\":true,\"charValue\":\"a\"}");
        TestCase.assertEquals(expected, gson.toJson(primitiveBundle));
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSerializingInfinity_thenShouldRaiseAnException() {
        InfinityValuesExample model = new InfinityValuesExample();
        model.negativeInfinity = Float.NEGATIVE_INFINITY;
        model.positiveInfinity = Float.POSITIVE_INFINITY;
        Gson gson = new Gson();
        gson.toJson(model);
    }

    @Test(expected = IllegalArgumentException.class)
    public void whenSerializingNaN_thenShouldRaiseAnException() {
        FloatExample model = new FloatExample();
        model.value = Float.NaN;
        Gson gson = new Gson();
        gson.toJson(model);
    }

    @Test
    public void whenDeserializingFromJSON_thenShouldParseTheValueInTheString() {
        String json = "{\"byteValue\": 17, \"shortValue\": 3, \"intValue\": 3, " + (("\"longValue\": 3, \"floatValue\": 3.5" + ", \"doubleValue\": 3.5") + ", \"booleanValue\": true, \"charValue\": \"a\"}");
        Gson gson = new Gson();
        PrimitiveBundle model = gson.fromJson(json, PrimitiveBundle.class);
        // @formatter:off
        TestCase.assertEquals(17, model.byteValue);
        TestCase.assertEquals(3, model.shortValue);
        TestCase.assertEquals(3, model.intValue);
        TestCase.assertEquals(3, model.longValue);
        TestCase.assertEquals(3.5, model.floatValue, 1.0E-4);
        TestCase.assertEquals(3.5, model.doubleValue, 1.0E-4);
        TestCase.assertTrue(model.booleanValue);
        TestCase.assertEquals('a', model.charValue);
        // @formatter:on
    }

    @Test
    public void whenDeserializingHighPrecissionNumberIntoFloat_thenShouldPerformRounding() {
        String json = "{\"value\": 12.123425589123456}";
        Gson gson = new Gson();
        FloatExample model = gson.fromJson(json, FloatExample.class);
        TestCase.assertEquals(12.123426F, model.value, 1.0E-6);
    }

    @Test
    public void whenDeserializingHighPrecissiongNumberIntoDouble_thenShouldPerformRounding() {
        String json = "{\"value\": 12.123425589123556}";
        Gson gson = new Gson();
        DoubleExample model = gson.fromJson(json, DoubleExample.class);
        TestCase.assertEquals(12.1234255F, model.value, 1.0E-6);
    }

    @Test
    public void whenDeserializingValueThatOverflows_thenShouldOverflowSilently() {
        Gson gson = new Gson();
        String json = "{\"value\": \"300\"}";
        ByteExample model = gson.fromJson(json, ByteExample.class);
        TestCase.assertEquals(44, model.value);
    }

    @Test
    public void whenDeserializingRealIntoByte_thenShouldRaiseAnException() {
        Gson gson = new Gson();
        String json = "{\"value\": 2.3}";
        try {
            gson.fromJson(json, ByteExample.class);
        } catch (Exception ex) {
            TestCase.assertTrue((ex instanceof JsonSyntaxException));
            TestCase.assertTrue(((ex.getCause()) instanceof NumberFormatException));
            return;
        }
        TestCase.fail();
    }

    @Test
    public void whenDeserializingRealIntoLong_thenShouldRaiseAnException() {
        Gson gson = new Gson();
        String json = "{\"value\": 2.3}";
        try {
            gson.fromJson(json, LongExample.class);
        } catch (Exception ex) {
            TestCase.assertTrue((ex instanceof JsonSyntaxException));
            TestCase.assertTrue(((ex.getCause()) instanceof NumberFormatException));
            return;
        }
        TestCase.fail();
    }

    @Test
    public void whenDeserializingRealWhoseDecimalPartIs0_thenShouldParseItCorrectly() {
        Gson gson = new Gson();
        String json = "{\"value\": 2.0}";
        LongExample model = gson.fromJson(json, LongExample.class);
        TestCase.assertEquals(2, model.value);
    }

    @Test
    public void whenDeserializingUnicodeChar_thenShouldParseItCorrectly() {
        Gson gson = new Gson();
        String json = "{\"value\": \"\\u00AE\"}";
        CharExample model = gson.fromJson(json, CharExample.class);
        TestCase.assertEquals('\u00ae', model.value);
    }

    @Test
    public void whenDeserializingNullValues_thenShouldIgnoreThoseFields() {
        Gson gson = new Gson();
        // @formatter:off
        String json = "{\"byteValue\": null, \"shortValue\": null, " + (("\"intValue\": null, " + "\"longValue\": null, \"floatValue\": null") + ", \"doubleValue\": null}");
        // @formatter:on
        PrimitiveBundleInitialized model = gson.fromJson(json, PrimitiveBundleInitialized.class);
        TestCase.assertEquals(1, model.byteValue);
        TestCase.assertEquals(1, model.shortValue);
        TestCase.assertEquals(1, model.intValue);
        TestCase.assertEquals(1, model.longValue);
        TestCase.assertEquals(1, model.floatValue, 1.0E-4);
        TestCase.assertEquals(1, model.doubleValue, 1.0E-4);
    }

    @Test(expected = JsonSyntaxException.class)
    public void whenDeserializingTheEmptyString_thenShouldRaiseAnException() {
        Gson gson = new Gson();
        // @formatter:off
        String json = "{\"byteValue\": \"\", \"shortValue\": \"\", " + ((("\"intValue\": \"\", " + "\"longValue\": \"\", \"floatValue\": \"\"") + ", \"doubleValue\": \"\"") + ", \"booleanValue\": \"\"}");
        // @formatter:on
        gson.fromJson(json, PrimitiveBundleInitialized.class);
    }

    @Test
    public void whenDeserializingTheEmptyStringIntoChar_thenShouldHaveTheEmtpyChar() {
        Gson gson = new Gson();
        // @formatter:off
        String json = "{\"charValue\": \"\"}";
        // @formatter:on
        CharExample model = gson.fromJson(json, CharExample.class);
        TestCase.assertEquals(Character.MIN_VALUE, model.value);
    }

    @Test
    public void whenDeserializingValidValueAppearingInAString_thenShouldParseTheValue() {
        Gson gson = new Gson();
        // @formatter:off
        String json = "{\"byteValue\": \"15\", \"shortValue\": \"15\", " + (("\"intValue\": \"15\", " + "\"longValue\": \"15\", \"floatValue\": \"15.0\"") + ", \"doubleValue\": \"15.0\"}");
        // @formatter:on
        PrimitiveBundleInitialized model = gson.fromJson(json, PrimitiveBundleInitialized.class);
        TestCase.assertEquals(15, model.byteValue);
        TestCase.assertEquals(15, model.shortValue);
        TestCase.assertEquals(15, model.intValue);
        TestCase.assertEquals(15, model.longValue);
        TestCase.assertEquals(15, model.floatValue, 1.0E-4);
        TestCase.assertEquals(15, model.doubleValue, 1.0E-4);
    }

    @Test
    public void whenDeserializingABooleanFrom0Or1Integer_thenShouldRaiseAnException() {
        String json = "{\"value\": 1}";
        Gson gson = new Gson();
        try {
            gson.fromJson(json, BooleanExample.class);
        } catch (Exception ex) {
            TestCase.assertTrue((ex instanceof JsonSyntaxException));
            TestCase.assertTrue(((ex.getCause()) instanceof IllegalStateException));
            return;
        }
        TestCase.fail();
    }

    @Test
    public void whenDeserializingWithCustomDeserializerABooleanFrom0Or1Integer_thenShouldWork() {
        String json = "{\"value\": 1}";
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(BooleanExample.class, new PrimitiveValuesUnitTest.BooleanAs2ValueIntegerDeserializer());
        Gson gson = builder.create();
        BooleanExample model = gson.fromJson(json, BooleanExample.class);
        TestCase.assertTrue(model.value);
    }

    // @formatter:off
    static class BooleanAs2ValueIntegerDeserializer implements JsonDeserializer<BooleanExample> {
        @Override
        public BooleanExample deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
            BooleanExample model = new BooleanExample();
            int value = jsonElement.getAsJsonObject().getAsJsonPrimitive("value").getAsInt();
            if (value == 0) {
                model.value = false;
            } else
                if (value == 1) {
                    model.value = true;
                } else {
                    throw new JsonParseException(("Unexpected value. Trying to deserialize " + "a boolean from an integer different than 0 and 1."));
                }

            return model;
        }
    }
}

