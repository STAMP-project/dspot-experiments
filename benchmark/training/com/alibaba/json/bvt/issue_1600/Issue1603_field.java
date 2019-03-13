package com.alibaba.json.bvt.issue_1600;


import com.alibaba.fastjson.JSON;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import junit.framework.TestCase;


public class Issue1603_field extends TestCase {
    public void test_emptySet() throws Exception {
        Issue1603_field.Model_1 m = JSON.parseObject("{\"values\":[\"a\"]}", Issue1603_field.Model_1.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public void test_emptyList() throws Exception {
        Issue1603_field.Model_2 m = JSON.parseObject("{\"values\":[\"a\"]}", Issue1603_field.Model_2.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public void test_unmodifier() throws Exception {
        Issue1603_field.Model_3 m = JSON.parseObject("{\"values\":[\"a\"]}", Issue1603_field.Model_3.class);
        TestCase.assertEquals(0, m.values.size());
    }

    public static class Model_1 {
        public final Collection<String> values = Collections.emptySet();
    }

    public static class Model_2 {
        public final Collection<String> values = Collections.emptyList();
    }

    public static class Model_3 {
        public final Collection<String> values = Collections.unmodifiableList(new ArrayList<String>());
    }
}

