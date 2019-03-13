package com.vaadin.v7.data.util.filter;


import com.vaadin.v7.data.Container.Filter;
import com.vaadin.v7.data.Item;
import java.text.SimpleDateFormat;
import org.junit.Assert;
import org.junit.Test;


public class CompareFilterDateTest extends AbstractFilterTestBase<Compare> {
    protected Item itemNullUtilDate;

    protected Item itemNullSqlDate;

    protected Item itemUtilDate;

    protected Item itemSqlDate;

    protected SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyy");

    protected Filter equalCompUtilDate;

    protected Filter greaterCompUtilDate;

    protected Filter lessCompUtilDate;

    protected Filter greaterEqualCompUtilDate;

    protected Filter lessEqualCompUtilDate;

    protected Filter equalCompSqlDate;

    protected Filter greaterCompSqlDate;

    protected Filter lessCompSqlDate;

    protected Filter greaterEqualCompSqlDate;

    protected Filter lessEqualCompSqlDate;

    @Test
    public void testCompareUtilDatesAndUtilDates() {
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemNullUtilDate));
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(greaterCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(greaterEqualCompUtilDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessEqualCompUtilDate.passesFilter(null, itemUtilDate));
    }

    @Test
    public void testCompareUtilDatesAndSqlDates() {
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemNullSqlDate));
        Assert.assertFalse(equalCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterEqualCompUtilDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessEqualCompUtilDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndSqlDates() {
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemNullSqlDate));
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertFalse(greaterEqualCompSqlDate.passesFilter(null, itemSqlDate));
        Assert.assertTrue(lessEqualCompSqlDate.passesFilter(null, itemSqlDate));
    }

    @Test
    public void testCompareSqlDatesAndUtilDates() {
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemNullUtilDate));
        Assert.assertFalse(equalCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(greaterCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertFalse(greaterEqualCompSqlDate.passesFilter(null, itemUtilDate));
        Assert.assertTrue(lessEqualCompSqlDate.passesFilter(null, itemUtilDate));
    }
}

