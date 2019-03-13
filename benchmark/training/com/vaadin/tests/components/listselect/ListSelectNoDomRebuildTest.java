package com.vaadin.tests.components.listselect;


import Keys.SHIFT;
import com.vaadin.testbench.elements.ListSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class ListSelectNoDomRebuildTest extends SingleBrowserTest {
    @Test
    public void testNoDomRebuild() {
        openTestURL();
        // Testbench doesn't seem to support sending key events to the right
        // location, so we will just verify that the DOM is not rebuilt
        selectMenuPath("Component", "Selection", "Multi select");
        selectMenuPath("Component", "Listeners", "Value change listener");
        ListSelectElement list = $(ListSelectElement.class).first();
        List<WebElement> options = list.findElements(By.tagName("option"));
        assertNotStale(options);
        options.get(4).click();
        assertNotStale(options);
        new org.openqa.selenium.interactions.Actions(driver).keyDown(SHIFT).perform();
        options.get(2).click();
        options.get(6).click();
        new org.openqa.selenium.interactions.Actions(driver).keyUp(SHIFT).perform();
        assertNotStale(options);
    }
}

