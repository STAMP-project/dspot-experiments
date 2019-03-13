package com.vaadin.v7.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridCellElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("grid")
public class JavaScriptRenderersTest extends MultiBrowserTest {
    @Test
    public void testJavaScriptRenderer() {
        setDebug(true);
        openTestURL();
        GridElement grid = $(GridElement.class).first();
        GridCellElement cell_1_1 = grid.getCell(1, 1);
        GridCellElement cell_2_2 = grid.getCell(2, 2);
        // Verify render functionality
        Assert.assertEquals("Bean(2, 0)", cell_1_1.getText());
        Assert.assertEquals("string2", cell_2_2.getText());
        // Verify init functionality
        Assert.assertEquals("1", cell_1_1.getAttribute("column"));
        // Verify onbrowserevent
        cell_1_1.click();
        Assert.assertTrue(cell_1_1.getText().startsWith("Clicked 1 with key 2 at"));
    }
}

