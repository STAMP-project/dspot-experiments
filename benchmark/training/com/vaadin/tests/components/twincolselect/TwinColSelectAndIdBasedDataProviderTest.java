package com.vaadin.tests.components.twincolselect;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class TwinColSelectAndIdBasedDataProviderTest extends MultiBrowserTest {
    @Test
    public void TestSelection() {
        Assert.assertEquals(getTwinColElement().getValues().size(), 1);
        $(ButtonElement.class).first().click();
        Assert.assertEquals(getTwinColElement().getValues().size(), 0);
    }
}

