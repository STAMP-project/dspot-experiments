package io.searchbox.fields;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class FieldsStatsTest {
    static final String TEST_FIELD = "test_name";

    static final String INDEX = "twitter";

    static final List FIELDS = Collections.singletonList(FieldsStatsTest.TEST_FIELD);

    @Test
    public void testBasicUriGeneration() {
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FieldsStatsTest.FIELDS).setIndex(FieldsStatsTest.INDEX).build();
        Assert.assertEquals("POST", fieldCapabilities.getRestMethodName());
        Assert.assertEquals(((FieldsStatsTest.INDEX) + "/_field_caps"), fieldCapabilities.getURI(UNKNOWN));
        Assert.assertEquals((("{\"fields\":[\"" + (FieldsStatsTest.TEST_FIELD)) + "\"]}"), fieldCapabilities.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationNoIndex() {
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FieldsStatsTest.FIELDS).build();
        Assert.assertEquals("POST", fieldCapabilities.getRestMethodName());
        Assert.assertEquals("_field_caps", fieldCapabilities.getURI(UNKNOWN));
        Assert.assertEquals((("{\"fields\":[\"" + (FieldsStatsTest.TEST_FIELD)) + "\"]}"), fieldCapabilities.getData(new Gson()));
    }

    @Test
    public void testBasicUriGenerationWithLevel() {
        FieldCapabilities fieldCapabilities = new FieldCapabilities.Builder(FieldsStatsTest.FIELDS).setIndex(FieldsStatsTest.INDEX).setLevel("indices").build();
        Assert.assertEquals("POST", fieldCapabilities.getRestMethodName());
        Assert.assertEquals(((FieldsStatsTest.INDEX) + "/_field_caps?level=indices"), fieldCapabilities.getURI(UNKNOWN));
        Assert.assertEquals((("{\"fields\":[\"" + (FieldsStatsTest.TEST_FIELD)) + "\"]}"), fieldCapabilities.getData(new Gson()));
    }
}

