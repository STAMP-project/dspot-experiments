package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.model.org.opentripplanner.model.ServiceCalendar;


public class ServiceCalendarMapperTest {
    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final int MONDAY = 1;

    private static final int TUESDAY = 2;

    private static final int WEDNESDAY = 3;

    private static final int THURSDAY = 4;

    private static final int FRIDAY = 5;

    private static final int SATURDAY = 6;

    private static final int SUNDAY = 7;

    private static final ServiceDate START_DATE = new ServiceDate(2017, 10, 17);

    private static final ServiceDate END_DATE = new ServiceDate(2018, 1, 2);

    private static final ServiceCalendar CALENDAR = new ServiceCalendar();

    static {
        ServiceCalendarMapperTest.CALENDAR.setId(ServiceCalendarMapperTest.ID);
        ServiceCalendarMapperTest.CALENDAR.setServiceId(ServiceCalendarMapperTest.AGENCY_AND_ID);
        ServiceCalendarMapperTest.CALENDAR.setMonday(ServiceCalendarMapperTest.MONDAY);
        ServiceCalendarMapperTest.CALENDAR.setTuesday(ServiceCalendarMapperTest.TUESDAY);
        ServiceCalendarMapperTest.CALENDAR.setWednesday(ServiceCalendarMapperTest.WEDNESDAY);
        ServiceCalendarMapperTest.CALENDAR.setThursday(ServiceCalendarMapperTest.THURSDAY);
        ServiceCalendarMapperTest.CALENDAR.setFriday(ServiceCalendarMapperTest.FRIDAY);
        ServiceCalendarMapperTest.CALENDAR.setSaturday(ServiceCalendarMapperTest.SATURDAY);
        ServiceCalendarMapperTest.CALENDAR.setSunday(ServiceCalendarMapperTest.SUNDAY);
        ServiceCalendarMapperTest.CALENDAR.setStartDate(ServiceCalendarMapperTest.START_DATE);
        ServiceCalendarMapperTest.CALENDAR.setEndDate(ServiceCalendarMapperTest.END_DATE);
    }

    private ServiceCalendarMapper subject = new ServiceCalendarMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<ServiceCalendar>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(ServiceCalendarMapperTest.CALENDAR)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.ServiceCalendar result = subject.map(ServiceCalendarMapperTest.CALENDAR);
        Assert.assertEquals("A_1", result.getServiceId().toString());
        Assert.assertEquals(ServiceCalendarMapperTest.MONDAY, result.getMonday());
        Assert.assertEquals(ServiceCalendarMapperTest.TUESDAY, result.getTuesday());
        Assert.assertEquals(ServiceCalendarMapperTest.WEDNESDAY, result.getWednesday());
        Assert.assertEquals(ServiceCalendarMapperTest.THURSDAY, result.getThursday());
        Assert.assertEquals(ServiceCalendarMapperTest.FRIDAY, result.getFriday());
        Assert.assertEquals(ServiceCalendarMapperTest.SATURDAY, result.getSaturday());
        Assert.assertEquals(ServiceCalendarMapperTest.SUNDAY, result.getSunday());
        Assert.assertEquals(ServiceCalendarMapperTest.START_DATE.getAsString(), result.getStartDate().getAsString());
        Assert.assertEquals(ServiceCalendarMapperTest.END_DATE.getAsString(), result.getEndDate().getAsString());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        ServiceCalendar input = new ServiceCalendar();
        org.opentripplanner.model.ServiceCalendar result = subject.map(input);
        Assert.assertEquals(0, result.getMonday());
        Assert.assertEquals(0, result.getTuesday());
        Assert.assertEquals(0, result.getWednesday());
        Assert.assertEquals(0, result.getThursday());
        Assert.assertEquals(0, result.getFriday());
        Assert.assertEquals(0, result.getSaturday());
        Assert.assertEquals(0, result.getSunday());
        Assert.assertNull(result.getStartDate());
        Assert.assertNull(result.getEndDate());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.ServiceCalendar result1 = subject.map(ServiceCalendarMapperTest.CALENDAR);
        org.opentripplanner.model.ServiceCalendar result2 = subject.map(ServiceCalendarMapperTest.CALENDAR);
        Assert.assertTrue((result1 == result2));
    }
}

