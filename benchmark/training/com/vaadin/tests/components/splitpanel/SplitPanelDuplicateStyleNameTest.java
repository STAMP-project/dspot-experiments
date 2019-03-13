package com.vaadin.tests.components.splitpanel;


import com.vaadin.shared.ui.splitpanel.HorizontalSplitPanelState;
import com.vaadin.testbench.elements.HorizontalSplitPanelElement;
import com.vaadin.testbench.elements.VerticalSplitPanelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test for duplicate primary style name in SplitPanel.
 *
 * @author Vaadin Ltd
 */
public class SplitPanelDuplicateStyleNameTest extends MultiBrowserTest {
    @Test
    public void testHorizontalNoDuplicateStyleName() {
        HorizontalSplitPanelElement split = $(HorizontalSplitPanelElement.class).first();
        String classNames = split.getAttribute("class");
        String primaryStyleName = new HorizontalSplitPanelState().primaryStyleName;
        Assert.assertEquals("Duplicate primary style name should not exist", classNames.indexOf(primaryStyleName), classNames.lastIndexOf(primaryStyleName));
    }

    @Test
    public void testVerticalNoDuplicateStyleName() {
        VerticalSplitPanelElement split = $(VerticalSplitPanelElement.class).first();
        String classNames = split.getAttribute("class");
        String primaryStyleName = new HorizontalSplitPanelState().primaryStyleName;
        Assert.assertEquals("Duplicate primary style name should not exist", classNames.indexOf(primaryStyleName), classNames.lastIndexOf(primaryStyleName));
    }
}

