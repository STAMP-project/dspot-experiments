package com.jayway.jsonpath;


import Option.DEFAULT_PATH_LEAF_TO_NULL;
import Option.REQUIRE_PROPERTIES;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;


/**
 * Deep scan is indefinite, so certain "illegal" actions become a no-op instead of a path evaluation exception.
 */
public class DeepScanTest extends BaseTest {
    @Test
    public void when_deep_scanning_non_array_subscription_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,null],null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
        result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,null],null], \"y\": [0,1,2]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
        result = JsonPath.parse("{\"x\": [0,1,[0,1,2],null], \"y\": [0,1,2]}").read("$..[2][3]");
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void when_deep_scanning_null_subscription_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3,null],null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
        result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3,null],null], \"y\": [0,1,null]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
    }

    @Test
    public void when_deep_scanning_array_index_oob_is_ignored() {
        Object result = JsonPath.parse("{\"x\": [0,1,[0,1,2,3,10],null]}").read("$..[4]");
        assertThat(result).asList().containsOnly(10);
        result = JsonPath.parse("{\"x\": [null,null,[0,1,2,3]], \"y\": [null,null,[0,1]]}").read("$..[2][3]");
        assertThat(result).asList().containsOnly(3);
    }

    @Test
    public void definite_upstream_illegal_array_access_throws() {
        TestUtils.assertEvaluationThrows("{\"foo\": {\"bar\": null}}", "$.foo.bar.[5]", PathNotFoundException.class);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"bar\": null}}", "$.foo.bar.[5, 10]", PathNotFoundException.class);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"bar\": 4}}", "$.foo.bar.[5]", PathNotFoundException.class);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"bar\": 4}}", "$.foo.bar.[5, 10]", PathNotFoundException.class);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"bar\": []}}", "$.foo.bar.[5]", PathNotFoundException.class);
    }

    @Test
    public void when_deep_scanning_illegal_property_access_is_ignored() {
        Object result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..foo");
        assertThat(result).asList().hasSize(2);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..foo.bar");
        assertThat(result).asList().containsOnly(4);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..[*].foo.bar");
        assertThat(result).asList().containsOnly(4);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"baz\": 4}}, \"y\": {\"foo\": 1}}").read("$..[*].foo.bar");
        assertThat(result).asList().isEmpty();
    }

    @Test
    public void when_deep_scanning_illegal_predicate_is_ignored() {
        Object result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..foo[?(@.bar)].bar");
        assertThat(result).asList().containsOnly(4);
        result = JsonPath.parse("{\"x\": {\"foo\": {\"bar\": 4}}, \"y\": {\"foo\": 1}}").read("$..[*]foo[?(@.bar)].bar");
        assertThat(result).asList().containsOnly(4);
    }

    @Test
    public void when_deep_scanning_require_properties_is_ignored_on_scan_target() {
        final Configuration conf = Configuration.defaultConfiguration().addOptions(REQUIRE_PROPERTIES);
        Object result = JsonPath.parse("[{\"x\": {\"foo\": {\"x\": 4}, \"x\": null}, \"y\": {\"x\": 1}}, {\"x\": []}]").read("$..x");
        assertThat(result).asList().hasSize(5);
        List<Integer> result1 = JsonPath.using(conf).parse("{\"foo\": {\"bar\": 4}}").read("$..foo.bar");
        assertThat(result1).containsExactly(4);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"baz\": 4}}", "$..foo.bar", PathNotFoundException.class, conf);
    }

    @Test
    public void when_deep_scanning_require_properties_is_ignored_on_scan_target_but_not_on_children() {
        final Configuration conf = Configuration.defaultConfiguration().addOptions(REQUIRE_PROPERTIES);
        TestUtils.assertEvaluationThrows("{\"foo\": {\"baz\": 4}}", "$..foo.bar", PathNotFoundException.class, conf);
    }

    @Test
    public void when_deep_scanning_leaf_multi_props_work() {
        Object result = JsonPath.parse("[{\"a\": \"a-val\", \"b\": \"b-val\", \"c\": \"c-val\"}, [1, 5], {\"a\": \"a-val\"}]").read("$..['a', 'c']");
        // This is current deep scan semantics: only objects containing all properties specified in multiprops token are
        // considered.
        assertThat(result).asList().hasSize(1);
        result = ((List) (result)).get(0);
        assertThat(result).isInstanceOf(Map.class);
        assertThat(((Map) (result))).hasSize(2).containsEntry("a", "a-val").containsEntry("c", "c-val");
        // But this semantics changes when DEFAULT_PATH_LEAF_TO_NULL comes into play.
        Configuration conf = Configuration.defaultConfiguration().addOptions(DEFAULT_PATH_LEAF_TO_NULL);
        result = JsonPath.using(conf).parse("[{\"a\": \"a-val\", \"b\": \"b-val\", \"c\": \"c-val\"}, [1, 5], {\"a\": \"a-val\"}]").read("$..['a', 'c']");
        // todo: deep equality test, but not tied to any json provider
        assertThat(result).asList().hasSize(2);
        for (final Object node : ((List) (result))) {
            assertThat(node).isInstanceOf(Map.class);
            assertThat(((Map) (node))).hasSize(2).containsEntry("a", "a-val");
        }
    }

    @Test
    public void require_single_property_ok() {
        List json = new ArrayList() {
            {
                add(Collections.singletonMap("a", "a0"));
                add(Collections.singletonMap("a", "a1"));
            }
        };
        Configuration configuration = BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES);
        Object result = JsonPath.using(configuration).parse(json).read("$..a");
        assertThat(result).asList().containsExactly("a0", "a1");
    }

    @Test
    public void require_single_property() {
        List json = new ArrayList() {
            {
                add(Collections.singletonMap("a", "a0"));
                add(Collections.singletonMap("b", "b2"));
            }
        };
        Configuration configuration = BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES);
        Object result = JsonPath.using(configuration).parse(json).read("$..a");
        assertThat(result).asList().containsExactly("a0");
    }

    @Test
    public void require_multi_property_all_match() {
        final Map ab = new HashMap() {
            {
                put("a", "aa");
                put("b", "bb");
            }
        };
        List json = new ArrayList() {
            {
                add(ab);
                add(ab);
            }
        };
        Configuration configuration = BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES);
        List<Map<String, String>> result = JsonPath.using(configuration).parse(json).read("$..['a', 'b']");
        assertThat(result).containsExactly(ab, ab);
    }

    @Test
    public void require_multi_property_some_match() {
        final Map ab = new HashMap() {
            {
                put("a", "aa");
                put("b", "bb");
            }
        };
        final Map ad = new HashMap() {
            {
                put("a", "aa");
                put("d", "dd");
            }
        };
        List json = new ArrayList() {
            {
                add(ab);
                add(ad);
            }
        };
        Configuration configuration = BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES);
        List<Map<String, String>> result = JsonPath.using(configuration).parse(json).read("$..['a', 'b']");
        assertThat(result).containsExactly(ab);
    }

    @Test
    public void scan_for_single_property() {
        final Map a = new HashMap() {
            {
                put("a", "aa");
            }
        };
        final Map b = new HashMap() {
            {
                put("b", "bb");
            }
        };
        final Map ab = new HashMap() {
            {
                put("a", a);
                put("b", b);
            }
        };
        final Map b_ab = new HashMap() {
            {
                put("b", b);
                put("ab", ab);
            }
        };
        List json = new ArrayList() {
            {
                add(a);
                add(b);
                add(b_ab);
            }
        };
        assertThat(JsonPath.parse(json).read("$..['a']", List.class)).containsExactly("aa", a, "aa");
    }

    @Test
    public void scan_for_property_path() {
        final Map a = new HashMap() {
            {
                put("a", "aa");
            }
        };
        final Map x = new HashMap() {
            {
                put("x", "xx");
            }
        };
        final Map y = new HashMap() {
            {
                put("a", x);
            }
        };
        final Map z = new HashMap() {
            {
                put("z", y);
            }
        };
        List json = new ArrayList() {
            {
                add(a);
                add(x);
                add(y);
                add(z);
            }
        };
        assertThat(JsonPath.parse(json).read("$..['a'].x", List.class)).containsExactly("xx", "xx");
    }

    @Test
    public void scan_for_property_path_missing_required_property() {
        final Map a = new HashMap() {
            {
                put("a", "aa");
            }
        };
        final Map x = new HashMap() {
            {
                put("x", "xx");
            }
        };
        final Map y = new HashMap() {
            {
                put("a", x);
            }
        };
        final Map z = new HashMap() {
            {
                put("z", y);
            }
        };
        List json = new ArrayList() {
            {
                add(a);
                add(x);
                add(y);
                add(z);
            }
        };
        assertThat(JsonPath.using(BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES)).parse(json).read("$..['a'].x", List.class)).containsExactly("xx", "xx");
    }

    @Test
    public void scans_can_be_filtered() {
        final Map brown = Collections.singletonMap("val", "brown");
        final Map white = Collections.singletonMap("val", "white");
        final Map cow = new HashMap() {
            {
                put("mammal", true);
                put("color", brown);
            }
        };
        final Map dog = new HashMap() {
            {
                put("mammal", true);
                put("color", white);
            }
        };
        final Map frog = new HashMap() {
            {
                put("mammal", false);
            }
        };
        List animals = new ArrayList() {
            {
                add(cow);
                add(dog);
                add(frog);
            }
        };
        assertThat(JsonPath.using(BaseTest.JSON_SMART_CONFIGURATION.addOptions(REQUIRE_PROPERTIES)).parse(animals).read("$..[?(@.mammal == true)].color", List.class)).containsExactly(brown, white);
    }

    @Test
    public void scan_with_a_function_filter() {
        List result = JsonPath.parse(BaseTest.JSON_DOCUMENT).read("$..*[?(@.length() > 5)]");
        assertThat(result).hasSize(1);
    }

    @Test
    public void deepScanPathDefault() {
        executeScanPath();
    }

    @Test
    public void deepScanPathRequireProperties() {
        executeScanPath(REQUIRE_PROPERTIES);
    }
}

