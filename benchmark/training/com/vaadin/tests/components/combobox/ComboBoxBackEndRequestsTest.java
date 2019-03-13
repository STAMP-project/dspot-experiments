package com.vaadin.tests.components.combobox;


import ComboBoxBackEndRequests.DEFAULT_NUMBER_OF_ITEMS;
import ComboBoxBackEndRequests.DEFAULT_PAGE_LENGTH;
import com.vaadin.tests.tb3.SingleBrowserTest;
import org.junit.Test;


public class ComboBoxBackEndRequestsTest extends SingleBrowserTest {
    @Test
    public void testInitialLoad_onlySizeAndFirstItemsRequested() {
        verifyInitialLoadRequests();
    }

    @Test
    public void testOpeningDropDown_noRequests() {
        verifyInitialLoadRequests();
        clearLogs();
        openPopup();
        verifyNumberOrRequests("opening drop down should not have caused requests", 0);
        // select something to close popup
        selectByClick("Item 2");
        verifyNumberOrRequests("selecting should have not caused requests", 0);
        openPopup();
        verifyNumberOrRequests("opening drop down should not have caused requests", 0);
    }

    @Test
    public void testNoPaging_nullSelectionAllowed() {
        final int pageLength = 0;
        final int items = 20;
        open(pageLength, items);
        verifyInitialLoadRequests();
        clearLogs();
        openPopup();
        verifyNumberOrRequests("extra request expected", 1);
        // TODO with pageLength 0, the cache strategy forces to fetch the whole
        // set for opening popup
        verifyFetchRequest(0, 0, items, null);
        verifyPopupItems(true, 0, (items - 1));
        // pop up status not shown for only one page!
    }

    @Test
    public void testNoPaging_nullSelectionDisallowed() {
        final int pageLength = 0;
        final int items = 20;
        open(pageLength, items);
        verifyInitialLoadRequests();
        clearLogs();
        triggerNullSelectionAllowed(false);
        openPopup();
        verifyNumberOrRequests("extra request expected", 1);
        // TODO the cache strategy forces to fetch the whole range, instead of
        // just the missing 40-50
        verifyFetchRequest(0, 0, items, null);
        verifyPopupItems(false, 0, (items - 1));
        // pop up status not shown for only one page!
    }

    @Test
    public void testPagingWorks_nullSelectionAllowed_defaultSizes() {
        verifyPopupPages(DEFAULT_PAGE_LENGTH, DEFAULT_NUMBER_OF_ITEMS, true);
    }

    @Test
    public void testPagingWorks_nullSelectionDisallowed_defaultSizes() {
        triggerNullSelectionAllowed(false);
        verifyPopupPages(DEFAULT_PAGE_LENGTH, DEFAULT_NUMBER_OF_ITEMS, false);
    }

    @Test
    public void testInitialPage_pageLengthBiggerThanInitialCache() {
        // initial request is for 40 items
        final int pageLength = 50;
        final int items = 100;
        open(pageLength, items);
        verifyNumberOrRequests("three initial requests expected", 3);
        verifySizeRequest(0, null);
        verifyFetchRequest(1, 0, 40, null);
        verifyFetchRequest(2, 40, 60, null);
        clearLogs();
        verifyPopupPages(pageLength, items, true);
        // browsing through the pages should't have caused more requests
        verifyNumberOrRequests("no additional requests should have happened", 0);
    }

    @Test
    public void testPagingWorks_nullSelectionAllowed_customSizes() {
        final int pageLength = 23;
        final int items = 333;
        open(pageLength, items);
        // with null selection allowed
        verifyPopupPages(pageLength, items, true);
    }

    @Test
    public void testPagingWorks_nullSelectionDisallowed_customSizes() {
        final int pageLength = 23;
        final int items = 333;
        open(pageLength, items);
        triggerNullSelectionAllowed(false);
        verifyPopupPages(pageLength, items, false);
    }

    @Test
    public void testPaging_nullSelectionAllowed_correctNumberOfItemsShown() {
        verifyInitialLoadRequests();
        openPopup();
        verifyPopupItems(true, 0, 8);
        nextPage();
        verifyPopupItems(false, 9, 18);
    }

    @Test
    public void testPaging_nullSelectionDisallowed_correctNumberOfItemsShown() {
        verifyInitialLoadRequests();
        triggerNullSelectionAllowed(false);
        openPopup();
        verifyPopupItems(false, 0, 9);
        nextPage();
        verifyPopupItems(false, 10, 19);
    }
}

