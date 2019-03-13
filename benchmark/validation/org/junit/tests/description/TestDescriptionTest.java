package org.junit.tests.description;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;


public class TestDescriptionTest {
    @Test
    public void equalsIsFalseForNonTestDescription() {
        Assert.assertFalse(Description.createTestDescription(getClass(), "a").equals(new Integer(5)));
    }

    @Test
    public void equalsIsTrueForSameNameAndNoExplicitUniqueId() {
        Assert.assertTrue(Description.createSuiteDescription("Hello").equals(Description.createSuiteDescription("Hello")));
    }

    @Test
    public void equalsIsFalseForSameNameAndDifferentUniqueId() {
        Assert.assertFalse(Description.createSuiteDescription("Hello", 2).equals(Description.createSuiteDescription("Hello", 3)));
    }
}

