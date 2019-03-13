package com.vaadin.tests.components.gridlayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class UniformGridLayoutUITest extends MultiBrowserTest {
    @Test
    public void noncollapsed() throws Exception {
        openTestURL();
        compareScreen("noncollapsed");
    }

    @Test
    public void collapsed() throws Exception {
        openTestURL("collapse");
        compareScreen("collapsed");
    }
}

