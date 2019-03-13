package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class EscalatorBasicsTest extends EscalatorBasicClientFeaturesTest {
    @Test
    public void testDetachingAnEmptyEscalator() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingASemiPopulatedEscalator() throws IOException {
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.ADD_ONE_OF_EACH_ROW);
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.COLUMNS, EscalatorBasicClientFeaturesTest.ADD_ONE_COLUMN_TO_BEGINNING);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingAPopulatedEscalator() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.DETACH_ESCALATOR);
        assertEscalatorIsRemovedCorrectly();
    }

    @Test
    public void testDetachingAndReattachingAnEscalator() {
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.POPULATE_COLUMN_ROW);
        scrollVerticallyTo(50);
        scrollHorizontallyTo(50);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.DETACH_ESCALATOR);
        selectMenuPath(EscalatorBasicClientFeaturesTest.GENERAL, EscalatorBasicClientFeaturesTest.ATTACH_ESCALATOR);
        Assert.assertEquals("Vertical scroll position", 50, getScrollTop());
        Assert.assertEquals("Horizontal scroll position", 50, getScrollLeft());
        Assert.assertEquals("First cell of first visible row", "Row 2: 0,2", getBodyCell(0, 0).getText());
    }
}

