package com.vaadin.v7.data.util.filter;


import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.PropertysetItem;
import org.junit.Assert;
import org.junit.Test;


public class IsNullFilterTest extends AbstractFilterTestBase<IsNull> {
    @Test
    public void testIsNull() {
        Item item1 = new PropertysetItem();
        item1.addItemProperty("a", new com.vaadin.v7.data.util.ObjectProperty<String>(null, String.class));
        item1.addItemProperty("b", new com.vaadin.v7.data.util.ObjectProperty<String>("b", String.class));
        Item item2 = new PropertysetItem();
        item2.addItemProperty("a", new com.vaadin.v7.data.util.ObjectProperty<String>("a", String.class));
        item2.addItemProperty("b", new com.vaadin.v7.data.util.ObjectProperty<String>(null, String.class));
        Filter filter1 = new IsNull("a");
        Filter filter2 = new IsNull("b");
        Assert.assertTrue(filter1.passesFilter(null, item1));
        Assert.assertFalse(filter1.passesFilter(null, item2));
        Assert.assertFalse(filter2.passesFilter(null, item1));
        Assert.assertTrue(filter2.passesFilter(null, item2));
    }

    @Test
    public void testIsNullAppliesToProperty() {
        Filter filterA = new IsNull("a");
        Filter filterB = new IsNull("b");
        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testIsNullEqualsHashCode() {
        Filter filter1 = new IsNull("a");
        Filter filter1b = new IsNull("a");
        Filter filter2 = new IsNull("b");
        // equals()
        Assert.assertEquals(filter1, filter1b);
        Assert.assertFalse(filter1.equals(filter2));
        Assert.assertFalse(filter1.equals(new And()));
        // hashCode()
        Assert.assertEquals(filter1.hashCode(), filter1b.hashCode());
    }
}

