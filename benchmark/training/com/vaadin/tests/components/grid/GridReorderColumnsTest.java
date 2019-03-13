package com.vaadin.tests.components.grid;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class GridReorderColumnsTest extends MultiBrowserTest {
    @Test
    public void testEmptyGrid() {
        openTestURL();
        testReordering("emptyGrid");
    }

    @Test
    public void testContentGrid() {
        openTestURL();
        testReordering("contentGrid");
    }
}

