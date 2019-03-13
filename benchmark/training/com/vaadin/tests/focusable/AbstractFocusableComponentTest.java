package com.vaadin.tests.focusable;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public abstract class AbstractFocusableComponentTest extends MultiBrowserTest {
    @Test
    public void testProgrammaticFocus() {
        selectMenuPath("Component", "State", "Set focus");
        Assert.assertTrue("Component should be focused", isFocused());
    }

    @Test
    public void testTabIndex() {
        Assert.assertEquals("0", getTabIndex());
        selectMenuPath("Component", "State", "Tab index", "-1");
        Assert.assertEquals("-1", getTabIndex());
        selectMenuPath("Component", "State", "Tab index", "10");
        Assert.assertEquals("10", getTabIndex());
    }
}

