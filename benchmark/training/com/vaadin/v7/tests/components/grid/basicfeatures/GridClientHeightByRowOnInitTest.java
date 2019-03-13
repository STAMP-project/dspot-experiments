package com.vaadin.v7.tests.components.grid.basicfeatures;


import com.vaadin.testbench.parallel.TestCategory;
import com.vaadin.tests.tb3.AbstractTB3Test;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.By;


@SuppressWarnings("all")
@TestCategory("grid")
public class GridClientHeightByRowOnInitTest extends MultiBrowserTest {
    @Test
    public void gridHeightIsMoreThanACoupleOfRows() {
        openTestURL();
        int height = findElement(By.className("v-grid")).getSize().getHeight();
        AbstractTB3Test.assertGreater((("Grid should be much taller than 150px (was " + height) + "px)"), height, 150);
    }
}

