package com.vaadin.tests.components.splitpanel;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for horizontal split panel height in case when only second component is
 * set.
 *
 * @author Vaadin Ltd
 */
public class HorizontalSplitPanelHeightTest extends MultiBrowserTest {
    @Test
    public void testHorizontalWithoutFirstComponent() {
        testSplitPanel("Horizontal 1");
    }

    @Test
    public void testHorizontalWithFirstComponent() {
        testSplitPanel("Horizontal 2");
    }

    @Test
    public void testHorizontalWithFixedHeight() {
        testSplitPanel("Horizontal 3");
    }

    @Test
    public void testVerticalWithoutFirstComponent() {
        testSplitPanel("Vertical 1");
    }
}

