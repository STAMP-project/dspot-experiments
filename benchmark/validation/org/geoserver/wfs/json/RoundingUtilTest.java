/**
 * (c) 2016 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package org.geoserver.wfs.json;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test rounding
 *
 * @author Dean Povey
 */
public class RoundingUtilTest {
    @Test
    public void testSpecialCases() {
        for (int numDecimals = 0; numDecimals < 17; numDecimals++) {
            Assert.assertThat(Double.isNaN(RoundingUtil.round(Double.NaN, numDecimals)), Matchers.is(true));
            Assert.assertThat(RoundingUtil.round(Double.NEGATIVE_INFINITY, numDecimals), Matchers.is(Matchers.equalTo(Double.NEGATIVE_INFINITY)));
            Assert.assertThat(RoundingUtil.round(Double.POSITIVE_INFINITY, numDecimals), Matchers.is(Matchers.equalTo(Double.POSITIVE_INFINITY)));
        }
    }

    @Test
    public void testSpecificCases() {
        Assert.assertThat(RoundingUtil.round(0.0, 0), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(0.1, 0), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(0.1, 1), Matchers.is(Matchers.equalTo(0.1)));
        Assert.assertThat(RoundingUtil.round(0.1, 2), Matchers.is(Matchers.equalTo(0.1)));
        Assert.assertThat(RoundingUtil.round(0.05, 1), Matchers.is(Matchers.equalTo(0.1)));
        Assert.assertThat(RoundingUtil.round((-0.05), 1), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(1.0E-7, 5), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(1.0000001, 7), Matchers.is(Matchers.equalTo(1.0000001)));
        Assert.assertThat(RoundingUtil.round(1.00000015, 7), Matchers.is(Matchers.equalTo(1.0000002)));
        Assert.assertThat(RoundingUtil.round(0.001, 7), Matchers.is(Matchers.equalTo(0.001)));
        Assert.assertThat(RoundingUtil.round(1.0E-4, 3), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(1.0E-10, 10), Matchers.is(Matchers.equalTo(1.0E-10)));
    }

    @Test
    public void testNoRoundingWhenPrecisionWouldBeExceeded() {
        // Test cases where precision is exceeded.
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E12, 1), Matchers.is(Matchers.equalTo(1.0123456789012E12)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E12, 2), Matchers.is(Matchers.equalTo(1.01234567890123E12)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E13, 1), Matchers.is(Matchers.equalTo(1.01234567890123E13)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E13, 2), Matchers.is(Matchers.equalTo(1.012345678901235E13)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012345E14, 1), Matchers.is(Matchers.equalTo(1.012345678901235E14)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012345E14, 2), Matchers.is(Matchers.equalTo(1.0123456789012345E14)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012345E15, 1), Matchers.is(Matchers.equalTo(1.0123456789012345E15)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012345E15, 2), Matchers.is(Matchers.equalTo(1.0123456789012345E15)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E16, 1), Matchers.is(Matchers.equalTo(1.0123456789012346E16)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012346E16, 2), Matchers.is(Matchers.equalTo(1.0123456789012346E16)));
        Assert.assertThat(RoundingUtil.round(1.01234567890123456E17, 1), Matchers.is(Matchers.equalTo(1.01234567890123456E17)));
        Assert.assertThat(RoundingUtil.round(1.01234567890123456E18, 1), Matchers.is(Matchers.equalTo(1.01234567890123456E18)));
        Assert.assertThat(RoundingUtil.round(1.0123456789012345E19, 1), Matchers.is(Matchers.equalTo(1.0123456789012345E19)));
        Assert.assertThat(RoundingUtil.round(Double.MIN_VALUE, 15), Matchers.is(Matchers.equalTo(0.0)));
        Assert.assertThat(RoundingUtil.round(Double.MAX_VALUE, 1), Matchers.is(Matchers.equalTo(Double.MAX_VALUE)));
    }

    @Test
    public void testRandomRoundingVsBigDecimal() {
        Random r = new Random();
        for (int i = 0; i < 10000; i++) {
            double value = r.nextDouble();
            for (int numDecimals = 0; numDecimals <= 8; numDecimals++) {
                double expected = new BigDecimal(Double.toString(value)).setScale(numDecimals, RoundingMode.HALF_UP).doubleValue();
                double actual = RoundingUtil.round(value, numDecimals);
                Assert.assertThat(actual, Matchers.is(Matchers.equalTo(expected)));
            }
        }
    }
}

