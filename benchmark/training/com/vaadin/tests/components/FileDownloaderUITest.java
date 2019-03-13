package com.vaadin.tests.components;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


public class FileDownloaderUITest extends MultiBrowserTest {
    @Test
    public void ensureButtonWithDownloaderCanBeRemoved() {
        openTestURL();
        By id = By.id("com.vaadin.ui.ButtonDynamicimage");
        assertElementPresent(id);
        $(ButtonElement.class).caption("Remove first download button").first().click();
        assertElementNotPresent(id);
    }
}

