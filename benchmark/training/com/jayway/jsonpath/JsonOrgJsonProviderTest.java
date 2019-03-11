package com.jayway.jsonpath;


import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;


public class JsonOrgJsonProviderTest extends BaseTest {
    @Test
    public void an_object_can_be_read() {
        JSONObject book = JsonPath.using(BaseTest.JSON_ORG_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.store.book[0]");
        assertThat(book.get("author").toString()).isEqualTo("Nigel Rees");
    }

    @Test
    public void a_property_can_be_read() {
        String category = JsonPath.using(BaseTest.JSON_ORG_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.store.book[0].category");
        assertThat(category).isEqualTo("reference");
    }

    @Test
    public void a_filter_can_be_applied() {
        JSONArray fictionBooks = JsonPath.using(BaseTest.JSON_ORG_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.store.book[?(@.category == 'fiction')]");
        assertThat(fictionBooks.length()).isEqualTo(3);
    }

    @Test
    public void result_can_be_mapped_to_object() {
        List<Map<String, Object>> books = JsonPath.using(BaseTest.JSON_ORG_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$.store.book", List.class);
        assertThat(books.size()).isEqualTo(4);
    }

    @Test
    public void read_books_with_isbn() {
        JSONArray books = JsonPath.using(BaseTest.JSON_ORG_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT).read("$..book[?(@.isbn)]");
        assertThat(books.length()).isEqualTo(2);
    }
}

