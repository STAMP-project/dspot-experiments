package com.vaadin.tests.components.menubar;


import Keys.ARROW_DOWN;
import Keys.ARROW_RIGHT;
import Keys.DOWN;
import Keys.ENTER;
import Keys.LEFT;
import Keys.RIGHT;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class MenuBarNavigationKeyboardTest extends MultiBrowserTest {
    @Test
    public void testKeyboardNavigation() throws Exception {
        openTestURL();
        openMenu("File");
        getMenuBar().sendKeys(DOWN, DOWN, DOWN, DOWN, RIGHT, ENTER);
        Assert.assertEquals("1. MenuItem File/Export../As PDF... selected", getLogRow(0));
        openMenu("File");
        getMenuBar().sendKeys(RIGHT, RIGHT, RIGHT, ENTER);
        Assert.assertEquals("2. MenuItem Help selected", getLogRow(0));
        openMenu("Edit");
        getMenuBar().sendKeys(LEFT, DOWN, DOWN, ENTER);
        Assert.assertEquals("3. MenuItem Edit/Cut selected", getLogRow(0));
        openMenu("Edit");
        getMenuBar().sendKeys(ENTER);
        Assert.assertEquals("3. MenuItem Edit/Cut selected", getLogRow(0));
        getMenuBar().sendKeys(ENTER);
        Assert.assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));
        /* Enter while menubar has focus but no selection should focus "File" */
        getMenuBar().sendKeys(ENTER);
        Assert.assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));
        /* Enter again should open File and focus Open */
        getMenuBar().sendKeys(ENTER);
        Assert.assertEquals("4. MenuItem Edit/Copy selected", getLogRow(0));
        getMenuBar().sendKeys(ENTER);
        Assert.assertEquals("5. MenuItem File/Open selected", getLogRow(0));
    }

    @Test
    public void testMenuSelectWithKeyboardStateClearedCorrectly() throws InterruptedException {
        openTestURL();
        openMenu("File");
        getMenuBar().sendKeys(ARROW_RIGHT, ARROW_RIGHT, ARROW_RIGHT, ENTER);
        Assert.assertTrue("Help menu was not selected", logContainsText("MenuItem Help selected"));
        new org.openqa.selenium.interactions.Actions(driver).moveToElement(getMenuBar(), 10, 10).perform();
        Assert.assertFalse("Unexpected MenuBar popup is visible", isElementPresent(By.className("v-menubar-popup")));
    }

    @Test
    public void testNavigatingToDisabled() throws InterruptedException {
        openTestURL();
        openMenu("File");
        getMenuBar().sendKeys(ARROW_RIGHT, ARROW_RIGHT, ARROW_RIGHT, ARROW_RIGHT, ENTER);
        Assert.assertTrue("Disabled menu not selected", getFocusedElement().getText().contains("Disabled"));
        Assert.assertFalse("Disabled menu was triggered", logContainsText("MenuItem Disabled selected"));
        getMenuBar().sendKeys(ARROW_DOWN, ENTER);
        Assert.assertFalse("Disabled submenu was opened", logContainsText("MenuItem Disabled/Can't reach selected"));
        Assert.assertTrue("Disabled menu not selected", getFocusedElement().getText().contains("Disabled"));
    }
}

