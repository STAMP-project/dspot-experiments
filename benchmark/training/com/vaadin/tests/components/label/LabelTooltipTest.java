package com.vaadin.tests.components.label;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class LabelTooltipTest extends MultiBrowserTest {
    @Test
    public void testLabelTooltip() throws IOException {
        openTestURL();
        assertTooltips();
    }

    @Test
    public void testLabelToolTipChameleonTheme() throws IOException {
        openTestURL("theme=chameleon");
        assertTooltips();
    }

    @Test
    public void testLabelToolTipRunoTheme() throws IOException {
        openTestURL("theme=runo");
        assertTooltips();
    }
}

