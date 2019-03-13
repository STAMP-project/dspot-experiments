package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class UnnecessaryPaddingInResponsiveUITest extends SingleBrowserTest {
    @Test
    public void testUIShouldHaveNoPaddingTop() {
        openTestURL();
        WebElement ui = vaadinElementById("UI");
        String paddingTop = ui.getCssValue("padding-top");
        Integer paddingHeight = Integer.parseInt(paddingTop.substring(0, ((paddingTop.length()) - 2)));
        Assert.assertThat(paddingHeight, Matchers.equalTo(0));
    }
}

