package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


public class GridLayoutCaptionAlignmentTest extends MultiBrowserTest {
    @Test
    public void testCaptionAlignments() throws IOException {
        openTestURL();
        compareScreen("gridlayout-caption-alignment");
    }
}

