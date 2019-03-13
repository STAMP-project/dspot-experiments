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
package org.apache.flink.runtime.client;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.testutils.CommonTestUtils;
import org.apache.flink.runtime.operators.testutils.ExpectedTestException;
import org.apache.flink.util.ExceptionUtils;
import org.apache.flink.util.FlinkRuntimeException;
import org.apache.flink.util.OptionalFailure;
import org.apache.flink.util.SerializedValue;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the SerializedJobExecutionResult
 */
public class SerializedJobExecutionResultTest extends TestLogger {
    @Test
    public void testSerialization() throws Exception {
        final ClassLoader classloader = getClass().getClassLoader();
        JobID origJobId = new JobID();
        long origTime = 65927436589267L;
        Map<String, SerializedValue<OptionalFailure<Object>>> origMap = new HashMap<>();
        origMap.put("name1", new SerializedValue(OptionalFailure.of(723L)));
        origMap.put("name2", new SerializedValue(OptionalFailure.of("peter")));
        origMap.put("name3", new SerializedValue(OptionalFailure.ofFailure(new ExpectedTestException())));
        SerializedJobExecutionResult result = new SerializedJobExecutionResult(origJobId, origTime, origMap);
        // serialize and deserialize the object
        SerializedJobExecutionResult cloned = CommonTestUtils.createCopySerializable(result);
        Assert.assertEquals(origJobId, cloned.getJobId());
        Assert.assertEquals(origTime, cloned.getNetRuntime());
        Assert.assertEquals(origTime, cloned.getNetRuntime(TimeUnit.MILLISECONDS));
        Assert.assertEquals(origMap, cloned.getSerializedAccumulatorResults());
        // convert to deserialized result
        JobExecutionResult jResult = result.toJobExecutionResult(classloader);
        JobExecutionResult jResultCopied = result.toJobExecutionResult(classloader);
        Assert.assertEquals(origJobId, jResult.getJobID());
        Assert.assertEquals(origJobId, jResultCopied.getJobID());
        Assert.assertEquals(origTime, jResult.getNetRuntime());
        Assert.assertEquals(origTime, jResult.getNetRuntime(TimeUnit.MILLISECONDS));
        Assert.assertEquals(origTime, jResultCopied.getNetRuntime());
        Assert.assertEquals(origTime, jResultCopied.getNetRuntime(TimeUnit.MILLISECONDS));
        for (Map.Entry<String, SerializedValue<OptionalFailure<Object>>> entry : origMap.entrySet()) {
            String name = entry.getKey();
            OptionalFailure<Object> value = entry.getValue().deserializeValue(classloader);
            if (value.isFailure()) {
                try {
                    jResult.getAccumulatorResult(name);
                    Assert.fail("expected failure");
                } catch (FlinkRuntimeException ex) {
                    Assert.assertTrue(ExceptionUtils.findThrowable(ex, ExpectedTestException.class).isPresent());
                }
                try {
                    jResultCopied.getAccumulatorResult(name);
                    Assert.fail("expected failure");
                } catch (FlinkRuntimeException ex) {
                    Assert.assertTrue(ExceptionUtils.findThrowable(ex, ExpectedTestException.class).isPresent());
                }
            } else {
                Assert.assertEquals(value.get(), jResult.getAccumulatorResult(name));
                Assert.assertEquals(value.get(), jResultCopied.getAccumulatorResult(name));
            }
        }
    }

    @Test
    public void testSerializationWithNullValues() throws Exception {
        SerializedJobExecutionResult result = new SerializedJobExecutionResult(null, 0L, null);
        SerializedJobExecutionResult cloned = CommonTestUtils.createCopySerializable(result);
        Assert.assertNull(cloned.getJobId());
        Assert.assertEquals(0L, cloned.getNetRuntime());
        Assert.assertNull(cloned.getSerializedAccumulatorResults());
        JobExecutionResult jResult = result.toJobExecutionResult(getClass().getClassLoader());
        Assert.assertNull(jResult.getJobID());
        Assert.assertTrue(jResult.getAllAccumulatorResults().isEmpty());
    }
}

