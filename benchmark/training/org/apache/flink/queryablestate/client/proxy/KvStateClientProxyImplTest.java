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
package org.apache.flink.queryablestate.client.proxy;


import HighAvailabilityServices.DEFAULT_JOB_ID;
import java.util.concurrent.CompletableFuture;
import org.apache.flink.api.common.JobID;
import org.apache.flink.runtime.jobmaster.KvStateLocationOracle;
import org.apache.flink.runtime.query.KvStateLocation;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the {@link KvStateClientProxyImpl}.
 */
public class KvStateClientProxyImplTest extends TestLogger {
    private KvStateClientProxyImpl kvStateClientProxy;

    /**
     * Tests that we can set and retrieve the {@link KvStateLocationOracle}.
     */
    @Test
    public void testKvStateLocationOracle() {
        final JobID jobId1 = new JobID();
        final KvStateClientProxyImplTest.TestingKvStateLocationOracle kvStateLocationOracle1 = new KvStateClientProxyImplTest.TestingKvStateLocationOracle();
        kvStateClientProxy.updateKvStateLocationOracle(jobId1, kvStateLocationOracle1);
        final JobID jobId2 = new JobID();
        final KvStateClientProxyImplTest.TestingKvStateLocationOracle kvStateLocationOracle2 = new KvStateClientProxyImplTest.TestingKvStateLocationOracle();
        kvStateClientProxy.updateKvStateLocationOracle(jobId2, kvStateLocationOracle2);
        Assert.assertThat(kvStateClientProxy.getKvStateLocationOracle(new JobID()), Matchers.nullValue());
        Assert.assertThat(kvStateClientProxy.getKvStateLocationOracle(jobId1), Matchers.equalTo(kvStateLocationOracle1));
        Assert.assertThat(kvStateClientProxy.getKvStateLocationOracle(jobId2), Matchers.equalTo(kvStateLocationOracle2));
        kvStateClientProxy.updateKvStateLocationOracle(jobId1, null);
        Assert.assertThat(kvStateClientProxy.getKvStateLocationOracle(jobId1), Matchers.nullValue());
    }

    /**
     * Tests that {@link KvStateLocationOracle} registered under {@link HighAvailabilityServices#DEFAULT_JOB_ID}
     * will be used for all requests.
     */
    @Test
    public void testLegacyCodePathPreference() {
        final KvStateClientProxyImplTest.TestingKvStateLocationOracle kvStateLocationOracle = new KvStateClientProxyImplTest.TestingKvStateLocationOracle();
        kvStateClientProxy.updateKvStateLocationOracle(DEFAULT_JOB_ID, kvStateLocationOracle);
        final JobID jobId = new JobID();
        kvStateClientProxy.updateKvStateLocationOracle(jobId, new KvStateClientProxyImplTest.TestingKvStateLocationOracle());
        Assert.assertThat(kvStateClientProxy.getKvStateLocationOracle(jobId), Matchers.equalTo(kvStateLocationOracle));
    }

    /**
     * Testing implementation of {@link KvStateLocationOracle}.
     */
    private static final class TestingKvStateLocationOracle implements KvStateLocationOracle {
        @Override
        public CompletableFuture<KvStateLocation> requestKvStateLocation(JobID jobId, String registrationName) {
            return null;
        }
    }
}

