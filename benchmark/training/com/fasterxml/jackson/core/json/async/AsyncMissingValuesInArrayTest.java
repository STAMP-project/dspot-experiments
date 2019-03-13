package com.fasterxml.jackson.core.json.async;


import JsonReadFeature.ALLOW_MISSING_VALUES;
import JsonReadFeature.ALLOW_TRAILING_COMMA;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.async.AsyncTestBase;
import com.fasterxml.jackson.core.json.JsonFactoryBuilder;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.core.testsupport.AsyncReaderWrapper;
import java.util.Collection;
import java.util.HashSet;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class AsyncMissingValuesInArrayTest extends AsyncTestBase {
    private final JsonFactory factory;

    private final HashSet<JsonReadFeature> features;

    public AsyncMissingValuesInArrayTest(Collection<JsonReadFeature> features) {
        this.features = new HashSet<JsonReadFeature>(features);
        JsonFactoryBuilder b = builder();
        for (JsonReadFeature feature : features) {
            b = b.enable(feature);
        }
        factory = b.build();
    }

    // Could test, but this case is covered by other tests anyway
    /* @Test
    public void testArrayBasic() throws Exception {
    String json = "[\"a\", \"b\"]";

    AsyncReaderWrapper p = createParser(factory, json);

    assertEquals(JsonToken.START_ARRAY, p.nextToken());

    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals("a", p.currentText());

    assertToken(JsonToken.VALUE_STRING, p.nextToken());
    assertEquals("b", p.currentText());

    assertEquals(JsonToken.END_ARRAY, p.nextToken());
    assertEnd(p);
    }
     */
    @Test
    public void testArrayInnerComma() throws Exception {
        String json = "[\"a\",, \"b\"]";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        if (!(features.contains(ALLOW_MISSING_VALUES))) {
            assertUnexpected(p, ',');
            return;
        }
        assertToken(JsonToken.VALUE_NULL, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
        TestCase.assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEnd(p);
    }

    @Test
    public void testArrayLeadingComma() throws Exception {
        String json = "[,\"a\", \"b\"]";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        if (!(features.contains(ALLOW_MISSING_VALUES))) {
            assertUnexpected(p, ',');
            return;
        }
        assertToken(JsonToken.VALUE_NULL, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
        TestCase.assertEquals(JsonToken.END_ARRAY, p.nextToken());
        assertEnd(p);
        p.close();
    }

    @Test
    public void testArrayTrailingComma() throws Exception {
        String json = "[\"a\", \"b\",]";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
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
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
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
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_ARRAY, p.nextToken());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_STRING, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
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
}

