/**
 * Copyright (C) 2015 Square, Inc.
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
package com.squareup.leakcanary;


import ExcludedRefs.BuilderWithParams;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.core.StringContains;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


// 
@RunWith(Parameterized.class)
public class AsyncTaskLeakTest {
    static final String ASYNC_TASK_THREAD = "AsyncTask #1";

    static final String ASYNC_TASK_CLASS = "android.os.AsyncTask";

    static final String EXECUTOR_FIELD_1 = "SERIAL_EXECUTOR";

    static final String EXECUTOR_FIELD_2 = "sDefaultExecutor";

    private final TestUtil.HeapDumpFile heapDumpFile;

    BuilderWithParams excludedRefs;

    public AsyncTaskLeakTest(TestUtil.HeapDumpFile heapDumpFile) {
        this.heapDumpFile = heapDumpFile;
    }

    @Test
    public void leakFound() {
        AnalysisResult result = TestUtil.analyze(heapDumpFile, excludedRefs);
        Assert.assertTrue(result.leakFound);
        Assert.assertFalse(result.excludedLeak);
        LeakTraceElement gcRoot = result.leakTrace.elements.get(0);
        Assert.assertEquals(Thread.class.getName(), gcRoot.className);
        Assert.assertEquals(Holder.THREAD, gcRoot.holder);
        Assert.assertThat(gcRoot.extra, StringContains.containsString(AsyncTaskLeakTest.ASYNC_TASK_THREAD));
    }

    @Test
    public void excludeThread() {
        excludedRefs.thread(AsyncTaskLeakTest.ASYNC_TASK_THREAD);
        AnalysisResult result = TestUtil.analyze(heapDumpFile, excludedRefs);
        Assert.assertTrue(result.leakFound);
        Assert.assertFalse(result.excludedLeak);
        LeakTraceElement gcRoot = result.leakTrace.elements.get(0);
        Assert.assertEquals(AsyncTaskLeakTest.ASYNC_TASK_CLASS, gcRoot.className);
        Assert.assertEquals(Type.STATIC_FIELD, gcRoot.type);
        Assert.assertTrue(((gcRoot.referenceName.equals(AsyncTaskLeakTest.EXECUTOR_FIELD_1)) || (gcRoot.referenceName.equals(AsyncTaskLeakTest.EXECUTOR_FIELD_2))));
    }

    @Test
    public void excludeStatic() {
        excludedRefs.thread(AsyncTaskLeakTest.ASYNC_TASK_THREAD).named(AsyncTaskLeakTest.ASYNC_TASK_THREAD);
        excludedRefs.staticField(AsyncTaskLeakTest.ASYNC_TASK_CLASS, AsyncTaskLeakTest.EXECUTOR_FIELD_1).named(AsyncTaskLeakTest.EXECUTOR_FIELD_1);
        excludedRefs.staticField(AsyncTaskLeakTest.ASYNC_TASK_CLASS, AsyncTaskLeakTest.EXECUTOR_FIELD_2).named(AsyncTaskLeakTest.EXECUTOR_FIELD_2);
        AnalysisResult result = TestUtil.analyze(heapDumpFile, excludedRefs);
        Assert.assertTrue(result.leakFound);
        Assert.assertTrue(result.excludedLeak);
        LeakTrace leakTrace = result.leakTrace;
        List<LeakTraceElement> elements = leakTrace.elements;
        Exclusion exclusion = elements.get(0).exclusion;
        List<String> expectedExclusions = Arrays.asList(AsyncTaskLeakTest.ASYNC_TASK_THREAD, AsyncTaskLeakTest.EXECUTOR_FIELD_1, AsyncTaskLeakTest.EXECUTOR_FIELD_2);
        Assert.assertTrue(expectedExclusions.contains(exclusion.name));
    }
}

