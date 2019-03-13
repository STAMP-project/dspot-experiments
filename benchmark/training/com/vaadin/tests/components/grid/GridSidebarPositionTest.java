package com.vaadin.tests.components.grid;


import GridSidebarPosition.POPUP_ABOVE;
import GridSidebarPosition.POPUP_WINDOW_HEIGHT;
import GridSidebarPosition.POPUP_WINDOW_MOVED_UP;
import com.vaadin.testbench.elements.GridElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;


public class GridSidebarPositionTest extends MultiBrowserTest {
    @Test
    public void heightRestrictedToBrowserWindow() {
        openTestURL();
        GridElement gridWithVeryManyColumns = $(GridElement.class).id(POPUP_WINDOW_HEIGHT);
        getSidebarOpenButton(gridWithVeryManyColumns).click();
        Dimension popupSize = getSidebarPopup().getSize();
        Dimension browserWindowSize = getDriver().manage().window().getSize();
        Assert.assertTrue(((popupSize.getHeight()) <= (browserWindowSize.getHeight())));
    }

    @Test
    public void popupNotBelowBrowserWindow() {
        openTestURL();
        GridElement gridAtBottom = $(GridElement.class).id(POPUP_WINDOW_MOVED_UP);
        getSidebarOpenButton(gridAtBottom).click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = (popupLocation.getY()) + (popupSize.getHeight());
        Dimension browserWindowSize = getDriver().manage().window().getSize();
        Assert.assertTrue((popupBottom <= (browserWindowSize.getHeight())));
    }

    @Test
    public void popupAbove() {
        openTestURL();
        GridElement gridPopupAbove = $(GridElement.class).id(POPUP_ABOVE);
        WebElement sidebarOpenButton = getSidebarOpenButton(gridPopupAbove);
        sidebarOpenButton.click();
        WebElement sidebarPopup = getSidebarPopup();
        Dimension popupSize = sidebarPopup.getSize();
        Point popupLocation = sidebarPopup.getLocation();
        int popupBottom = (popupLocation.getY()) + (popupSize.getHeight());
        int sideBarButtonTop = sidebarOpenButton.getLocation().getY();
        Assert.assertTrue((popupBottom <= sideBarButtonTop));
    }
}

