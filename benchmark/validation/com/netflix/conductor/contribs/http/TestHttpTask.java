/**
 * Copyright 2016 Netflix, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
/**
 *
 */
package com.netflix.conductor.contribs.http;


import HttpTask.MISSING_REQUEST;
import HttpTask.REQUEST_PARAMETER_NAME;
import Status.SCHEDULED;
import Task.Status.COMPLETED;
import Task.Status.FAILED;
import TaskType.USER_DEFINED;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.metadata.workflow.WorkflowTask;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.contribs.http.HttpTask.Input;
import com.netflix.conductor.core.config.Configuration;
import com.netflix.conductor.core.execution.ParametersUtils;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.core.execution.mapper.TaskMapper;
import com.netflix.conductor.core.utils.ExternalPayloadStorageUtils;
import com.netflix.conductor.dao.MetadataDAO;
import com.netflix.conductor.dao.QueueDAO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Viren
 */
@SuppressWarnings("unchecked")
public class TestHttpTask {
    private static final String ERROR_RESPONSE = "Something went wrong!";

    private static final String TEXT_RESPONSE = "Text Response";

    private static final double NUM_RESPONSE = 42.42;

    private static String JSON_RESPONSE;

    private HttpTask httpTask;

    private WorkflowExecutor workflowExecutor;

    private Configuration config;

    private Workflow workflow = new Workflow();

