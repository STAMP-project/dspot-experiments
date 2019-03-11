/**
 * Copyright 2019 LinkedIn Corp. Licensed under the BSD 2-Clause License (the "License"). See License in the project root for license information.
 */
package com.linkedin.kafka.cruisecontrol.servlet;


import EndPoint.LOAD;
import EndPoint.PROPOSALS;
import EndPoint.REBALANCE;
import EndPoint.REMOVE_BROKER;
import EndPoint.USER_TASKS;
import UserTaskManager.TaskState.COMPLETED;
import UserTaskManager.UUIDGenerator;
import UserTaskManager.UserTaskInfo;
import com.linkedin.kafka.cruisecontrol.async.OperationFuture;
import com.linkedin.kafka.cruisecontrol.servlet.parameters.CruiseControlParameters;
import com.linkedin.kafka.cruisecontrol.servlet.parameters.ParameterUtils;
import com.linkedin.kafka.cruisecontrol.servlet.parameters.UserTasksParameters;
import com.linkedin.kafka.cruisecontrol.servlet.response.CruiseControlResponse;
import com.linkedin.kafka.cruisecontrol.servlet.response.UserTaskState;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import kafka.utils.MockTime;
import org.apache.kafka.common.utils.Time;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;


public class KafkaCruiseControlServletEndpointTest {
    private static final String GET_METHOD = "GET";

    private static final String POST_METHOD = "POST";

    private static final Function<String, OperationFuture> FUTURE_CREATOR = ( uuid) -> new OperationFuture("future");

    // A hack to make 2 requests to same endpoint 'look' different to UserTaskManager
    private static final Map<String, String[]> DIFF_PARAM = new HashMap<>();

    private static final Map<String, String[]> EMPTY_PARAM = Collections.emptyMap();

    private static final UUID repeatUUID = UUID.randomUUID();

    private static Collection<Object[]> _initializeServletRequestsOutput = new ArrayList<>();

    private static Collection<Object[]> _populateUserTaskManagerOutput = new ArrayList<>();

    private static UUIDGenerator _mockUUIDGenerator;

    private static HttpSession _mockHttpSession;

    private static HttpServletResponse _mockHttpServletResponse;

    private static UserTaskManager _userTaskManager;

    private static final String[] PARAMS_TO_GET = new String[]{ ParameterUtils.CLIENT_IDS_PARAM, ParameterUtils.ENDPOINTS_PARAM, ParameterUtils.TYPES_PARAM, ParameterUtils.USER_TASK_IDS_PARAM, ParameterUtils.ENTRIES_PARAM };

    static {
        KafkaCruiseControlServletEndpointTest.DIFF_PARAM.put("param", new String[]{ "true" });
        Time mockTime = new MockTime();
        KafkaCruiseControlServletEndpointTest._mockUUIDGenerator = EasyMock.mock(UUIDGenerator.class);
        KafkaCruiseControlServletEndpointTest._mockHttpSession = EasyMock.mock(HttpSession.class);
        KafkaCruiseControlServletEndpointTest._mockHttpServletResponse = EasyMock.mock(HttpServletResponse.class);
        EasyMock.expect(KafkaCruiseControlServletEndpointTest._mockHttpSession.getLastAccessedTime()).andReturn(mockTime.milliseconds()).anyTimes();
        KafkaCruiseControlServletEndpointTest._mockHttpSession.invalidate();
        KafkaCruiseControlServletEndpointTest._mockHttpServletResponse.setHeader(EasyMock.anyString(), EasyMock.anyString());
        EasyMock.expectLastCall().anyTimes();
        KafkaCruiseControlServletEndpointTest._userTaskManager = new UserTaskManager(1000, 10, TimeUnit.HOURS.toMillis(6), 100, mockTime, KafkaCruiseControlServletEndpointTest._mockUUIDGenerator);
    }

    private static class MockResult implements CruiseControlResponse {
        public void discardIrrelevantResponse(CruiseControlParameters parameters) {
        }

        public void writeSuccessResponse(CruiseControlParameters parameters, HttpServletResponse response) {
        }
    }

