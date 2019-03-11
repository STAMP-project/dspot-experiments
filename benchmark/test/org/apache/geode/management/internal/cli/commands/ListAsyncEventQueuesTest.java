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
package org.apache.geode.management.internal.cli.commands;


import CliStrings.LIST_ASYNC_EVENT_QUEUES__NO_QUEUES_FOUND_MESSAGE;
import CliStrings.NO_MEMBERS_FOUND_MESSAGE;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import org.apache.geode.distributed.DistributedMember;
import org.apache.geode.management.internal.cli.domain.AsyncEventQueueDetails;
import org.apache.geode.management.internal.cli.functions.CliFunctionResult;
import org.apache.geode.test.junit.categories.AEQTest;
import org.apache.geode.test.junit.rules.GfshParserRule;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@Category({ AEQTest.class })
public class ListAsyncEventQueuesTest {
    private static final String COMMAND = "list async-event-queues ";

    @ClassRule
    public static GfshParserRule gfsh = new GfshParserRule();

    private static final String FUNCTION_EXCEPTION_MESSAGE = "A mysterious test exception occurred during function execution.";

    private ListAsyncEventQueuesCommand command;

    private List<CliFunctionResult> memberCliResults;

    private DistributedMember mockedMember;

    private DistributedMember anotherMockedMember;

    @Test
    public void noMembersFound() {
        // Mock zero members
        Mockito.doReturn(Collections.emptySet()).when(command).getAllNormalMembers();
        // Command should succeed with one row of data
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsError().containsOutput(NO_MEMBERS_FOUND_MESSAGE);
    }

    @Test
    public void oneServerWithOneQueue() {
        // Mock one member
        Mockito.doReturn(new java.util.HashSet(Collections.singletonList(mockedMember))).when(command).getAllNormalMembers();
        // Mock member's queue details
        ListAsyncEventQueuesTest.FakeDetails details = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id", 5, true, "diskStoreName", 10, "my.listener.class", new Properties());
        CliFunctionResult memberResult = new CliFunctionResult(details.getMemberName(), Collections.singletonList(details.asAsyncEventQueueDetails()));
        memberCliResults = Collections.singletonList(memberResult);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed with one row of data
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().tableHasRowWithValues(details.expectedRowHeaderAndValue());
    }

    @Test
    public void oneServerWithOneParameterizedQueue() {
        // Mock one member
        Mockito.doReturn(new java.util.HashSet(Collections.singletonList(mockedMember))).when(command).getAllNormalMembers();
        // Mock member's queue details
        Properties listenerProperties = new Properties();
        listenerProperties.setProperty("special-property", "special-value");
        listenerProperties.setProperty("another-property", "mundane-value");
        ListAsyncEventQueuesTest.FakeDetails details = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id", 5, true, "diskStoreName", 10, "my.listener.class", listenerProperties);
        CliFunctionResult memberResult = new CliFunctionResult(details.getMemberName(), Collections.singletonList(details.asAsyncEventQueueDetails()));
        memberCliResults = Collections.singletonList(memberResult);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed with one row of data
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().tableHasRowWithValues(details.expectedRowHeaderAndValue());
    }

    @Test
    public void oneMemberButNoQueues() {
        // Mock one member
        Mockito.doReturn(new java.util.HashSet(Collections.singletonList(mockedMember))).when(command).getAllNormalMembers();
        // Mock member's lack of queue details
        CliFunctionResult memberResult = new CliFunctionResult("server1", Collections.emptyList());
        memberCliResults = Collections.singletonList(memberResult);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed, but indicate there were no queues
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().containsOutput(LIST_ASYNC_EVENT_QUEUES__NO_QUEUES_FOUND_MESSAGE);
    }

    @Test
    public void oneMemberWhoErrorsOut() {
        // Mock one member
        Mockito.doReturn(new java.util.HashSet(Collections.singletonList(mockedMember))).when(command).getAllNormalMembers();
        // Mock member's error result
        CliFunctionResult memberResult = new CliFunctionResult("server1", new Exception(ListAsyncEventQueuesTest.FUNCTION_EXCEPTION_MESSAGE));
        memberCliResults = Collections.singletonList(memberResult);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed, but indicate there were no queues
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().containsOutput(LIST_ASYNC_EVENT_QUEUES__NO_QUEUES_FOUND_MESSAGE);
    }

