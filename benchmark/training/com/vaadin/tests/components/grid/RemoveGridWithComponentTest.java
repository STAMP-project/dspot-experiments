package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class RemoveGridWithComponentTest extends SingleBrowserTest {
    @Test
    public void RemoveGrid_CheckGridNotPresent() {
        openTestURL();
        ButtonElement button = $(ButtonElement.class).id("remove-grid");
        button.click();
        assertElementNotPresent(By.id("grid-with-component"));
    }
}

