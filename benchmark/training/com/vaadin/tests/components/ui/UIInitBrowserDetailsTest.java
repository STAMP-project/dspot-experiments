package com.vaadin.tests.components.ui;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;


public class UIInitBrowserDetailsTest extends MultiBrowserTest {
    @Test
    public void testBrowserDetails() throws Exception {
        openTestURL();
        /* location */
        compareRequestAndBrowserValue("v-loc", "location", "null");
        /* browser window width */
        compareRequestAndBrowserValue("v-cw", "browser window width", "-1");
        /* browser window height */
        compareRequestAndBrowserValue("v-ch", "browser window height", "-1");
        /* screen width */
        compareRequestAndBrowserValue("v-sw", "screen width", "-1");
        /* screen height */
        compareRequestAndBrowserValue("v-sh", "screen height", "-1");
        /* timezone offset */
        assertTextNotNull("timezone offset");
        /* raw timezone offset */
        assertTextNotNull("raw timezone offset");
        /* dst saving */
        assertTextNotNull("dst saving");
        /* dst in effect */
        assertTextNotNull("dst in effect");
        /* current date */
        assertTextNotNull("v-curdate");
        assertTextNotNull("current date");
    }
}

