package com.vaadin.tests.components.treegrid;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class TreeGridEmptyTest extends SingleBrowserTest {
    @Test
    public void empty_treegrid_initialized_correctly() {
        setDebug(true);
        openTestURL();
        assertNoErrorNotifications();
        Assert.assertFalse(isElementPresent(By.className("v-errorindicator")));
    }
}

