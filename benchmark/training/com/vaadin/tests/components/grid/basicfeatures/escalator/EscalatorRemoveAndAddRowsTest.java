package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;


/**
 * Test class to test the escalator level issue for ticket #16832
 */
public class EscalatorRemoveAndAddRowsTest extends EscalatorBasicClientFeaturesTest {
    @Test
    public void testRemoveAllRowsAndAddThirtyThenScroll() throws IOException {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        scrollVerticallyTo(99999);
        Assert.assertTrue("Escalator is not scrolled to bottom.", isElementPresent(By.xpath("//td[text() = 'Row 99: 0,99']")));
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ALL_INSERT_SCROLL);
        scrollVerticallyTo(99999);
        Assert.assertTrue("Escalator is not scrolled to bottom.", isElementPresent(By.xpath("//td[text() = 'Row 29: 0,129']")));
    }

    @Test
    public void testRemoveRowsFromMiddle() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.HEADER_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_ROW_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.ADD_22_ROWS_TO_TOP);
        // remove enough rows from middle, so that the total size of escalator
        // rows drops to below the size of the rows shown, forcing the escalator
        // to remove & move & update rows
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_15_ROWS_FROM_MIDDLE);
        // first there was rows 0-21, then removed 15 rows 3-18, thus the rows
        // should be 0,1,2,18,19,20,21
        verifyRow(0, 0);
        verifyRow(1, 1);
        verifyRow(2, 2);
        verifyRow(3, 18);
        verifyRow(4, 19);
        verifyRow(5, 20);
        verifyRow(6, 21);
    }
}

