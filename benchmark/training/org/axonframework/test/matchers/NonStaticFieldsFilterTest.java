package org.axonframework.test.matchers;


import org.junit.Assert;
import org.junit.Test;


public class NonStaticFieldsFilterTest {
    @SuppressWarnings("unused")
    private static String staticField;

    @SuppressWarnings("unused")
    private String nonStaticField;

    @Test
    public void testAcceptNonTransientField() throws Exception {
        Assert.assertTrue(NonStaticFieldsFilter.instance().accept(getClass().getDeclaredField("nonStaticField")));
    }

    @Test
    public void testRejectTransientField() throws Exception {
        Assert.assertFalse(NonStaticFieldsFilter.instance().accept(getClass().getDeclaredField("staticField")));
    }
}

