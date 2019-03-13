package com.vaadin.tests.components.window;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


/**
 * Test for window order position access.
 *
 * @author Vaadin Ltd
 */
public class WindowOrderTest extends MultiBrowserTest {
    @Test
    public void orderGetterTest() {
        openTestURL();
        checkPositionsAfterFirstWindowActivation();
        checkPositionsAfterActivationThirdFirstSecond();
        checkPositionsAfterDetachingThirdWindow();
        checkPositionsAfterNewWindowAttach();
    }
}

