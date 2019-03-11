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
package org.apache.flink.dist;


import TaskManagerOptions.MANAGED_MEMORY_FRACTION;
import TaskManagerOptions.MANAGED_MEMORY_SIZE;
import java.util.Random;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Unit test that verifies that the task manager heap size calculation used by the bash script
 * <tt>taskmanager.sh</tt> returns the same values as the heap size calculation of
 * {@link TaskManagerServices#calculateHeapSizeMB(long, Configuration)}.
 *
 * <p>NOTE: the shell script uses <tt>awk</tt> to perform floating-point arithmetic which uses
 * <tt>double</tt> precision but our Java code restrains to <tt>float</tt> because we actually do
 * not need high precision.
 */
public class TaskManagerHeapSizeCalculationJavaBashTest extends TestLogger {
    /**
     * Key that is used by <tt>config.sh</tt>.
     */
    private static final String KEY_TASKM_MEM_SIZE = "taskmanager.heap.size";

    /**
     * Number of tests with random values.
     *
     * <p>NOTE: calling the external test script is slow and thus low numbers are preferred for general
     * testing.
     */
    private static final int NUM_RANDOM_TESTS = 20;

    /**
     * Tests that {@link TaskManagerServices#calculateNetworkBufferMemory(long, Configuration)} has the same
     * result as the shell script.
     */
    @Test
    public void compareNetworkBufShellScriptWithJava() throws Exception {
        int managedMemSize = Integer.valueOf(MANAGED_MEMORY_SIZE.defaultValue());
        float managedMemFrac = MANAGED_MEMORY_FRACTION.defaultValue();
        // manual tests from org.apache.flink.runtime.taskexecutor.TaskManagerServices.calculateHeapSizeMB()
        compareNetworkBufJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, false, 0.1F, (64L << 20), (1L << 30), managedMemSize, managedMemFrac), 0.0F);
        compareNetworkBufJavaVsScript(/* MB */
        TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, true, 0.1F, (64L << 20), (1L << 30), 10, managedMemFrac), 0.0F);
        compareNetworkBufJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, true, 0.1F, (64L << 20), (1L << 30), managedMemSize, 0.1F), 0.0F);
        // some automated tests with random (but valid) values
        Random ran = new Random();
        for (int i = 0; i < (TaskManagerHeapSizeCalculationJavaBashTest.NUM_RANDOM_TESTS); ++i) {
            // tolerate that values differ by 1% (due to different floating point precisions)
            compareNetworkBufJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getRandomConfig(ran), 0.01F);
        }
    }

    /**
     * Tests that {@link TaskManagerServices#calculateHeapSizeMB(long, Configuration)} has the same
     * result as the shell script.
     */
    @Test
    public void compareHeapSizeShellScriptWithJava() throws Exception {
        int managedMemSize = Integer.valueOf(MANAGED_MEMORY_SIZE.defaultValue());
        float managedMemFrac = MANAGED_MEMORY_FRACTION.defaultValue();
        // manual tests from org.apache.flink.runtime.taskexecutor.TaskManagerServices.calculateHeapSizeMB()
        compareHeapSizeJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, false, 0.1F, (64L << 20), (1L << 30), managedMemSize, managedMemFrac), 0.0F);
        compareHeapSizeJavaVsScript(/* MB */
        TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, true, 0.1F, (64L << 20), (1L << 30), 10, managedMemFrac), 0.0F);
        compareHeapSizeJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getConfig(1000, true, 0.1F, (64L << 20), (1L << 30), managedMemSize, 0.1F), 0.0F);
        // some automated tests with random (but valid) values
        Random ran = new Random();
        for (int i = 0; i < (TaskManagerHeapSizeCalculationJavaBashTest.NUM_RANDOM_TESTS); ++i) {
            // tolerate that values differ by 1% (due to different floating point precisions)
            compareHeapSizeJavaVsScript(TaskManagerHeapSizeCalculationJavaBashTest.getRandomConfig(ran), 0.01F);
        }
    }
}

