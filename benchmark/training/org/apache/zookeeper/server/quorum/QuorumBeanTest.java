/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.zookeeper.server.quorum;


import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public class QuorumBeanTest {
    @Test
    public void testGetNameProperty() {
        QuorumPeer qpMock = Mockito.mock(QuorumPeer.class);
        Mockito.when(qpMock.getId()).thenReturn(1L);
        QuorumBean qb = new QuorumBean(qpMock);
        MatcherAssert.assertThat("getName property should return Bean name in the right format", qb.getName(), CoreMatchers.equalTo("ReplicatedServer_id1"));
    }

    @Test
    public void testIsHiddenProperty() {
        QuorumPeer qpMock = Mockito.mock(QuorumPeer.class);
        QuorumBean qb = new QuorumBean(qpMock);
        MatcherAssert.assertThat("isHidden should return false", qb.isHidden(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testGetQuorumSizeProperty() {
        QuorumPeer qpMock = Mockito.mock(QuorumPeer.class);
        QuorumBean qb = new QuorumBean(qpMock);
        Mockito.when(qpMock.getQuorumSize()).thenReturn(5);
        MatcherAssert.assertThat("getQuorumSize property should return value of peet.getQuorumSize()", qb.getQuorumSize(), CoreMatchers.equalTo(5));
    }

    @Test
    public void testSslQuorumProperty() {
        QuorumPeer qpMock = Mockito.mock(QuorumPeer.class);
        QuorumBean qb = new QuorumBean(qpMock);
        Mockito.when(qpMock.isSslQuorum()).thenReturn(true);
        MatcherAssert.assertThat("isSslQuorum property should return value of peer.isSslQuorum()", qb.isSslQuorum(), CoreMatchers.equalTo(true));
        Mockito.when(qpMock.isSslQuorum()).thenReturn(false);
        MatcherAssert.assertThat("isSslQuorum property should return value of peer.isSslQuorum()", qb.isSslQuorum(), CoreMatchers.equalTo(false));
    }

    @Test
    public void testPortUnificationProperty() {
        QuorumPeer qpMock = Mockito.mock(QuorumPeer.class);
        QuorumBean qb = new QuorumBean(qpMock);
        Mockito.when(qpMock.shouldUsePortUnification()).thenReturn(true);
        MatcherAssert.assertThat("isPortUnification property should return value of peer.shouldUsePortUnification()", qb.isPortUnification(), CoreMatchers.equalTo(true));
        Mockito.when(qpMock.shouldUsePortUnification()).thenReturn(false);
        MatcherAssert.assertThat("isPortUnification property should return value of peer.shouldUsePortUnification()", qb.isPortUnification(), CoreMatchers.equalTo(false));
    }
}

