package com.vaadin.tests.elements.nativeselect;


import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class NativeSelectSetValueTest extends MultiBrowserTest {
    NativeSelectElement select;

    LabelElement counter;

    @Test
    public void testSetValue() throws InterruptedException {
        select.setValue("item 2");
        checkTestValue();
    }

    @Test
    public void testSelectByText() {
        select.selectByText("item 2");
        checkTestValue();
    }
}

