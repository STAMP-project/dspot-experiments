package com.fasterxml.jackson.core.json.async;


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
public class AsyncMissingValuesInObjectTest extends AsyncTestBase {
    private JsonFactory factory;

    private HashSet<JsonReadFeature> features;

    public AsyncMissingValuesInObjectTest(Collection<JsonReadFeature> features) {
        this.features = new HashSet<JsonReadFeature>(features);
        JsonFactoryBuilder b = builder();
        for (JsonReadFeature feature : features) {
            b = b.enable(feature);
        }
        this.factory = b.build();
    }

    @Test
    public void testObjectBasic() throws Exception {
        String json = "{\"a\": true, \"b\": false}";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        TestCase.assertEquals(JsonToken.END_OBJECT, p.nextToken());
        assertEnd(p);
        p.close();
    }

    @Test
    public void testObjectInnerComma() throws Exception {
        String json = "{\"a\": true,, \"b\": false}";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }

    @Test
    public void testObjectLeadingComma() throws Exception {
        String json = "{,\"a\": true, \"b\": false}";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }

    @Test
    public void testObjectTrailingComma() throws Exception {
        String json = "{\"a\": true, \"b\": false,}";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
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
    public void testObjectTrailingCommas() throws Exception {
        String json = "{\"a\": true, \"b\": false,,}";
        AsyncReaderWrapper p = createParser(factory, json);
        TestCase.assertEquals(JsonToken.START_OBJECT, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("a", p.currentText());
        assertToken(JsonToken.VALUE_TRUE, p.nextToken());
        assertToken(JsonToken.FIELD_NAME, p.nextToken());
        TestCase.assertEquals("b", p.currentText());
        assertToken(JsonToken.VALUE_FALSE, p.nextToken());
        assertUnexpected(p, ',');
        p.close();
    }
}

