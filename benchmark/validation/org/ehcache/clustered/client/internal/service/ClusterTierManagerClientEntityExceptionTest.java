/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.client.internal.service;


import java.net.URI;
import org.ehcache.clustered.client.config.ClusteringServiceConfiguration;
import org.ehcache.clustered.client.config.builders.ClusteringServiceConfigurationBuilder;
import org.ehcache.clustered.client.internal.ClusterTierManagerValidationException;
import org.ehcache.clustered.common.internal.exceptions.InvalidServerSideConfigurationException;
import org.ehcache.config.units.MemoryUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * This class includes tests to ensure server-side exceptions returned as responses to
 * {@link SimpleClusterTierManagerClientEntity} messages are wrapped before being re-thrown.  This class
 * relies on {@link DefaultClusteringService} to set up conditions for the test and
 * is placed accordingly.
 */
public class ClusterTierManagerClientEntityExceptionTest {
    private static final String CLUSTER_URI_BASE = "terracotta://example.com:9540/";

    /**
     * Tests to ensure that a {@link ClusterException ClusterException}
     * originating in the server is properly wrapped on the client before being re-thrown.
     */
    @Test
    public void testServerExceptionPassThrough() throws Exception {
        ClusteringServiceConfiguration creationConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((ClusterTierManagerClientEntityExceptionTest.CLUSTER_URI_BASE) + "my-application"))).autoCreate().defaultServerResource("defaultResource").resourcePool("sharedPrimary", 2, MemoryUnit.MB, "serverResource1").resourcePool("sharedSecondary", 2, MemoryUnit.MB, "serverResource2").resourcePool("sharedTertiary", 4, MemoryUnit.MB).build();
        DefaultClusteringService creationService = new DefaultClusteringService(creationConfig);
        creationService.start(null);
        creationService.stop();
        ClusteringServiceConfiguration accessConfig = ClusteringServiceConfigurationBuilder.cluster(URI.create(((ClusterTierManagerClientEntityExceptionTest.CLUSTER_URI_BASE) + "my-application"))).expecting().defaultServerResource("different").build();
        DefaultClusteringService accessService = new DefaultClusteringService(accessConfig);
        /* Induce an "InvalidStoreException: cluster tier 'cacheAlias' does not exist" on the server. */
        try {
            accessService.start(null);
            Assert.fail("Expecting ClusterTierManagerValidationException");
        } catch (ClusterTierManagerValidationException e) {
            /* Find the last ClusterTierManagerClientEntity involved exception in the causal chain.  This
            is where the server-side exception should have entered the client.
             */
            Throwable clientSideException = null;
            for (Throwable t = e; ((t.getCause()) != null) && ((t.getCause()) != t); t = t.getCause()) {
                for (StackTraceElement element : t.getStackTrace()) {
                    if (element.getClassName().endsWith("ClusterTierManagerClientEntity")) {
                        clientSideException = t;
                    }
                }
            }
            assert clientSideException != null;
            /* In this specific failure case, the exception is expected to be an InvalidStoreException from
            the server and re-thrown in the client.
             */
            Throwable clientSideCause = clientSideException.getCause();
            Assert.assertThat(clientSideCause, Matchers.is(Matchers.instanceOf(InvalidServerSideConfigurationException.class)));
            serverCheckLoop : {
                for (StackTraceElement element : clientSideCause.getStackTrace()) {
                    if (element.getClassName().endsWith("ClusterTierManagerActiveEntity")) {
                        break serverCheckLoop;
                    }
                }
                Assert.fail((clientSideException + " lacks server-based cause"));
            }
            Assert.assertThat(clientSideException, Matchers.is(Matchers.instanceOf(InvalidServerSideConfigurationException.class)));
        } finally {
            accessService.stop();
        }
    }
}

