package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.GridElement.GridRowElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class DisallowedDeselectionTest extends MultiBrowserTest {
    @Test
    public void checkDeselection() {
        openTestURL();
        GridRowElement row = $(GridElement.class).first().getRow(0);
        Assert.assertFalse(row.isSelected());
        select(row);
        Assert.assertTrue(row.isSelected());
        // deselection is disallowed
        select(row);
        Assert.assertTrue(row.isSelected());
        // select another row
        GridRowElement oldRow = row;
        row = $(GridElement.class).first().getRow(1);
        select(row);
        Assert.assertTrue(row.isSelected());
        Assert.assertFalse(oldRow.isSelected());
        $(ButtonElement.class).first().click();
        select(row);
        Assert.assertFalse(row.isSelected());
    }
}

