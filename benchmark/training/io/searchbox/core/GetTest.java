package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class GetTest {
    @Test
    public void getDocument() {
        Get get = new Get.Builder("twitter", "1").type("tweet").build();
        Assert.assertEquals("GET", get.getRestMethodName());
        Assert.assertEquals("twitter/tweet/1", get.getURI(UNKNOWN));
    }
}

