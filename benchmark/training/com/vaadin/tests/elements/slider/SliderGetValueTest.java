package com.vaadin.tests.elements.slider;


import com.vaadin.testbench.elements.SliderElement;
import com.vaadin.tests.elements.ComponentElementGetValue;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class SliderGetValueTest extends MultiBrowserTest {
    @Test
    public void checkSlider() {
        SliderElement pb = $(SliderElement.class).get(0);
        String expected = "" + (ComponentElementGetValue.TEST_SLIDER_VALUE);
        String actual = pb.getValue();
        Assert.assertEquals(expected, actual);
    }
}

