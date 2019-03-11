/**
 * Copyright (c) 2010-2019 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.openhab.persistence.dynamodb.internal;


import OnOffType.OFF;
import OnOffType.ON;
import OpenClosedType.CLOSED;
import OpenClosedType.OPEN;
import UnDefType.UNDEF;
import UpDownType.DOWN;
import UpDownType.UP;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.openhab.core.library.items.ColorItem;
import org.openhab.core.library.items.ContactItem;
import org.openhab.core.library.items.DateTimeItem;
import org.openhab.core.library.items.DimmerItem;
import org.openhab.core.library.items.LocationItem;
import org.openhab.core.library.items.NumberItem;
import org.openhab.core.library.items.RollershutterItem;
import org.openhab.core.library.items.StringItem;
import org.openhab.core.library.items.SwitchItem;
import org.openhab.core.library.types.DateTimeType;
import org.openhab.core.library.types.DecimalType;
import org.openhab.core.library.types.HSBType;
import org.openhab.core.library.types.PercentType;
import org.openhab.core.library.types.PointType;
import org.openhab.core.library.types.StringType;
import org.openhab.library.tel.items.CallItem;
import org.openhab.library.tel.types.CallType;


/**
 * Test for AbstractDynamoDBItem.fromState and AbstractDynamoDBItem.asHistoricItem for all kind of states
 *
 * @author Sami Salonen
 */
public class AbstractDynamoDBItemSerializationTest {
    private final Date date = new Date(400);

    @Test
    public void testUndefWithNumberItem() throws IOException {
        final DynamoDBItem<?> dbitem = testStateGeneric(UNDEF, "<org.openhab.core.types.UnDefType.UNDEF>");
        Assert.assertTrue((dbitem instanceof DynamoDBStringItem));
        testAsHistoricGeneric(dbitem, new NumberItem("foo"), UNDEF);
    }

    @Test
    public void testCallTypeWithCallItem() throws IOException {
        final DynamoDBItem<?> dbitem = testStateGeneric(new CallType("origNum", "destNum"), "destNum##origNum");
        testAsHistoricGeneric(dbitem, new CallItem("foo"), new CallType("origNum", "destNum"));
    }

    @Test
    public void testOpenClosedTypeWithContactItem() throws IOException {
        final DynamoDBItem<?> dbitemOpen = testStateGeneric(CLOSED, BigDecimal.ZERO);
        testAsHistoricGeneric(dbitemOpen, new ContactItem("foo"), CLOSED);
        final DynamoDBItem<?> dbitemClosed = testStateGeneric(OPEN, BigDecimal.ONE);
        testAsHistoricGeneric(dbitemClosed, new ContactItem("foo"), OPEN);
    }

