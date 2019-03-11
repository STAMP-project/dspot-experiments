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
package org.apache.geode;


import org.apache.geode.internal.cache.EventID;
import org.apache.geode.internal.cache.tier.sockets.ClientProxyMembershipID;
import org.junit.Test;
import org.mockito.Mockito;


public class InstantiatorTest {
    @Test
    public void shouldBeMockable() throws Exception {
        Instantiator mockInstantiator = Mockito.mock(Instantiator.class);
        EventID mockEventID = Mockito.mock(EventID.class);
        ClientProxyMembershipID mockClientProxyMembershipID = Mockito.mock(ClientProxyMembershipID.class);
        Mockito.when(mockInstantiator.getInstantiatedClass()).thenReturn(null);
        Mockito.when(mockInstantiator.getId()).thenReturn(0);
        Mockito.when(mockInstantiator.getEventId()).thenReturn(mockEventID);
        Mockito.when(mockInstantiator.getContext()).thenReturn(mockClientProxyMembershipID);
        mockInstantiator.setEventId(mockEventID);
        mockInstantiator.setContext(mockClientProxyMembershipID);
        Mockito.verify(mockInstantiator, Mockito.times(1)).setEventId(mockEventID);
        Mockito.verify(mockInstantiator, Mockito.times(1)).setContext(mockClientProxyMembershipID);
        assertThat(mockInstantiator.getEventId()).isSameAs(mockEventID);
        assertThat(mockInstantiator.getContext()).isSameAs(mockClientProxyMembershipID);
        assertThat(mockInstantiator.getInstantiatedClass()).isNull();
    }
}

