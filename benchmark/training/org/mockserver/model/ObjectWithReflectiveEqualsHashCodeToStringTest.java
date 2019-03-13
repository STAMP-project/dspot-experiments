package org.mockserver.model;


import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test is mainly for coverage but also to check underlying API is called correctly
 *
 * @author jamesdbloom
 */
public class ObjectWithReflectiveEqualsHashCodeToStringTest {
    @Test
    public void hashCodeIdentical() {
        Assert.assertEquals(new Header("name", "value").hashCode(), new Header("name", "value").hashCode());
    }

    @Test
    public void hashCodeDifferent() {
        Assert.assertNotEquals(new Header("name", "value").hashCode(), new Header("foo", "bar").hashCode());
    }

    @Test
    public void equalsIdentical() {
        Assert.assertTrue(new Header("name", "value").equals(new Header("name", "value")));
    }

    @Test
    public void notEqualsDifferent() {
        Assert.assertFalse(new Header("name", "value").equals(new Header("foo", "bar")));
    }

    @Test
    public void toStringReturnStrings() {
        Assert.assertThat(new Header("name", "value").toString(), IsInstanceOf.instanceOf(String.class));
    }
}

