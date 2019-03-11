/**
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.core.jsr;


import javax.batch.api.listener.StepListener;
import javax.batch.operations.BatchRuntimeException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;


public class StepListenerAdapterTests {
    private StepListenerAdapter adapter;

    @Mock
    private StepListener delegate;

    @Mock
    private StepExecution execution;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        adapter = new StepListenerAdapter(null);
    }

    @Test
    public void testBeforeStep() throws Exception {
        adapter.beforeStep(null);
        Mockito.verify(delegate).beforeStep();
    }

    @Test(expected = BatchRuntimeException.class)
    public void testBeforeStepException() throws Exception {
        Mockito.doThrow(new Exception("expected")).when(delegate).beforeStep();
        adapter.beforeStep(null);
    }

    @Test
    public void testAfterStep() throws Exception {
        ExitStatus exitStatus = new ExitStatus("complete");
        Mockito.when(execution.getExitStatus()).thenReturn(exitStatus);
        Assert.assertEquals(exitStatus, adapter.afterStep(execution));
        Mockito.verify(delegate).afterStep();
    }

    @Test(expected = BatchRuntimeException.class)
    public void testAfterStepException() throws Exception {
        Mockito.doThrow(new Exception("expected")).when(delegate).afterStep();
        adapter.afterStep(null);
    }
}

