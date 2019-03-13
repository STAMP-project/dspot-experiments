package com.vaadin.tests.components.grid;


import com.vaadin.testbench.By;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


@TestCategory("grid")
public class GridEditorMultiselectTest extends MultiBrowserTest {
    @Test
    public void testSelectCheckboxesDisabled() {
        openTestURL();
        GridElement grid = openEditor();
        assertCheckboxesEnabled(grid, false);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnSave() {
        openTestURL();
        GridElement grid = openEditor();
        grid.getEditor().save();
        waitForElementNotPresent(By.className("v-grid-editor-cells"));
        assertCheckboxesEnabled(grid, true);
    }

    @Test
    public void testSelectCheckboxesEnabledBackOnCancel() {
        openTestURL();
        GridElement grid = openEditor();
        grid.getEditor().cancel();
        assertCheckboxesEnabled(grid, true);
    }
}