    @Test
    public void testDateTimeTypeWithDateTimeItem() throws IOException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(2016, Calendar.MAY, 1, 13, 46, 0);
        calendar.set(Calendar.MILLISECOND, 50);
        DynamoDBItem<?> dbitem = testStateGeneric(new DateTimeType(calendar), "2016-05-01T13:46:00.050Z");
        testAsHistoricGeneric(dbitem, new DateTimeItem("foo"), new DateTimeType(calendar));
    }

    @Test
    public void testDateTimeTypeWithStringItem() throws IOException {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(2016, Calendar.MAY, 1, 13, 46, 0);
        calendar.set(Calendar.MILLISECOND, 50);
        DynamoDBItem<?> dbitem = testStateGeneric(new DateTimeType(calendar), "2016-05-01T13:46:00.050Z");
        testAsHistoricGeneric(dbitem, new StringItem("foo"), new StringType("2016-05-01T13:46:00.050Z"));
    }

    @Test
    public void testDateTimeTypeLocalWithDateTimeItem() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        calendar.setTimeInMillis(1468773487050L);// GMT: Sun, 17 Jul 2016 16:38:07.050 GMT

        DynamoDBItem<?> dbitem = testStateGeneric(new DateTimeType(calendar), "2016-07-17T16:38:07.050Z");
        // when deserializing data, we get the date in UTC Calendar
        Calendar expectedCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        expectedCal.setTimeInMillis(1468773487050L);
        testAsHistoricGeneric(dbitem, new DateTimeItem("foo"), new DateTimeType(expectedCal));
    }

    @Test
    public void testDateTimeTypeLocalWithStringItem() throws IOException {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+03:00"));
        calendar.setTimeInMillis(1468773487050L);// GMT: Sun, 17 Jul 2016 16:38:07.050 GMT

        DynamoDBItem<?> dbitem = testStateGeneric(new DateTimeType(calendar), "2016-07-17T16:38:07.050Z");
        testAsHistoricGeneric(dbitem, new StringItem("foo"), new StringType("2016-07-17T16:38:07.050Z"));
    }

    @Test
    public void testPointTypeWithLocationItem() throws IOException {
        final PointType point = new PointType(new DecimalType(60.3), new DecimalType(30.2), new DecimalType(510.9));
        String expected = StringUtils.join(new String[]{ point.getLatitude().toBigDecimal().toString(), point.getLongitude().toBigDecimal().toString(), point.getAltitude().toBigDecimal().toString() }, ",");
        DynamoDBItem<?> dbitem = testStateGeneric(point, expected);
        testAsHistoricGeneric(dbitem, new LocationItem("foo"), point);
    }

    @Test
    public void testDecimalTypeWithNumberItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new DecimalType(3.2), new BigDecimal(3.2));
        testAsHistoricGeneric(dbitem, new NumberItem("foo"), new DecimalType(3.2));
    }

    @Test
    public void testPercentTypeWithColorItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new PercentType(new BigDecimal(3.2)), new BigDecimal(3.2));
        testAsHistoricGeneric(dbitem, new ColorItem("foo"), new PercentType(new BigDecimal(3.2)));
    }

    @Test
    public void testPercentTypeWithDimmerItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new PercentType(new BigDecimal(3.2)), new BigDecimal(3.2));
        testAsHistoricGeneric(dbitem, new DimmerItem("foo"), new PercentType(new BigDecimal(3.2)));
    }

    @Test
    public void testPercentTypeWithRollerShutterItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new PercentType(new BigDecimal(3.2)), new BigDecimal(3.2));
        testAsHistoricGeneric(dbitem, new RollershutterItem("foo"), new PercentType(new BigDecimal(3.2)));
    }

    @Test
    public void testPercentTypeWithNumberItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new PercentType(new BigDecimal(3.2)), new BigDecimal(3.2));
        // note: comes back as DecimalType instead of the original PercentType
        testAsHistoricGeneric(dbitem, new NumberItem("foo"), new DecimalType(new BigDecimal(3.2)));
    }

    @Test
    public void testUpDownTypeWithRollershutterItem() throws IOException {
        // note: comes back as PercentType instead of the original UpDownType
        DynamoDBItem<?> dbItemDown = testStateGeneric(DOWN, BigDecimal.ZERO);
        testAsHistoricGeneric(dbItemDown, new RollershutterItem("foo"), new PercentType(BigDecimal.ZERO));
        DynamoDBItem<?> dbItemUp = testStateGeneric(UP, BigDecimal.ONE);
        testAsHistoricGeneric(dbItemUp, new RollershutterItem("foo"), new PercentType(BigDecimal.ONE));
    }

    @Test
    public void testStringTypeWithStringItem() throws IOException {
        DynamoDBItem<?> dbitem = testStateGeneric(new StringType("foo bar"), "foo bar");
        testAsHistoricGeneric(dbitem, new StringItem("foo"), new StringType("foo bar"));
    }

    @Test
    public void testOnOffTypeWithColorItem() throws IOException {
        DynamoDBItem<?> dbitemOff = testStateGeneric(OFF, BigDecimal.ZERO);
        testAsHistoricGeneric(dbitemOff, new ColorItem("foo"), new PercentType(BigDecimal.ZERO));
        DynamoDBItem<?> dbitemOn = testStateGeneric(ON, BigDecimal.ONE);
        testAsHistoricGeneric(dbitemOn, new ColorItem("foo"), new PercentType(BigDecimal.ONE));
    }

    @Test
    public void testOnOffTypeWithDimmerItem() throws IOException {
        DynamoDBItem<?> dbitemOff = testStateGeneric(OFF, BigDecimal.ZERO);
        testAsHistoricGeneric(dbitemOff, new DimmerItem("foo"), new PercentType(BigDecimal.ZERO));
        DynamoDBItem<?> dbitemOn = testStateGeneric(ON, BigDecimal.ONE);
        testAsHistoricGeneric(dbitemOn, new DimmerItem("foo"), new PercentType(BigDecimal.ONE));
    }

    @Test
    public void testOnOffTypeWithSwitchItem() throws IOException {
        DynamoDBItem<?> dbitemOff = testStateGeneric(OFF, BigDecimal.ZERO);
        testAsHistoricGeneric(dbitemOff, new SwitchItem("foo"), OFF);
        DynamoDBItem<?> dbitemOn = testStateGeneric(ON, BigDecimal.ONE);
        testAsHistoricGeneric(dbitemOn, new SwitchItem("foo"), ON);
    }

    @Test
    public void testHSBTypeWithColorItem() throws IOException {
        HSBType hsb = new HSBType(new DecimalType(1.5), new PercentType(new BigDecimal(2.5)), new PercentType(new BigDecimal(3.5)));
        DynamoDBItem<?> dbitem = testStateGeneric(hsb, "1.5,2.5,3.5");
        testAsHistoricGeneric(dbitem, new ColorItem("foo"), hsb);
    }
}

