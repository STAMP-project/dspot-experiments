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
package org.apache.geode.distributed.internal.membership.gms.messenger;


import java.util.Properties;
import java.util.concurrent.RejectedExecutionException;
import org.apache.geode.distributed.ConfigurationProperties;
import org.apache.geode.distributed.internal.ClusterDistributionManager;
import org.apache.geode.distributed.internal.DistributionConfigImpl;
import org.apache.geode.distributed.internal.LonerDistributionManager.DummyDMStats;
import org.apache.geode.distributed.internal.membership.gms.ServiceConfig;
import org.apache.geode.distributed.internal.membership.gms.Services;
import org.apache.geode.distributed.internal.membership.gms.interfaces.Manager;
import org.apache.geode.internal.admin.remote.RemoteTransportConfig;
import org.apache.geode.test.junit.categories.MembershipTest;
import org.jgroups.Event;
import org.jgroups.Message;
import org.jgroups.protocols.UNICAST3.Header;
import org.jgroups.protocols.pbcast.NakAckHeader2;
import org.jgroups.stack.Protocol;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * This class tests the GMS StatRecorder class, which records JGroups messaging statistics
 */
@Category({ MembershipTest.class })
public class StatRecorderJUnitTest {
    private Protocol mockDownProtocol;

    private Protocol mockUpProtocol;

    private StatRecorder recorder;

    private StatRecorderJUnitTest.MyStats stats;

    private Services services;

    /**
     * Ensure that unicast events are recorded in DMStats
     */
    @Test
    public void testUnicastStats() throws Exception {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(Header.createDataHeader(1L, ((short) (1)), true));
        Mockito.when(msg.size()).thenReturn(150L);
        Event evt = new Event(Event.MSG, msg);
        recorder.up(evt);
        Assert.assertTrue(("stats.ucastMessagesReceived =" + (stats.ucastMessagesReceived)), ((stats.ucastMessagesReceived) == 1));
        Assert.assertEquals(stats.ucastMessageBytesReceived, 150);
        recorder.down(evt);
        Assert.assertTrue(("stats.ucastMessagesSent =" + (stats.ucastMessagesSent)), ((stats.ucastMessagesSent) == 1));
        Assert.assertEquals(stats.ucastMessageBytesSent, 150);
        msg = Mockito.mock(Message.class);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(Header.createXmitReqHeader());
        Mockito.when(msg.size()).thenReturn(150L);
        evt = new Event(Event.MSG, msg);
        recorder.down(evt);
        Assert.assertTrue(("stats.ucastRetransmits =" + (stats.ucastRetransmits)), ((stats.ucastRetransmits) == 1));
    }

    @Test
    public void recorderHandlesRejectedExecution() throws Exception {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(Header.createDataHeader(1L, ((short) (1)), true));
        Mockito.when(msg.size()).thenReturn(150L);
        // GEODE-1178, the TP protocol may throw a RejectedExecutionException & StatRecorder should
        // retry
        Mockito.when(mockDownProtocol.down(ArgumentMatchers.any(Event.class))).thenThrow(new RejectedExecutionException());
        // after the first down() throws an exception we want StatRecorder to retry, so
        // we set the Manager to say no shutdown is in progress the first time and then say
        // one IS in progress so we can break out of the StatRecorder exception handling loop
        Mockito.when(services.getCancelCriterion()).thenReturn(new Services().getCancelCriterion());
        Manager manager = Mockito.mock(Manager.class);
        Mockito.when(services.getManager()).thenReturn(manager);
        Mockito.when(manager.shutdownInProgress()).thenReturn(Boolean.FALSE, Boolean.TRUE);
        Mockito.verify(mockDownProtocol, Mockito.never()).down(ArgumentMatchers.isA(Event.class));
        Event evt = new Event(Event.MSG, msg);
        recorder.down(evt);
        Mockito.verify(mockDownProtocol, Mockito.times(2)).down(ArgumentMatchers.isA(Event.class));
    }

