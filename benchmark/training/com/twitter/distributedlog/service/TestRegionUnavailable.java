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
package com.twitter.distributedlog.service;


import ServerFeatureKeys.REGION_STOP_ACCEPT_NEW_STREAM;
import com.twitter.distributedlog.DistributedLogConfiguration;
import com.twitter.distributedlog.feature.DefaultFeatureProvider;
import com.twitter.distributedlog.service.DistributedLogCluster.DLServer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import org.apache.bookkeeper.feature.Feature;
import org.apache.bookkeeper.feature.FeatureProvider;
import org.apache.bookkeeper.feature.SettableFeature;
import org.apache.bookkeeper.stats.StatsLogger;
import org.junit.Test;


public class TestRegionUnavailable extends DistributedLogServerTestCase {
    public static class TestFeatureProvider extends DefaultFeatureProvider {
        public TestFeatureProvider(String rootScope, DistributedLogConfiguration conf, StatsLogger statsLogger) {
            super(rootScope, conf, statsLogger);
        }

        @Override
        protected Feature makeFeature(String featureName) {
            if (featureName.contains(REGION_STOP_ACCEPT_NEW_STREAM.name().toLowerCase())) {
                return new SettableFeature(featureName, 10000);
            }
            return super.makeFeature(featureName);
        }

        @Override
        protected FeatureProvider makeProvider(String fullScopeName) {
            return super.makeProvider(fullScopeName);
        }
    }

    private final int numServersPerDC = 3;

    private final List<DLServer> localCluster;

    private final List<DLServer> remoteCluster;

    private DistributedLogServerTestCase.TwoRegionDLClient client;

    public TestRegionUnavailable() {
        this.localCluster = new ArrayList<DLServer>();
        this.remoteCluster = new ArrayList<DLServer>();
    }

    @Test(timeout = 60000)
    public void testRegionUnavailable() throws Exception {
        String name = "dlserver-region-unavailable";
        registerStream(name);
        for (long i = 1; i <= 10; i++) {
            client.dlClient.write(name, ByteBuffer.wrap(("" + i).getBytes())).get();
        }
        // check local region
        for (DLServer server : localCluster) {
            DistributedLogServerTestCase.checkStreams(0, server);
        }
    }
}

