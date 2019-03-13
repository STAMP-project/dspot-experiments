package com.bumptech.glide.load.model;


import ModelCache.ModelKey;
import com.google.common.testing.EqualsTester;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class ModelCacheTest {
    private ModelCache<Object, Object> cache;

    @Test
    public void testModelKeyEquivalence() {
        new EqualsTester().addEqualityGroup(ModelKey.get(14.0F, 100, 200), ModelKey.get(14.0F, 100, 200)).addEqualityGroup(ModelKey.get(13.0F, 100, 200)).addEqualityGroup(ModelKey.get(14.0F, 200, 200)).addEqualityGroup(ModelKey.get(14.0F, 100, 300)).testEquals();
    }

    @Test
    public void testCanSetAndGetModel() {
        Object model = new Object();
        int width = 10;
        int height = 20;
        Object result = new Object();
        cache.put(model, width, height, result);
        Assert.assertEquals(result, cache.get(model, width, height));
    }

    @Test
    public void testCanSetAndGetMultipleResultsWithDifferentDimensionsForSameObject() {
        Object model = new Object();
        int firstWidth = 10;
        int firstHeight = 20;
        Object firstResult = new Object();
        int secondWidth = 30;
        int secondHeight = 40;
        Object secondResult = new Object();
        cache.put(model, firstWidth, firstHeight, firstResult);
        cache.put(model, secondWidth, secondHeight, secondResult);
        Assert.assertEquals(firstResult, cache.get(model, firstWidth, firstHeight));
        Assert.assertEquals(secondResult, cache.get(model, secondWidth, secondHeight));
    }
}

