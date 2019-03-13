package com.vaadin.ui;


import com.vaadin.shared.ui.splitpanel.VerticalSplitPanelState;
import org.junit.Assert;
import org.junit.Test;


public class VerticalSplitPanelTest {
    @Test
    public void primaryStyleName() {
        Assert.assertEquals(new VerticalSplitPanelState().primaryStyleName, new VerticalSplitPanel().getPrimaryStyleName());
    }
}

