/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.standard;


import CoreAttributes.FILENAME;
import DebugFlow.FF_EXCEPTION_CLASS;
import DebugFlow.FF_EXCEPTION_ITERATIONS;
import DebugFlow.FF_FAILURE_ITERATIONS;
import DebugFlow.FF_ROLLBACK_ITERATIONS;
import DebugFlow.FF_ROLLBACK_PENALTY_ITERATIONS;
import DebugFlow.FF_ROLLBACK_YIELD_ITERATIONS;
import DebugFlow.FF_SUCCESS_ITERATIONS;
import DebugFlow.NO_FF_EXCEPTION_CLASS;
import DebugFlow.NO_FF_EXCEPTION_ITERATIONS;
import DebugFlow.NO_FF_SKIP_ITERATIONS;
import DebugFlow.NO_FF_YIELD_ITERATIONS;
import DebugFlow.REL_FAILURE;
import DebugFlow.REL_SUCCESS;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.processor.ProcessSession;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestDebugFlow {
    private DebugFlow debugFlow;

    private TestRunner runner;

    private ProcessSession session;

    private final Map<Integer, String> contents = new HashMap<>();

    private final Map<Integer, Map<String, String>> attribs = new HashMap<>();

    private Map<String, String> namesToContent = new HashMap<>();

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Test
    public void testFlowFileSuccess() {
        runner.setProperty(FF_SUCCESS_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 6);
        runner.assertTransferCount(REL_FAILURE, 0);
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(2).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(3).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(4).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(5).toByteArray()));
    }

    @Test
    public void testFlowFileFailure() {
        runner.setProperty(FF_FAILURE_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 6);
        runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals(contents.get(0));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(1).assertContentEquals(contents.get(1));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(2).assertContentEquals(contents.get(2));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(3).assertContentEquals(contents.get(3));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(4).assertContentEquals(contents.get(4));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(5).assertContentEquals(contents.get(5));
    }

    @Test
    public void testFlowFileSuccessAndFailure() {
        runner.setProperty(FF_SUCCESS_ITERATIONS, "1");
        runner.setProperty(FF_FAILURE_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 3);
        runner.assertTransferCount(REL_FAILURE, 3);
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).assertContentEquals(contents.get(0));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(0).assertContentEquals(contents.get(1));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(1).assertContentEquals(contents.get(2));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(1).assertContentEquals(contents.get(3));
        runner.getFlowFilesForRelationship(REL_SUCCESS).get(2).assertContentEquals(contents.get(4));
        runner.getFlowFilesForRelationship(REL_FAILURE).get(2).assertContentEquals(contents.get(5));
    }

    @Test
    public void testFlowFileRollback() throws IOException {
        runner.setProperty(FF_ROLLBACK_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Assert.assertEquals(6, runner.getQueueSize().getObjectCount());
        MockFlowFile ff1 = ((MockFlowFile) (session.get()));
        Assert.assertNotNull(ff1);
        Assert.assertEquals(namesToContent.get(ff1.getAttribute(FILENAME.key())), new String(ff1.toByteArray()));
        session.rollback();
    }

    @Test
    public void testFlowFileRollbackYield() {
        runner.setProperty(FF_ROLLBACK_YIELD_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Assert.assertEquals(6, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void testFlowFileRollbackPenalty() {
        runner.setProperty(FF_ROLLBACK_PENALTY_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(7);
        runner.assertTransferCount(REL_SUCCESS, 0);
        runner.assertTransferCount(REL_FAILURE, 0);
        runner.assertQueueNotEmpty();
        Assert.assertEquals(6, runner.getQueueSize().getObjectCount());
    }

    @Test
    public void testFlowFileDefaultException() {
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "1");
        runner.assertValid();
        runner.enqueue(contents.get(0).getBytes(), attribs.get(0));
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(RuntimeException.class));
        runner.run(2);
    }

    @Test
    public void testFlowFileNonDefaultException() {
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(FF_EXCEPTION_CLASS, "java.lang.RuntimeException");
        runner.assertValid();
        runner.enqueue(contents.get(0).getBytes(), attribs.get(0));
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(RuntimeException.class));
        runner.run(2);
    }

    @Test
    public void testFlowFileNPEException() {
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(FF_EXCEPTION_CLASS, "java.lang.NullPointerException");
        runner.assertValid();
        runner.enqueue(contents.get(0).getBytes(), attribs.get(0));
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(NullPointerException.class));
        runner.run(2);
    }

    @Test
    public void testFlowFileBadException() {
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(FF_EXCEPTION_CLASS, "java.lang.NonExistantException");
        runner.assertNotValid();
    }

    @Test
    public void testFlowFileExceptionRollover() {
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "2");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(RuntimeException.class));
        runner.run(8);
    }

    @Test
    public void testFlowFileAll() {
        runner.setProperty(FF_SUCCESS_ITERATIONS, "1");
        runner.setProperty(FF_FAILURE_ITERATIONS, "1");
        runner.setProperty(FF_ROLLBACK_ITERATIONS, "1");
        runner.setProperty(FF_ROLLBACK_YIELD_ITERATIONS, "1");
        runner.setProperty(FF_ROLLBACK_PENALTY_ITERATIONS, "1");
        runner.setProperty(FF_EXCEPTION_ITERATIONS, "1");
        runner.assertValid();
        for (int n = 0; n < 6; n++) {
            runner.enqueue(contents.get(n).getBytes(), attribs.get(n));
        }
        runner.run(5);
        runner.assertTransferCount(REL_SUCCESS, 1);
        runner.assertTransferCount(REL_FAILURE, 1);
        Assert.assertEquals(4, runner.getQueueSize().getObjectCount());
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_SUCCESS).get(0).toByteArray()));
        Assert.assertTrue(isInContents(runner.getFlowFilesForRelationship(REL_FAILURE).get(0).toByteArray()));
        runner.run(2);
    }

    @Test
    public void testNoFlowFileZeroIterations() {
        runner.run(4);
    }

    @Test
    public void testNoFlowFileSkip() {
        runner.setProperty(NO_FF_SKIP_ITERATIONS, "1");
        runner.assertValid();
        runner.run(4);
    }

    @Test
    public void testNoFlowFileDefaultException() {
        runner.setProperty(NO_FF_EXCEPTION_ITERATIONS, "1");
        runner.assertValid();
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(RuntimeException.class));
        runner.run(3);
    }

    @Test
    public void testNoFlowFileNonDefaultException() {
        runner.setProperty(NO_FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(NO_FF_EXCEPTION_CLASS, "java.lang.RuntimeException");
        runner.assertValid();
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(RuntimeException.class));
        runner.run(3);
    }

    @Test
    public void testNoFlowFileOtherException() {
        runner.setProperty(NO_FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(NO_FF_EXCEPTION_CLASS, "java.lang.NullPointerException");
        runner.assertValid();
        exception.expectMessage(CoreMatchers.containsString("forced by org.apache.nifi.processors.standard.DebugFlow"));
        exception.expectCause(CoreMatchers.isA(NullPointerException.class));
        runner.run(3);
    }

    @Test
    public void testNoFlowFileBadException() {
        runner.setProperty(NO_FF_EXCEPTION_ITERATIONS, "1");
        runner.setProperty(NO_FF_EXCEPTION_CLASS, "java.lang.NonExistantException");
        runner.assertNotValid();
    }

    @Test
    public void testNoFlowFileYield() {
        runner.setProperty(NO_FF_YIELD_ITERATIONS, "1");
        runner.assertValid();
        runner.run(4);
    }
}

