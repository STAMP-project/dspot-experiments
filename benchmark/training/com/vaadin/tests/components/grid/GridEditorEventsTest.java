package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class GridEditorEventsTest extends MultiBrowserTest {
    @Test
    public void editorEvents() throws InterruptedException {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        assertEditorEvents(0, grid);
        assertEditorEvents(1, grid);
    }
}

