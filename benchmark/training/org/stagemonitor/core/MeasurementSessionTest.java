package org.stagemonitor.core;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.stagemonitor.core.util.JsonUtils;
import org.stagemonitor.junit.ConditionalTravisTestRunner;
import org.stagemonitor.junit.ExcludeOnTravis;


@RunWith(ConditionalTravisTestRunner.class)
public class MeasurementSessionTest {
    @Test
    @ExcludeOnTravis
    public void testGetHostname() {
        Assert.assertNotNull(CorePlugin.getNameOfLocalHost());
    }

    @Test
    public void testToJson() throws Exception {
        MeasurementSession measurementSession = new MeasurementSession("app", "host", "instance");
        final MeasurementSession jsonSession = JsonUtils.getMapper().readValue(JsonUtils.toJson(measurementSession), MeasurementSession.class);
        Assert.assertEquals(measurementSession.getApplicationName(), jsonSession.getApplicationName());
        Assert.assertEquals(measurementSession.getHostName(), jsonSession.getHostName());
        Assert.assertEquals(measurementSession.getInstanceName(), jsonSession.getInstanceName());
        Assert.assertEquals(measurementSession.getInstanceName(), jsonSession.getInstanceName());
        Assert.assertEquals(measurementSession.getId(), jsonSession.getId());
        Assert.assertEquals(measurementSession.getStart(), jsonSession.getStart());
    }
}

