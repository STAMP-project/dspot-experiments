package com.vaadin.tests.elements.link;


import com.vaadin.testbench.elements.LinkElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class LinkUITest extends MultiBrowserTest {
    LinkElement link;

    @Test
    public void testLinkClick() {
        String currentUrl = getDriver().getCurrentUrl();
        Assert.assertTrue((("Current URL " + currentUrl) + " should end with LinkUI?"), currentUrl.endsWith("LinkUI"));
        link.click();
        currentUrl = getDriver().getCurrentUrl();
        Assert.assertFalse((("Current URL " + currentUrl) + " should not end with LinkUI?"), currentUrl.endsWith("LinkUI"));
    }

    @Test
    public void getLinkCaption() {
        Assert.assertEquals("server root", link.getCaption());
    }
}

