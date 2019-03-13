/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.broker;


import org.apache.activemq.command.BrokerInfo;
import org.junit.Assert;
import org.junit.Test;


public class TransportConnectorTest {
    TransportConnector underTest;

    @Test
    public void addPeerBrokerWithFilter() throws Exception {
        underTest.setUpdateClusterFilter("e.*,w.*");
        final String validName = "west";
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setBrokerURL(validName);
        brokerInfo.setBrokerName(validName);
        Assert.assertFalse(underTest.getPeerBrokers().contains(validName));
        underTest.addPeerBroker(brokerInfo);
        Assert.assertTrue(underTest.getPeerBrokers().contains(validName));
        final String validName2 = "east";
        brokerInfo = new BrokerInfo();
        brokerInfo.setBrokerURL(validName2);
        brokerInfo.setBrokerName(validName2);
        Assert.assertFalse(underTest.getPeerBrokers().contains(validName2));
        underTest.addPeerBroker(brokerInfo);
        Assert.assertTrue(underTest.getPeerBrokers().contains(validName2));
        final String inValidName = "boo";
        brokerInfo = new BrokerInfo();
        brokerInfo.setBrokerURL(inValidName);
        brokerInfo.setBrokerName(inValidName);
        Assert.assertFalse(underTest.getPeerBrokers().contains(inValidName));
        underTest.addPeerBroker(brokerInfo);
        Assert.assertFalse(underTest.getPeerBrokers().contains(inValidName));
    }

    @Test
    public void addPeerBrokerWithoutFilter() throws Exception {
        underTest.setBrokerService(new BrokerService());
        final String validName = "west";
        BrokerInfo brokerInfo = new BrokerInfo();
        brokerInfo.setBrokerURL(validName);
        brokerInfo.setBrokerName(validName);
        Assert.assertFalse(underTest.getPeerBrokers().contains(validName));
        underTest.addPeerBroker(brokerInfo);
        Assert.assertTrue(underTest.getPeerBrokers().contains(validName));
        final String validName2 = "east";
        brokerInfo = new BrokerInfo();
        brokerInfo.setBrokerURL(validName2);
        brokerInfo.setBrokerName(validName2);
        Assert.assertFalse(underTest.getPeerBrokers().contains(validName2));
        underTest.addPeerBroker(brokerInfo);
        Assert.assertTrue(underTest.getPeerBrokers().contains(validName2));
    }
}

