package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Bartosz Polnik
 */
public class CatAliasesBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.AliasesBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.AliasesBuilder().build();
        Assert.assertEquals("_cat/aliases/_all", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.AliasesBuilder().addIndex("testIndex").build();
        Assert.assertEquals("_cat/aliases/testIndex", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.AliasesBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/aliases/_all?v=true", cat.getURI(UNKNOWN));
    }
}

