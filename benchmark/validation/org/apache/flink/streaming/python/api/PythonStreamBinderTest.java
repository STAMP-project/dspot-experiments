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
package org.apache.flink.streaming.python.api;


import java.util.List;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.client.JobExecutionException;
import org.apache.flink.test.util.AbstractTestBase;
import org.apache.flink.util.Preconditions;
import org.junit.Test;
import org.python.core.PyException;


/**
 * Tests for the {@link PythonStreamBinder}.
 */
public class PythonStreamBinderTest extends AbstractTestBase {
    @Test
    public void testProgram() throws Exception {
        Path testEntryPoint = new Path(PythonStreamBinderTest.getBaseTestPythonDir(), "run_all_tests.py");
        List<String> testFiles = PythonStreamBinderTest.findTestFiles();
        Preconditions.checkState(((testFiles.size()) > 0), "No test files were found in {}.", PythonStreamBinderTest.getBaseTestPythonDir());
        String[] arguments = new String[(1 + 1) + (testFiles.size())];
        arguments[0] = testEntryPoint.getPath();
        arguments[1] = PythonStreamBinderTest.findUtilsModule().getPath();
        int index = 2;
        for (String testFile : testFiles) {
            arguments[index] = testFile;
            index++;
        }
        try {
            runPlan(arguments);
        } catch (PyException e) {
            if ((e.getCause()) instanceof JobExecutionException) {
                // JobExecutionExceptions are wrapped again by the jython interpreter resulting in horrible stacktraces
                throw ((JobExecutionException) (e.getCause()));
            } else {
                // probably caused by some issue in the main script itself
                throw e;
            }
        }
    }
}

