package com.vaadin.tests.components.window;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TwinColSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class WindowTwinColSelectTest extends MultiBrowserTest {
    @Test
    public void testBothVisibleInitially() {
        openTestURL();
        TwinColSelectElement twinColSelect = $(TwinColSelectElement.class).first();
        WebElement optionsElement = twinColSelect.getOptionsElement();
        WebElement selectionsElement = twinColSelect.getSelectionsElement();
        Assert.assertTrue(optionsElement.isDisplayed());
        Assert.assertTrue(selectionsElement.isDisplayed());
        Assert.assertThat(selectionsElement.getLocation().getY(), CoreMatchers.is(optionsElement.getLocation().getY()));
    }

    @Test
    public void testBothVisibleAfterResize() {
        openTestURL();
        waitForElementPresent(By.className("v-window-resizebox"));
        TwinColSelectElement twinColSelect = $(TwinColSelectElement.class).first();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(findElement(By.className("v-window-resizebox"))).clickAndHold().moveByOffset((-30), (-30)).release().build().perform();
        WebElement optionsElement = twinColSelect.getOptionsElement();
        WebElement selectionsElement = twinColSelect.getSelectionsElement();
        Assert.assertTrue(optionsElement.isDisplayed());
        Assert.assertTrue(selectionsElement.isDisplayed());
        Assert.assertThat(selectionsElement.getLocation().getY(), CoreMatchers.is(optionsElement.getLocation().getY()));
    }
}

