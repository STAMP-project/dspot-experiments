package com.vaadin.tests.components.combobox;


import com.vaadin.tests.tb3.SingleBrowserTest;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class ComboBoxTestBenchPerformanceTest extends SingleBrowserTest {
    /**
     * TestBench timeout is 20s, require 15s to make sure cluster load won't
     * affect the result badly.
     */
    private static final double TIME_LIMIT = 15000.0;

    @Test
    public void testSelectionPerformance() throws Exception {
        openTestURL();
        long before = System.currentTimeMillis();
        setComboBoxValue("abc123");// new

        long after = System.currentTimeMillis();
        MatcherAssert.assertThat((((double) (after)) - before), closeTo(0.0, ComboBoxTestBenchPerformanceTest.TIME_LIMIT));
        before = System.currentTimeMillis();
        setComboBoxValue("11");// existing (2nd page)

        after = System.currentTimeMillis();
        MatcherAssert.assertThat((((double) (after)) - before), closeTo(0.0, ComboBoxTestBenchPerformanceTest.TIME_LIMIT));
        before = System.currentTimeMillis();
        setComboBoxValue("abc123");// previously added (3rd page)

        after = System.currentTimeMillis();
        MatcherAssert.assertThat((((double) (after)) - before), closeTo(0.0, ComboBoxTestBenchPerformanceTest.TIME_LIMIT));
    }
}

