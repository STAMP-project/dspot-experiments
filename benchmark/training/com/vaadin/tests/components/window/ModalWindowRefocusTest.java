package com.vaadin.tests.components.window;


import Keys.TAB;
import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.testbench.org.openqa.selenium.By;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


/**
 * Tests that a modal window is focused on creation and that on closing a window
 * focus is given to underlying modal window
 *
 * @author Vaadin Ltd
 */
public class ModalWindowRefocusTest extends ModalWindowFocusTest {
    /**
     * Open modal window -> click modality curtain to remove focus from Window
     * -> press tab thrice so that focus goes into Window again and focuses the
     * text field so that the focus event is fired.
     */
    @Test
    public void testFocusOutsideModal() {
        waitForElementPresent(By.id("modalWindowButton"));
        WebElement button = findElement(By.id("modalWindowButton"));
        button.click();
        waitForElementPresent(By.id("focusfield"));
        WebElement curtain = findElement(org.openqa.selenium.By.className("v-window-modalitycurtain"));
        testBenchElement(curtain).click(getXOffset(curtain, 20), getYOffset(curtain, 20));
        pressKeyAndWait(TAB);
        pressKeyAndWait(TAB);
        pressKeyAndWait(TAB);
        TextFieldElement tfe = $(TextFieldElement.class).id("focusfield");
        Assert.assertTrue("First TextField should have received focus", "this has been focused".equals(tfe.getValue()));
    }
}

