package com.vaadin.v7.tests.components.grid.basicfeatures;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.io.IOException;
import org.junit.Test;


@TestCategory("grid")
public class GridSortingIndicatorsTest extends MultiBrowserTest {
    @Test
    public void testSortingIndicators() throws IOException {
        openTestURL();
        compareScreen("initialSort");
        $(ButtonElement.class).first().click();
        compareScreen("reversedSort");
    }
}

