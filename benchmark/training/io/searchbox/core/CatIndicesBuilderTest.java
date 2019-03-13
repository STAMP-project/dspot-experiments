package io.searchbox.core;


import ElasticsearchVersion.UNKNOWN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Bartosz Polnik
 */
public class CatIndicesBuilderTest {
    @Test
    public void shouldSetApplicationJsonHeader() {
        Cat cat = new Cat.IndicesBuilder().build();
        Assert.assertEquals("application/json", cat.getHeader("accept"));
        Assert.assertEquals("application/json", cat.getHeader("content-type"));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexNotGiven() {
        Cat cat = new Cat.IndicesBuilder().build();
        Assert.assertEquals("_cat/indices/_all", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexGiven() {
        Cat cat = new Cat.IndicesBuilder().addIndex("testIndex").build();
        Assert.assertEquals("_cat/indices/testIndex", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenIndexAndTypeGiven() {
        Cat cat = new Cat.IndicesBuilder().addIndex("testIndex").addType("testType").build();
        Assert.assertEquals("_cat/indices/testIndex/testType", cat.getURI(UNKNOWN));
    }

    @Test
    public void shouldGenerateValidUriWhenParameterGiven() {
        Cat cat = new Cat.IndicesBuilder().setParameter("v", "true").build();
        Assert.assertEquals("_cat/indices/_all?v=true", cat.getURI(UNKNOWN));
    }
}

