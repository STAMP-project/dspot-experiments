package com.jayway.jsonpath;


import java.util.List;
import java.util.Map;
import org.junit.Test;


@SuppressWarnings("ALL")
public class ReturnTypeTest extends BaseTest {
    private static ReadContext reader = JsonPath.parse(BaseTest.JSON_DOCUMENT);

    @Test
    public void assert_strings_can_be_read() {
        assertThat(((String) (ReturnTypeTest.reader.read("$.string-property")))).isEqualTo("string-value");
    }

    @Test
    public void assert_ints_can_be_read() {
        assertThat(ReturnTypeTest.reader.read("$.int-max-property", Integer.class)).isEqualTo(Integer.MAX_VALUE);
    }

    @Test
    public void assert_longs_can_be_read() {
        assertThat(((Long) (ReturnTypeTest.reader.read("$.long-max-property")))).isEqualTo(Long.MAX_VALUE);
    }

    @Test
    public void assert_boolean_values_can_be_read() {
        assertThat(((Boolean) (ReturnTypeTest.reader.read("$.boolean-property")))).isEqualTo(true);
    }

    @Test
    public void assert_null_values_can_be_read() {
        assertThat(((String) (ReturnTypeTest.reader.read("$.null-property")))).isNull();
    }

    @Test
    public void assert_arrays_can_be_read() {
        /* Object result = reader.read("$.store.book");

        assertThat(reader.configuration().jsonProvider().isArray(result)).isTrue();

        assertThat(reader.configuration().jsonProvider().length(result)).isEqualTo(4);
         */
        assertThat(ReturnTypeTest.reader.read("$.store.book", List.class)).hasSize(4);
    }

    @Test
    public void assert_maps_can_be_read() {
        assertThat(ReturnTypeTest.reader.read("$.store.book[0]", Map.class)).containsEntry("category", "reference").containsEntry("author", "Nigel Rees").containsEntry("title", "Sayings of the Century").containsEntry("display-price", 8.95);
    }

    @Test
    public void a_path_evaluation_can_be_returned_as_PATH_LIST() {
        Configuration conf = Configuration.builder().options(Option.AS_PATH_LIST).build();
        List<String> pathList = JsonPath.using(conf).parse(BaseTest.JSON_DOCUMENT).read("$..author");
        assertThat(pathList).containsExactly("$['store']['book'][0]['author']", "$['store']['book'][1]['author']", "$['store']['book'][2]['author']", "$['store']['book'][3]['author']");
    }

    @Test(expected = ClassCastException.class)
    public void class_cast_exception_is_thrown_when_return_type_is_not_expected() {
        List<String> list = ReturnTypeTest.reader.read("$.store.book[0].author");
    }
}

