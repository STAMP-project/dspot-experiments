package com.netflix.conductor.service;


import com.netflix.conductor.core.execution.WorkflowExecutor;
import com.netflix.conductor.utility.TestUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolationException;
import org.junit.Assert;
import org.junit.Test;


public class WorkflowBulkServiceTest {
    private WorkflowExecutor workflowExecutor;

    private WorkflowBulkService workflowBulkService;

    @Test(expected = ConstraintViolationException.class)
    public void testPauseWorkflowNull() {
        try {
            workflowBulkService.pauseWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowIds list cannot be null."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testPauseWorkflowWithInvalidListSize() {
        try {
            List<String> list = new ArrayList<>(1001);
            for (int i = 0; i < 1002; i++) {
                list.add("test");
            }
            workflowBulkService.pauseWorkflow(list);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("Cannot process more than 1000 workflows. Please use multiple requests."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testResumeWorkflowNull() {
        try {
            workflowBulkService.resumeWorkflow(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowIds list cannot be null."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRestartWorkflowNull() {
        try {
            workflowBulkService.restart(null, false);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowIds list cannot be null."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testRetryWorkflowNull() {
        try {
            workflowBulkService.retry(null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowIds list cannot be null."));
            throw ex;
        }
    }

    @Test(expected = ConstraintViolationException.class)
    public void testTerminateNull() {
        try {
            workflowBulkService.terminate(null, null);
        } catch (ConstraintViolationException ex) {
            Assert.assertEquals(1, ex.getConstraintViolations().size());
            Set<String> messages = TestUtils.getConstraintViolationMessages(ex.getConstraintViolations());
            Assert.assertTrue(messages.contains("WorkflowIds list cannot be null."));
            throw ex;
        }
    }
}

