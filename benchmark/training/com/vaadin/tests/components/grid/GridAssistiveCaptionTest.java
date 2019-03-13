package com.vaadin.tests.components.grid;


import GridElement.GridCellElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class GridAssistiveCaptionTest extends SingleBrowserTest {
    @Test
    public void checkGridAriaLabel() {
        openTestURL();
        GridElement.GridCellElement headerCell = $(GridElement.class).first().getHeaderCell(0, 0);
        // default grid has no aria-label
        Assert.assertNull("Column should not contain aria-label", headerCell.getAttribute("aria-label"));
        $(ButtonElement.class).caption("addAssistiveCaption").first().click();
        Assert.assertTrue("Column should contain aria-label", headerCell.getAttribute("aria-label").equals("Press Enter to sort."));
        $(ButtonElement.class).caption("removeAssistiveCaption").first().click();
        Assert.assertNull("Column should not contain aria-label", headerCell.getAttribute("aria-label"));
    }
}

