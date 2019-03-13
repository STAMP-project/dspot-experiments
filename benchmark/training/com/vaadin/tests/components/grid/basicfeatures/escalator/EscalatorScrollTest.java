package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("all")
public class EscalatorScrollTest extends EscalatorBasicClientFeaturesTest {
    /**
     * Before the fix, removing and adding rows and also scrolling would put the
     * scroll state in an internally inconsistent state. The scrollbar would've
     * been scrolled correctly, but the body wasn't.
     *
     * This was due to optimizations that didn't keep up with the promises, so
     * to say. So the optimizations were removed.
     */
    @Test
    public void testScrollRaceCondition() {
        scrollVerticallyTo(40);
        String originalStyle = getTBodyStyle();
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.BODY_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ALL_INSERT_SCROLL);
        // body should be scrolled to exactly the same spot. (not 0)
        Assert.assertEquals(originalStyle, getTBodyStyle());
    }

    @Test
    public void scrollToBottomAndRemoveHeader() throws Exception {
        scrollVerticallyTo(999999);// to bottom

        /* apparently the scroll event isn't fired by the time the next assert
        would've been done.
         */
        Thread.sleep(50);
        Assert.assertEquals("Unexpected last row cell before header removal", "Row 99: 0,99", getBodyCell((-1), 0).getText());
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.HEADER_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ONE_ROW_FROM_BEGINNING);
        Assert.assertEquals("Unexpected last row cell after header removal", "Row 99: 0,99", getBodyCell((-1), 0).getText());
    }

    @Test
    public void scrollToBottomAndRemoveFooter() throws Exception {
        scrollVerticallyTo(999999);// to bottom

        /* apparently the scroll event isn't fired by the time the next assert
        would've been done.
         */
        Thread.sleep(50);
        Assert.assertEquals("Unexpected last row cell before footer removal", "Row 99: 0,99", getBodyCell((-1), 0).getText());
        selectMenuPath(EscalatorBasicClientFeaturesTest.COLUMNS_AND_ROWS, EscalatorBasicClientFeaturesTest.FOOTER_ROWS, EscalatorBasicClientFeaturesTest.REMOVE_ONE_ROW_FROM_BEGINNING);
        Assert.assertEquals("Unexpected last row cell after footer removal", "Row 99: 0,99", getBodyCell((-1), 0).getText());
    }
}