    @Test
    public void testUserTaskParameters() throws UnsupportedEncodingException {
        // Set up all mocked requests,  UserTaskManager, and start mocked objects.
        initializeServletRequests(KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockUUIDGenerator);
        EasyMock.replay(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
        populateUserTaskManager(KafkaCruiseControlServletEndpointTest._mockHttpServletResponse, KafkaCruiseControlServletEndpointTest._userTaskManager);
        UserTaskState userTaskState = new UserTaskState(KafkaCruiseControlServletEndpointTest._userTaskManager.getActiveUserTasks(), KafkaCruiseControlServletEndpointTest._userTaskManager.getCompletedUserTasks());
        // Test Case 1: Get all PROPOSAL or REBALANCE tasks
        Map<String, String[]> answerQueryParam1 = new HashMap<>();
        answerQueryParam1.put("param", new String[]{ "true" });
        answerQueryParam1.put("endpoints", new String[]{ ((PROPOSALS.toString()) + ",") + (REBALANCE.toString()) });
        HttpServletRequest answerQueryRequest1 = prepareRequest(KafkaCruiseControlServletEndpointTest._mockHttpSession, null, "", USER_TASKS.toString(), answerQueryParam1, KafkaCruiseControlServletEndpointTest.GET_METHOD);
        UserTasksParameters parameters1 = mockUserTasksParameters(answerQueryRequest1);
        List<UserTaskManager.UserTaskInfo> result1 = userTaskState.prepareResultList(parameters1);
        // Test Case 1 result
        Assert.assertEquals(3, result1.size());
        EasyMock.reset(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
        // Test Case 2: Get all tasks from client 0.0.0.1
        Map<String, String[]> answerQueryParam2 = new HashMap<>();
        answerQueryParam2.put("param", new String[]{ "true" });
        answerQueryParam2.put("client_ids", new String[]{ "0.0.0.1" });
        HttpServletRequest answerQueryRequest2 = prepareRequest(KafkaCruiseControlServletEndpointTest._mockHttpSession, null, "", USER_TASKS.toString(), answerQueryParam2, KafkaCruiseControlServletEndpointTest.GET_METHOD);
        UserTasksParameters parameters2 = mockUserTasksParameters(answerQueryRequest2);
        List<UserTaskManager.UserTaskInfo> result2 = userTaskState.prepareResultList(parameters2);
        // Test Case 2 result
        Assert.assertEquals(3, result2.size());
        EasyMock.reset(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
        // Test Case 3: Get all PROPOSALS and REMOVE_BROKERS from client 0.0.0.1
        Map<String, String[]> answerQueryParam3 = new HashMap<>();
        answerQueryParam3.put("param", new String[]{ "true" });
        answerQueryParam3.put("client_ids", new String[]{ "0.0.0.1" });
        answerQueryParam3.put("endpoints", new String[]{ ((PROPOSALS.toString()) + ",") + (REMOVE_BROKER.toString()) });
        HttpServletRequest answerQueryRequest3 = prepareRequest(KafkaCruiseControlServletEndpointTest._mockHttpSession, null, "", USER_TASKS.toString(), answerQueryParam3, KafkaCruiseControlServletEndpointTest.GET_METHOD);
        UserTasksParameters parameters3 = mockUserTasksParameters(answerQueryRequest3);
        List<UserTaskManager.UserTaskInfo> result3 = userTaskState.prepareResultList(parameters3);
        // Test Case 3 result
        Assert.assertEquals(2, result3.size());
        EasyMock.reset(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
        // Test Case 4: Get all tasks limit to 4 entries
        Map<String, String[]> answerQueryParam4 = new HashMap<>();
        answerQueryParam4.put("param", new String[]{ "true" });
        answerQueryParam4.put("entries", new String[]{ "4" });
        HttpServletRequest answerQueryRequest4 = prepareRequest(KafkaCruiseControlServletEndpointTest._mockHttpSession, null, "", USER_TASKS.toString(), answerQueryParam4, KafkaCruiseControlServletEndpointTest.GET_METHOD);
        UserTasksParameters parameters4 = mockUserTasksParameters(answerQueryRequest4);
        List<UserTaskManager.UserTaskInfo> result4 = userTaskState.prepareResultList(parameters4);
        // Test Case 4 result
        Assert.assertEquals(4, result4.size());
        EasyMock.reset(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
        // Transition UserTaskManager state: some tasks will move from ACTIVE to COMPLETED
        // Resolve futures. Allow those tasks to be moved into completed state
        KafkaCruiseControlServletEndpointTest.getFuture(0).complete(new KafkaCruiseControlServletEndpointTest.MockResult());// Complete 1st requst

        KafkaCruiseControlServletEndpointTest.getFuture(4).complete(new KafkaCruiseControlServletEndpointTest.MockResult());// Complete 5th request

        KafkaCruiseControlServletEndpointTest.getFuture(5).complete(new KafkaCruiseControlServletEndpointTest.MockResult());// Complete 6th request

        // Update task manager active vs completed state
        KafkaCruiseControlServletEndpointTest._userTaskManager.checkActiveUserTasks();
        // Now the UserTaskManager state has changed, so we reload the states
        UserTaskState userTaskState2 = new UserTaskState(KafkaCruiseControlServletEndpointTest._userTaskManager.getActiveUserTasks(), KafkaCruiseControlServletEndpointTest._userTaskManager.getCompletedUserTasks());
        // Test Case 5: Get all LOAD or REMOVE_BROKER tasks that's completed and with user task id repeatUUID
        Map<String, String[]> answerQueryParam5 = new HashMap<>();
        answerQueryParam5.put("param", new String[]{ "true" });
        answerQueryParam5.put("endpoints", new String[]{ ((LOAD.toString()) + ",") + (REMOVE_BROKER.toString()) });
        answerQueryParam5.put("user_task_ids", new String[]{ KafkaCruiseControlServletEndpointTest.repeatUUID.toString() });
        answerQueryParam5.put("types", new String[]{ COMPLETED.toString() });
        HttpServletRequest answerQueryRequest5 = prepareRequest(KafkaCruiseControlServletEndpointTest._mockHttpSession, null, "", USER_TASKS.toString(), answerQueryParam5, KafkaCruiseControlServletEndpointTest.GET_METHOD);
        UserTasksParameters parameters5 = mockUserTasksParameters(answerQueryRequest5);
        List<UserTaskManager.UserTaskInfo> result5 = userTaskState2.prepareResultList(parameters5);
        // Test Case 5 result
        Assert.assertEquals(1, result5.size());
        EasyMock.reset(KafkaCruiseControlServletEndpointTest._mockUUIDGenerator, KafkaCruiseControlServletEndpointTest._mockHttpSession, KafkaCruiseControlServletEndpointTest._mockHttpServletResponse);
    }
}

