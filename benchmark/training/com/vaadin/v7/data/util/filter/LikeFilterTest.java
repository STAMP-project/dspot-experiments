package com.vaadin.v7.data.util.filter;


import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.PropertysetItem;
import org.junit.Assert;
import org.junit.Test;


public class LikeFilterTest extends AbstractFilterTestBase<Like> {
    protected Item item1 = new PropertysetItem();

    protected Item item2 = new PropertysetItem();

    protected Item item3 = new PropertysetItem();

    @Test
    public void testLikeWithNulls() {
        Like filter = new Like("value", "a");
        item1.addItemProperty("value", new com.vaadin.v7.data.util.ObjectProperty<String>("a"));
        item2.addItemProperty("value", new com.vaadin.v7.data.util.ObjectProperty<String>("b"));
        item3.addItemProperty("value", new com.vaadin.v7.data.util.ObjectProperty<String>(null, String.class));
        Assert.assertTrue(filter.passesFilter(null, item1));
        Assert.assertFalse(filter.passesFilter(null, item2));
        Assert.assertFalse(filter.passesFilter(null, item3));
    }
}

