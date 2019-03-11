package com.jayway.jsonpath.internal;


import com.jayway.jsonpath.BaseTest;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.JsonPath;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class JsonContextTest extends BaseTest {
    @Test
    public void cached_path_with_predicates() {
        Filter feq = Filter.filter(com.jayway.jsonpath.Criteria.where("category").eq("reference"));
        Filter fne = Filter.filter(com.jayway.jsonpath.Criteria.where("category").ne("reference"));
        DocumentContext JsonDoc = JsonPath.parse(BaseTest.JSON_DOCUMENT);
        List<String> eq = JsonDoc.read("$.store.book[?].category", feq);
        List<String> ne = JsonDoc.read("$.store.book[?].category", fne);
        Assertions.assertThat(eq).contains("reference");
        Assertions.assertThat(ne).doesNotContain("reference");
    }
}

