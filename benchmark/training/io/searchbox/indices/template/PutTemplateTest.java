package io.searchbox.indices.template;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class PutTemplateTest {
    @Test
    public void tesstBsicUriGeneration() {
        PutTemplate putTemplate = new PutTemplate.Builder("sponsored_tweet", new Object()).build();
        Assert.assertEquals("PUT", putTemplate.getRestMethodName());
        Assert.assertEquals("_template/sponsored_tweet", putTemplate.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameTemplateNameAndSource() {
        PutTemplate putTemplate1 = new PutTemplate.Builder("sponsored_tweet", "{}").build();
        PutTemplate putTemplate1Duplicate = new PutTemplate.Builder("sponsored_tweet", "{}").build();
        Assert.assertEquals(putTemplate1, putTemplate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentTemplateSource() {
        PutTemplate putTemplate1 = new PutTemplate.Builder("sponsored_tweet", "{}").build();
        PutTemplate putTemplate2 = new PutTemplate.Builder("sponsored_tweet", "{source}").build();
        Assert.assertNotEquals(putTemplate1, putTemplate2);
    }
}

