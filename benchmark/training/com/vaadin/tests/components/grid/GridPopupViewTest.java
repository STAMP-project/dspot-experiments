package com.vaadin.tests.components.grid;


import com.vaadin.testbench.elements.GridElement;
import com.vaadin.testbench.elements.PopupViewElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;


public class GridPopupViewTest extends MultiBrowserTest {
    @Test
    public void gridSizeCorrect() {
        openTestURL();
        PopupViewElement pv = $(PopupViewElement.class).first();
        for (int i = 0; i < 3; i++) {
            pv.click();
            GridElement grid = $(GridElement.class).first();
            Dimension rect = grid.getCell(0, 0).getSize();
            Assert.assertEquals(500, rect.width);
            Assert.assertEquals(38, rect.height);
            findElement(By.className("v-ui")).click();
            waitForElementNotPresent(By.className("v-grid"));
        }
    }
}