    @Test
    public void twoServersWithManyQueues() {
        // Mock two members, even though they're not directly consumed
        Mockito.doReturn(new java.util.HashSet(Arrays.asList(mockedMember, anotherMockedMember))).when(command).getAllNormalMembers();
        // Mock member's queue details
        ListAsyncEventQueuesTest.FakeDetails details1 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id1", 5, false, "diskStoreName", 1, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details2 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id2", 15, true, "otherDiskStoreName", 10, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details3 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id3", 25, true, "diskStoreName", 100, "my.listener.class", new Properties());
        CliFunctionResult member1Result = new CliFunctionResult("server1", Arrays.asList(details1.asAsyncEventQueueDetails(), details2.asAsyncEventQueueDetails(), details3.asAsyncEventQueueDetails()));
        ListAsyncEventQueuesTest.FakeDetails details4 = new ListAsyncEventQueuesTest.FakeDetails("server2", "s2-queue-id1", 5, false, "diskStoreName", 1, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details5 = new ListAsyncEventQueuesTest.FakeDetails("server2", "s2-queue-id2", 15, true, "otherDiskStoreName", 10, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details6 = new ListAsyncEventQueuesTest.FakeDetails("server2", "s2-queue-id3", 25, true, "diskStoreName", 100, "my.listener.class", new Properties());
        CliFunctionResult member2Result = new CliFunctionResult("server2", Arrays.asList(details4.asAsyncEventQueueDetails(), details5.asAsyncEventQueueDetails(), details6.asAsyncEventQueueDetails()));
        memberCliResults = Arrays.asList(member1Result, member2Result);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed with one row of data
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().tableHasRowWithValues(details1.expectedRowHeaderAndValue()).tableHasRowWithValues(details2.expectedRowHeaderAndValue()).tableHasRowWithValues(details3.expectedRowHeaderAndValue()).tableHasRowWithValues(details4.expectedRowHeaderAndValue()).tableHasRowWithValues(details5.expectedRowHeaderAndValue()).tableHasRowWithValues(details6.expectedRowHeaderAndValue());
    }

    @Test
    public void oneMemberSucceedsAndOneFails() {
        // Mock two members, even though they're not directly consumed
        Mockito.doReturn(new java.util.HashSet(Arrays.asList(mockedMember, anotherMockedMember))).when(command).getAllNormalMembers();
        // Mock member's queue details
        ListAsyncEventQueuesTest.FakeDetails details1 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id1", 5, false, "diskStoreName", 1, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details2 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id2", 15, true, "otherDiskStoreName", 10, "my.listener.class", new Properties());
        ListAsyncEventQueuesTest.FakeDetails details3 = new ListAsyncEventQueuesTest.FakeDetails("server1", "s1-queue-id3", 25, true, "diskStoreName", 100, "my.listener.class", new Properties());
        CliFunctionResult member1Result = new CliFunctionResult("server1", Arrays.asList(details1.asAsyncEventQueueDetails(), details2.asAsyncEventQueueDetails(), details3.asAsyncEventQueueDetails()));
        // Mock the other's failure
        CliFunctionResult member2Result = new CliFunctionResult("server2", new Exception(ListAsyncEventQueuesTest.FUNCTION_EXCEPTION_MESSAGE));
        memberCliResults = Arrays.asList(member1Result, member2Result);
        Mockito.doReturn(memberCliResults).when(command).executeAndGetFunctionResult(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Command should succeed with one row of data
        ListAsyncEventQueuesTest.gfsh.executeAndAssertThat(command, ListAsyncEventQueuesTest.COMMAND).statusIsSuccess().tableHasRowWithValues(details1.expectedRowHeaderAndValue()).tableHasRowWithValues(details2.expectedRowHeaderAndValue()).tableHasRowWithValues(details3.expectedRowHeaderAndValue()).containsOutput(ListAsyncEventQueuesTest.FUNCTION_EXCEPTION_MESSAGE);
    }

    /**
     * Wrapper for mocked AsyncEventQueueData, with convenience method for expected table output.
     */
    class FakeDetails {
        private String memberName;

        private String queueId;

        private int batchSize;

        private boolean persistent;

        private String diskStoreName;

        private int maxQueueMemory;

        private String listener;

        private Properties listenerProperties;

        private FakeDetails(String memberName, String queueId, int batchSize, boolean persistent, String diskStoreName, int maxQueueMemory, String listener, Properties listenerProperties) {
            this.memberName = memberName;
            this.queueId = queueId;
            this.batchSize = batchSize;
            this.persistent = persistent;
            this.diskStoreName = diskStoreName;
            this.maxQueueMemory = maxQueueMemory;
            this.listener = listener;
            this.listenerProperties = listenerProperties;
        }

        public String getMemberName() {
            return memberName;
        }

        private AsyncEventQueueDetails asAsyncEventQueueDetails() {
            return new AsyncEventQueueDetails(queueId, batchSize, persistent, diskStoreName, maxQueueMemory, listener, listenerProperties);
        }

        private String[] expectedRowHeaderAndValue() {
            return new String[]{ "Member", "ID", "Batch Size", "Persistent", "Disk Store", "Max Memory", "Listener", memberName, queueId, String.valueOf(batchSize), String.valueOf(persistent), diskStoreName, String.valueOf(maxQueueMemory), expectedListenerOutput() };
        }

        private String expectedListenerOutput() {
            return (listener) + (ListAsyncEventQueuesCommand.propertiesToString(listenerProperties));
        }
    }
}

