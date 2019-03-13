package com.vaadin.tests.components.window;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Tests dialogs with WAI-ARIA.
 *
 * @author Vaadin Ltd
 */
public class ExtraWindowShownWaiAriaTest extends MultiBrowserTest {
    @Test
    public void testDialogs() throws InterruptedException {
        openTestURL();
        List<ButtonElement> buttons = $(ButtonElement.class).all();
        Assert.assertFalse(buttons.isEmpty());
        // open alert dialog
        ButtonElement button = buttons.get(0);
        button.click();
        // ensure dialog opened
        waitForElementPresent(By.className("v-window"));
        WindowElement window = $(WindowElement.class).first();
        // ensure correct attributes
        Assert.assertEquals("alertdialog", window.getAttribute("role"));
        WebElement header = window.findElement(By.className("v-window-header"));
        Assert.assertEquals(header.getAttribute("id"), window.getAttribute("aria-labelledby"));
        WebElement label = window.findElement(By.className("v-label"));
        Assert.assertEquals(label.getAttribute("id"), window.getAttribute("aria-describedby"));
        List<WebElement> wButtons = window.findElements(By.className("v-button"));
        Assert.assertEquals("button", wButtons.get(0).getAttribute("role"));
        Assert.assertEquals("button", wButtons.get(1).getAttribute("role"));
        // close dialog
        wButtons.get(0).click();
        // ensure dialog closed
        List<WindowElement> windows = $(WindowElement.class).all();
        Assert.assertTrue(windows.isEmpty());
        // check additional description (second checkbox on the page)
        List<CheckBoxElement> checkBoxes = $(CheckBoxElement.class).all();
        WebElement input = checkBoxes.get(1).findElement(By.tagName("input"));
        // ensure that not checked yet
        Assert.assertEquals(null, input.getAttribute("checked"));
        input.click();
        // ensure that checked now
        Assert.assertEquals("true", input.getAttribute("checked"));
        // open alert dialog
        button = $(ButtonElement.class).first();
        button.click();
        waitForElementPresent(By.className("v-window"));
        // ensure correct attributes
        window = $(WindowElement.class).first();
        List<WebElement> labels = window.findElements(By.className("v-label"));
        Assert.assertEquals((((labels.get(0).getAttribute("id")) + " ") + (labels.get(1).getAttribute("id"))), window.getAttribute("aria-describedby"));
        // close dialog
        wButtons = window.findElements(By.className("v-button"));
        wButtons.get(0).click();
        // ensure dialog closed
        windows = $(WindowElement.class).all();
        Assert.assertTrue(windows.isEmpty());
        // add prefix and postfix
        List<TextFieldElement> textFields = $(TextFieldElement.class).all();
        textFields.get(0).sendKeys("Important");
        textFields.get(1).sendKeys(" - do ASAP");
        // open alert dialog
        button = $(ButtonElement.class).first();
        button.click();
        waitForElementPresent(By.className("v-window"));
        // ensure the assistive spans have been added to the header
        window = $(WindowElement.class).first();
        header = window.findElement(By.className("v-window-header"));
        List<WebElement> assistiveElements = header.findElements(By.className("v-assistive-device-only"));
        Assert.assertEquals("Important", assistiveElements.get(0).getAttribute("innerHTML"));
        Assert.assertEquals(" - do ASAP", assistiveElements.get(1).getAttribute("innerHTML"));
    }
}

