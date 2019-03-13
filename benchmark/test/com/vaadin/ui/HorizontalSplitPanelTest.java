package com.vaadin.ui;


import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;
import org.junit.Assert;
import org.junit.Test;


public class HorizontalSplitPanelTest {
    @Test
    public void primaryStyleName() {
        Assert.assertEquals(new HorizontalSplitPanelState().primaryStyleName, new HorizontalSplitPanel().getPrimaryStyleName());
    }
}

