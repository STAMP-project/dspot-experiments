package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import org.junit.Test;


public class GridComponentsVisibilityTest extends MultiBrowserTest {
    @Test
    public void changingVisibilityOfComponentInFirstRowShouldNotThrowClientSideExceptions() {
        testHideComponent(( grid) -> 0);
    }

    @Test
    public void changingVisibilityOfComponentShouldNotThrowClientSideExceptions() {
        testHideComponent(( grid) -> ThreadLocalRandom.current().nextInt(1, (((int) (grid.getRowCount())) - 1)));
    }

    @Test
    public void changingVisibilityOfComponentInLastRowShouldNotThrowClientSideExceptions() {
        testHideComponent(( grid) -> ((int) (grid.getRowCount())) - 1);
    }
}

