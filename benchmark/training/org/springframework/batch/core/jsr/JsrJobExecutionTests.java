/**
 * Copyright 2014 the original author or authors.
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


import JsrJobParametersConverter.JOB_RUN_ID;
import java.util.Date;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.converter.JobParametersConverterSupport;
import org.springframework.batch.core.javax.batch.runtime.BatchStatus;


public class JsrJobExecutionTests {
    private JsrJobExecution adapter;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        adapter = new JsrJobExecution(null, new JobParametersConverterSupport());
    }

    @Test
    public void testGetBasicValues() {
        Assert.assertEquals(javax.batch.runtime.BatchStatus, adapter.getBatchStatus());
        Assert.assertEquals(new Date(0), adapter.getCreateTime());
        Assert.assertEquals(new Date(999999999L), adapter.getEndTime());
        Assert.assertEquals(5L, adapter.getExecutionId());
        Assert.assertEquals("exit status", adapter.getExitStatus());
        Assert.assertEquals("job name", adapter.getJobName());
        Assert.assertEquals(new Date(12345), adapter.getLastUpdatedTime());
        Assert.assertEquals(new Date(98765), adapter.getStartTime());
        Properties props = adapter.getJobParameters();
        Assert.assertEquals("value1", props.get("key1"));
        Assert.assertNull(props.get(JOB_RUN_ID));
    }
}

