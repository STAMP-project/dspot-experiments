package org.junit.tests.description;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.Description;


public class SuiteDescriptionTest {
    Description childless = Description.createSuiteDescription("a");

    Description anotherChildless = Description.createSuiteDescription("a");

    Description namedB = Description.createSuiteDescription("b");

    Description twoKids = descriptionWithTwoKids("foo", "bar");

    Description anotherTwoKids = descriptionWithTwoKids("foo", "baz");

    @Test
    public void equalsIsCorrect() {
        Assert.assertEquals(childless, anotherChildless);
        Assert.assertFalse(childless.equals(namedB));
        Assert.assertEquals(childless, twoKids);
        Assert.assertEquals(twoKids, anotherTwoKids);
        Assert.assertFalse(twoKids.equals(new Integer(5)));
    }

    @Test
    public void hashCodeIsReasonable() {
        Assert.assertEquals(childless.hashCode(), anotherChildless.hashCode());
        Assert.assertFalse(((childless.hashCode()) == (namedB.hashCode())));
    }
}

