package com.vaadin.tests.components.grid;


import GridElement.GridCellElement;
import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class GridEditorScrollSyncTest extends MultiBrowserTest {
    private GridElement grid;

    @Test
    public void testScrollAndEdit() {
        openTestURL();
        grid = $(GridElement.class).first();
        scrollLeft(300);
        openEditor();
        GridElement.GridCellElement rowCell = grid.getCell(1, 6);
        TestBenchElement editorField = grid.getEditor().getField(6);
        assertPosition(rowCell.getLocation().getX(), editorField.getWrappedElement().getLocation().getX());
    }
}

