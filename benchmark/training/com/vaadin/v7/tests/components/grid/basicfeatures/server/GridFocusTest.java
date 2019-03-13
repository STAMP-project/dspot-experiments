package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.testbench.elements.MenuBarElement;
import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for server-side Grid focus features.
 *
 * @author Vaadin Ltd
 */
public class GridFocusTest extends GridBasicFeaturesTest {
    @Test
    public void testFocusListener() {
        selectMenuPath("Component", "Listeners", "Focus listener");
        getGridElement().click();
        Assert.assertTrue("Focus listener should be invoked", getLogRow(0).contains("FocusEvent"));
    }

    @Test
    public void testBlurListener() {
        selectMenuPath("Component", "Listeners", "Blur listener");
        getGridElement().click();
        $(MenuBarElement.class).first().click();
        Assert.assertTrue("Blur listener should be invoked", getLogRow(0).contains("BlurEvent"));
    }

    @Test
    public void testProgrammaticFocus() {
        selectMenuPath("Component", "State", "Set focus");
        Assert.assertTrue("Grid cell (0, 0) should be focused", getGridElement().getCell(0, 0).isFocused());
    }

    @Test
    public void testTabIndex() {
        Assert.assertEquals(getGridElement().getAttribute("tabindex"), "0");
        selectMenuPath("Component", "State", "Tab index", "-1");
        Assert.assertEquals(getGridElement().getAttribute("tabindex"), "-1");
        selectMenuPath("Component", "State", "Tab index", "10");
        Assert.assertEquals(getGridElement().getAttribute("tabindex"), "10");
    }
}

