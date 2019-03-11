package com.jayway.jsonpath.old.internal;


import com.jayway.jsonpath.JsonPath;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.Test;


public class ArrayPathTokenTest extends TestBase {
    @Test
    public void array_can_select_multiple_indexes() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[0,1]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-0"), Collections.singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void array_can_be_sliced_to_2() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[:2]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-0"), Collections.singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void array_can_be_sliced_to_2_from_tail() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[:-5]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-0"), Collections.singletonMap("foo", "foo-val-1"));
    }

    @Test
    public void array_can_be_sliced_from_2() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[5:]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-5"), Collections.singletonMap("foo", "foo-val-6"));
    }

    @Test
    public void array_can_be_sliced_from_2_from_tail() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[-2:]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-5"), Collections.singletonMap("foo", "foo-val-6"));
    }

    @Test
    public void array_can_be_sliced_between() {
        List<Map> result = JsonPath.read(TestBase.ARRAY, "$[2:4]");
        assertThat(result).containsOnly(Collections.singletonMap("foo", "foo-val-2"), Collections.singletonMap("foo", "foo-val-3"));
    }
}

