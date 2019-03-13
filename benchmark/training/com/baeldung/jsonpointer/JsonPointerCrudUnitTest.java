package com.baeldung.jsonpointer;


import org.junit.Assert;
import org.junit.Test;


public class JsonPointerCrudUnitTest {
    @Test
    public void testJsonPointerCrudForAddress() {
        JsonPointerCrud jsonPointerCrud = new JsonPointerCrud(JsonPointerCrudUnitTest.class.getResourceAsStream("/address.json"));
        Assert.assertFalse(jsonPointerCrud.check("city"));
        // insert a value
        jsonPointerCrud.insert("city", "Rio de Janeiro");
        Assert.assertTrue(jsonPointerCrud.check("city"));
        // fetch full json
        String fullJSON = jsonPointerCrud.fetchFullJSON();
        Assert.assertTrue(fullJSON.contains("name"));
        Assert.assertTrue(fullJSON.contains("city"));
        // fetch value
        String cityName = jsonPointerCrud.fetchValueFromKey("city");
        Assert.assertEquals(cityName, "Rio de Janeiro");
        // update value
        jsonPointerCrud.update("city", "Sao Paulo");
        // fetch value
        cityName = jsonPointerCrud.fetchValueFromKey("city");
        Assert.assertEquals(cityName, "Sao Paulo");
        // delete
        jsonPointerCrud.delete("city");
        Assert.assertFalse(jsonPointerCrud.check("city"));
    }

    @Test
    public void testJsonPointerCrudForBooks() {
        JsonPointerCrud jsonPointerCrud = new JsonPointerCrud(JsonPointerCrudUnitTest.class.getResourceAsStream("/books.json"));
        // fetch value
        String book = jsonPointerCrud.fetchListValues("books/1");
        Assert.assertEquals(book, "{\"title\":\"Title 2\",\"author\":\"John Doe\"}");
    }
}

