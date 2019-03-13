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
package org.apache.ambari.server.stack;


import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class HostsTypeTest {
    @Test
    public void testGuessMasterFrom1() {
        HostsType hosts = HostsType.guessHighAvailability(Sets.newLinkedHashSet(Arrays.asList("c6401")));
        Assert.assertThat(hosts.getMasters(), Is.is(Collections.singleton("c6401")));
        Assert.assertThat(hosts.getSecondaries(), hasSize(0));
    }

    @Test
    public void testGuessMasterFrom3() {
        HostsType hosts = HostsType.guessHighAvailability(Sets.newLinkedHashSet(Arrays.asList("c6401", "c6402", "c6403")));
        Assert.assertThat(hosts.getMasters(), Is.is(Collections.singleton("c6401")));
        Assert.assertThat(hosts.getSecondaries(), Is.is(Sets.newLinkedHashSet(Arrays.asList("c6402", "c6403"))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGuessMasterFromEmptyList() {
        HostsType.guessHighAvailability(new LinkedHashSet(Collections.emptySet()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMasterIsMandatory() {
        new HostsType.HighAvailabilityHosts(null, Collections.emptyList());
    }

    @Test
    public void testFederatedMastersAndSecondaries() {
        HostsType federated = HostsType.federated(Arrays.asList(new HostsType.HighAvailabilityHosts("master1", Arrays.asList("sec1", "sec2")), new HostsType.HighAvailabilityHosts("master2", Arrays.asList("sec3", "sec4"))), new LinkedHashSet(Collections.emptySet()));
        Assert.assertThat(federated.getMasters(), Is.is(Sets.newHashSet("master1", "master2")));
        Assert.assertThat(federated.getSecondaries(), Is.is(Sets.newHashSet("sec1", "sec2", "sec3", "sec4")));
    }

    @Test
    public void testArrangeHosts() {
        HostsType federated = HostsType.federated(Arrays.asList(new HostsType.HighAvailabilityHosts("master1", Arrays.asList("sec1", "sec2")), new HostsType.HighAvailabilityHosts("master2", Arrays.asList("sec3", "sec4"))), new LinkedHashSet(Collections.emptySet()));
        federated.arrangeHostSecondariesFirst();
        Assert.assertThat(federated.getHosts(), Is.is(Sets.newLinkedHashSet(Arrays.asList("sec1", "sec2", "master1", "sec3", "sec4", "master2"))));
    }
}

