package com.vaadin.v7.tests.components.grid.basicfeatures.server;


import com.vaadin.v7.tests.components.grid.basicfeatures.GridBasicFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


public class GridColumnMaxWidthTest extends GridBasicFeaturesTest {
    @Test
    public void testMaxWidthAffectsColumnWidth() {
        setDebug(true);
        openTestURL();
        selectMenuPath("Component", "Columns", "All columns expanding, Col 0 has max width of 30px");
        Assert.assertEquals("Column 0 did not obey max width of 30px.", 30, getGridElement().getCell(0, 0).getSize().getWidth());
    }
}

