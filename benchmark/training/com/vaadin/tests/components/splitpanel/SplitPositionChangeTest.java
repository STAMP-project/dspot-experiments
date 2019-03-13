package com.vaadin.tests.components.splitpanel;


import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;


/**
 * Test for {@link SplitPositionChangeListeners}.
 *
 * @author Vaadin Ltd
 */
public class SplitPositionChangeTest extends MultiBrowserTest {
    @Test
    public void testHorizontalSplit() {
        HorizontalSplitPanelElement split = $(HorizontalSplitPanelElement.class).first();
        WebElement splitter = split.findElement(By.className("v-splitpanel-hsplitter"));
        int position = splitter.getLocation().getX();
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(50, 0).release().perform();
        assertPosition(position, splitter.getLocation().getX());
        assertLogText(true);
    }

    @Test
    public void testVerticalSplit() {
        VerticalSplitPanelElement split = $(VerticalSplitPanelElement.class).first();
        WebElement splitter = split.findElement(By.className("v-splitpanel-vsplitter"));
        int position = splitter.getLocation().getY();
        Actions actions = new Actions(driver);
        actions.clickAndHold(splitter).moveByOffset(0, 50).release().perform();
        assertPosition(position, splitter.getLocation().getY());
        assertLogText(false);
    }
}

