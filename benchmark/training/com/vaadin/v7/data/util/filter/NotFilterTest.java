package com.vaadin.v7.data.util.filter;


import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import org.junit.Assert;
import org.junit.Test;


public class NotFilterTest extends AbstractFilterTestBase<Not> {
    protected Item item1 = new com.vaadin.v7.data.util.BeanItem<Integer>(1);

    protected Item item2 = new com.vaadin.v7.data.util.BeanItem<Integer>(2);

    @Test
    public void testNot() {
        Filter origFilter = new AbstractFilterTestBase.SameItemFilter(item1);
        Filter filter = new Not(origFilter);
        Assert.assertTrue(origFilter.passesFilter(null, item1));
        Assert.assertFalse(origFilter.passesFilter(null, item2));
        Assert.assertFalse(filter.passesFilter(null, item1));
        Assert.assertTrue(filter.passesFilter(null, item2));
    }

    @Test
    public void testANotAppliesToProperty() {
        Filter filterA = new Not(new AbstractFilterTestBase.SameItemFilter(item1, "a"));
        Filter filterB = new Not(new AbstractFilterTestBase.SameItemFilter(item1, "b"));
        Assert.assertTrue(filterA.appliesToProperty("a"));
        Assert.assertFalse(filterA.appliesToProperty("b"));
        Assert.assertFalse(filterB.appliesToProperty("a"));
        Assert.assertTrue(filterB.appliesToProperty("b"));
    }

    @Test
    public void testNotEqualsHashCode() {
        Filter origFilter = new AbstractFilterTestBase.SameItemFilter(item1);
        Filter filter1 = new Not(origFilter);
        Filter filter1b = new Not(new AbstractFilterTestBase.SameItemFilter(item1));
        Filter filter2 = new Not(new AbstractFilterTestBase.SameItemFilter(item2));
        // equals()
        Assert.assertEquals(filter1, filter1b);
        Assert.assertFalse(filter1.equals(filter2));
        Assert.assertFalse(filter1.equals(origFilter));
        Assert.assertFalse(filter1.equals(new And()));
        // hashCode()
        Assert.assertEquals(filter1.hashCode(), filter1b.hashCode());
    }
}

