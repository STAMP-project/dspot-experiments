package com.vaadin.tests.applicationcontext;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class ChangeSessionIdTest extends MultiBrowserTest {
    @Test
    public void testSessionIdChange() throws Exception {
        openTestURL();
        checkLogMatches("1. Session id: .*");
        $(ButtonElement.class).first().click();
        checkLogMatches("2. Session id changed successfully from .* to .*");
        $(ButtonElement.class).get(1).click();
        checkLogMatches("3. Session id: .*");
    }
}

