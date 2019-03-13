package com.vaadin.tests.elements;


import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TestBenchElementRightClickTest extends MultiBrowserTest {
    TestBenchElement cell;

    LabelElement label;

    @Test
    public void testTableRightClick() {
        cell.contextClick();
        String actual = label.getText();
        String expected = "RightClick";
        Assert.assertEquals("TestBenchElement right click fails", expected, actual);
    }

    @Test
    public void testTableDoubleClick() {
        cell.doubleClick();
        String actual = label.getText();
        String expected = "DoubleClick";
        Assert.assertEquals("TestBenchElement double click fails", expected, actual);
    }
}

