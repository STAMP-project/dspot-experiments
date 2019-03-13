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
package org.apache.hadoop.mapreduce;


import MRConfig.SHUFFLE_CONSUMER_PLUGIN;
import ShuffleConsumerPlugin.Context;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalDirAllocator;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.mapred.Counters.Counter;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapOutputFile;
import org.apache.hadoop.mapred.RawKeyValueIterator;
import org.apache.hadoop.mapred.ReduceTask;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.ShuffleConsumerPlugin;
import org.apache.hadoop.mapred.Task;
import org.apache.hadoop.mapred.Task.CombineOutputCollector;
import org.apache.hadoop.mapred.TaskStatus;
import org.apache.hadoop.mapred.TaskUmbilicalProtocol;
import org.apache.hadoop.mapreduce.task.reduce.Shuffle;
import org.apache.hadoop.util.Progress;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static MRConfig.SHUFFLE_CONSUMER_PLUGIN;


/**
 * A JUnit for testing availability and accessibility of shuffle related API.
 * It is needed for maintaining comptability with external sub-classes of
 * ShuffleConsumerPlugin and AuxiliaryService(s) like ShuffleHandler.
 *
 * The importance of this test is for preserving API with 3rd party plugins.
 */
public class TestShufflePlugin<K, V> {
    static class TestShuffleConsumerPlugin<K, V> implements ShuffleConsumerPlugin<K, V> {
        @Override
        public void init(Context<K, V> context) {
            // just verify that Context has kept its public interface
            context.getReduceId();
            context.getJobConf();
            context.getLocalFS();
            context.getUmbilical();
            context.getLocalDirAllocator();
            context.getReporter();
            context.getCodec();
            context.getCombinerClass();
            context.getCombineCollector();
            context.getSpilledRecordsCounter();
            context.getReduceCombineInputCounter();
            context.getShuffledMapsCounter();
            context.getReduceShuffleBytes();
            context.getFailedShuffleCounter();
            context.getMergedMapOutputsCounter();
            context.getStatus();
            context.getCopyPhase();
            context.getMergePhase();
            context.getReduceTask();
            context.getMapOutputFile();
        }

        @Override
        public void close() {
        }

        @Override
        public RawKeyValueIterator run() throws IOException, InterruptedException {
            return null;
        }
    }

    /**
     * A testing method instructing core hadoop to load an external ShuffleConsumerPlugin
     * as if it came from a 3rd party.
     */
    @Test
    public void testPluginAbility() {
        try {
            // create JobConf with mapreduce.job.shuffle.consumer.plugin=TestShuffleConsumerPlugin
            JobConf jobConf = new JobConf();
            jobConf.setClass(SHUFFLE_CONSUMER_PLUGIN, TestShufflePlugin.TestShuffleConsumerPlugin.class, ShuffleConsumerPlugin.class);
            ShuffleConsumerPlugin shuffleConsumerPlugin = null;
            Class<? extends ShuffleConsumerPlugin> clazz = jobConf.getClass(SHUFFLE_CONSUMER_PLUGIN, Shuffle.class, ShuffleConsumerPlugin.class);
            Assert.assertNotNull(("Unable to get " + (SHUFFLE_CONSUMER_PLUGIN)), clazz);
            // load 3rd party plugin through core's factory method
            shuffleConsumerPlugin = ReflectionUtils.newInstance(clazz, jobConf);
            Assert.assertNotNull(("Unable to load " + (SHUFFLE_CONSUMER_PLUGIN)), shuffleConsumerPlugin);
        } catch (Exception e) {
            Assert.assertTrue(("Threw exception:" + e), false);
        }
    }

    /**
     * A testing method verifying availability and accessibility of API that is needed
     * for sub-classes of ShuffleConsumerPlugin
     */
    @Test
    public void testConsumerApi() {
        JobConf jobConf = new JobConf();
        ShuffleConsumerPlugin<K, V> shuffleConsumerPlugin = new TestShufflePlugin.TestShuffleConsumerPlugin<K, V>();
        // mock creation
        ReduceTask mockReduceTask = Mockito.mock(ReduceTask.class);
        TaskUmbilicalProtocol mockUmbilical = Mockito.mock(TaskUmbilicalProtocol.class);
        Reporter mockReporter = Mockito.mock(Reporter.class);
        FileSystem mockFileSystem = Mockito.mock(FileSystem.class);
        Class<? extends org.apache.hadoop.mapred.Reducer> combinerClass = jobConf.getCombinerClass();
        // needed for mock with generic
        @SuppressWarnings("unchecked")
        CombineOutputCollector<K, V> mockCombineOutputCollector = ((CombineOutputCollector<K, V>) (Mockito.mock(CombineOutputCollector.class)));
        org.apache.hadoop.mapreduce.TaskAttemptID mockTaskAttemptID = Mockito.mock(TaskAttemptID.class);
        LocalDirAllocator mockLocalDirAllocator = Mockito.mock(LocalDirAllocator.class);
        CompressionCodec mockCompressionCodec = Mockito.mock(CompressionCodec.class);
        Counter mockCounter = Mockito.mock(Counter.class);
        TaskStatus mockTaskStatus = Mockito.mock(TaskStatus.class);
        Progress mockProgress = Mockito.mock(Progress.class);
        MapOutputFile mockMapOutputFile = Mockito.mock(MapOutputFile.class);
        Task mockTask = Mockito.mock(Task.class);
        try {
            String[] dirs = jobConf.getLocalDirs();
            // verify that these APIs are available through super class handler
            Context<K, V> context = new Context<K, V>(mockTaskAttemptID, jobConf, mockFileSystem, mockUmbilical, mockLocalDirAllocator, mockReporter, mockCompressionCodec, combinerClass, mockCombineOutputCollector, mockCounter, mockCounter, mockCounter, mockCounter, mockCounter, mockCounter, mockTaskStatus, mockProgress, mockProgress, mockTask, mockMapOutputFile, null);
            shuffleConsumerPlugin.init(context);
            shuffleConsumerPlugin.run();
            shuffleConsumerPlugin.close();
        } catch (Exception e) {
            Assert.assertTrue(("Threw exception:" + e), false);
        }
        // verify that these APIs are available for 3rd party plugins
        mockReduceTask.getTaskID();
        mockReduceTask.getJobID();
        mockReduceTask.getNumMaps();
        mockReduceTask.getPartition();
        mockReporter.progress();
    }

    /**
     * A testing method verifying availability and accessibility of API needed for
     * AuxiliaryService(s) which are "Shuffle-Providers" (ShuffleHandler and 3rd party plugins)
     */
    @Test
    public void testProviderApi() {
        LocalDirAllocator mockLocalDirAllocator = Mockito.mock(LocalDirAllocator.class);
        JobConf mockJobConf = Mockito.mock(JobConf.class);
        try {
            mockLocalDirAllocator.getLocalPathToRead("", mockJobConf);
        } catch (Exception e) {
            Assert.assertTrue(("Threw exception:" + e), false);
        }
    }
}

