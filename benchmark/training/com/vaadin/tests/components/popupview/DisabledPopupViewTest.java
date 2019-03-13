package com.vaadin.tests.components.popupview;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class DisabledPopupViewTest extends MultiBrowserTest {
    @Test
    public void disabledPopupDoesNotOpen() {
        openTestURL();
        $(PopupViewElement.class).first().click();
        Assert.assertFalse($(ButtonElement.class).exists());
    }
}

