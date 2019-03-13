package com.vaadin.tests.fields;


import com.vaadin.tests.tb3.SingleBrowserTest;
import java.util.List;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class TabIndexesTest extends SingleBrowserTest {
    @Test
    public void testTabIndexesSetToZero() {
        // clicked by default
        assertLogText("1. Setting tab indexes to 0");
        for (WebElement element : getFocusElements()) {
            assertTabIndex("0", element);
        }
    }

    @Test
    public void testTabIndexesSetToOne() {
        setTabIndexesTo("1");
        for (WebElement element : getFocusElements()) {
            assertTabIndex("1", element);
        }
    }

    @Test
    public void testTabIndexesSetToOneThroughN() {
        setTabIndexesTo("1..N");
        int counter = 0;
        for (WebElement element : getFocusElements()) {
            ++counter;
            assertTabIndex(String.valueOf(counter), element);
        }
    }

    @Test
    public void testTabIndexesSetToNThroughOne() {
        setTabIndexesTo("N..1");
        List<WebElement> fieldElements = getFocusElements();
        int counter = fieldElements.size();
        for (WebElement element : fieldElements) {
            assertTabIndex(String.valueOf(counter), element);
            --counter;
        }
    }
}

