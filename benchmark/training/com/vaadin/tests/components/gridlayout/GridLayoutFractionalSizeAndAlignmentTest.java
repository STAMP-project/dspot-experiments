package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class GridLayoutFractionalSizeAndAlignmentTest extends MultiBrowserTest {
    @Test
    public void ensureNoScrollbarsWithAlignBottomRight() throws IOException {
        openTestURL();
        compareScreen("noscrollbars");
    }
}

