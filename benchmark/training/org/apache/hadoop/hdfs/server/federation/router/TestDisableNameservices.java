/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.federation.router;


import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.server.federation.MiniRouterDFSCluster;
import org.apache.hadoop.hdfs.server.federation.StateStoreDFSCluster;
import org.apache.hadoop.hdfs.server.federation.metrics.FederationMetrics;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the behavior when disabling name services.
 */
public class TestDisableNameservices {
    private static StateStoreDFSCluster cluster;

    private static MiniRouterDFSCluster.RouterContext routerContext;

    private static RouterClient routerAdminClient;

    private static ClientProtocol routerProtocol;

    @Test
    public void testWithoutDisabling() throws IOException {
        // ns0 is slow and renewLease should take a long time
        long t0 = monotonicNow();
        TestDisableNameservices.routerProtocol.renewLease("client0");
        long t = (monotonicNow()) - t0;
        Assert.assertTrue((("It took too little: " + t) + "ms"), (t > (TimeUnit.SECONDS.toMillis(1))));
        // Return the results from all subclusters even if slow
        FileSystem routerFs = TestDisableNameservices.routerContext.getFileSystem();
        FileStatus[] filesStatus = routerFs.listStatus(new Path("/"));
        Assert.assertEquals(2, filesStatus.length);
        Assert.assertEquals("dirns0", filesStatus[0].getPath().getName());
        Assert.assertEquals("dirns1", filesStatus[1].getPath().getName());
    }

    @Test
    public void testDisabling() throws Exception {
        TestDisableNameservices.disableNameservice("ns0");
        // renewLease should be fast as we are skipping ns0
        long t0 = monotonicNow();
        TestDisableNameservices.routerProtocol.renewLease("client0");
        long t = (monotonicNow()) - t0;
        Assert.assertTrue((("It took too long: " + t) + "ms"), (t < (TimeUnit.SECONDS.toMillis(1))));
        // We should not report anything from ns0
        FileSystem routerFs = TestDisableNameservices.routerContext.getFileSystem();
        FileStatus[] filesStatus = routerFs.listStatus(new Path("/"));
        Assert.assertEquals(1, filesStatus.length);
        Assert.assertEquals("dirns1", filesStatus[0].getPath().getName());
    }

    @Test
    public void testMetrics() throws Exception {
        TestDisableNameservices.disableNameservice("ns0");
        int numActive = 0;
        int numDisabled = 0;
        Router router = TestDisableNameservices.routerContext.getRouter();
        FederationMetrics metrics = router.getMetrics();
        String jsonString = metrics.getNameservices();
        JSONObject jsonObject = new JSONObject(jsonString);
        Iterator<?> keys = jsonObject.keys();
        while (keys.hasNext()) {
            String key = ((String) (keys.next()));
            JSONObject json = jsonObject.getJSONObject(key);
            String nsId = json.getString("nameserviceId");
            String state = json.getString("state");
            if (nsId.equals("ns0")) {
                Assert.assertEquals("DISABLED", state);
                numDisabled++;
            } else {
                Assert.assertEquals("ACTIVE", state);
                numActive++;
            }
        } 
        Assert.assertEquals(1, numActive);
        Assert.assertEquals(1, numDisabled);
    }
}

