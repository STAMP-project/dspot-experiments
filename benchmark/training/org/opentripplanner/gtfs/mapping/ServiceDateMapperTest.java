package org.opentripplanner.gtfs.mapping;


import org.junit.Assert;
import org.junit.Test;
import org.onebusaway.gtfs.model.calendar.ServiceDate;


public class ServiceDateMapperTest {
    @Test
    public void testMapServiceDate() throws Exception {
        ServiceDate input = new ServiceDate(2017, 10, 3);
        org.opentripplanner.model.calendar.ServiceDate result = ServiceDateMapper.mapServiceDate(input);
        Assert.assertEquals(2017, result.getYear());
        Assert.assertEquals(10, result.getMonth());
        Assert.assertEquals(3, result.getDay());
    }

    @Test
    public void testMapServiceDateNullRef() throws Exception {
        Assert.assertNull(ServiceDateMapper.mapServiceDate(null));
    }
}

