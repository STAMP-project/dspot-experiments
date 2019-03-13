package com.vaadin.tests.themes.valo;


import Keys.ESCAPE;
import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class ModalWindowTest extends SingleBrowserTest {
    @Test
    public void modalAnimationsAreDisabled() {
        openTestURL("theme=tests-valo-disabled-animations");
        openModalWindow();
        WebElement modalityCurtain = findElement(By.className("v-window-modalitycurtain"));
        Assert.assertThat(modalityCurtain.getCssValue("-webkit-animation-name"), Is.is("none"));
    }

    @Test
    public void modal_curtains_close_correctly() {
        openTestURL();
        openModalWindow();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(findHeaderElement()).clickAndHold().moveByOffset(1, 1).perform();
        Assert.assertTrue(isElementPresent(By.className("v-window-draggingCurtain")));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(findHeaderElement(), ESCAPE).release().perform();
        verifyCurtainsNotPresent();
        openModalWindow();
        new org.openqa.selenium.interactions.Actions(getDriver()).moveToElement(findResizingElement()).clickAndHold().moveByOffset(1, 1).perform();
        Assert.assertTrue(isElementPresent(By.className("v-window-resizingCurtain")));
        new org.openqa.selenium.interactions.Actions(getDriver()).sendKeys(findResizingElement(), ESCAPE).release().perform();
        verifyCurtainsNotPresent();
    }
}

