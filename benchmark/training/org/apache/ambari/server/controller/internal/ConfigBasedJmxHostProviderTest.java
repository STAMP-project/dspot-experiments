/**
 * * Licensed to the Apache Software Foundation (ASF) under one
 *  * or more contributor license agreements.  See the NOTICE file
 *  * distributed with this work for additional information
 *  * regarding copyright ownership.  The ASF licenses this file
 *  * to you under the Apache License, Version 2.0 (the
 *  * "License"); you may not use this file except in compliance
 *  * with the License.  You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 */
package org.apache.ambari.server.controller.internal;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.ambari.server.controller.jmx.JMXHostProvider;
import org.apache.ambari.server.state.ConfigHelper;
import org.easymock.EasyMockRule;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ConfigBasedJmxHostProviderTest extends EasyMockSupport {
    private static final String COMPONENT_WITH_OVERRIDDEN_HOST = "component1";

    private static final String CLUSTER_1 = "cluster1";

    private static final String COMPONENT_WITHOUT_OVERRIDDEN_HOST = "componentWithoutOverriddenHost";

    private static final String RESOLVED_HOST = "resolved.fqdn";

    private static final Set<String> resolvedUris = new HashSet<>(Arrays.asList(ConfigBasedJmxHostProviderTest.RESOLVED_HOST));

    @Rule
    public EasyMockRule mocks = new EasyMockRule(this);

    @Mock
    private JMXHostProvider defaultHostProvider;

    @Mock
    private ConfigHelper configHelper;

    private JMXHostProvider hostProvider;

    @Test
    public void testDelegatesWhenHostIsNotOverridden() throws Exception {
        expect(defaultHostProvider.getHostNames(ConfigBasedJmxHostProviderTest.CLUSTER_1, ConfigBasedJmxHostProviderTest.COMPONENT_WITHOUT_OVERRIDDEN_HOST)).andReturn(ConfigBasedJmxHostProviderTest.resolvedUris).anyTimes();
        replayAll();
        Assert.assertThat(hostProvider.getHostNames(ConfigBasedJmxHostProviderTest.CLUSTER_1, ConfigBasedJmxHostProviderTest.COMPONENT_WITHOUT_OVERRIDDEN_HOST), Is.is(ConfigBasedJmxHostProviderTest.resolvedUris));
        verifyAll();
    }

    @Test
    public void testGetsUriFromConfigWhenHostIsOverridden() throws Exception {
        expect(configHelper.getEffectiveConfigProperties(ConfigBasedJmxHostProviderTest.CLUSTER_1, null)).andReturn(config()).anyTimes();
        replayAll();
        Assert.assertThat(hostProvider.getHostNames(ConfigBasedJmxHostProviderTest.CLUSTER_1, ConfigBasedJmxHostProviderTest.COMPONENT_WITH_OVERRIDDEN_HOST), Is.is(ConfigBasedJmxHostProviderTest.resolvedUris));
        verifyAll();
    }
}

