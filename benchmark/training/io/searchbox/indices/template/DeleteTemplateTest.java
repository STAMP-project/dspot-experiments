package io.searchbox.indices.template;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class DeleteTemplateTest {
    @Test
    public void testBasicUriGeneration() {
        DeleteTemplate deleteTemplate = new DeleteTemplate.Builder("personal_tweet").build();
        Assert.assertEquals("DELETE", deleteTemplate.getRestMethodName());
        Assert.assertEquals("_template/personal_tweet", deleteTemplate.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameTemplate() {
        DeleteTemplate deleteTemplate1 = new DeleteTemplate.Builder("personal_tweet").build();
        DeleteTemplate deleteTemplate1Duplicate = new DeleteTemplate.Builder("personal_tweet").build();
        Assert.assertEquals(deleteTemplate1, deleteTemplate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentTemplate() {
        DeleteTemplate deleteTemplate1 = new DeleteTemplate.Builder("personal_tweet").build();
        DeleteTemplate deleteTemplate2 = new DeleteTemplate.Builder("company_tweet").build();
        Assert.assertNotEquals(deleteTemplate1, deleteTemplate2);
    }
}