    /**
     * ensure that multicast events are recorded in DMStats
     */
    @Test
    public void testMulticastStats() throws Exception {
        Message msg = Mockito.mock(Message.class);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(NakAckHeader2.createMessageHeader(1L));
        Mockito.when(msg.size()).thenReturn(150L);
        Event evt = new Event(Event.MSG, msg);
        recorder.up(evt);
        Assert.assertTrue(("mcastMessagesReceived = " + (stats.mcastMessagesReceived)), ((stats.mcastMessagesReceived) == 1));
        Assert.assertEquals(stats.mcastMessageBytesReceived, 150);
        recorder.down(evt);
        Assert.assertTrue(("mcastMessagesSent = " + (stats.mcastMessagesSent)), ((stats.mcastMessagesSent) == 1));
        Assert.assertEquals(stats.mcastMessageBytesSent, 150);
        msg = Mockito.mock(Message.class);
        Mockito.when(msg.size()).thenReturn(150L);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(NakAckHeader2.createXmitRequestHeader(null));
        evt = new Event(Event.MSG, msg);
        recorder.down(evt);
        Assert.assertTrue(("mcastRetransmitRequests = " + (stats.mcastRetransmitRequests)), ((stats.mcastRetransmitRequests) == 1));
        msg = Mockito.mock(Message.class);
        Mockito.when(msg.size()).thenReturn(150L);
        Mockito.when(msg.getHeader(ArgumentMatchers.any(Short.class))).thenReturn(NakAckHeader2.createXmitResponseHeader());
        evt = new Event(Event.MSG, msg);
        recorder.down(evt);
        Assert.assertTrue(("mcastRetransmits = " + (stats.mcastRetransmits)), ((stats.mcastRetransmits) == 1));
    }

    /**
     * Ensure that the messenger JGroups configuration XML strings contain the statistics recorder
     * protocol
     */
    @Test
    public void messengerStackHoldsStatRecorder() throws Exception {
        Services mockServices = Mockito.mock(Services.class);
        ServiceConfig mockConfig = Mockito.mock(ServiceConfig.class);
        Mockito.when(mockServices.getConfig()).thenReturn(mockConfig);
        // first test to see if the non-multicast stack has the recorder installed
        Properties nonDefault = new Properties();
        nonDefault.put(ConfigurationProperties.MCAST_PORT, "0");
        nonDefault.put(ConfigurationProperties.LOCATORS, "localhost[12345]");
        DistributionConfigImpl config = new DistributionConfigImpl(nonDefault);
        Mockito.when(mockConfig.getDistributionConfig()).thenReturn(config);
        RemoteTransportConfig transport = new RemoteTransportConfig(config, ClusterDistributionManager.NORMAL_DM_TYPE);
        Mockito.when(mockConfig.getTransport()).thenReturn(transport);
        JGroupsMessenger messenger = new JGroupsMessenger();
        messenger.init(mockServices);
        String jgroupsConfig = messenger.jgStackConfig;
        System.out.println(jgroupsConfig);
        Assert.assertTrue(jgroupsConfig.contains("gms.messenger.StatRecorder"));
        // now test to see if the multicast stack has the recorder installed
        nonDefault.put(ConfigurationProperties.MCAST_PORT, "12345");
        config = new DistributionConfigImpl(nonDefault);
        transport = new RemoteTransportConfig(config, ClusterDistributionManager.NORMAL_DM_TYPE);
        Mockito.when(mockConfig.getDistributionConfig()).thenReturn(config);
        Mockito.when(mockConfig.getTransport()).thenReturn(transport);
        messenger = new JGroupsMessenger();
        messenger.init(mockServices);
        Assert.assertTrue(jgroupsConfig.contains("gms.messenger.StatRecorder"));
    }

    private static class MyStats extends DummyDMStats {
        public int ucastMessagesReceived;

        public int ucastMessageBytesReceived;

        public int ucastMessagesSent;

        public int ucastMessageBytesSent;

        public int ucastRetransmits;

        public int mcastMessagesReceived;

        public int mcastMessageBytesReceived;

        public int mcastMessagesSent;

        public int mcastMessageBytesSent;

        public int mcastRetransmits;

        public int mcastRetransmitRequests;

        @Override
        public void incUcastReadBytes(int i) {
            (ucastMessagesReceived)++;
            ucastMessageBytesReceived += i;
        }

        @Override
        public void incUcastWriteBytes(int i) {
            (ucastMessagesSent)++;
            ucastMessageBytesSent += i;
        }

        @Override
        public void incUcastRetransmits() {
            (ucastRetransmits)++;
        }

        @Override
        public void incMcastReadBytes(int i) {
            (mcastMessagesReceived)++;
            mcastMessageBytesReceived += i;
        }

        @Override
        public void incMcastWriteBytes(int i) {
            (mcastMessagesSent)++;
            mcastMessageBytesSent += i;
        }

        @Override
        public void incMcastRetransmits() {
            (mcastRetransmits)++;
        }

        @Override
        public void incMcastRetransmitRequests() {
            (mcastRetransmitRequests)++;
        }
    }
}

