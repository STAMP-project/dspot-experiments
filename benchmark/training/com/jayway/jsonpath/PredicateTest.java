package com.jayway.jsonpath;


import java.util.List;
import java.util.Map;
import org.junit.Test;


public class PredicateTest extends BaseTest {
    private static ReadContext reader = JsonPath.using(BaseTest.GSON_CONFIGURATION).parse(BaseTest.JSON_DOCUMENT);

    @Test
    public void predicates_filters_can_be_applied() {
        Predicate booksWithISBN = new Predicate() {
            @Override
            public boolean apply(PredicateContext ctx) {
                return ctx.item(Map.class).containsKey("isbn");
            }
        };
        assertThat(PredicateTest.reader.read("$.store.book[?].isbn", List.class, booksWithISBN)).containsOnly("0-395-19395-8", "0-553-21311-3");
    }
}

