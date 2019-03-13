package com.vaadin.tests.components.table;


import com.vaadin.testbench.elements.TableElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests sorting labels in table.
 *
 * @author Vaadin Ltd
 */
public class SortLabelsInTableTest extends MultiBrowserTest {
    @Test
    public void testSorting() {
        openTestURL();
        TableElement table = $(TableElement.class).first();
        // check unsorted
        Assert.assertEquals("Text 0", table.getCell(0, 0).getText());
        Assert.assertEquals("Label 0", table.getCell(0, 1).getText());
        Assert.assertEquals("Text 14", table.getCell(14, 0).getText());
        Assert.assertEquals("Label 14", table.getCell(14, 1).getText());
        // sort by first column (ascending order)
        table.getHeaderCell(0).click();
        // check sorted
        Assert.assertEquals("Text 0", table.getCell(0, 0).getText());
        Assert.assertEquals("Label 0", table.getCell(0, 1).getText());
        Assert.assertEquals("Text 10", table.getCell(2, 0).getText());
        Assert.assertEquals("Label 10", table.getCell(2, 1).getText());
        Assert.assertEquals("Text 19", table.getCell(11, 0).getText());
        Assert.assertEquals("Label 19", table.getCell(11, 1).getText());
        Assert.assertEquals("Text 4", table.getCell(14, 0).getText());
        Assert.assertEquals("Label 4", table.getCell(14, 1).getText());
        // sort by first column (descending order)
        table.getHeaderCell(0).click();
        // check sorted
        Assert.assertEquals("Text 9", table.getCell(0, 0).getText());
        Assert.assertEquals("Label 9", table.getCell(0, 1).getText());
        Assert.assertEquals("Text 19", table.getCell(8, 0).getText());
        Assert.assertEquals("Label 19", table.getCell(8, 1).getText());
        Assert.assertEquals("Text 13", table.getCell(14, 0).getText());
        Assert.assertEquals("Label 13", table.getCell(14, 1).getText());
        // sort by second column (descending order)
        table.getHeaderCell(1).click();
        // check no change
        Assert.assertEquals("Text 9", table.getCell(0, 0).getText());
        Assert.assertEquals("Label 9", table.getCell(0, 1).getText());
        Assert.assertEquals("Text 19", table.getCell(8, 0).getText());
        Assert.assertEquals("Label 19", table.getCell(8, 1).getText());
        Assert.assertEquals("Text 13", table.getCell(14, 0).getText());
        Assert.assertEquals("Label 13", table.getCell(14, 1).getText());
        // sort by second column (ascending order)
        table.getHeaderCell(1).click();
        // check back to first sorting results
        Assert.assertEquals("Text 0", table.getCell(0, 0).getText());
        Assert.assertEquals("Label 0", table.getCell(0, 1).getText());
        Assert.assertEquals("Text 10", table.getCell(2, 0).getText());
        Assert.assertEquals("Label 10", table.getCell(2, 1).getText());
        Assert.assertEquals("Text 19", table.getCell(11, 0).getText());
        Assert.assertEquals("Label 19", table.getCell(11, 1).getText());
        Assert.assertEquals("Text 4", table.getCell(14, 0).getText());
        Assert.assertEquals("Label 4", table.getCell(14, 1).getText());
    }
}

