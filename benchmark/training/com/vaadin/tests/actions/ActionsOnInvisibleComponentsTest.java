package com.vaadin.tests.actions;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class ActionsOnInvisibleComponentsTest extends MultiBrowserTest {
    private static final String LAST_INIT_LOG = "3. 'C' triggers a click on a visible and enabled button";

    @Test
    public void testShortcutsOnInvisibleDisabledButtons() {
        openTestURL();
        Assert.assertEquals(ActionsOnInvisibleComponentsTest.LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("a");
        Assert.assertEquals(ActionsOnInvisibleComponentsTest.LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("b");
        Assert.assertEquals(ActionsOnInvisibleComponentsTest.LAST_INIT_LOG, getLogRow(0));
        invokeShortcut("c");
        Assert.assertEquals("4. Click event for enabled button", getLogRow(0));
    }
}

