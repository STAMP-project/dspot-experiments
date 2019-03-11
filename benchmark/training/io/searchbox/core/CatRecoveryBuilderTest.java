package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


public class CatRecoveryBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.RecoveryBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.RecoveryBuilder().build();
        Assert.assertEquals("_cat/recovery/_all", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.RecoveryBuilder().addIndex("testIndex").build();
        Assert.assertEquals("_cat/recovery/testIndex", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.RecoveryBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/recovery/_all?v=true", cat.getURI(UNKNOWN));
    }
}

