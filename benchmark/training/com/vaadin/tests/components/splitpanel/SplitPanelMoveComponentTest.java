package com.vaadin.tests.components.splitpanel;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class SplitPanelMoveComponentTest extends MultiBrowserTest {
    private static final String BUTTON_TEXT = "Button in splitpanel. Click to move to the other side";

    @Test
    public void moveComponent() {
        openTestURL();
        Assert.assertEquals(SplitPanelMoveComponentTest.BUTTON_TEXT, getFirstChild().getText());
        getFirstChild().click();
        Assert.assertEquals(SplitPanelMoveComponentTest.BUTTON_TEXT, getSecondChild().getText());
        getSecondChild().click();
        Assert.assertEquals(SplitPanelMoveComponentTest.BUTTON_TEXT, getFirstChild().getText());
    }
}

