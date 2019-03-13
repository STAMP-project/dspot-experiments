package com.vaadin.tests.components.window;


import com.vaadin.testbench.By;
import com.vaadin.testbench.commands.TestBenchElementCommands;
import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.WindowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


/**
 * Tests that a ComboBox at the bottom of a Window remains visible when clicked.
 *
 * @author Vaadin Ltd
 */
public class ComboboxScrollableWindowTest extends MultiBrowserTest {
    @Test
    public void testWindowScrollbars() throws Exception {
        openTestURL();
        WindowElement window = $(WindowElement.class).id(ComboboxScrollableWindow.WINDOW_ID);
        WebElement scrollableElement = window.findElement(By.className("v-scrollable"));
        TestBenchElementCommands scrollable = testBenchElement(scrollableElement);
        scrollable.scroll(1000);
        int beforeClick = getScrollTop(scrollableElement);
        ComboBoxElement comboBox = $(ComboBoxElement.class).id(ComboboxScrollableWindow.COMBOBOX_ID);
        Point location = comboBox.getLocation();
        comboBox.openPopup();
        waitForElementPresent(By.className("v-filterselect-suggestpopup"));
        Assert.assertEquals("Clicking should not cause scrolling", beforeClick, getScrollTop(scrollableElement));
        Assert.assertEquals("ComboBox should not move along x-axis", location.getX(), comboBox.getLocation().getX());
        Assert.assertEquals("ComboBox should not move along y-axis", location.getY(), comboBox.getLocation().getY());
    }
}

