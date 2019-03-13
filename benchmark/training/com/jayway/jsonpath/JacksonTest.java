package com.jayway.jsonpath;


import java.util.Collections;
import java.util.Date;
import org.junit.Test;


public class JacksonTest extends BaseTest {
    @Test
    public void an_object_can_be_mapped_to_pojo() {
        String json = "{\n" + ((("   \"foo\" : \"foo\",\n" + "   \"bar\" : 10,\n") + "   \"baz\" : true\n") + "}");
        JacksonTest.FooBarBaz fooBarBaz = JsonPath.using(BaseTest.JACKSON_CONFIGURATION).parse(json).read("$", JacksonTest.FooBarBaz.class);
        assertThat(fooBarBaz.foo).isEqualTo("foo");
        assertThat(fooBarBaz.bar).isEqualTo(10L);
        assertThat(fooBarBaz.baz).isEqualTo(true);
    }

    public static class FooBarBaz {
        public String foo;

        public Long bar;

        public boolean baz;
    }

    @Test
    public void jackson_converts_dates() {
        Date now = new Date();
        Object json = Collections.singletonMap("date_as_long", now.getTime());
        Date date = JsonPath.using(BaseTest.JACKSON_CONFIGURATION).parse(json).read("$['date_as_long']", Date.class);
        assertThat(date).isEqualTo(now);
    }

    // https://github.com/jayway/JsonPath/issues/275
    @Test
    public void single_quotes_work_with_in_filter() {
        final String jsonArray = "[{\"foo\": \"bar\"}, {\"foo\": \"baz\"}]";
        final Object readFromSingleQuote = JsonPath.using(BaseTest.JACKSON_CONFIGURATION).parse(jsonArray).read("$.[?(@.foo in ['bar'])].foo");
        final Object readFromDoubleQuote = JsonPath.using(BaseTest.JACKSON_CONFIGURATION).parse(jsonArray).read("$.[?(@.foo in [\"bar\"])].foo");
        assertThat(readFromSingleQuote).isEqualTo(readFromDoubleQuote);
    }
}

