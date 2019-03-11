package com.twitter.ambrose.model;


import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link EventTest}.
 */
public class EventTest {
    @Test
    public void testRoundTrip() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("someprop", "propvalue");
        Map<String, Number> metrics = Maps.newHashMap();
        metrics.put("somemetric", 6);
        Job job = new Job("scope-123", properties, metrics);
        DAGNode<Job> node = new DAGNode<Job>("dag name", job);
        testRoundTrip(new Event.JobStartedEvent(node));
    }

    @Test
    public void testFromJson() throws IOException {
        String json = "{\n" + (((((((((((((((("  \"type\" : \"JOB_STARTED\",\n" + "  \"payload\" : {\n") + "    \"name\" : \"scope-29\",\n") + "    \"job\" : {\n") + "      \"runtime\" : \"default\",\n") + "      \"id\" : \"job_local_0001\",\n") + "      \"aliases\" : [ \"A\", \"AA\", \"B\", \"C\" ],\n") + "      \"features\" : [ \"GROUP_BY\", \"COMBINER\", \"MAP_PARTIALAGG\" ],\n") + "      \"metrics\" : { \n") + "            \"somemetrics\" : 111 \n") + "        } \n ") + "    },\n") + "    \"successorNames\" : [ ]\n") + "  },\n") + "  \"id\" : 1,\n") + "  \"timestamp\" : 1373560988033\n") + "}");
        Event event = Event.fromJson(json);
        Job job = ((DAGNode<Job>) (event.getPayload())).getJob();
        Assert.assertEquals("job_local_0001", job.getId());
        Assert.assertEquals(111, job.getMetrics().get("somemetrics"));
    }
}

