package com.vaadin.v7.data.util.filter;


import org.junit.Assert;
import org.junit.Test;

import static com.vaadin.v7.data.util.filter.AbstractFilterTestBase.TestItem.addItemProperty;


public class SimpleStringFilterTest extends AbstractFilterTestBase<SimpleStringFilter> {
    @Test
    public void testStartsWithCaseSensitive() {
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "ab", false, true));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "", false, true));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY2, "ab", false, true));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY1, "AB", false, true));
    }

    @Test
    public void testStartsWithCaseInsensitive() {
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "AB", true, true));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY2, "te", true, true));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY2, "AB", true, true));
    }

    @Test
    public void testContainsCaseSensitive() {
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "ab", false, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "abcde", false, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "cd", false, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "e", false, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "", false, false));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY2, "ab", false, false));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY1, "es", false, false));
    }

    @Test
    public void testContainsCaseInsensitive() {
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "AB", true, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "aBcDe", true, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "CD", true, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY1, "", true, false));
        Assert.assertTrue(passes(AbstractFilterTestBase.PROPERTY2, "es", true, false));
        Assert.assertFalse(passes(AbstractFilterTestBase.PROPERTY2, "ab", true, false));
    }

    @Test
    public void testAppliesToProperty() {
        SimpleStringFilter filter = f(AbstractFilterTestBase.PROPERTY1, "ab", false, true);
        Assert.assertTrue(filter.appliesToProperty(AbstractFilterTestBase.PROPERTY1));
        Assert.assertFalse(filter.appliesToProperty(AbstractFilterTestBase.PROPERTY2));
        Assert.assertFalse(filter.appliesToProperty("other"));
    }

    @Test
    public void testEqualsHashCode() {
        SimpleStringFilter filter = f(AbstractFilterTestBase.PROPERTY1, "ab", false, true);
        SimpleStringFilter f1 = f(AbstractFilterTestBase.PROPERTY2, "ab", false, true);
        SimpleStringFilter f1b = f(AbstractFilterTestBase.PROPERTY2, "ab", false, true);
        SimpleStringFilter f2 = f(AbstractFilterTestBase.PROPERTY1, "cd", false, true);
        SimpleStringFilter f2b = f(AbstractFilterTestBase.PROPERTY1, "cd", false, true);
        SimpleStringFilter f3 = f(AbstractFilterTestBase.PROPERTY1, "ab", true, true);
        SimpleStringFilter f3b = f(AbstractFilterTestBase.PROPERTY1, "ab", true, true);
        SimpleStringFilter f4 = f(AbstractFilterTestBase.PROPERTY1, "ab", false, false);
        SimpleStringFilter f4b = f(AbstractFilterTestBase.PROPERTY1, "ab", false, false);
        // equal but not same instance
        Assert.assertEquals(f1, f1b);
        Assert.assertEquals(f2, f2b);
        Assert.assertEquals(f3, f3b);
        Assert.assertEquals(f4, f4b);
        // more than one property differ
        Assert.assertFalse(f1.equals(f2));
        Assert.assertFalse(f1.equals(f3));
        Assert.assertFalse(f1.equals(f4));
        Assert.assertFalse(f2.equals(f1));
        Assert.assertFalse(f2.equals(f3));
        Assert.assertFalse(f2.equals(f4));
        Assert.assertFalse(f3.equals(f1));
        Assert.assertFalse(f3.equals(f2));
        Assert.assertFalse(f3.equals(f4));
        Assert.assertFalse(f4.equals(f1));
        Assert.assertFalse(f4.equals(f2));
        Assert.assertFalse(f4.equals(f3));
        // only one property differs
        Assert.assertFalse(filter.equals(f1));
        Assert.assertFalse(filter.equals(f2));
        Assert.assertFalse(filter.equals(f3));
        Assert.assertFalse(filter.equals(f4));
        Assert.assertFalse(f1.equals(null));
        Assert.assertFalse(f1.equals(new Object()));
        Assert.assertEquals(f1.hashCode(), f1b.hashCode());
        Assert.assertEquals(f2.hashCode(), f2b.hashCode());
        Assert.assertEquals(f3.hashCode(), f3b.hashCode());
        Assert.assertEquals(f4.hashCode(), f4b.hashCode());
    }

    @Test
    public void testNonExistentProperty() {
        Assert.assertFalse(passes("other1", "ab", false, true));
    }

    @Test
    public void testNullValueForProperty() {
        AbstractFilterTestBase.TestItem<String, String> item = SimpleStringFilterTest.createTestItem();
        addItemProperty("other1", new AbstractFilterTestBase.NullProperty());
        Assert.assertFalse(f("other1", "ab", false, true).passesFilter(null, item));
    }
}

