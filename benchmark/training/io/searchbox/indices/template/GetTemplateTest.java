package io.searchbox.indices.template;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class GetTemplateTest {
    @Test
    public void testBasicUriGeneration() {
        GetTemplate getTemplate = new GetTemplate.Builder("personal_tweet").build();
        Assert.assertEquals("GET", getTemplate.getRestMethodName());
        Assert.assertEquals("_template/personal_tweet", getTemplate.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameTemplate() {
        GetTemplate getTemplate1 = new GetTemplate.Builder("personal_tweet").build();
        GetTemplate getTemplate1Duplicate = new GetTemplate.Builder("personal_tweet").build();
        Assert.assertEquals(getTemplate1, getTemplate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentTemplate() {
        GetTemplate getTemplate1 = new GetTemplate.Builder("personal_tweet").build();
        GetTemplate getTemplate2 = new GetTemplate.Builder("company_tweet").build();
        Assert.assertNotEquals(getTemplate1, getTemplate2);
    }
}

