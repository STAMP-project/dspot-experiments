package io.searchbox.indices.aliases;


import ElasticsearchVersion.UNKNOWN;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;


public class GetAliasesTest {
    @Test
    public void testBasicUriGeneration() {
        GetAliases getAliases = new GetAliases.Builder().addIndex("twitter").build();
        Assert.assertEquals("GET", getAliases.getRestMethodName());
        Assert.assertEquals("twitter/_alias", getAliases.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithAliases() {
        GetAliases getAliases = new GetAliases.Builder().addIndex("twitter").addAlias("alias").build();
        Assert.assertEquals("GET", getAliases.getRestMethodName());
        Assert.assertEquals("twitter/_alias/alias", getAliases.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithMultipleAliases() {
        GetAliases getAliases = new GetAliases.Builder().addIndex("twitter").addAliases(Arrays.asList(new String[]{ "alias1", "alias2" })).build();
        Assert.assertEquals("GET", getAliases.getRestMethodName());
        Assert.assertEquals("twitter/_alias/alias1,alias2", getAliases.getURI(UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameIndex() {
        GetAliases getAliases1 = new GetAliases.Builder().addIndex("twitter").build();
        GetAliases getAliases1Duplicate = new GetAliases.Builder().addIndex("twitter").build();
        Assert.assertEquals(getAliases1, getAliases1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentIndex() {
        GetAliases getAliases1 = new GetAliases.Builder().addIndex("twitter").build();
        GetAliases getAliases2 = new GetAliases.Builder().addIndex("myspace").build();
        Assert.assertNotEquals(getAliases1, getAliases2);
    }
}

