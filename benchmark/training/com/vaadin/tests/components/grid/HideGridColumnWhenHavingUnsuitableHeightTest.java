package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.logging.Level;
import org.junit.Test;
import org.openqa.selenium.By;


public class HideGridColumnWhenHavingUnsuitableHeightTest extends SingleBrowserTest {
    @Test
    public void hideAndScroll() {
        openTestURL("debug");
        GridElement grid = $(GridElement.class).first();
        getSidebarOpenButton(grid).click();
        // Hide first column
        getSidebarPopup().findElements(By.tagName("td")).get(0).click();
        grid.scrollToRow(25);
        assertNoDebugMessage(Level.SEVERE);
    }
}

