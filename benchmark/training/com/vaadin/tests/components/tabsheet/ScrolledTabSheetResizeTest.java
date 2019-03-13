package com.vaadin.tests.components.tabsheet;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class ScrolledTabSheetResizeTest extends MultiBrowserTest {
    @Test
    public void testReindeer() throws IOException, InterruptedException {
        $(ButtonElement.class).first().click();
        StringBuilder exceptions = new StringBuilder();
        boolean failed = false;
        // upper limit is determined by the amount of tabs,
        // lower end by limits set by Selenium version
        for (int i = 1400; i >= 650; i = i - 50) {
            try {
                testResize(i);
            } catch (Exception e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(((i + ": ") + (e.getMessage())));
            } catch (AssertionError e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(((i + ": ") + (e.getMessage())));
            }
        }
        if (failed) {
            Assert.fail(("Combined error report: " + (exceptions.toString())));
        }
    }

    @Test
    public void testValo() throws IOException, InterruptedException {
        StringBuilder exceptions = new StringBuilder();
        boolean failed = false;
        // upper limit is determined by the amount of tabs (wider than for
        // reindeer), lower end by limits set by Selenium version
        for (int i = 1550; i >= 650; i = i - 50) {
            try {
                testResize(i);
            } catch (Exception e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(((i + ": ") + (e.getMessage())));
            } catch (AssertionError e) {
                if (failed) {
                    exceptions.append(" --- ");
                }
                failed = true;
                exceptions.append(((i + ": ") + (e.getMessage())));
            }
        }
        if (failed) {
            Assert.fail(("Combined error report: " + (exceptions.toString())));
        }
    }
}

