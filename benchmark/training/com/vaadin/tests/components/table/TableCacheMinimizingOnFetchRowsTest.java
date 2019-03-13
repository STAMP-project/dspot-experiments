package com.vaadin.tests.components.table;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public class TableCacheMinimizingOnFetchRowsTest extends MultiBrowserTest {
    @Test
    public void testCacheSize() throws InterruptedException {
        openTestURL();
        scrollToBottomOfTable();
        // the row request might vary slightly with different browsers
        String logtext1 = "requested 60 rows";
        String logtext2 = "requested 61 rows";
        MatcherAssert.assertThat("Requested cached rows did not match expected", ((logContainsText(logtext1)) || (logContainsText(logtext2))));
    }
}

