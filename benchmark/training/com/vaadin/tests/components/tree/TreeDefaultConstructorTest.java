package com.vaadin.tests.components.tree;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class TreeDefaultConstructorTest extends SingleBrowserTest {
    @Test
    public void default_constructor_no_exceptions() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
    }
}

