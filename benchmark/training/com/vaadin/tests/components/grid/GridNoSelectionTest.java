package com.vaadin.tests.components.grid;


import com.vaadin.tests.components.grid.basics.GridBasicsTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


public class GridNoSelectionTest extends GridBasicsTest {
    @Test
    public void clickToSelectDoesNothing() {
        setSelectionModelNone();
        verifyClickSelectDoesNothing();
    }

    @Test
    public void spaceBarSelectDoesNothing() {
        setSelectionModelNone();
        verifyKeyboardSelectionNotAllowed();
    }

    @Test
    public void serverSideSelectDoesNothing() {
        toggleFirstRowSelection();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
        setSelectionModelNone();
        toggleFirstRowSelection();
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
    }

    @Test
    public void changingSelectionModels_fromMulti() {
        setSelectionModelMulti();
        getGridElement().getCell(0, 0).click();
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
        getGridElement().scrollToRow(50);
        getGridElement().getCell(49, 0).click();
        Assert.assertTrue(getGridElement().getRow(49).isSelected());
        setSelectionModelNone();
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        verifyClickSelectDoesNothing();
        verifyKeyboardSelectionNotAllowed();
        getGridElement().scrollToRow(50);
        Assert.assertFalse(getGridElement().getRow(49).isSelected());
    }

    @Test
    public void changingSelectionModels_fromMultiAllSelected() {
        setSelectionModelMulti();
        getGridHeaderRowCells().get(0).click();// select all click

        Assert.assertTrue(getDefaultColumnHeader(0).findElement(By.tagName("input")).isSelected());
        Assert.assertTrue(getGridElement().getRow(0).isSelected());
        Assert.assertTrue(getGridElement().getRow(1).isSelected());
        Assert.assertTrue(getGridElement().getRow(10).isSelected());
        setSelectionModelNone();
        Assert.assertEquals(0, getDefaultColumnHeader(0).findElements(By.tagName("input")).size());
        Assert.assertFalse(getGridElement().getRow(0).isSelected());
        Assert.assertFalse(getGridElement().getRow(1).isSelected());
        Assert.assertFalse(getGridElement().getRow(10).isSelected());
    }

    @Test
    public void changingSelectionModels_fromSingle() {
        // this is the same as default
        getGridElement().getCell(3, 0).click();
        Assert.assertTrue(getGridElement().getRow(3).isSelected());
        setSelectionModelNone();
        Assert.assertFalse(getGridElement().getRow(3).isSelected());
        verifyClickSelectDoesNothing();
        verifyKeyboardSelectionNotAllowed();
    }
}

