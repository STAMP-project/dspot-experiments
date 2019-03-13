package com.baeldung.maths;


import org.apache.commons.math3.util.Precision;
import org.decimal4j.util.DoubleRounder;
import org.junit.Assert;
import org.junit.Test;


public class RoundUnitTest {
    private double value = 2.03456;

    private int places = 2;

    private double delta = 0.0;

    private double expected = 2.03;

    @Test
    public void givenDecimalNumber_whenRoundToNDecimalPlaces_thenGetExpectedResult() {
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertEquals(expected, Round.roundNotPrecise(value, places), delta);
        Assert.assertEquals(expected, Round.roundAvoid(value, places), delta);
        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertEquals(expected, DoubleRounder.round(value, places), delta);
        places = 3;
        expected = 2.035;
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertEquals(expected, Round.roundNotPrecise(value, places), delta);
        Assert.assertEquals(expected, Round.roundAvoid(value, places), delta);
        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertEquals(expected, DoubleRounder.round(value, places), delta);
        value = 1000.0;
        places = 17;
        expected = 1000.0;
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertEquals(expected, Round.roundNotPrecise(value, places), delta);
        Assert.assertNotEquals(expected, Round.roundAvoid(value, places), delta);// Returns: 92.23372036854776 !

        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertEquals(expected, DoubleRounder.round(value, places), delta);
        value = 256.025;
        places = 2;
        expected = 256.03;
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertNotEquals(expected, Round.roundNotPrecise(value, places), delta);// Returns: 256.02 !

        Assert.assertNotEquals(expected, Round.roundAvoid(value, places), delta);// Returns: 256.02 !

        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertNotEquals(expected, DoubleRounder.round(value, places), delta);// Returns: 256.02 !

        value = 260.775;
        places = 2;
        expected = 260.78;
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertNotEquals(expected, Round.roundNotPrecise(value, places), delta);// Returns: 260.77 !

        Assert.assertNotEquals(expected, Round.roundAvoid(value, places), delta);// Returns: 260.77 !

        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertNotEquals(expected, DoubleRounder.round(value, places), delta);// Returns: 260.77 !

        value = 9.00800700601E10;
        places = 9;
        expected = 9.00800700601E10;
        Assert.assertEquals(expected, Round.round(value, places), delta);
        Assert.assertEquals(expected, Round.roundNotPrecise(value, places), delta);
        Assert.assertNotEquals(expected, Round.roundAvoid(value, places), delta);// Returns: 9.223372036854776E9 !

        Assert.assertEquals(expected, Precision.round(value, places), delta);
        Assert.assertEquals(expected, DoubleRounder.round(value, places), delta);
    }
}

