package com.netflix.conductor.core.execution.tasks;


import Task.Status.COMPLETED;
import Task.Status.FAILED;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.core.execution.WorkflowExecutor;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author x-ultra
 */
public class TestLambda {
    private Workflow workflow = new Workflow();

    private WorkflowExecutor executor = Mockito.mock(WorkflowExecutor.class);

    @Test
    public void start() throws Exception {
        Lambda lambda = new Lambda();
        Map inputObj = new HashMap();
        inputObj.put("a", 1);
        /**
         * test for scriptExpression == null
         */
        Task task = new Task();
        task.getInputData().put("input", inputObj);
        lambda.execute(workflow, task, executor);
        Assert.assertEquals(FAILED, task.getStatus());
        /**
         * test for normal
         */
        task = new Task();
        task.getInputData().put("input", inputObj);
        task.getInputData().put("scriptExpression", "if ($.input.a==1){return 1}else{return 0 } ");
        lambda.execute(workflow, task, executor);
        Assert.assertEquals(COMPLETED, task.getStatus());
        Assert.assertEquals(task.getOutputData().toString(), "{result=1}");
        /**
         * test for scriptExpression ScriptException
         */
        task = new Task();
        task.getInputData().put("input", inputObj);
        task.getInputData().put("scriptExpression", "if ($.a.size==1){return 1}else{return 0 } ");
        lambda.execute(workflow, task, executor);
        Assert.assertEquals(FAILED, task.getStatus());
    }
}

