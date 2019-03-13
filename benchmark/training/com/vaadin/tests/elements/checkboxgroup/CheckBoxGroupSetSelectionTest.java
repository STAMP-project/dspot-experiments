package com.vaadin.tests.elements.checkboxgroup;


import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class CheckBoxGroupSetSelectionTest extends MultiBrowserTest {
    private static final String NEW_VALUE = "item2";

    private CheckBoxGroupElement group;

    @Test
    public void testSetSelection() {
        group.setValue(CheckBoxGroupSetSelectionTest.NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(CheckBoxGroupSetSelectionTest.NEW_VALUE), group.getValue());
    }

    @Test
    public void testSelectByText() {
        group.selectByText(CheckBoxGroupSetSelectionTest.NEW_VALUE);
        Assert.assertEquals(Collections.singletonList(CheckBoxGroupSetSelectionTest.NEW_VALUE), group.getValue());
    }

    @Test
    public void testSelectAll() {
        List<String> value = Arrays.asList("item1", "item2", "item3");
        group.setValue(value);
        Assert.assertEquals(value, group.getValue());
    }

    @Test
    public void testSelectNone() {
        testSelectByText();
        group.setValue();
        Assert.assertEquals(Collections.emptyList(), group.getValue());
    }

    @Test
    public void testDeselect() {
        testSelectAll();
        testSetSelection();
    }
}

