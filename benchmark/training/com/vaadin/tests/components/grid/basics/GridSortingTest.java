package com.vaadin.tests.components.grid.basics;


import Keys.SHIFT;
import java.util.Comparator;
import java.util.Date;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;


public class GridSortingTest extends GridBasicsTest {
    public static final Comparator<DataObject> BIG_RANDOM = Comparator.comparing(DataObject::getBigRandom);

    public static final Comparator<DataObject> SMALL_RANDOM = Comparator.comparing(DataObject::getSmallRandom);

    @Test
    public void testSortBySingleColumnByUser() {
        getGridElement().getHeaderCell(0, 6).click();
        int i = 0;
        for (Integer rowNumber : getTestData().sorted(GridSortingTest.BIG_RANDOM).map(DataObject::getRowNumber).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", rowNumber.toString(), getGridElement().getCell((i++), 3).getText());
        }
    }

    @Test
    public void testSortByMultipleColumnsByUser() {
        getGridElement().getHeaderCell(0, 7).click();
        getGridElement().getHeaderCell(0, 6).click(20, 20, SHIFT);
        int i = 0;
        for (Integer rowNumber : getTestData().sorted(GridSortingTest.SMALL_RANDOM.thenComparing(GridSortingTest.BIG_RANDOM)).map(DataObject::getRowNumber).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", rowNumber.toString(), getGridElement().getCell((i++), 3).getText());
        }
    }

    @Test
    public void serverSideOrderByColumn0() {
        selectMenuPath("Component", "Columns", "Column 0", "Sort ASC");
        Assert.assertEquals("1. SortEvent: isUserOriginated? false", getLogRow(0));
        Comparator<String> comparator = Comparator.naturalOrder();
        int i = 0;
        for (String coord : getTestData().map(DataObject::getCoordinates).sorted(comparator).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", coord, getGridElement().getCell((i++), 0).getText());
        }
        // self-verification
        Assert.assertTrue((i > 0));
        selectMenuPath("Component", "Columns", "Column 0", "Sort DESC");
        Assert.assertEquals("2. SortEvent: isUserOriginated? false", getLogRow(0));
        i = 0;
        for (String coord : getTestData().map(DataObject::getCoordinates).sorted(comparator.reversed()).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", coord, getGridElement().getCell((i++), 0).getText());
        }
    }

    @Test
    public void serverSideOrderByDate() {
        selectMenuPath("Component", "Columns", "Date", "Sort ASC");
        Assert.assertEquals("1. SortEvent: isUserOriginated? false", getLogRow(0));
        Comparator<Date> comparator = Comparator.naturalOrder();
        int i = 0;
        for (Date date : getTestData().map(DataObject::getDate).sorted(comparator).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", date.toString(), getGridElement().getCell((i++), 4).getText());
        }
        // self-verification
        Assert.assertTrue((i > 0));
        selectMenuPath("Component", "Columns", "Date", "Sort DESC");
        Assert.assertEquals("2. SortEvent: isUserOriginated? false", getLogRow(0));
        i = 0;
        for (Date date : getTestData().map(DataObject::getDate).sorted(comparator.reversed()).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", date.toString(), getGridElement().getCell((i++), 4).getText());
        }
    }

    @Test
    public void serverSideClearOrder() {
        selectMenuPath("Component", "Columns", "Column 0", "Sort ASC");
        selectMenuPath("Component", "Columns", "Clear sort");
        Assert.assertEquals("2. SortEvent: isUserOriginated? false", getLogRow(0));
        int i = 0;
        for (String coord : getTestData().map(DataObject::getCoordinates).limit(5).collect(Collectors.toList())) {
            Assert.assertEquals("Grid was not sorted as expected, row number mismatch", coord, getGridElement().getCell((i++), 0).getText());
        }
        // self-verification
        Assert.assertTrue((i > 0));
    }
}

