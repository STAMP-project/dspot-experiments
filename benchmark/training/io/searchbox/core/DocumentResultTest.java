package io.searchbox.core;


import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Bartosz Polnik
 */
public class DocumentResultTest {
    static DocumentResult validResult = new DocumentResult(new Gson());

    static DocumentResult invalidResult = new DocumentResult(new Gson());

    static String validResponse = "{\n" + (((("    \"_index\": \"testIndex\",\n" + "    \"_type\": \"testType\",\n") + "    \"_id\": \"testId\",\n") + "    \"_version\": 2\n") + "}");

    static String invalidResponse = "{\n" + (("    \"error\": \"NullPointerException[null]\",\n" + "    \"status\": 500\n") + "}");

    @Test
    public void shouldFetchIndexFromValidResponse() {
        Assert.assertEquals("testIndex", DocumentResultTest.validResult.getIndex());
    }

    @Test
    public void shouldFetchTypeFromValidResponse() {
        Assert.assertEquals("testType", DocumentResultTest.validResult.getType());
    }

    @Test
    public void shouldFetchIdFromValidResponse() {
        Assert.assertEquals("testId", DocumentResultTest.validResult.getId());
    }

    @Test
    public void shouldFetchVersionFromValidResponse() {
        Assert.assertEquals(Long.valueOf(2), DocumentResultTest.validResult.getVersion());
    }

    @Test
    public void shouldReturnNullIndexOnInvalidResponse() {
        Assert.assertNull(DocumentResultTest.invalidResult.getIndex());
    }
}

