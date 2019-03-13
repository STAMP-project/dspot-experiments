package com.vaadin.tests.components.gridlayout;


import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.GridLayoutElement;
import com.vaadin.testbench.elements.NativeButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Point;


public class GridLayoutAlignmentsTest extends MultiBrowserTest {
    private NativeButtonElement targetButton;

    private Point gridLayoutLocation;

    private int middleY = (400 / 2) - (30 / 2);

    private int middleX = middleY;

    private int bottomX = 400 - 30;

    private int bottomY = bottomX;

    @Test
    public void setAlignment() {
        openTestURL();
        targetButton = $(NativeButtonElement.class).first();
        GridLayoutElement gridLayout = $(GridLayoutElement.class).first();
        gridLayoutLocation = gridLayout.getLocation();
        assertOffset(middleX, middleY);
        $(ButtonElement.class).caption("Align top, left").first().click();
        assertOffset(0, 0);
        $(ButtonElement.class).caption("Align middle, left").first().click();
        assertOffset(0, middleY);
        $(ButtonElement.class).caption("Align bottom, left").first().click();
        assertOffset(0, bottomY);
        $(ButtonElement.class).caption("Align top, center").first().click();
        assertOffset(middleX, 0);
        $(ButtonElement.class).caption("Align middle, center").first().click();
        assertOffset(middleX, middleY);
        $(ButtonElement.class).caption("Align bottom, center").first().click();
        assertOffset(middleX, bottomY);
        $(ButtonElement.class).caption("Align top, right").first().click();
        assertOffset(bottomX, 0);
        $(ButtonElement.class).caption("Align middle, right").first().click();
        assertOffset(bottomX, middleY);
        $(ButtonElement.class).caption("Align bottom, right").first().click();
        assertOffset(bottomX, bottomY);
    }
}

