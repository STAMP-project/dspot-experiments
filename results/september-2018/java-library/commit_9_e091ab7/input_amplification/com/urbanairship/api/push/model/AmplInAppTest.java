package com.urbanairship.api.push.model;


import com.google.common.base.Optional;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Assert;
import org.junit.Test;


public class AmplInAppTest {
    @Test(timeout = 10000)
    public void testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147() throws Exception {
        try {
            DateTime o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3 = DateTime.now();
            Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getChronology())).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getChronology())).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getChronology())).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getChronology())).getZone())).getID());
            Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getChronology())).toString());
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getYear())));
            Assert.assertEquals(10, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getMonthOfYear())));
            Assert.assertEquals(9, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getDayOfMonth())));
            Assert.assertEquals(23, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getHourOfDay())));
            Assert.assertEquals(35, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getMinuteOfHour())));
            Assert.assertEquals(2, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getDayOfWeek())));
            Assert.assertEquals(282, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getDayOfYear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getWeekyear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getYearOfEra())));
            Assert.assertEquals(18, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getYearOfCentury())));
            Assert.assertEquals(20, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getCenturyOfEra())));
            Assert.assertEquals(1, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getEra())));
            Assert.assertEquals(1415, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getMinuteOfDay())));
            Assert.assertEquals(41, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getWeekOfWeekyear())));
            Assert.assertTrue(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).isBeforeNow());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).isEqualNow());
            Assert.assertFalse(((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).getZone())).getID());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1482_add6147__3)).isAfterNow());
            DateTime now = DateTime.now();
            InApp inApp = InApp.newBuilder().setAlert("te&st alert").setExpiry(null).build();
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
            Assert.assertEquals("te&st alert", ((InApp) (inApp)).getAlert());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExpiry())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExpiry())).hashCode())));
            Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
            Assert.assertEquals("InApp{alert=te&st alert, displayType=banner, expiry=Optional.absent(), actions=Optional.absent(), interactive=Optional.absent(), extra=Optional.absent()}", ((InApp) (inApp)).toString());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
            inApp.getAlert();
            inApp.getExpiry().get();
            org.junit.Assert.fail("testInAppMessagenull1433 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testInAppMessage_literalMutationString1418() throws Exception {
        DateTime now = DateTime.now();
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (now)).getChronology())).toString());
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (now)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (now)).getDayOfMonth())));
        Assert.assertEquals(23, ((int) (((DateTime) (now)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (now)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (now)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (now)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (now)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (now)).getEra())));
        Assert.assertEquals(41, ((int) (((DateTime) (now)).getWeekOfWeekyear())));
        Assert.assertFalse(((DateTimeZone) (((DateTime) (now)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (now)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).getID());
        Assert.assertTrue(((DateTime) (now)).isBeforeNow());
        Assert.assertFalse(((DateTime) (now)).isAfterNow());
        Assert.assertFalse(((DateTime) (now)).isEqualNow());
        InApp inApp = InApp.newBuilder().setAlert("").setExpiry(now).build();
        Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
        Assert.assertEquals("", ((InApp) (inApp)).getAlert());
        Assert.assertTrue(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
        inApp.getAlert();
        inApp.getExpiry().get();
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (now)).getChronology())).toString());
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (now)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (now)).getDayOfMonth())));
        Assert.assertEquals(23, ((int) (((DateTime) (now)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (now)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (now)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (now)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (now)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (now)).getEra())));
        Assert.assertEquals(41, ((int) (((DateTime) (now)).getWeekOfWeekyear())));
        Assert.assertFalse(((DateTimeZone) (((DateTime) (now)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (now)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).getID());
        Assert.assertTrue(((DateTime) (now)).isBeforeNow());
        Assert.assertFalse(((DateTime) (now)).isAfterNow());
        Assert.assertFalse(((DateTime) (now)).isEqualNow());
        Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
        Assert.assertEquals("", ((InApp) (inApp)).getAlert());
        Assert.assertTrue(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
    }

    @Test(timeout = 10000)
    public void testInAppMessage_add1428() throws Exception {
        DateTime now = DateTime.now();
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (now)).getChronology())).toString());
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (now)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (now)).getDayOfMonth())));
        Assert.assertEquals(23, ((int) (((DateTime) (now)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (now)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (now)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (now)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (now)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (now)).getEra())));
        Assert.assertEquals(41, ((int) (((DateTime) (now)).getWeekOfWeekyear())));
        Assert.assertFalse(((DateTimeZone) (((DateTime) (now)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (now)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).getID());
        Assert.assertTrue(((DateTime) (now)).isBeforeNow());
        Assert.assertFalse(((DateTime) (now)).isAfterNow());
        Assert.assertFalse(((DateTime) (now)).isEqualNow());
        InApp.newBuilder();
        InApp inApp = InApp.newBuilder().setAlert("test alert").setExpiry(now).build();
        Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
        Assert.assertEquals("test alert", ((InApp) (inApp)).getAlert());
        Assert.assertTrue(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
        inApp.getAlert();
        inApp.getExpiry().get();
        Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (now)).getChronology())).getZone())).getID());
        Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (now)).getChronology())).toString());
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYear())));
        Assert.assertEquals(10, ((int) (((DateTime) (now)).getMonthOfYear())));
        Assert.assertEquals(9, ((int) (((DateTime) (now)).getDayOfMonth())));
        Assert.assertEquals(23, ((int) (((DateTime) (now)).getHourOfDay())));
        Assert.assertEquals(2, ((int) (((DateTime) (now)).getDayOfWeek())));
        Assert.assertEquals(282, ((int) (((DateTime) (now)).getDayOfYear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getWeekyear())));
        Assert.assertEquals(2018, ((int) (((DateTime) (now)).getYearOfEra())));
        Assert.assertEquals(18, ((int) (((DateTime) (now)).getYearOfCentury())));
        Assert.assertEquals(20, ((int) (((DateTime) (now)).getCenturyOfEra())));
        Assert.assertEquals(1, ((int) (((DateTime) (now)).getEra())));
        Assert.assertEquals(41, ((int) (((DateTime) (now)).getWeekOfWeekyear())));
        Assert.assertFalse(((DateTimeZone) (((DateTime) (now)).getZone())).isFixed());
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).toString());
        Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (now)).getZone())).hashCode())));
        Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (now)).getZone())).getID());
        Assert.assertTrue(((DateTime) (now)).isBeforeNow());
        Assert.assertFalse(((DateTime) (now)).isAfterNow());
        Assert.assertFalse(((DateTime) (now)).isEqualNow());
        Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
        Assert.assertEquals("test alert", ((InApp) (inApp)).getAlert());
        Assert.assertTrue(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
        Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
        Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
        Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
    }

    @Test(timeout = 10000)
    public void testInAppMessagenull1433_failAssert17_add1493_add2519() throws Exception {
        try {
            DateTime o_testInAppMessagenull1433_failAssert17_add1493_add2519__3 = DateTime.now();
            Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getChronology())).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getChronology())).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getChronology())).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getChronology())).getZone())).getID());
            Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getChronology())).toString());
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getYear())));
            Assert.assertEquals(10, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getMonthOfYear())));
            Assert.assertEquals(9, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getDayOfMonth())));
            Assert.assertEquals(23, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getHourOfDay())));
            Assert.assertEquals(35, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getMinuteOfHour())));
            Assert.assertEquals(2, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getDayOfWeek())));
            Assert.assertEquals(282, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getDayOfYear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getWeekyear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getYearOfEra())));
            Assert.assertEquals(18, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getYearOfCentury())));
            Assert.assertEquals(20, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getCenturyOfEra())));
            Assert.assertEquals(1, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getEra())));
            Assert.assertEquals(1415, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getMinuteOfDay())));
            Assert.assertEquals(41, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getWeekOfWeekyear())));
            Assert.assertTrue(((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).isBeforeNow());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).isEqualNow());
            Assert.assertFalse(((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).getZone())).getID());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_add1493_add2519__3)).isAfterNow());
            DateTime now = DateTime.now();
            InApp.newBuilder();
            InApp inApp = InApp.newBuilder().setAlert("test alert").setExpiry(null).build();
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
            Assert.assertEquals("test alert", ((InApp) (inApp)).getAlert());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExpiry())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExpiry())).hashCode())));
            Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
            Assert.assertEquals("InApp{alert=test alert, displayType=banner, expiry=Optional.absent(), actions=Optional.absent(), interactive=Optional.absent(), extra=Optional.absent()}", ((InApp) (inApp)).toString());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
            inApp.getAlert();
            inApp.getExpiry().get();
            org.junit.Assert.fail("testInAppMessagenull1433 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
        }
    }

    @Test(timeout = 10000)
    public void testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905() throws Exception {
        try {
            DateTime o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3 = DateTime.now();
            Assert.assertFalse(((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getChronology())).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getChronology())).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getChronology())).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getChronology())).getZone())).getID());
            Assert.assertEquals("ISOChronology[Europe/Paris]", ((Chronology) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getChronology())).toString());
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getYear())));
            Assert.assertEquals(10, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getMonthOfYear())));
            Assert.assertEquals(9, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getDayOfMonth())));
            Assert.assertEquals(23, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getHourOfDay())));
            Assert.assertEquals(35, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getMinuteOfHour())));
            Assert.assertEquals(2, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getDayOfWeek())));
            Assert.assertEquals(282, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getDayOfYear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getWeekyear())));
            Assert.assertEquals(2018, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getYearOfEra())));
            Assert.assertEquals(18, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getYearOfCentury())));
            Assert.assertEquals(20, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getCenturyOfEra())));
            Assert.assertEquals(1, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getEra())));
            Assert.assertEquals(1415, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getMinuteOfDay())));
            Assert.assertEquals(41, ((int) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getWeekOfWeekyear())));
            Assert.assertTrue(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).isBeforeNow());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).isEqualNow());
            Assert.assertFalse(((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getZone())).isFixed());
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getZone())).toString());
            Assert.assertEquals(-672549097, ((int) (((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getZone())).hashCode())));
            Assert.assertEquals("Europe/Paris", ((DateTimeZone) (((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).getZone())).getID());
            Assert.assertFalse(((DateTime) (o_testInAppMessagenull1433_failAssert17_literalMutationString1483_add6905__3)).isAfterNow());
            DateTime now = DateTime.now();
            InApp inApp = InApp.newBuilder().setAlert("test alrt").setExpiry(null).build();
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getInteractive())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getInteractive())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getInteractive())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getDisplay())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getDisplay())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getDisplay())).hashCode())));
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExtra())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExtra())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExtra())).hashCode())));
            Assert.assertEquals("test alrt", ((InApp) (inApp)).getAlert());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getExpiry())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getExpiry())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getExpiry())).hashCode())));
            Assert.assertEquals("banner", ((InApp) (inApp)).getDisplayType());
            Assert.assertEquals("InApp{alert=test alrt, displayType=banner, expiry=Optional.absent(), actions=Optional.absent(), interactive=Optional.absent(), extra=Optional.absent()}", ((InApp) (inApp)).toString());
            Assert.assertFalse(((Optional) (((InApp) (inApp)).getActions())).isPresent());
            Assert.assertEquals("Optional.absent()", ((Optional) (((InApp) (inApp)).getActions())).toString());
            Assert.assertEquals(2040732332, ((int) (((Optional) (((InApp) (inApp)).getActions())).hashCode())));
            inApp.getAlert();
            inApp.getExpiry().get();
            org.junit.Assert.fail("testInAppMessagenull1433 should have thrown IllegalStateException");
        } catch (IllegalStateException expected) {
        }
    }
}

