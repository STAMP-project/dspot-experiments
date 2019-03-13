package com.vaadin.tests.elements;


import TreeElementGetValue.TEST_VALUE_LVL2;
import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.v7.testbench.elements.TreeElement;
import org.junit.Assert;
import org.junit.Test;


public class TreeElementGetValueTest extends MultiBrowserTest {
    @Test
    public void testGetValue() {
        TreeElement tree = $(TreeElement.class).get(0);
        Assert.assertEquals(tree.getValue(), TEST_VALUE_LVL2);
    }
}

