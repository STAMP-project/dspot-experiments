package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.WebElement;


public class EscalatorColspanTest extends EscalatorBasicClientFeaturesTest {
    private static final int NO_COLSPAN = 1;

    @Test
    public void testNoColspan() {
        openTestURL();
        populate();
        Assert.assertEquals(EscalatorColspanTest.NO_COLSPAN, EscalatorColspanTest.getColSpan(getHeaderCell(0, 0)));
        Assert.assertEquals(EscalatorColspanTest.NO_COLSPAN, EscalatorColspanTest.getColSpan(getBodyCell(0, 0)));
        Assert.assertEquals(EscalatorColspanTest.NO_COLSPAN, EscalatorColspanTest.getColSpan(getFooterCell(0, 0)));
    }

    @Test
    public void testColspan() {
        openTestURL();
        populate();
        int firstCellWidth = getBodyCell(0, 0).getSize().getWidth();
        int secondCellWidth = getBodyCell(0, 1).getSize().getWidth();
        int doubleCellWidth = firstCellWidth + secondCellWidth;
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.COLUMN_SPANNING, EscalatorBasicClientFeaturesTest.COLSPAN_NORMAL);
        WebElement bodyCell = getBodyCell(0, 0);
        Assert.assertEquals("Cell was not spanned correctly", 2, EscalatorColspanTest.getColSpan(bodyCell));
        Assert.assertEquals((((("Spanned cell's width was not the sum of the previous cells (" + firstCellWidth) + " + ") + secondCellWidth) + ")"), doubleCellWidth, bodyCell.getSize().getWidth(), 1);
    }

    @Test
    public void testColspanToggle() {
        openTestURL();
        populate();
        int singleCellWidth = getBodyCell(0, 0).getSize().getWidth();
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.COLUMN_SPANNING, EscalatorBasicClientFeaturesTest.COLSPAN_NORMAL);
        selectMenuPath(EscalatorBasicClientFeaturesTest.FEATURES, EscalatorBasicClientFeaturesTest.COLUMN_SPANNING, EscalatorBasicClientFeaturesTest.COLSPAN_NONE);
        WebElement bodyCell = getBodyCell(0, 0);
        Assert.assertEquals(EscalatorColspanTest.NO_COLSPAN, EscalatorColspanTest.getColSpan(bodyCell));
        Assert.assertEquals(singleCellWidth, bodyCell.getSize().getWidth(), 1);
    }
}

