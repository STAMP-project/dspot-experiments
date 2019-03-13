package com.vaadin.tests.elements.grid;


import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.NoSuchElementException;


public class GridDetailsTest extends MultiBrowserTest {
    private GridElement gridElement;

    @Test
    public void gridDetails_gridDetailsOpen_elementReturned() {
        gridElement.getCell(0, 0).doubleClick();
        final TestBenchElement details = gridElement.getDetails(0);
        Assert.assertEquals("Foo = foo 0 Bar = bar 0", details.$(LabelElement.class).first().getText());
    }

    @Test(expected = NoSuchElementException.class)
    public void gridDetails_gridDetailsClosed_exceptionThrown() {
        gridElement.getDetails(0);
    }
}

