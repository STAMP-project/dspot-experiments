package com.vaadin.tests.components.grid;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


@TestCategory("grid")
public class GridEditorFrozenColumnsUITest extends MultiBrowserTest {
    @Test
    public void testEditorWithFrozenColumns() throws IOException {
        openTestURL();
        openEditor(10);
        compareScreen("noscroll");
        scrollGridHorizontallyTo(100);
        compareScreen("scrolled");
    }
}

