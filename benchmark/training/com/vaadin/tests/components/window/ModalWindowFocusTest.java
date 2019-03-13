package com.vaadin.tests.components.window;


import Keys.ESCAPE;
import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowFocusTest extends MultiBrowserTest {
    /**
     * First scenario: press first button -> two windows appear, press Esc two
     * times -> all windows should be closed
     */
    @Test
    public void testModalWindowFocusTwoWindows() throws IOException {
        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();
        waitForElementPresent(By.id("windowButton"));
        Assert.assertTrue("Second window should be opened", ((findElements(By.id("windowButton")).size()) == 1));
        pressKeyAndWait(ESCAPE);
        pressKeyAndWait(ESCAPE);
        Assert.assertTrue("All windows should be closed", ((findElements(By.className("v-window")).size()) == 0));
    }

    /**
     * Second scenario: press first button -> two windows appear, press button
     * in the 2nd window -> 3rd window appears on top, press Esc three times ->
     * all windows should be closed
     */
    @Test
    public void testModalWindowFocusPressButtonInWindow() throws IOException {
        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();
        waitForElementPresent(By.id("windowButton"));
        WebElement buttonInWindow = findElement(By.id("windowButton"));
        buttonInWindow.click();
        waitForElementPresent(By.id("window3"));
        Assert.assertTrue("Third window should be opened", ((findElements(By.id("window3")).size()) == 1));
        pressKeyAndWait(ESCAPE);
        pressKeyAndWait(ESCAPE);
        pressKeyAndWait(ESCAPE);
        Assert.assertTrue("All windows should be closed", ((findElements(By.className("v-window")).size()) == 0));
    }

    /**
     * Third scenario: press second button -> a modal unclosable and
     * unresizeable window with two text fields opens -> second text field is
     * automatically focused -> press tab -> the focus rolls around to the top
     * of the modal window -> the first text field is focused and shows a text
     */
    @Test
    public void testModalWindowWithoutButtonsFocusHandling() {
        waitForElementPresent(By.id("modalWindowButton"));
        WebElement button = findElement(By.id("modalWindowButton"));
        button.click();
        waitForElementPresent(By.id("focusfield"));
        pressKeyAndWait(TAB);
        TextFieldElement tfe = $(TextFieldElement.class).id("focusfield");
        Assert.assertTrue("First TextField should have received focus", "this has been focused".equals(tfe.getValue()));
    }

    @Test
    public void verifyAriaModalAndRoleAttributes() {
        waitForElementPresent(By.id("firstButton"));
        WebElement button = findElement(By.id("firstButton"));
        button.click();
        waitForElementPresent(By.className("v-window"));
        WebElement windowElement = findElement(By.className("v-window"));
        String ariaModal = windowElement.getAttribute("aria-modal");
        Assert.assertEquals("true", ariaModal);
        String role = windowElement.getAttribute("role");
        Assert.assertEquals("dialog", role);
    }
}

