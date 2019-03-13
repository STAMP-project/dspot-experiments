package io.searchbox.indices;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Dogukan Sonmez
 */
public class DeleteIndexTest {
    @Test
    public void testBasicUriGenerationWithJustIndex() {
        DeleteIndex delete = new DeleteIndex.Builder("twitter").build();
        Assert.assertEquals("DELETE", delete.getRestMethodName());
        Assert.assertEquals("twitter", delete.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithIndexAndType() {
        DeleteIndex delete = new DeleteIndex.Builder("twitter").type("tweet").build();
        Assert.assertEquals("DELETE", delete.getRestMethodName());
        Assert.assertEquals("twitter/tweet", delete.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndexAndType() {
        DeleteIndex delete1 = new DeleteIndex.Builder("twitter").type("tweet").build();
        DeleteIndex delete1Duplicate = new DeleteIndex.Builder("twitter").type("tweet").build();
        Assert.assertEquals(delete1, delete1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndexAndType() {
        DeleteIndex delete1 = new DeleteIndex.Builder("twitter").type("tweet").build();
        DeleteIndex delete2 = new DeleteIndex.Builder("twitter2").type("tweet2").build();
        Assert.assertNotEquals(delete1, delete2);
    }
}

