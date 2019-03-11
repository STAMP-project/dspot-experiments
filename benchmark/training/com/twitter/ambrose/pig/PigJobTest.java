package com.twitter.ambrose.pig;


import com.twitter.ambrose.model.DAGNode;
import com.twitter.ambrose.model.Event;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link PigJobTest}.
 */
public class PigJobTest {
    PigJob pigJob;

    @Test
    public void testPigJobRoundTrip() throws IOException {
        doTestRoundTrip(pigJob);
    }

    @Test
    public void testDAGNodePigJobRoundTrip() throws IOException {
        DAGNode<PigJob> node = new DAGNode<PigJob>("dag name", pigJob);
        doTestRoundTrip(node);
    }

    @Test
    public void testFromJson() throws IOException {
        String json = "{\n" + (((((((((((((((("  \"type\" : \"JOB_STARTED\",\n" + "  \"payload\" : {\n") + "    \"name\" : \"scope-29\",\n") + "    \"job\" : {\n") + "      \"runtime\" : \"pig\",\n") + "      \"id\" : \"job_local_0001\",\n") + "      \"aliases\" : [ \"A\", \"AA\", \"B\", \"C\" ],\n") + "      \"features\" : [ \"GROUP_BY\", \"COMBINER\", \"MAP_PARTIALAGG\" ],\n") + "      \"metrics\" : {\n") + "        \"somemetric\": 123\n") + "      } \n") + "    },\n") + "    \"successorNames\" : [ ]\n") + "  },\n") + "  \"id\" : 1,\n") + "  \"timestamp\" : 1373560988033\n") + "}");
        Event event = Event.fromJson(json);
        PigJob job = ((DAGNode<PigJob>) (event.getPayload())).getJob();
        Assert.assertEquals("job_local_0001", job.getId());
        Assert.assertArrayEquals(new String[]{ "A", "AA", "B", "C" }, job.getAliases());
        Assert.assertArrayEquals(new String[]{ "GROUP_BY", "COMBINER", "MAP_PARTIALAGG" }, job.getFeatures());
        Assert.assertNotNull(job.getMetrics());
        Assert.assertEquals(123, job.getMetrics().get("somemetric"));
    }
}

