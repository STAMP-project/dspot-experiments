package com.vaadin.tests.components.absolutelayout;


import com.vaadin.tests.tb3.MultiBrowserTest;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;


/**
 * Tests how AbsoluteLayout handles relative sized contents.
 *
 * @author Vaadin Ltd
 */
public class AbsoluteLayoutRelativeSizeContentTest extends MultiBrowserTest {
    @Test
    public void testFullAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement full = findElement(By.id("full-table"));
        MatcherAssert.assertThat("Full table should be as wide as comparison table", full.getSize().width, Is.is(comparison.getSize().width));
        MatcherAssert.assertThat("Full table should be as high as comparison table", full.getSize().height, Is.is(comparison.getSize().height));
    }

    @Test
    public void testHalfAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement half = findElement(By.id("half-table"));
        MatcherAssert.assertThat("Half-sized table should be half as wide as comparison table", half.getSize().width, Is.is(((comparison.getSize().width) / 2)));
        MatcherAssert.assertThat("Half-sized table should be half as high as comparison table", half.getSize().height, Is.is(((comparison.getSize().height) / 2)));
    }

    @Test
    public void testHalfWithTinyAgainstComparison() {
        WebElement comparison = findElement(By.id("comparison-table"));
        WebElement half = findElement(By.id("halfwithtiny-table"));
        MatcherAssert.assertThat("Half-sized table should be half as wide as comparison table even if there are other components in the layout", half.getSize().width, Is.is(((comparison.getSize().width) / 2)));
        MatcherAssert.assertThat("Half-sized table should be half as high as comparison table even if there are other components in the layout", half.getSize().height, Is.is(((comparison.getSize().height) / 2)));
    }

    @Test
    public void testHalfAgainstFullLayout() {
        WebElement layout = findElement(By.id("halfinfull-layout"));
        WebElement half = findElement(By.id("halfinfull-table"));
        MatcherAssert.assertThat("Half-sized table should be half as wide as full layout", ((double) (half.getSize().width)), closeTo((((double) (layout.getSize().width)) / 2), 0.5));
        MatcherAssert.assertThat("Half-sized table should be half as high as full layout", ((double) (half.getSize().height)), closeTo((((double) (layout.getSize().height)) / 2), 0.5));
    }

    @Test
    public void testFullOnFixedWithSetLocation() {
        WebElement outer = findElement(By.id("fullonfixed-outer"));
        WebElement inner = findElement(By.id("fullonfixed-inner"));
        MatcherAssert.assertThat("Inner layout should be as wide as outer layout minus left position", inner.getSize().width, Is.is(((outer.getSize().width) - 100)));
        MatcherAssert.assertThat("Inner layout should be as high as outer layout minus top position", inner.getSize().height, Is.is(((outer.getSize().height) - 50)));
    }

    @Test
    public void testFullOnFullWithSetLocation() {
        WebElement outer = findElement(By.id("fullonfull-outer"));
        WebElement inner = findElement(By.id("fullonfull-inner"));
        MatcherAssert.assertThat("Inner layout should be as wide as outer layout minus left position", inner.getSize().width, Is.is(((outer.getSize().width) - 100)));
        MatcherAssert.assertThat("Inner layout should be as high as outer layout minus top position", inner.getSize().height, Is.is(((outer.getSize().height) - 50)));
    }
}

