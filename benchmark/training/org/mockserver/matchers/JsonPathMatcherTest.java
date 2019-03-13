package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;


/**
 *
 *
 * @author jamesdbloom
 */
public class JsonPathMatcherTest {
    @Test
    public void shouldMatchMatchingJsonPath() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchMatchingJsonPathWithNot() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches(null, "some_value"));
        Assert.assertFalse(matches(null, "some_other_value"));
    }

    @Test
    public void shouldNotMatchNullExpectation() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchEmptyExpectation() {
        Assert.assertFalse(matches(null, "some_value"));
    }

    @Test
    public void shouldNotMatchNotMatchingJsonPath() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
        Assert.assertFalse(matches(null, matched));
    }

    @Test
    public void shouldMatchNotMatchingJsonPathWithNot() {
        String matched = "" + ((((((((((((((((((((((("{\n" + "    \"store\": {\n") + "        \"book\": [\n") + "            {\n") + "                \"category\": \"reference\",\n") + "                \"author\": \"Nigel Rees\",\n") + "                \"title\": \"Sayings of the Century\",\n") + "                \"price\": 8.95\n") + "            },\n") + "            {\n") + "                \"category\": \"fiction\",\n") + "                \"author\": \"Herman Melville\",\n") + "                \"title\": \"Moby Dick\",\n") + "                \"isbn\": \"0-553-21311-3\",\n") + "                \"price\": 8.99\n") + "            }\n") + "        ],\n") + "        \"bicycle\": {\n") + "            \"color\": \"red\",\n") + "            \"price\": 19.95\n") + "        }\n") + "    },\n") + "    \"expensive\": 10\n") + "}");
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
        Assert.assertTrue(matches(null, matched));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new JsonPathMatcher(new MockServerLogger(), "some_value").matches(null, null));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(null, ""));
    }

    @Test
    public void showHaveCorrectEqualsBehaviour() {
        MockServerLogger mockServerLogger = new MockServerLogger();
        Assert.assertEquals(new JsonPathMatcher(mockServerLogger, "some_value"), new JsonPathMatcher(mockServerLogger, "some_value"));
    }
}

