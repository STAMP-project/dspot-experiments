package com.vaadin.tests.components.panel;


import com.vaadin.testbench.elements.PanelElement;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


public class PanelSetScrollTopWithLargeNumberTest extends MultiBrowserTest {
    private PanelElement panel;

    @Test
    public void testSetScrollTopWithLargeNumber() {
        WebElement contentNode = panel.findElement(By.className("v-panel-content"));
        int panelContentScrollTop = ((Number) (executeScript("return arguments[0].scrollTop", contentNode))).intValue();
        AbstractTB3Test.assertGreater("Panel should scroll when scrollTop is set to a number larger than panel height", panelContentScrollTop, 0);
    }
}

