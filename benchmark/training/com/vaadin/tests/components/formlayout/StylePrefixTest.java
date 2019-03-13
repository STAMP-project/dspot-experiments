package com.vaadin.tests.components.formlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Test for FormLayout style prefix: custom additional styles should be prefixed
 * with "v-formlayout-", not "v-layout-".
 *
 * @author Vaadin Ltd
 */
public class StylePrefixTest extends MultiBrowserTest {
    @Test
    public void testStylePrefix() {
        openTestURL();
        Assert.assertTrue("Custom style has unexpected prefix", isElementPresent(By.className("v-formlayout-mystyle")));
    }
}

