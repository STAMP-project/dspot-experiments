package com.vaadin.tests.server.component.datefield;


import DateResolution.DAY;
import DateResolution.MONTH;
import DateResolution.YEAR;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.util.TestUtil;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;


public class ResolutionTest {
    @Test
    public void testResolutionHigherOrEqualToYear() {
        Iterable<DateResolution> higherOrEqual = DateResolution.getResolutionsHigherOrEqualTo(YEAR);
        List<DateResolution> expected = new ArrayList<>();
        expected.add(YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    @Test
    public void testResolutionHigherOrEqualToDay() {
        Iterable<DateResolution> higherOrEqual = DateResolution.getResolutionsHigherOrEqualTo(DAY);
        List<DateResolution> expected = new ArrayList<>();
        expected.add(DAY);
        expected.add(MONTH);
        expected.add(YEAR);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }

    @Test
    public void testResolutionLowerThanYear() {
        Iterable<DateResolution> higherOrEqual = DateResolution.getResolutionsLowerThan(YEAR);
        List<DateResolution> expected = new ArrayList<>();
        expected.add(MONTH);
        expected.add(DAY);
        TestUtil.assertIterableEquals(expected, higherOrEqual);
    }
}

