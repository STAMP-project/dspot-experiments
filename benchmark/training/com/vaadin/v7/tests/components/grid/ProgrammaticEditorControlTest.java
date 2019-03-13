package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


@TestCategory("grid")
public class ProgrammaticEditorControlTest extends SingleBrowserTest {
    @Test
    public void multipleOpenFromServerSide() {
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        ButtonElement editButton = $(ButtonElement.class).caption("Edit").first();
        ButtonElement cancelButton = $(ButtonElement.class).caption("Cancel").first();
        editButton.click();
        assertEditorFieldContents(grid, "test");
        cancelButton.click();
        assertEditorNotPresent(grid);
        editButton.click();
        assertEditorFieldContents(grid, "test");
    }
}

