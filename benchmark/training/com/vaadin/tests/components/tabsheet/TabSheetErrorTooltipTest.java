package com.vaadin.tests.components.tabsheet;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class TabSheetErrorTooltipTest extends MultiBrowserTest {
    @Test
    public void checkTooltips() throws IOException {
        openTestURL();
        assertTabHasNoTooltipNorError(0);
        assertTabHasTooltipAndError(1, "", "Error!");
        assertTabHasTooltipAndError(2, "This is a tab", "");
        assertTabHasTooltipAndError(3, "This tab has both an error and a description", "Error!");
    }
}

