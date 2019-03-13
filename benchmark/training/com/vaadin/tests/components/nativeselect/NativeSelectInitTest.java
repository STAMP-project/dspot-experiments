package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vaadin Ltd
 */
public class NativeSelectInitTest extends MultiBrowserTest {
    @Test
    public void secondItemIsSelected() {
        openTestURL();
        String selected = $(NativeSelectElement.class).first().getValue();
        Assert.assertEquals("Bar", selected);
    }
}

