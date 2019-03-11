package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import com.google.gson.Gson;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class MultiGetTest {
    Doc doc1 = new Doc("twitter", "tweet", "1");

    Doc doc2 = new Doc("twitter", "tweet", "2");

    Doc doc3 = new Doc("twitter", "tweet", "3");

    @Test
    public void getMultipleDocs() throws JSONException {
        MultiGet get = build();
        Assert.assertEquals("POST", get.getRestMethodName());
        Assert.assertEquals("/_mget", get.getURI(UNKNOWN));
        JSONAssert.assertEquals(("{\"docs\":[" + (("{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"1\"}," + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"2\"},") + "{\"_index\":\"twitter\",\"_type\":\"tweet\",\"_id\":\"3\"}]}")), get.getData(new Gson()), false);
    }

    @Test
    public void equalsReturnsTrueForSameDocs() {
        MultiGet multiGet1 = build();
        MultiGet multiGet1Duplicate = build();
        Assert.assertEquals(multiGet1, multiGet1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDiffererntDocs() {
        MultiGet multiGet1 = build();
        MultiGet multiGet2 = build();
        Assert.assertNotEquals(multiGet1, multiGet2);
    }

    @Test
    public void getDocumentWithMultipleIds() throws JSONException {
        MultiGet get = build();
        Assert.assertEquals("POST", get.getRestMethodName());
        Assert.assertEquals("twitter/tweet/_mget", get.getURI(UNKNOWN));
        JSONAssert.assertEquals("{\"ids\":[\"1\",\"2\",\"3\"]}", get.getData(new Gson()), false);
    }

    @Test
    public void equalsReturnsTrueForSameIds() {
        MultiGet multiGet1 = build();
        MultiGet multiGet1Dupliacte = build();
        Assert.assertEquals(multiGet1, multiGet1Dupliacte);
    }

    @Test
    public void equalsReturnsFalseForDifferentIds() {
        MultiGet multiGet1 = build();
        MultiGet multiGet2 = build();
        Assert.assertNotEquals(multiGet1, multiGet2);
    }
}

