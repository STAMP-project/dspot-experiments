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
package org.apache.hadoop.yarn.security;


import CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTH_TO_LOCAL;
import CommonNodeLabelsManager.NO_LABEL;
import ContainerTokenIdentifierProto.Builder;
import ContainerType.APPLICATION_MASTER;
import ContainerType.TASK;
import ExecutionType.GUARANTEED;
import ExecutionType.OPPORTUNISTIC;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.security.HadoopKerberosName;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ExecutionType;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.nodelabels.CommonNodeLabelsManager;
import org.apache.hadoop.yarn.proto.YarnSecurityTokenProtos.ContainerTokenIdentifierProto;
import org.apache.hadoop.yarn.security.client.ClientToAMTokenIdentifier;
import org.apache.hadoop.yarn.security.client.TimelineDelegationTokenIdentifier;
import org.apache.hadoop.yarn.server.api.ContainerType;
import org.junit.Assert;
import org.junit.Test;


public class TestYARNTokenIdentifier {
    @Test
    public void testNMTokenIdentifier() throws IOException {
        testNMTokenIdentifier(false);
    }

    @Test
    public void testNMTokenIdentifierOldFormat() throws IOException {
        testNMTokenIdentifier(true);
    }

    @Test
    public void testAMRMTokenIdentifier() throws IOException {
        testAMRMTokenIdentifier(false);
    }

    @Test
    public void testAMRMTokenIdentifierOldFormat() throws IOException {
        testAMRMTokenIdentifier(true);
    }

    @Test
    public void testClientToAMTokenIdentifier() throws IOException {
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(ApplicationId.newInstance(1, 1), 1);
        String clientName = "user";
        ClientToAMTokenIdentifier token = new ClientToAMTokenIdentifier(appAttemptId, clientName);
        ClientToAMTokenIdentifier anotherToken = new ClientToAMTokenIdentifier();
        byte[] tokenContent = token.getBytes();
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(tokenContent, tokenContent.length);
        anotherToken.readFields(dib);
        // verify the whole record equals with original record
        Assert.assertEquals(("Token is not the same after serialization " + "and deserialization."), token, anotherToken);
        Assert.assertEquals("ApplicationAttemptId from proto is not the same with original token", anotherToken.getApplicationAttemptID(), appAttemptId);
        Assert.assertEquals("clientName from proto is not the same with original token", anotherToken.getClientName(), clientName);
    }

    @Test
    public void testContainerTokenIdentifierProtoMissingFields() throws IOException {
        ContainerTokenIdentifierProto.Builder builder = ContainerTokenIdentifierProto.newBuilder();
        ContainerTokenIdentifierProto proto = builder.build();
        Assert.assertFalse(proto.hasContainerType());
        Assert.assertFalse(proto.hasExecutionType());
        Assert.assertFalse(proto.hasNodeLabelExpression());
        byte[] tokenData = proto.toByteArray();
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(tokenData, tokenData.length);
        ContainerTokenIdentifier tid = new ContainerTokenIdentifier();
        tid.readFields(dib);
        Assert.assertEquals("container type", TASK, tid.getContainerType());
        Assert.assertEquals("execution type", GUARANTEED, tid.getExecutionType());
        Assert.assertEquals("node label expression", NO_LABEL, tid.getNodeLabelExpression());
    }

    @Test
    public void testContainerTokenIdentifier() throws IOException {
        testContainerTokenIdentifier(false, false);
    }

    @Test
    public void testContainerTokenIdentifierOldFormat() throws IOException {
        testContainerTokenIdentifier(true, true);
        testContainerTokenIdentifier(true, false);
    }

    @Test
    public void testRMDelegationTokenIdentifier() throws IOException {
        testRMDelegationTokenIdentifier(false);
    }

    @Test
    public void testRMDelegationTokenIdentifierOldFormat() throws IOException {
        testRMDelegationTokenIdentifier(true);
    }

