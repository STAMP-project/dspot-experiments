package com.jayway.jsonpath;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class JsonProviderTest extends BaseTest {
    private final Configuration conf;

    public JsonProviderTest(Configuration conf) {
        this.conf = conf;
    }

    @Test
    public void strings_are_unwrapped() {
        assertThat(JsonPath.using(conf).parse(BaseTest.JSON_DOCUMENT).read("$.string-property", String.class)).isEqualTo("string-value");
    }

    @Test
    public void integers_are_unwrapped() {
        assertThat(JsonPath.using(conf).parse(BaseTest.JSON_DOCUMENT).read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void ints_are_unwrapped() {
        assertThat(JsonPath.using(conf).parse(BaseTest.JSON_DOCUMENT).read("$.int-max-property", int.class)).isEqualTo(Integer.MAX_VALUE);
    }
}

