package com.querydsl.collections;


import org.hamcrest.Description;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public class PathMatcherTest {
    private static final QCar $ = QCar.car;

    @Test
    public void match() {
        Car car = new Car();
        car.setHorsePower(123);
        Assert.assertThat(car, PathMatcher.hasValue(PathMatcherTest.$.horsePower));
        Assert.assertThat(car, PathMatcher.hasValue(PathMatcherTest.$.horsePower, IsEqual.equalTo(123)));
    }

    @Test
    public void mismatch() {
        Car car = new Car();
        car.setHorsePower(123);
        Description mismatchDescription = new StringDescription();
        PathMatcher.hasValue(PathMatcherTest.$.horsePower, IsEqual.equalTo(321)).describeMismatch(car, mismatchDescription);
        Assert.assertEquals("value \"car.horsePower\" was <123>", mismatchDescription.toString());
    }

    @Test
    public void describe() {
        Description description = new StringDescription();
        PathMatcher.hasValue(PathMatcherTest.$.horsePower, IsEqual.equalTo(321)).describeTo(description);
        Assert.assertEquals("valueOf(\"car.horsePower\", <321>)", description.toString());
    }
}