    @Test
    public void testTimelineDelegationTokenIdentifier() throws IOException {
        Text owner = new Text("user1");
        Text renewer = new Text("user2");
        Text realUser = new Text("user3");
        long issueDate = 1;
        long maxDate = 2;
        int sequenceNumber = 3;
        int masterKeyId = 4;
        TimelineDelegationTokenIdentifier token = new TimelineDelegationTokenIdentifier(owner, renewer, realUser);
        token.setIssueDate(issueDate);
        token.setMaxDate(maxDate);
        token.setSequenceNumber(sequenceNumber);
        token.setMasterKeyId(masterKeyId);
        TimelineDelegationTokenIdentifier anotherToken = new TimelineDelegationTokenIdentifier();
        byte[] tokenContent = token.getBytes();
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(tokenContent, tokenContent.length);
        anotherToken.readFields(dib);
        // verify the whole record equals with original record
        Assert.assertEquals(("Token is not the same after serialization " + "and deserialization."), token, anotherToken);
        Assert.assertEquals("owner from proto is not the same with original token", anotherToken.getOwner(), owner);
        Assert.assertEquals("renewer from proto is not the same with original token", anotherToken.getRenewer(), renewer);
        Assert.assertEquals("realUser from proto is not the same with original token", anotherToken.getRealUser(), realUser);
        Assert.assertEquals("issueDate from proto is not the same with original token", anotherToken.getIssueDate(), issueDate);
        Assert.assertEquals("maxDate from proto is not the same with original token", anotherToken.getMaxDate(), maxDate);
        Assert.assertEquals("sequenceNumber from proto is not the same with original token", anotherToken.getSequenceNumber(), sequenceNumber);
        Assert.assertEquals("masterKeyId from proto is not the same with original token", anotherToken.getMasterKeyId(), masterKeyId);
    }

    @Test
    public void testParseTimelineDelegationTokenIdentifierRenewer() throws IOException {
        // Server side when generation a timeline DT
        Configuration conf = new YarnConfiguration();
        conf.set(HADOOP_SECURITY_AUTH_TO_LOCAL, "RULE:[2:$1@$0]([nr]m@.*EXAMPLE.COM)s/.*/yarn/");
        HadoopKerberosName.setConfiguration(conf);
        Text owner = new Text("owner");
        Text renewer = new Text("rm/localhost@EXAMPLE.COM");
        Text realUser = new Text("realUser");
        TimelineDelegationTokenIdentifier token = new TimelineDelegationTokenIdentifier(owner, renewer, realUser);
        Assert.assertEquals(new Text("yarn"), token.getRenewer());
    }

    @Test
    public void testAMContainerTokenIdentifier() throws IOException {
        ContainerId containerID = ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(1, 1), 1), 1);
        String hostName = "host0";
        String appSubmitter = "usr0";
        Resource r = Resource.newInstance(1024, 1);
        long expiryTimeStamp = 1000;
        int masterKeyId = 1;
        long rmIdentifier = 1;
        Priority priority = Priority.newInstance(1);
        long creationTime = 1000;
        ContainerTokenIdentifier token = new ContainerTokenIdentifier(containerID, hostName, appSubmitter, r, expiryTimeStamp, masterKeyId, rmIdentifier, priority, creationTime, null, CommonNodeLabelsManager.NO_LABEL, ContainerType.APPLICATION_MASTER);
        ContainerTokenIdentifier anotherToken = new ContainerTokenIdentifier();
        byte[] tokenContent = token.getBytes();
        DataInputBuffer dib = new DataInputBuffer();
        dib.reset(tokenContent, tokenContent.length);
        anotherToken.readFields(dib);
        Assert.assertEquals(APPLICATION_MASTER, anotherToken.getContainerType());
        Assert.assertEquals(GUARANTEED, anotherToken.getExecutionType());
        token = new ContainerTokenIdentifier(containerID, 0, hostName, appSubmitter, r, expiryTimeStamp, masterKeyId, rmIdentifier, priority, creationTime, null, CommonNodeLabelsManager.NO_LABEL, ContainerType.TASK, ExecutionType.OPPORTUNISTIC);
        anotherToken = new ContainerTokenIdentifier();
        tokenContent = token.getBytes();
        dib = new DataInputBuffer();
        dib.reset(tokenContent, tokenContent.length);
        anotherToken.readFields(dib);
        Assert.assertEquals(TASK, anotherToken.getContainerType());
        Assert.assertEquals(OPPORTUNISTIC, anotherToken.getExecutionType());
    }
}

