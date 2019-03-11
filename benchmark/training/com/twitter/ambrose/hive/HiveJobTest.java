/**
 * Copyright 2013, Lorand Bendig
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.twitter.ambrose.hive;


import com.twitter.ambrose.model.DAGNode;
import com.twitter.ambrose.model.Event;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit tests for {@link HiveJobTest}.
 */
public class HiveJobTest {
    private HiveJob hiveJob;

    @Test
    public void testHiveJobRoundTrip() throws IOException {
        doTestRoundTrip(hiveJob);
    }

    @Test
    public void testDAGNodeHiveJobRoundTrip() throws IOException {
        DAGNode<HiveJob> node = new DAGNode<HiveJob>("dag name", hiveJob);
        doTestRoundTrip(node);
    }

    @Test
    public void testFromJson() throws IOException {
        String json = "{" + ((((((((((((((((((((((("  \"type\" : \"JOB_STARTED\"," + "  \"payload\" : {") + "    \"name\" : \"Stage-1_user_20130723105858_3f0d530c-34a6-4bb9-8964-22c4ea289895\",") + "    \"job\" : {") + "      \"runtime\" : \"hive\",") + "      \"id\" : \"job_201307231015_0004 (Stage-1, query-id: ...22c4ea289895)\",") + "      \"aliases\" : [ \"src\" ],") + "      \"features\" : [ \"SELECT\", \"FILTER\" ],") + "      \"metrics\" : {\n") + "        \"somemetric\": 123\n") + "      } \n") + "    },") + "    \"successorNames\" : [ ]") + "  },") + "  \"id\" : 1,") + "  \"timestamp\" : 1374569908714") + "}, {") + "  \"type\" : \"WORKFLOW_PROGRESS\",") + "  \"payload\" : {") + "    \"workflowProgress\" : \"0\"") + "  },") + "  \"id\" : 2,") + "  \"timestamp\" : 1374569908754") + "}");
        Event<?> event = Event.fromJson(json);
        @SuppressWarnings("unchecked")
        HiveJob job = ((DAGNode<HiveJob>) (event.getPayload())).getJob();
        Assert.assertEquals("job_201307231015_0004 (Stage-1, query-id: ...22c4ea289895)", job.getId());
        Assert.assertArrayEquals(new String[]{ "src" }, job.getAliases());
        Assert.assertArrayEquals(new String[]{ "SELECT", "FILTER" }, job.getFeatures());
        Assert.assertNotNull(job.getMetrics());
        Assert.assertEquals(123, job.getMetrics().get("somemetric"));
    }
}

