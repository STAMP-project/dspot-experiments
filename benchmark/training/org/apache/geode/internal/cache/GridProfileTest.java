/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.internal.cache;


import java.util.ArrayList;
import java.util.List;
import org.apache.geode.distributed.internal.DistributionAdvisor.Profile;
import org.apache.geode.distributed.internal.DistributionAdvisor.ProfileId;
import org.apache.geode.internal.cache.GridAdvisor.GridProfile;
import org.apache.geode.test.fake.Fakes;
import org.junit.Test;
import org.mockito.Mockito;


public class GridProfileTest {
    @Test
    public void shouldBeMockable() throws Exception {
        GridProfile mockGridProfile = Mockito.mock(GridProfile.class);
        InternalCache cache = Fakes.cache();
        ProfileId mockProfileId = Mockito.mock(ProfileId.class);
        List<Profile> listOfProfiles = new ArrayList<>();
        listOfProfiles.add(Mockito.mock(Profile.class));
        Mockito.when(mockGridProfile.getHost()).thenReturn("HOST");
        Mockito.when(mockGridProfile.getPort()).thenReturn(1);
        Mockito.when(mockGridProfile.getId()).thenReturn(mockProfileId);
        mockGridProfile.setHost("host");
        mockGridProfile.setPort(2);
        mockGridProfile.tellLocalControllers(true, true, listOfProfiles);
        mockGridProfile.tellLocalBridgeServers(cache, true, true, listOfProfiles);
        Mockito.verify(mockGridProfile, Mockito.times(1)).setHost("host");
        Mockito.verify(mockGridProfile, Mockito.times(1)).setPort(2);
        Mockito.verify(mockGridProfile, Mockito.times(1)).tellLocalControllers(true, true, listOfProfiles);
        Mockito.verify(mockGridProfile, Mockito.times(1)).tellLocalBridgeServers(cache, true, true, listOfProfiles);
        assertThat(mockGridProfile.getHost()).isEqualTo("HOST");
        assertThat(mockGridProfile.getPort()).isEqualTo(1);
        assertThat(mockGridProfile.getId()).isSameAs(mockProfileId);
    }
}

