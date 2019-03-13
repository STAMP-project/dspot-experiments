package com.vaadin.tests.push;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Assert;
import org.junit.Test;


public class MakeComponentVisibleWithPushTest extends SingleBrowserTest {
    @Test
    public void showingHiddenComponentByPushWorks() {
        setDebug(true);
        openTestURL();
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Unexpected row count", 1, $(GridElement.class).first().getRowCount());
        $(ButtonElement.class).first().click();
        Assert.assertEquals("Unexpected row count", 2, $(GridElement.class).first().getRowCount());
        assertNoErrorNotifications();
    }
}

