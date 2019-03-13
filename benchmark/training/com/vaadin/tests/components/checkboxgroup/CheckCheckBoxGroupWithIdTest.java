package com.vaadin.tests.components.checkboxgroup;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class CheckCheckBoxGroupWithIdTest extends MultiBrowserTest {
    private CheckBoxGroupElement checkBoxGroup;

    @Test
    public void TestSelection() {
        Assert.assertEquals(checkBoxGroup.getValue().size(), 1);
        $(ButtonElement.class).first().click();
        Assert.assertEquals(checkBoxGroup.getValue().size(), 0);
    }
}

