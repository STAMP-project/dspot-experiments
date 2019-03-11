package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class ValidateTest {
    @Test
    public void validateQuery() {
        Validate validate = new Validate.Builder("{query:query}").build();
        Assert.assertEquals("POST", validate.getRestMethodName());
        Assert.assertEquals("{query:query}", validate.getData(null));
        Assert.assertEquals("/_validate/query", validate.getURI(UNKNOWN));
    }

    @Test
    public void validateQueryWithIndex() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").build();
        Assert.assertEquals("POST", validate.getRestMethodName());
        Assert.assertEquals("{query:query}", validate.getData(null));
        Assert.assertEquals("twitter/_validate/query", validate.getURI(UNKNOWN));
    }

    @Test
    public void validateQueryWithIndexAndType() {
        Validate validate = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Assert.assertEquals("POST", validate.getRestMethodName());
        Assert.assertEquals("{query:query}", validate.getData(null));
        Assert.assertEquals("twitter/tweet/_validate/query", validate.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameQuery() {
        Validate validate1 = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Validate validate1Duplicate = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Assert.assertEquals(validate1, validate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentQueries() {
        Validate validate1 = new Validate.Builder("{query:query}").index("twitter").type("tweet").build();
        Validate validate2 = new Validate.Builder("{query2:query2}").index("twitter").type("tweet").build();
        Assert.assertNotEquals(validate1, validate2);
    }
}

