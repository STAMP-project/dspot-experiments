package com.vaadin.tests.components.nativeselect;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class NativeSelectDisabledTest extends MultiBrowserTest {
    @Test
    public void testDisabled() {
        openTestURL();
        NativeSelectElement el = $(NativeSelectElement.class).first();
        Assert.assertEquals(false, el.isEnabled());
        ButtonElement but = $(ButtonElement.class).first();
        but.click();
        Assert.assertEquals(true, el.isEnabled());
        Assert.assertEquals(null, el.getSelectElement().getAttribute("disabled"));
        but.click();
        System.out.println(el.getSelectElement().getText());
        Assert.assertEquals("true", el.getSelectElement().getAttribute("disabled"));
    }
}

