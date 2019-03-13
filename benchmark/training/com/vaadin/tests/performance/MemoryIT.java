package com.vaadin.tests.performance;


import com.vaadin.testcategory.MeasurementTest;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static GridMemory.PATH;


@Category(MeasurementTest.class)
public class MemoryIT extends SingleBrowserTest {
    private static final int MAX_ITERATIONS = 20;

    @Test
    public void measureMemory() {
        performTest(((PATH) + "?items=1"), "grid-v8-one-item-");
        performTest(((CompatibilityGridMemory.PATH) + "?items=1"), "grid-v7-one-item-");
        performTest(((PATH) + "?items=1"), "grid-v8-100thousand-items-");
        performTest(((CompatibilityGridMemory.PATH) + "?items=100000"), "grid-v7-100thousand-items-");
        performTest(((TreeGridMemory.PATH) + "?items=1"), "tree-grid-one-item-");
        performTest(((TreeTableMemory.PATH) + "?items=1"), "tree-table-one-item-");
        performTest(((TreeGridMemory.PATH) + "?items=100&initiallyExpanded"), "tree-grid-100-items-initially-expanded-");
        performTest(((TreeTableMemory.PATH) + "?items=100&initiallyExpanded"), "tree-table-100-items-initially-expanded-");
        performTest(((TreeGridMemory.PATH) + "?items=100000"), "tree-grid-100thousand-items-");
        performTest(((TreeTableMemory.PATH) + "?items=100000"), "tree-table-100thousand-items-");
    }
}

