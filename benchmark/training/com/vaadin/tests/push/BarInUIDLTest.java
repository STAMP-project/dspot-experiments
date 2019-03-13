package com.vaadin.tests.push;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;


@TestCategory("push")
public class BarInUIDLTest extends MultiBrowserTest {
    @Test
    public void sendBarInUIDL() {
        openTestURL();
        getButton().click();
        Assert.assertEquals("Thank you for clicking | bar", vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[1]/VLabel[0]").getText());
        getButton().click();
        Assert.assertEquals("Thank you for clicking | bar", vaadinElement("/VVerticalLayout[0]/Slot[1]/VVerticalLayout[0]/Slot[2]/VLabel[0]").getText());
    }
}

