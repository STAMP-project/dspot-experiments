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
package org.apache.flink.runtime.jobgraph;


import DistributionPattern.ALL_TO_ALL;
import DistributionPattern.POINTWISE;
import ResultPartitionType.PIPELINED;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.flink.api.common.io.FinalizeOnMaster;
import org.apache.flink.api.common.io.GenericInputFormat;
import org.apache.flink.api.common.io.InitializeOnMaster;
import org.apache.flink.api.common.io.OutputFormat;
import org.apache.flink.api.java.io.DiscardingOutputFormat;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.io.GenericInputSplit;
import org.apache.flink.core.io.InputSplit;
import org.apache.flink.util.InstantiationUtil;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("serial")
public class JobTaskVertexTest {
    @Test
    public void testConnectDirectly() {
        JobVertex source = new JobVertex("source");
        JobVertex target = new JobVertex("target");
        target.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
        Assert.assertTrue(source.isInputVertex());
        Assert.assertFalse(source.isOutputVertex());
        Assert.assertFalse(target.isInputVertex());
        Assert.assertTrue(target.isOutputVertex());
        Assert.assertEquals(1, source.getNumberOfProducedIntermediateDataSets());
        Assert.assertEquals(1, target.getNumberOfInputs());
        Assert.assertEquals(target.getInputs().get(0).getSource(), source.getProducedDataSets().get(0));
        Assert.assertEquals(1, source.getProducedDataSets().get(0).getConsumers().size());
        Assert.assertEquals(target, source.getProducedDataSets().get(0).getConsumers().get(0).getTarget());
    }

    @Test
    public void testConnectMultipleTargets() {
        JobVertex source = new JobVertex("source");
        JobVertex target1 = new JobVertex("target1");
        JobVertex target2 = new JobVertex("target2");
        target1.connectNewDataSetAsInput(source, POINTWISE, PIPELINED);
        target2.connectDataSetAsInput(source.getProducedDataSets().get(0), ALL_TO_ALL);
        Assert.assertTrue(source.isInputVertex());
        Assert.assertFalse(source.isOutputVertex());
        Assert.assertFalse(target1.isInputVertex());
        Assert.assertTrue(target1.isOutputVertex());
        Assert.assertFalse(target2.isInputVertex());
        Assert.assertTrue(target2.isOutputVertex());
        Assert.assertEquals(1, source.getNumberOfProducedIntermediateDataSets());
        Assert.assertEquals(2, source.getProducedDataSets().get(0).getConsumers().size());
        Assert.assertEquals(target1.getInputs().get(0).getSource(), source.getProducedDataSets().get(0));
        Assert.assertEquals(target2.getInputs().get(0).getSource(), source.getProducedDataSets().get(0));
    }

    @Test
    public void testOutputFormatVertex() {
        try {
            final OutputFormat outputFormat = new JobTaskVertexTest.TestingOutputFormat();
            final OutputFormatVertex of = new OutputFormatVertex("Name");
            setStubWrapper(new org.apache.flink.api.common.operators.util.UserCodeObjectWrapper<OutputFormat<?>>(outputFormat));
            final ClassLoader cl = new JobTaskVertexTest.TestClassLoader();
            try {
                of.initializeOnMaster(cl);
                Assert.fail("Did not throw expected exception.");
            } catch (JobTaskVertexTest.TestException e) {
                // all good
            }
            OutputFormatVertex copy = InstantiationUtil.clone(of);
            ClassLoader ctxCl = Thread.currentThread().getContextClassLoader();
            try {
                copy.initializeOnMaster(cl);
                Assert.fail("Did not throw expected exception.");
            } catch (JobTaskVertexTest.TestException e) {
                // all good
            }
            Assert.assertEquals("Previous classloader was not restored.", ctxCl, Thread.currentThread().getContextClassLoader());
            try {
                copy.finalizeOnMaster(cl);
                Assert.fail("Did not throw expected exception.");
            } catch (JobTaskVertexTest.TestException e) {
                // all good
            }
            Assert.assertEquals("Previous classloader was not restored.", ctxCl, Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testInputFormatVertex() {
        try {
            final JobTaskVertexTest.TestInputFormat inputFormat = new JobTaskVertexTest.TestInputFormat();
            final InputFormatVertex vertex = new InputFormatVertex("Name");
            new org.apache.flink.runtime.operators.util.TaskConfig(vertex.getConfiguration()).setStubWrapper(new org.apache.flink.api.common.operators.util.UserCodeObjectWrapper<org.apache.flink.api.common.io.InputFormat<?, ?>>(inputFormat));
            final ClassLoader cl = getClass().getClassLoader();
            vertex.initializeOnMaster(cl);
            InputSplit[] splits = vertex.getInputSplitSource().createInputSplits(77);
            Assert.assertNotNull(splits);
            Assert.assertEquals(1, splits.length);
            Assert.assertEquals(JobTaskVertexTest.TestSplit.class, splits[0].getClass());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------
    private static final class TestException extends IOException {}

    private static final class TestSplit extends GenericInputSplit {
        public TestSplit(int partitionNumber, int totalNumberOfPartitions) {
            super(partitionNumber, totalNumberOfPartitions);
        }
    }

    private static final class TestInputFormat extends GenericInputFormat<Object> {
        @Override
        public boolean reachedEnd() {
            return false;
        }

        @Override
        public Object nextRecord(Object reuse) {
            return null;
        }

        @Override
        public GenericInputSplit[] createInputSplits(int numSplits) throws IOException {
            return new GenericInputSplit[]{ new JobTaskVertexTest.TestSplit(0, 1) };
        }
    }

    private static final class TestingOutputFormat extends DiscardingOutputFormat<Object> implements FinalizeOnMaster , InitializeOnMaster {
        private boolean isConfigured = false;

        @Override
        public void initializeGlobal(int parallelism) throws IOException {
            if (!(isConfigured)) {
                throw new IllegalStateException("OutputFormat was not configured before initializeGlobal was called.");
            }
            if (!((Thread.currentThread().getContextClassLoader()) instanceof JobTaskVertexTest.TestClassLoader)) {
                throw new IllegalStateException("Context ClassLoader was not correctly switched.");
            }
            // notify we have been here.
            throw new JobTaskVertexTest.TestException();
        }

        @Override
        public void finalizeGlobal(int parallelism) throws IOException {
            if (!(isConfigured)) {
                throw new IllegalStateException("OutputFormat was not configured before finalizeGlobal was called.");
            }
            if (!((Thread.currentThread().getContextClassLoader()) instanceof JobTaskVertexTest.TestClassLoader)) {
                throw new IllegalStateException("Context ClassLoader was not correctly switched.");
            }
            // notify we have been here.
            throw new JobTaskVertexTest.TestException();
        }

        @Override
        public void configure(Configuration parameters) {
            if (isConfigured) {
                throw new IllegalStateException("OutputFormat is already configured.");
            }
            if (!((Thread.currentThread().getContextClassLoader()) instanceof JobTaskVertexTest.TestClassLoader)) {
                throw new IllegalStateException("Context ClassLoader was not correctly switched.");
            }
            isConfigured = true;
        }
    }

    private static class TestClassLoader extends URLClassLoader {
        public TestClassLoader() {
            super(new URL[0], Thread.currentThread().getContextClassLoader());
        }
    }
}

