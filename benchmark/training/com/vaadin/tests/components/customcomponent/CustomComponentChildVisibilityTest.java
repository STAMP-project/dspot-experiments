package com.vaadin.tests.components.customcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CustomComponentChildVisibilityTest extends MultiBrowserTest {
    @Test
    public void childVisibilityIsSet() {
        openTestURL();
        Assert.assertTrue(isChildElementVisible());
        $(ButtonElement.class).first().click();
        Assert.assertFalse(isChildElementVisible());
    }
}

