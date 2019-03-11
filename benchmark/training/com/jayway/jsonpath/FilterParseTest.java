package com.jayway.jsonpath;


import java.util.Collections;
import java.util.regex.Pattern;
import org.assertj.core.api.Assertions;
import org.junit.Test;


public class FilterParseTest {
    @Test
    public void a_filter_can_be_parsed() {
        Filter.parse("[?(@.foo)]");
        Filter.parse("[?(@.foo == 1)]");
        Filter.parse("[?(@.foo == 1 || @['bar'])]");
        Filter.parse("[?(@.foo == 1 && @['bar'])]");
    }

    @Test
    public void an_invalid_filter_can_not_be_parsed() {
        try {
            Filter.parse("[?(@.foo == 1)");
            Assertions.fail(("expected " + (InvalidPathException.class.getName())));
        } catch (InvalidPathException ipe) {
        }
        try {
            Filter.parse("[?(@.foo == 1) ||]");
            Assertions.fail(("expected " + (InvalidPathException.class.getName())));
        } catch (InvalidPathException ipe) {
        }
        try {
            Filter.parse("[(@.foo == 1)]");
            Assertions.fail(("expected " + (InvalidPathException.class.getName())));
        } catch (InvalidPathException ipe) {
        }
        try {
            Filter.parse("[?@.foo == 1)]");
            Assertions.fail(("expected " + (InvalidPathException.class.getName())));
        } catch (InvalidPathException ipe) {
        }
    }

    @Test
    public void a_gte_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").gte(1)).toString();
        String parsed = Filter.parse("[?(@['a'] >= 1)]").toString();
        assertThat(filter).isEqualTo(Filter.parse(parsed).toString());
    }

    @Test
    public void a_lte_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").lte(1)).toString();
        String parsed = Filter.parse("[?(@['a'] <= 1)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_eq_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").eq(1)).toString();
        String parsed = Filter.parse("[?(@['a'] == 1)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_ne_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").ne(1)).toString();
        String parsed = Filter.parse("[?(@['a'] != 1)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_lt_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").lt(1)).toString();
        String parsed = Filter.parse("[?(@['a'] < 1)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_gt_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").gt(1)).toString();
        String parsed = Filter.parse("[?(@['a'] > 1)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_nin_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").nin(1)).toString();
        String parsed = Filter.parse("[?(@['a'] NIN [1])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_in_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").in("a")).toString();
        String parsed = Filter.parse("[?(@['a'] IN ['a'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_contains_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").contains("a")).toString();
        String parsed = Filter.parse("[?(@['a'] CONTAINS 'a')]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_all_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").all("a", "b")).toString();
        String parsed = Filter.parse("[?(@['a'] ALL ['a','b'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_size_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").size(5)).toString();
        String parsed = Filter.parse("[?(@['a'] SIZE 5)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_subsetof_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").subsetof(Collections.emptyList())).toString();
        String parsed = Filter.parse("[?(@['a'] SUBSETOF [])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_exists_filter_can_be_serialized() {
        Filter a = Filter.filter(Criteria.where("a").exists(true));
        String filter = a.toString();
        String parsed = Filter.parse("[?(@['a'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_not_exists_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").exists(false)).toString();
        String parsed = Filter.parse("[?(!@['a'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_type_filter_can_be_serialized() {
        assertThat(Filter.filter(Criteria.where("a").type(String.class)).toString()).isEqualTo("[?(@['a'] TYPE java.lang.String)]");
    }

    @Test
    public void a_matches_filter_can_be_serialized() {
        Filter a = Filter.filter(Criteria.where("x").eq(1000));
        assertThat(Filter.filter(Criteria.where("a").matches(a)).toString()).isEqualTo("[?(@['a'] MATCHES [?(@['x'] == 1000)])]");
    }

    @Test
    public void a_not_empty_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").empty(false)).toString();
        String parsed = Filter.parse("[?(@['a'] EMPTY false)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void and_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").eq(1).and("b").eq(2)).toString();
        String parsed = Filter.parse("[?(@['a'] == 1 && @['b'] == 2)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void in_string_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a").in("1", "2")).toString();
        String parsed = Filter.parse("[?(@['a'] IN ['1','2'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_deep_path_filter_can_be_serialized() {
        String filter = Filter.filter(Criteria.where("a.b.c").in("1", "2")).toString();
        String parsed = Filter.parse("[?(@['a']['b']['c'] IN ['1','2'])]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void a_regex_filter_can_be_serialized() {
        assertThat(Filter.filter(Criteria.where("a").regex(Pattern.compile("/.*?/i"))).toString()).isEqualTo("[?(@['a'] =~ /.*?/i)]");
    }

    @Test
    public void a_doc_ref_filter_can_be_serialized() {
        Filter f = Filter.parse("[?(@.display-price <= $.max-price)]");
        assertThat(f.toString()).isEqualTo("[?(@['display-price'] <= $['max-price'])]");
    }

    @Test
    public void and_combined_filters_can_be_serialized() {
        Filter a = Filter.filter(Criteria.where("a").eq(1));
        Filter b = Filter.filter(Criteria.where("b").eq(2));
        Filter c = a.and(b);
        String filter = c.toString();
        String parsed = Filter.parse("[?(@['a'] == 1 && @['b'] == 2)]").toString();
        assertThat(filter).isEqualTo(parsed);
    }

    @Test
    public void or_combined_filters_can_be_serialized() {
        Filter a = Filter.filter(Criteria.where("a").eq(1));
        Filter b = Filter.filter(Criteria.where("b").eq(2));
        Filter c = a.or(b);
        String filter = c.toString();
        Filter d = Filter.parse("[?(@['a'] == 1 || @['b'] == 2)]");
        String parsed = d.toString();
        assertThat(filter).isEqualTo(parsed);
    }
}

