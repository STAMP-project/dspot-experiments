package com.vaadin.tests.components.grid.basicfeatures.escalator;


import com.vaadin.tests.components.grid.basicfeatures.EscalatorBasicClientFeaturesTest;
import org.junit.Test;


public class EscalatorUpdaterUiTest extends EscalatorBasicClientFeaturesTest {
    @Test
    public void testHeaderPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testHeaderPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.HEADER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testBodyPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.BODY_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderRowColRowCol() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderRowColColRow() {
        boolean addColumnFirst = false;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderColRowColRow() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = true;
        testPaintOrder(EscalatorBasicClientFeaturesTest.FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }

    @Test
    public void testFooterPaintOrderColRowRowCol() {
        boolean addColumnFirst = true;
        boolean removeColumnFirst = false;
        testPaintOrder(EscalatorBasicClientFeaturesTest.FOOTER_ROWS, addColumnFirst, removeColumnFirst);
    }
}

