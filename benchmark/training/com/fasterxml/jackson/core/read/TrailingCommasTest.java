package com.fasterxml.jackson.core.read;


import JsonReadFeature.ALLOW_MISSING_VALUES;
import JsonReadFeature.ALLOW_TRAILING_COMMA;
import com.fasterxml.jackson.core.BaseTest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.json.JsonFactoryBuilder;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
@SuppressWarnings("resource")
public class TrailingCommasTest extends BaseTest {
    private final JsonFactory factory;

    private final Set<JsonReadFeature> features;

    private final int mode;

    public TrailingCommasTest(int mode, List<JsonReadFeature> features) {
        this.features = new HashSet<JsonReadFeature>(features);
        JsonFactoryBuilder b = builder();
        for (JsonReadFeature feature : features) {
            b = b.enable(feature);
        }
        this.factory = b.build();
        this.mode = mode;
    }

    @Test
    public void testArrayBasic() throws Exception {
        String json = "[\"a\", \"b\"]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        TestCase.assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEnd(p);
    }

    @Test
    public void testArrayInnerComma() throws Exception {
        String json = "[\"a\",, \"b\"]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        if (!(features.contains(ALLOW_MISSING_VALUES))) {
            assertUnexpected(p, ',');
            return;
        }
        assertToken(JsonToken.VALUE_NULL, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        TestCase.assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEnd(p);
    }

    @Test
    public void testArrayLeadingComma() throws Exception {
        String json = "[,\"a\", \"b\"]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        if (!(features.contains(ALLOW_MISSING_VALUES))) {
            assertUnexpected(p, ',');
            return;
        }
        assertToken(JsonToken.VALUE_NULL, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        TestCase.assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEnd(p);
        p.close();
    }

    @Test
    public void testArrayTrailingComma() throws Exception {
        String json = "[\"a\", \"b\",]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        // ALLOW_TRAILING_COMMA takes priority over ALLOW_MISSING_VALUES
        if (features.contains(ALLOW_TRAILING_COMMA)) {
            assertToken(JsonToken.END_ARRAY, p.nextToken());
            assertEnd(p);
        } else
            if (features.contains(ALLOW_MISSING_VALUES)) {
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.END_ARRAY, p.nextToken());
                assertEnd(p);
            } else {
                assertUnexpected(p, ']');
            }

        p.close();
    }

    @Test
    public void testArrayTrailingCommas() throws Exception {
        String json = "[\"a\", \"b\",,]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        // ALLOW_TRAILING_COMMA takes priority over ALLOW_MISSING_VALUES
        if ((features.contains(ALLOW_MISSING_VALUES)) && (features.contains(ALLOW_TRAILING_COMMA))) {
            assertToken(JsonToken.VALUE_NULL, p.nextToken());
            assertToken(JsonToken.END_ARRAY, p.nextToken());
            assertEnd(p);
        } else
            if (features.contains(ALLOW_MISSING_VALUES)) {
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.END_ARRAY, p.nextToken());
                assertEnd(p);
            } else {
                assertUnexpected(p, ',');
            }

        p.close();
    }

    @Test
    public void testArrayTrailingCommasTriple() throws Exception {
        String json = "[\"a\", \"b\",,,]";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        // ALLOW_TRAILING_COMMA takes priority over ALLOW_MISSING_VALUES
        if ((features.contains(ALLOW_MISSING_VALUES)) && (features.contains(ALLOW_TRAILING_COMMA))) {
            assertToken(JsonToken.VALUE_NULL, p.nextToken());
            assertToken(JsonToken.VALUE_NULL, p.nextToken());
            assertToken(JsonToken.END_ARRAY, p.nextToken());
            assertEnd(p);
        } else
            if (features.contains(ALLOW_MISSING_VALUES)) {
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.VALUE_NULL, p.nextToken());
                assertToken(JsonToken.END_ARRAY, p.nextToken());
                assertEnd(p);
            } else {
                assertUnexpected(p, ',');
            }

        p.close();
    }

    @Test
    public void testObjectBasic() throws Exception {
        String json = "{\"a\": true, \"b\": false}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        TestCase.assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEnd(p);
        p.close();
    }

    @Test
    public void testObjectInnerComma() throws Exception {
        String json = "{\"a\": true,, \"b\": false}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }

    @Test
    public void testObjectLeadingComma() throws Exception {
        String json = "{,\"a\": true, \"b\": false}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }

    @Test
    public void testObjectTrailingComma() throws Exception {
        String json = "{\"a\": true, \"b\": false,}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        if (features.contains(ALLOW_TRAILING_COMMA)) {
            assertToken(JsonToken.END_OBJECT, p.nextToken());
            assertEnd(p);
        } else {
            assertUnexpected(p, '}');
        }
        p.close();
    }

    @Test
    public void testObjectTrailingCommaWithNextFieldName() throws Exception {
        String json = "{\"a\": true, \"b\": false,}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        TestCase.assertEquals("a", p.nextFieldName());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        TestCase.assertEquals("b", p.nextFieldName());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        if (features.contains(ALLOW_TRAILING_COMMA)) {
            TestCase.assertEquals(null, p.nextFieldName());
            assertToken(JsonToken.END_OBJECT, p.currentToken());
            assertEnd(p);
        } else {
            try {
                p.nextFieldName();
                TestCase.fail("No exception thrown");
            } catch (Exception e) {
                verifyException(e, "Unexpected character ('}' (code 125))");
            }
        }
        p.close();
    }

    @Test
    public void testObjectTrailingCommaWithNextFieldNameStr() throws Exception {
        String json = "{\"a\": true, \"b\": false,}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        TestCase.assertTrue(p.nextFieldName(new SerializedString("a")));
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        TestCase.assertTrue(p.nextFieldName(new SerializedString("b")));
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        if (features.contains(ALLOW_TRAILING_COMMA)) {
            TestCase.assertFalse(p.nextFieldName(new SerializedString("c")));
            assertToken(JsonToken.END_OBJECT, p.currentToken());
            assertEnd(p);
        } else {
            try {
                p.nextFieldName(new SerializedString("c"));
                TestCase.fail("No exception thrown");
            } catch (Exception e) {
                verifyException(e, "Unexpected character ('}' (code 125))");
            }
        }
        p.close();
    }

    @Test
    public void testObjectTrailingCommas() throws Exception {
        String json = "{\"a\": true, \"b\": false,,}";
        JsonParser p = createParser(factory, mode, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.getText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.getText());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }
}

