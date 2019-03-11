package com.netflix.conductor.core.utils;


import ExternalPayloadStorage.Operation.WRITE;
import ExternalPayloadStorage.PayloadType.TASK_INPUT;
import ExternalPayloadStorage.PayloadType.TASK_OUTPUT;
import ExternalPayloadStorage.PayloadType.WORKFLOW_OUTPUT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.run.ExternalStorageLocation;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.common.utils.ExternalPayloadStorage;
import com.netflix.conductor.core.execution.TerminateWorkflowException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class ExternalPayloadStorageUtilsTest {
    private ExternalPayloadStorage externalPayloadStorage;

    private ObjectMapper objectMapper;

    private ExternalStorageLocation location;

    // Subject
    private ExternalPayloadStorageUtils externalPayloadStorageUtils;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testDownloadPayload() throws IOException {
        String path = "test/payload";
        Map<String, Object> payload = new HashMap<>();
        payload.put("key1", "value1");
        payload.put("key2", 200);
        byte[] payloadBytes = objectMapper.writeValueAsString(payload).getBytes();
        Mockito.when(externalPayloadStorage.download(path)).thenReturn(new ByteArrayInputStream(payloadBytes));
        Map<String, Object> result = externalPayloadStorageUtils.downloadPayload(path);
        Assert.assertNotNull(result);
        Assert.assertEquals(payload, result);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUploadTaskPayload() throws IOException {
        AtomicInteger uploadCount = new AtomicInteger(0);
        InputStream stream = ExternalPayloadStorageUtilsTest.class.getResourceAsStream("/payload.json");
        Map<String, Object> payload = objectMapper.readValue(stream, Map.class);
        Mockito.when(externalPayloadStorage.getLocation(WRITE, TASK_INPUT, "")).thenReturn(location);
        Mockito.doAnswer(( invocation) -> {
            uploadCount.incrementAndGet();
            return null;
        }).when(externalPayloadStorage).upload(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Task task = new Task();
        task.setInputData(payload);
        externalPayloadStorageUtils.verifyAndUpload(task, TASK_INPUT);
        Assert.assertNull(task.getInputData());
        Assert.assertEquals(1, uploadCount.get());
        Assert.assertNotNull(task.getExternalInputPayloadStoragePath());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testUploadWorkflowPayload() throws IOException {
        AtomicInteger uploadCount = new AtomicInteger(0);
        InputStream stream = ExternalPayloadStorageUtilsTest.class.getResourceAsStream("/payload.json");
        Map<String, Object> payload = objectMapper.readValue(stream, Map.class);
        Mockito.when(externalPayloadStorage.getLocation(WRITE, WORKFLOW_OUTPUT, "")).thenReturn(location);
        Mockito.doAnswer(( invocation) -> {
            uploadCount.incrementAndGet();
            return null;
        }).when(externalPayloadStorage).upload(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Workflow workflow = new Workflow();
        workflow.setOutput(payload);
        externalPayloadStorageUtils.verifyAndUpload(workflow, WORKFLOW_OUTPUT);
        Assert.assertNull(workflow.getOutput());
        Assert.assertEquals(1, uploadCount.get());
        Assert.assertNotNull(workflow.getExternalOutputPayloadStoragePath());
    }

    @Test
    public void testUploadHelper() {
        AtomicInteger uploadCount = new AtomicInteger(0);
        String path = "some/test/path.json";
        ExternalStorageLocation location = new ExternalStorageLocation();
        location.setPath(path);
        Mockito.when(externalPayloadStorage.getLocation(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(location);
        Mockito.doAnswer(( invocation) -> {
            uploadCount.incrementAndGet();
            return null;
        }).when(externalPayloadStorage).upload(ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.anyLong());
        Assert.assertEquals(path, externalPayloadStorageUtils.uploadHelper(new byte[]{  }, 10L, TASK_OUTPUT));
        Assert.assertEquals(1, uploadCount.get());
    }

    @Test
    public void testFailTaskWithInputPayload() {
        Task task = new Task();
        task.setInputData(new HashMap());
        expectedException.expect(TerminateWorkflowException.class);
        externalPayloadStorageUtils.failTask(task, TASK_INPUT, "error");
        Assert.assertNotNull(task);
        Assert.assertNull(task.getInputData());
    }

    @Test
    public void testFailTaskWithOutputPayload() {
        Task task = new Task();
        task.setOutputData(new HashMap());
        expectedException.expect(TerminateWorkflowException.class);
        externalPayloadStorageUtils.failTask(task, TASK_OUTPUT, "error");
        Assert.assertNotNull(task);
        Assert.assertNull(task.getOutputData());
    }
}