    private static Server server;

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPost() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/post");
        Map<String, Object> body = new HashMap<>();
        body.put("input_key1", "value1");
        body.put("input_key2", 45.3);
        input.setBody(body);
        input.setMethod("POST");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(task.getReasonForIncompletion(), COMPLETED, task.getStatus());
        Map<String, Object> hr = ((Map<String, Object>) (task.getOutputData().get("response")));
        Object response = hr.get("body");
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertTrue(("response is: " + response), (response instanceof Map));
        Map<String, Object> map = ((Map<String, Object>) (response));
        Set<String> inputKeys = body.keySet();
        Set<String> responseKeys = map.keySet();
        inputKeys.containsAll(responseKeys);
        responseKeys.containsAll(inputKeys);
    }

    @Test
    public void testPostNoContent() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/post2");
        Map<String, Object> body = new HashMap<>();
        body.put("input_key1", "value1");
        body.put("input_key2", 45.3);
        input.setBody(body);
        input.setMethod("POST");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(task.getReasonForIncompletion(), COMPLETED, task.getStatus());
        Map<String, Object> hr = ((Map<String, Object>) (task.getOutputData().get("response")));
        Object response = hr.get("body");
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertNull(("response is: " + response), response);
    }

    @Test
    public void testFailure() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/failure");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(("Task output: " + (task.getOutputData())), FAILED, task.getStatus());
        Assert.assertEquals(TestHttpTask.ERROR_RESPONSE, task.getReasonForIncompletion());
        task.setStatus(SCHEDULED);
        task.getInputData().remove(REQUEST_PARAMETER_NAME);
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(FAILED, task.getStatus());
        Assert.assertEquals(MISSING_REQUEST, task.getReasonForIncompletion());
    }

    @Test
    public void testTextGET() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/text");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Map<String, Object> hr = ((Map<String, Object>) (task.getOutputData().get("response")));
        Object response = hr.get("body");
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertEquals(TestHttpTask.TEXT_RESPONSE, response);
    }

    @Test
    public void testNumberGET() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/numeric");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Map<String, Object> hr = ((Map<String, Object>) (task.getOutputData().get("response")));
        Object response = hr.get("body");
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertEquals(TestHttpTask.NUM_RESPONSE, response);
        Assert.assertTrue((response instanceof Number));
    }

    @Test
    public void testJsonGET() throws JsonProcessingException {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/json");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Map<String, Object> hr = ((Map<String, Object>) (task.getOutputData().get("response")));
        Object response = hr.get("body");
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertTrue((response instanceof Map));
        Map<String, Object> map = ((Map<String, Object>) (response));
        Assert.assertEquals(TestHttpTask.JSON_RESPONSE, TestHttpTask.objectMapper.writeValueAsString(map));
    }

    @Test
    public void testExecute() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/json");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        task.setStatus(SCHEDULED);
        task.setScheduledTime(0);
        boolean executed = httpTask.execute(workflow, task, workflowExecutor);
        Assert.assertFalse(executed);
    }

    @Test
    public void testOptional() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/failure");
        input.setMethod("GET");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(("Task output: " + (task.getOutputData())), FAILED, task.getStatus());
        Assert.assertEquals(TestHttpTask.ERROR_RESPONSE, task.getReasonForIncompletion());
        Assert.assertTrue((!(task.getStatus().isSuccessful())));
        task.setStatus(SCHEDULED);
        task.getInputData().remove(REQUEST_PARAMETER_NAME);
        task.setReferenceTaskName("t1");
        httpTask.start(workflow, task, workflowExecutor);
        Assert.assertEquals(FAILED, task.getStatus());
        Assert.assertEquals(MISSING_REQUEST, task.getReasonForIncompletion());
        Assert.assertTrue((!(task.getStatus().isSuccessful())));
        WorkflowTask workflowTask = new WorkflowTask();
        workflowTask.setOptional(true);
        workflowTask.setName("HTTP");
        workflowTask.setWorkflowTaskType(USER_DEFINED);
        workflowTask.setTaskReferenceName("t1");
        WorkflowDef def = new WorkflowDef();
        def.getTasks().add(workflowTask);
        Workflow workflow = new Workflow();
        workflow.setWorkflowDefinition(def);
        workflow.getTasks().add(task);
        QueueDAO queueDAO = Mockito.mock(QueueDAO.class);
        MetadataDAO metadataDAO = Mockito.mock(MetadataDAO.class);
        ExternalPayloadStorageUtils externalPayloadStorageUtils = Mockito.mock(ExternalPayloadStorageUtils.class);
        ParametersUtils parametersUtils = Mockito.mock(ParametersUtils.class);
        Map<String, TaskMapper> taskMappers = new HashMap<>();
        new com.netflix.conductor.core.execution.DeciderService(parametersUtils, queueDAO, metadataDAO, externalPayloadStorageUtils, taskMappers).decide(workflow);
        System.out.println(workflow.getTasks());
        System.out.println(workflow.getStatus());
    }

    @Test
    public void testOAuth() {
        Task task = new Task();
        Input input = new Input();
        input.setUri("http://localhost:7009/oauth");
        input.setMethod("POST");
        input.setOauthConsumerKey("someKey");
        input.setOauthConsumerSecret("someSecret");
        task.getInputData().put(REQUEST_PARAMETER_NAME, input);
        httpTask.start(workflow, task, workflowExecutor);
        Map<String, Object> response = ((Map<String, Object>) (task.getOutputData().get("response")));
        Map<String, String> body = ((Map<String, String>) (response.get("body")));
        Assert.assertEquals("someKey", body.get("oauth_consumer_key"));
        Assert.assertTrue("Should have OAuth nonce", body.containsKey("oauth_nonce"));
        Assert.assertTrue("Should have OAuth signature", body.containsKey("oauth_signature"));
        Assert.assertTrue("Should have OAuth signature method", body.containsKey("oauth_signature_method"));
        Assert.assertTrue("Should have OAuth oauth_timestamp", body.containsKey("oauth_timestamp"));
        Assert.assertTrue("Should have OAuth oauth_version", body.containsKey("oauth_version"));
        Assert.assertEquals(("Task output: " + (task.getOutputData())), Status.COMPLETED, task.getStatus());
    }

    private static class EchoHandler extends AbstractHandler {
        private TypeReference<Map<String, Object>> mapOfObj = new TypeReference<Map<String, Object>>() {};

        @Override
        public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
            if ((request.getMethod().equals("GET")) && (request.getRequestURI().equals("/text"))) {
                PrintWriter writer = response.getWriter();
                writer.print(TestHttpTask.TEXT_RESPONSE);
                writer.flush();
                writer.close();
            } else
                if ((request.getMethod().equals("GET")) && (request.getRequestURI().equals("/json"))) {
                    response.addHeader("Content-Type", "application/json");
                    PrintWriter writer = response.getWriter();
                    writer.print(TestHttpTask.JSON_RESPONSE);
                    writer.flush();
                    writer.close();
                } else
                    if ((request.getMethod().equals("GET")) && (request.getRequestURI().equals("/failure"))) {
                        response.addHeader("Content-Type", "text/plain");
                        response.setStatus(500);
                        PrintWriter writer = response.getWriter();
                        writer.print(TestHttpTask.ERROR_RESPONSE);
                        writer.flush();
                        writer.close();
                    } else
                        if ((request.getMethod().equals("POST")) && (request.getRequestURI().equals("/post"))) {
                            response.addHeader("Content-Type", "application/json");
                            BufferedReader reader = request.getReader();
                            Map<String, Object> input = TestHttpTask.objectMapper.readValue(reader, mapOfObj);
                            Set<String> keys = input.keySet();
                            for (String key : keys) {
                                input.put(key, key);
                            }
                            PrintWriter writer = response.getWriter();
                            writer.print(TestHttpTask.objectMapper.writeValueAsString(input));
                            writer.flush();
                            writer.close();
                        } else
                            if ((request.getMethod().equals("POST")) && (request.getRequestURI().equals("/post2"))) {
                                response.addHeader("Content-Type", "application/json");
                                response.setStatus(204);
                                BufferedReader reader = request.getReader();
                                Map<String, Object> input = TestHttpTask.objectMapper.readValue(reader, mapOfObj);
                                Set<String> keys = input.keySet();
                                System.out.println(keys);
                                response.getWriter().close();
                            } else
                                if ((request.getMethod().equals("GET")) && (request.getRequestURI().equals("/numeric"))) {
                                    PrintWriter writer = response.getWriter();
                                    writer.print(TestHttpTask.NUM_RESPONSE);
                                    writer.flush();
                                    writer.close();
                                } else
                                    if ((request.getMethod().equals("POST")) && (request.getRequestURI().equals("/oauth"))) {
                                        // echo back oauth parameters generated in the Authorization header in the response
                                        Map<String, String> params = parseOauthParameters(request);
                                        response.addHeader("Content-Type", "application/json");
                                        PrintWriter writer = response.getWriter();
                                        writer.print(TestHttpTask.objectMapper.writeValueAsString(params));
                                        writer.flush();
                                        writer.close();
                                    }






        }

        private Map<String, String> parseOauthParameters(HttpServletRequest request) {
            String paramString = request.getHeader("Authorization").replaceAll("^OAuth (.*)", "$1");
            return Arrays.stream(paramString.split("\\s*,\\s*")).map(( pair) -> pair.split("=")).collect(Collectors.toMap(( o) -> o[0], ( o) -> o[1].replaceAll("\"", "")));
        }
    }
}

