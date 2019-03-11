package io.searchbox.indices.aliases;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class AliasExistsTest {
    @Test
    public void testBasicUriGeneration() {
        AliasExists aliasExists = new AliasExists.Builder().build();
        Assert.assertEquals("HEAD", aliasExists.getRestMethodName());
        Assert.assertEquals("_all/_alias/*", aliasExists.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithIndex() {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("indexName").build();
        Assert.assertEquals("HEAD", aliasExists.getRestMethodName());
        Assert.assertEquals("indexName/_alias/*", aliasExists.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithAlias() {
        AliasExists aliasExists = new AliasExists.Builder().alias("aliasName").build();
        Assert.assertEquals("HEAD", aliasExists.getRestMethodName());
        Assert.assertEquals("_all/_alias/aliasName", aliasExists.getURI(UNKNOWN));
    }

    @Test
    public void testBasicUriGenerationWithAliasAndIndex() {
        AliasExists aliasExists = new AliasExists.Builder().addIndex("indexName").alias("aliasName").build();
        Assert.assertEquals("HEAD", aliasExists.getRestMethodName());
        Assert.assertEquals("indexName/_alias/aliasName", aliasExists.getURI(UNKNOWN));
    }
}

