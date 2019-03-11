package com.jayway.jsonpath.old;


import Option.REQUIRE_PROPERTIES;
import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.InvalidPathException;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.jayway.jsonpath.internal.path.PathCompiler;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class JsonPathTest extends BaseTest {
    static {
        // JsonProviderFactory.setDefaultProvider(JacksonProvider.class);
    }

    public static final String ARRAY = "[{\"value\": 1},{\"value\": 2}, {\"value\": 3},{\"value\": 4}]";

    public static final String DOCUMENT = "{ \"store\": {\n" + (((((((((((((((((((((((((((((((("    \"book\": [ \n" + "      { \"category\": \"reference\",\n") + "        \"author\": \"Nigel Rees\",\n") + "        \"title\": \"Sayings of the Century\",\n") + "        \"display-price\": 8.95\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Evelyn Waugh\",\n") + "        \"title\": \"Sword of Honour\",\n") + "        \"display-price\": 12.99\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"Herman Melville\",\n") + "        \"title\": \"Moby Dick\",\n") + "        \"isbn\": \"0-553-21311-3\",\n") + "        \"display-price\": 8.99\n") + "      },\n") + "      { \"category\": \"fiction\",\n") + "        \"author\": \"J. R. R. Tolkien\",\n") + "        \"title\": \"The Lord of the Rings\",\n") + "        \"isbn\": \"0-395-19395-8\",\n") + "        \"display-price\": 22.99\n") + "      }\n") + "    ],\n") + "    \"bicycle\": {\n") + "      \"color\": \"red\",\n") + "      \"display-price\": 19.95,\n") + "      \"foo:bar\": \"fooBar\",\n") + "      \"dot.notation\": \"new\",\n") + "      \"dash-notation\": \"dashes\"\n") + "    }\n") + "  }\n") + "}");

    public static final Object OBJ_DOCUMENT = JsonPath.parse(JsonPathTest.DOCUMENT).json();

    private static final String PRODUCT_JSON = "{\n" + (((((((((("\t\"product\": [ {\n" + "\t    \"version\": \"A\", \n") + "\t    \"codename\": \"Seattle\", \n") + "\t    \"attr.with.dot\": \"A\"\n") + "\t},\n") + "\t{\n") + "\t    \"version\": \"4.0\", \n") + "\t    \"codename\": \"Montreal\", \n") + "\t    \"attr.with.dot\": \"B\"\n") + "\t}]\n") + "}");

    private static final String ARRAY_EXPAND = "[{\"parent\": \"ONE\", \"child\": {\"name\": \"NAME_ONE\"}}, [{\"parent\": \"TWO\", \"child\": {\"name\": \"NAME_TWO\"}}]]";

    @Test(expected = PathNotFoundException.class)
    public void missing_prop() {
        // Object read = JsonPath.using(Configuration.defaultConfiguration().setOptions(Option.THROW_ON_MISSING_PROPERTY)).parse(DOCUMENT).read("$.store.book[*].fooBar");
        // Object read = JsonPath.using(Configuration.defaultConfiguration()).parse(DOCUMENT).read("$.store.book[*].fooBar");
        Object read2 = JsonPath.using(Configuration.defaultConfiguration().addOptions(REQUIRE_PROPERTIES)).parse(JsonPathTest.DOCUMENT).read("$.store.book[*].fooBar.not");
    }

    @Test
    public void bracket_notation_with_dots() {
        String json = "{\n" + (((((((((("    \"store\": {\n" + "        \"book\": [\n") + "            {\n") + "                \"author.name\": \"Nigel Rees\", \n") + "                \"category\": \"reference\", \n") + "                \"price\": 8.95, \n") + "                \"title\": \"Sayings of the Century\"\n") + "            }\n") + "        ]\n") + "    }\n") + "}");
        Assert.assertEquals("Nigel Rees", JsonPath.read(json, "$.store.book[0]['author.name']"));
    }

    @Test
    public void null_object_in_path() {
        String json = "{\n" + (((((((((((((("  \"success\": true,\n" + "  \"data\": {\n") + "    \"user\": 3,\n") + "    \"own\": null,\n") + "    \"passes\": null,\n") + "    \"completed\": null\n") + "  },\n") + "  \"data2\": {\n") + "    \"user\": 3,\n") + "    \"own\": null,\n") + "    \"passes\": [{\"id\":\"1\"}],\n") + "    \"completed\": null\n") + "  },\n") + "  \"version\": 1371160528774\n") + "}");
        try {
            JsonPath.read(json, "$.data.passes[0].id");
            Assertions.fail("Expected PathNotFoundException");
        } catch (PathNotFoundException e) {
        }
        Assertions.assertThat(((String) (JsonPath.read(json, "$.data2.passes[0].id")))).isEqualTo("1");
    }

    @Test
    public void array_start_expands() throws Exception {
        // assertThat(JsonPath.<List<String>>read(ARRAY_EXPAND, "$[?(@.parent = 'ONE')].child.name"), hasItems("NAME_ONE"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.ARRAY_EXPAND, "$[?(@['parent'] == 'ONE')].child.name"), Matchers.hasItems("NAME_ONE"));
    }

    @Test
    public void bracket_notation_can_be_used_in_path() throws Exception {
        Assert.assertEquals("new", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store'].bicycle.['dot.notation']"));
        Assert.assertEquals("new", JsonPath.read(JsonPathTest.DOCUMENT, "$['store']['bicycle']['dot.notation']"));
        Assert.assertEquals("new", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store']['bicycle']['dot.notation']"));
        Assert.assertEquals("new", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store'].['bicycle'].['dot.notation']"));
        Assert.assertEquals("dashes", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store'].bicycle.['dash-notation']"));
        Assert.assertEquals("dashes", JsonPath.read(JsonPathTest.DOCUMENT, "$['store']['bicycle']['dash-notation']"));
        Assert.assertEquals("dashes", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store']['bicycle']['dash-notation']"));
        Assert.assertEquals("dashes", JsonPath.read(JsonPathTest.DOCUMENT, "$.['store'].['bicycle'].['dash-notation']"));
    }

    @Test
    public void filter_an_array() throws Exception {
        List<Object> matches = JsonPath.read(JsonPathTest.ARRAY, "$.[?(@.value == 1)]");
        Assert.assertEquals(1, matches.size());
    }

    @Test
    public void filter_an_array_on_index() throws Exception {
        Integer matches = JsonPath.read(JsonPathTest.ARRAY, "$.[1].value");
        Assert.assertEquals(new Integer(2), matches);
    }

    @Test
    public void read_path_with_colon() throws Exception {
        Assert.assertEquals(JsonPath.read(JsonPathTest.DOCUMENT, "$['store']['bicycle']['foo:bar']"), "fooBar");
    }

    @Test
    public void read_document_from_root() throws Exception {
        Map result = JsonPath.read(JsonPathTest.DOCUMENT, "$.store");
        Assert.assertEquals(2, result.values().size());
    }

    @Test
    public void read_store_book_1() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[1]");
        Map map = path.read(JsonPathTest.DOCUMENT);
        Assert.assertEquals("Evelyn Waugh", map.get("author"));
    }

    @Test
    public void read_store_book_wildcard() throws Exception {
        JsonPath path = JsonPath.compile("$.store.book[*]");
        List<Object> list = path.read(JsonPathTest.DOCUMENT);
        Assertions.assertThat(list.size()).isEqualTo(4);
    }

    @Test
    public void read_store_book_author() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[0,1].author"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[*].author"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.['store'].['book'][*].['author']"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$['store']['book'][*]['author']"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$['store'].book[*]['author']"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_authors() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..author"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void all_store_properties() throws Exception {
        /* List<Object> itemsInStore = JsonPath.read(DOCUMENT, "$.store.*");

        assertEquals(JsonPath.read(itemsInStore, "$.[0].[0].author"), "Nigel Rees");
        assertEquals(JsonPath.read(itemsInStore, "$.[0][0].author"), "Nigel Rees");
         */
        List<String> result = PathCompiler.compile("$.store.*").evaluate(JsonPathTest.OBJ_DOCUMENT, JsonPathTest.OBJ_DOCUMENT, Configuration.defaultConfiguration()).getPathList();
        Assertions.assertThat(result).containsOnly("$['store']['bicycle']", "$['store']['book']");
    }

    @Test
    public void all_prices_in_store() throws Exception {
        Assert.assertThat(JsonPath.<List<Double>>read(JsonPathTest.DOCUMENT, "$.store..['display-price']"), Matchers.hasItems(8.95, 12.99, 8.99, 19.95));
    }

    @Test
    public void access_array_by_index_from_tail() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..book[1:].author"), Matchers.hasItems("Evelyn Waugh", "Herman Melville", "J. R. R. Tolkien"));
    }

    @Test
    public void read_store_book_index_0_and_1() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[0,1].author"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh"));
        Assert.assertTrue(((JsonPath.<List>read(JsonPathTest.DOCUMENT, "$.store.book[0,1].author").size()) == 2));
    }

    @Test
    public void read_store_book_pull_first_2() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[:2].author"), Matchers.hasItems("Nigel Rees", "Evelyn Waugh"));
        Assert.assertTrue(((JsonPath.<List>read(JsonPathTest.DOCUMENT, "$.store.book[:2].author").size()) == 2));
    }

    @Test
    public void read_store_book_filter_by_isbn() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[?(@.isbn)].isbn"), Matchers.hasItems("0-553-21311-3", "0-395-19395-8"));
        Assert.assertTrue(((JsonPath.<List>read(JsonPathTest.DOCUMENT, "$.store.book[?(@.isbn)].isbn").size()) == 2));
        Assert.assertTrue(((JsonPath.<List>read(JsonPathTest.DOCUMENT, "$.store.book[?(@['isbn'])].isbn").size()) == 2));
    }

    @Test
    public void all_books_cheaper_than_10() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..book[?(@['display-price'] < 10)].title"), Matchers.hasItems("Sayings of the Century", "Moby Dick"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..book[?(@.display-price < 10)].title"), Matchers.hasItems("Sayings of the Century", "Moby Dick"));
    }

    @Test
    public void all_books() throws Exception {
        Assertions.assertThat(JsonPath.<List<Object>>read(JsonPathTest.DOCUMENT, "$..book")).hasSize(1);
    }

    @Test
    public void dot_in_predicate_works() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.PRODUCT_JSON, "$.product[?(@.version=='4.0')].codename"), Matchers.hasItems("Montreal"));
    }

    @Test
    public void dots_in_predicate_works() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.PRODUCT_JSON, "$.product[?(@.['attr.with.dot']=='A')].codename"), Matchers.hasItems("Seattle"));
    }

    @Test
    public void all_books_with_category_reference() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..book[?(@.category=='reference')].title"), Matchers.hasItems("Sayings of the Century"));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$.store.book[?(@.category=='reference')].title"), Matchers.hasItems("Sayings of the Century"));
    }

    @Test
    public void all_members_of_all_documents() throws Exception {
        List<String> all = JsonPath.read(JsonPathTest.DOCUMENT, "$..*");
    }

    @Test(expected = PathNotFoundException.class)
    public void access_index_out_of_bounds_does_not_throw_exception() throws Exception {
        JsonPath.read(JsonPathTest.DOCUMENT, "$.store.book[100].author");
    }

    @Test
    public void exists_filter_with_nested_path() throws Exception {
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..[?(@.bicycle.color)]"), Matchers.hasSize(1));
        Assert.assertThat(JsonPath.<List<String>>read(JsonPathTest.DOCUMENT, "$..[?(@.bicycle.numberOfGears)]"), Matchers.hasSize(0));
    }

    // see https://code.google.com/p/json-path/issues/detail?id=58
    @Test
    public void invalid_paths_throw_invalid_path_exception() throws Exception {
        for (String path : new String[]{ "$.", "$.results[?" }) {
            try {
                JsonPath.compile(path);
            } catch (InvalidPathException e) {
                // that's expected
            } catch (Exception e) {
                Assert.fail(((("Expected an InvalidPathException trying to compile '" + path) + "', but got a ") + (e.getClass().getName())));
            }
        }
    }

    // see https://github.com/json-path/JsonPath/issues/428
    @Test(expected = InvalidPathException.class)
    public void prevent_stack_overflow_error_when_unclosed_property() {
        JsonPath.compile("$['boo','foo][?(@ =~ /bar/)]");
    }
}

