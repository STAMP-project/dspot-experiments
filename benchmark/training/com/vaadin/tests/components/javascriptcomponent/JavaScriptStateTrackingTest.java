package com.vaadin.tests.components.javascriptcomponent;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class JavaScriptStateTrackingTest extends SingleBrowserTest {
    @Test
    public void testStateTracking() {
        openTestURL();
        // field2 should really be null instead of undefined, but that's a
        // separate issue
        assertValues(0, "initial value", "undefined");
        $(ButtonElement.class).id("setField2").click();
        assertValues(1, "initial value", "updated value 1");
        $(ButtonElement.class).id("clearField1").click();
        assertValues(2, "null", "updated value 1");
        $(ButtonElement.class).id("setField2").click();
        assertValues(3, "null", "updated value 3");
    }
}

