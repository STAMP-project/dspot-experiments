package org.opentripplanner.gtfs.mapping;


import java.util.Collection;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.model.org.opentripplanner.model.ServiceCalendarDate;


public class ServiceCalendarDateMapperTest {
    private static final ServiceCalendarDate SERVICE_DATE = new ServiceCalendarDate();

    private static final AgencyAndId AGENCY_AND_ID = new AgencyAndId("A", "1");

    private static final Integer ID = 45;

    private static final ServiceDate DATE = new ServiceDate(2017, 10, 15);

    private static final int EXCEPTION_TYPE = 2;

    static {
        ServiceCalendarDateMapperTest.SERVICE_DATE.setId(ServiceCalendarDateMapperTest.ID);
        ServiceCalendarDateMapperTest.SERVICE_DATE.setDate(ServiceCalendarDateMapperTest.DATE);
        ServiceCalendarDateMapperTest.SERVICE_DATE.setExceptionType(ServiceCalendarDateMapperTest.EXCEPTION_TYPE);
        ServiceCalendarDateMapperTest.SERVICE_DATE.setServiceId(ServiceCalendarDateMapperTest.AGENCY_AND_ID);
    }

    private ServiceCalendarDateMapper subject = new ServiceCalendarDateMapper();

    @Test
    public void testMapCollection() throws Exception {
        Assert.assertNull(null, subject.map(((Collection<ServiceCalendarDate>) (null))));
        Assert.assertTrue(subject.map(Collections.emptyList()).isEmpty());
        Assert.assertEquals(1, subject.map(Collections.singleton(ServiceCalendarDateMapperTest.SERVICE_DATE)).size());
    }

    @Test
    public void testMap() throws Exception {
        org.opentripplanner.model.ServiceCalendarDate result = subject.map(ServiceCalendarDateMapperTest.SERVICE_DATE);
        Assert.assertEquals(ServiceCalendarDateMapperTest.DATE.getAsString(), result.getDate().getAsString());
        Assert.assertEquals(ServiceCalendarDateMapperTest.EXCEPTION_TYPE, result.getExceptionType());
        Assert.assertEquals("A_1", result.getServiceId().toString());
    }

    @Test
    public void testMapWithNulls() throws Exception {
        ServiceCalendarDate input = new ServiceCalendarDate();
        org.opentripplanner.model.ServiceCalendarDate result = subject.map(input);
        Assert.assertNull(result.getDate());
        Assert.assertEquals(0, result.getExceptionType());
        Assert.assertNull(result.getServiceId());
    }

    /**
     * Mapping the same object twice, should return the the same instance.
     */
    @Test
    public void testMapCache() throws Exception {
        org.opentripplanner.model.ServiceCalendarDate result1 = subject.map(ServiceCalendarDateMapperTest.SERVICE_DATE);
        org.opentripplanner.model.ServiceCalendarDate result2 = subject.map(ServiceCalendarDateMapperTest.SERVICE_DATE);
        Assert.assertTrue((result1 == result2));
    }
}

