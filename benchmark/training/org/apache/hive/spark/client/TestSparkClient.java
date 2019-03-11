/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hive.spark.client;


import HiveConf.ConfVars.HIVE_IN_TEST;
import HiveConf.ConfVars.SPARK_CLIENT_TYPE;
import HiveConf.HIVE_SPARK_LAUNCHER_CLIENT;
import JobHandle.State.SENT;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hive.spark.counter.SparkCounters;
import org.apache.spark.SparkFiles;
import org.apache.spark.api.java.JavaFutureAction;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestSparkClient {
    // Timeouts are bad... mmmkay.
    private static final long TIMEOUT = 20;

    private static final HiveConf HIVECONF = new HiveConf();

    static {
        String confDir = "../data/conf/spark/standalone/hive-site.xml";
        try {
            HiveConf.setHiveSiteLocation(new File(confDir).toURI().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        TestSparkClient.HIVECONF.setBoolVar(HIVE_IN_TEST, true);
        TestSparkClient.HIVECONF.setVar(SPARK_CLIENT_TYPE, HIVE_SPARK_LAUNCHER_CLIENT);
    }

    @Test
    public void testJobSubmission() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle.Listener<String> listener = newListener();
                List<JobHandle.Listener<String>> listeners = Lists.newArrayList(listener);
                JobHandle<String> handle = client.submit(new TestSparkClient.SimpleJob(), listeners);
                Assert.assertEquals("hello", handle.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS));
                // Try an invalid state transition on the handle. This ensures that the actual state
                // change we're interested in actually happened, since internally the handle serializes
                // state changes.
                Assert.assertFalse(((JobHandleImpl<String>) (handle)).changeState(SENT));
                Mockito.verify(listener).onJobStarted(handle);
                Mockito.verify(listener).onJobSucceeded(ArgumentMatchers.same(handle), ArgumentMatchers.eq(handle.get()));
            }
        });
    }

    @Test
    public void testSimpleSparkJob() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle<Long> handle = client.submit(new TestSparkClient.SparkJob());
                Assert.assertEquals(Long.valueOf(5L), handle.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS));
            }
        });
    }

    @Test
    public void testErrorJob() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle.Listener<String> listener = newListener();
                List<JobHandle.Listener<String>> listeners = Lists.newArrayList(listener);
                JobHandle<String> handle = client.submit(new TestSparkClient.ErrorJob(), listeners);
                try {
                    handle.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    Assert.fail("Should have thrown an exception.");
                } catch (ExecutionException ee) {
                    Assert.assertTrue(((ee.getCause()) instanceof IllegalStateException));
                    Assert.assertTrue(ee.getCause().getMessage().contains("Hello"));
                }
                // Try an invalid state transition on the handle. This ensures that the actual state
                // change we're interested in actually happened, since internally the handle serializes
                // state changes.
                Assert.assertFalse(((JobHandleImpl<String>) (handle)).changeState(SENT));
                Mockito.verify(listener).onJobQueued(handle);
                Mockito.verify(listener).onJobStarted(handle);
                Mockito.verify(listener).onJobFailed(ArgumentMatchers.same(handle), ArgumentMatchers.any(Throwable.class));
            }
        });
    }

    @Test
    public void testErrorJobNotSerializable() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle.Listener<String> listener = newListener();
                List<JobHandle.Listener<String>> listeners = Lists.newArrayList(listener);
                JobHandle<String> handle = client.submit(new TestSparkClient.ErrorJobNotSerializable(), listeners);
                try {
                    handle.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    Assert.fail("Should have thrown an exception.");
                } catch (ExecutionException ee) {
                    Assert.assertTrue(((ee.getCause()) instanceof RuntimeException));
                    Assert.assertTrue(ee.getCause().getMessage().contains("Hello"));
                }
                // Try an invalid state transition on the handle. This ensures that the actual state
                // change we're interested in actually happened, since internally the handle serializes
                // state changes.
                Assert.assertFalse(((JobHandleImpl<String>) (handle)).changeState(SENT));
                Mockito.verify(listener).onJobQueued(handle);
                Mockito.verify(listener).onJobStarted(handle);
                Mockito.verify(listener).onJobFailed(ArgumentMatchers.same(handle), ArgumentMatchers.any(Throwable.class));
            }
        });
    }

    @Test
    public void testSyncRpc() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                Future<String> result = client.run(new TestSparkClient.SyncRpc());
                Assert.assertEquals("Hello", result.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS));
            }
        });
    }

    @Test
    public void testMetricsCollection() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle.Listener<Integer> listener = newListener();
                List<JobHandle.Listener<Integer>> listeners = Lists.newArrayList(listener);
                JobHandle<Integer> future = client.submit(new TestSparkClient.AsyncSparkJob(), listeners);
                future.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                MetricsCollection metrics = future.getMetrics();
                Assert.assertEquals(1, metrics.getJobIds().size());
                Assert.assertTrue(((metrics.getAllMetrics().executorRunTime) >= 0L));
                Mockito.verify(listener).onSparkJobStarted(ArgumentMatchers.same(future), ArgumentMatchers.eq(metrics.getJobIds().iterator().next()));
                JobHandle.Listener<Integer> listener2 = newListener();
                List<JobHandle.Listener<Integer>> listeners2 = Lists.newArrayList(listener2);
                JobHandle<Integer> future2 = client.submit(new TestSparkClient.AsyncSparkJob(), listeners2);
                future2.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                MetricsCollection metrics2 = future2.getMetrics();
                Assert.assertEquals(1, metrics2.getJobIds().size());
                Assert.assertFalse(Objects.equal(metrics.getJobIds(), metrics2.getJobIds()));
                Assert.assertTrue(((metrics2.getAllMetrics().executorRunTime) >= 0L));
                Mockito.verify(listener2).onSparkJobStarted(ArgumentMatchers.same(future2), ArgumentMatchers.eq(metrics2.getJobIds().iterator().next()));
            }
        });
    }

    @Test
    public void testAddJarsAndFiles() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                File jar = null;
                File file = null;
                try {
                    // Test that adding a jar to the remote context makes it show up in the classpath.
                    jar = File.createTempFile("test", ".jar");
                    JarOutputStream jarFile = new JarOutputStream(new FileOutputStream(jar));
                    jarFile.putNextEntry(new ZipEntry("test.resource"));
                    jarFile.write("test resource".getBytes("UTF-8"));
                    jarFile.closeEntry();
                    jarFile.close();
                    client.addJar(new URI(("file:" + (jar.getAbsolutePath())))).get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    // Need to run a Spark job to make sure the jar is added to the class loader. Monitoring
                    // SparkContext#addJar() doesn't mean much, we can only be sure jars have been distributed
                    // when we run a task after the jar has been added.
                    String result = client.submit(new TestSparkClient.JarJob()).get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    Assert.assertEquals("test resource", result);
                    // Test that adding a file to the remote context makes it available to executors.
                    file = File.createTempFile("test", ".file");
                    FileOutputStream fileStream = new FileOutputStream(file);
                    fileStream.write("test file".getBytes("UTF-8"));
                    fileStream.close();
                    client.addJar(new URI(("file:" + (file.getAbsolutePath())))).get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    // The same applies to files added with "addFile". They're only guaranteed to be available
                    // to tasks started after the addFile() call completes.
                    result = client.submit(new TestSparkClient.FileJob(file.getName())).get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                    Assert.assertEquals("test file", result);
                } finally {
                    if (jar != null) {
                        jar.delete();
                    }
                    if (file != null) {
                        file.delete();
                    }
                }
            }
        });
    }

    @Test
    public void testCounters() throws Exception {
        runTest(new TestSparkClient.TestFunction() {
            @Override
            public void call(SparkClient client) throws Exception {
                JobHandle<?> job = client.submit(new TestSparkClient.CounterIncrementJob());
                job.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
                SparkCounters counters = job.getSparkCounters();
                Assert.assertNotNull(counters);
                long expected = (((1 + 2) + 3) + 4) + 5;
                Assert.assertEquals(expected, counters.getCounter("group1", "counter1").getValue());
                Assert.assertEquals(expected, counters.getCounter("group2", "counter2").getValue());
            }
        });
    }

    @Test
    public void testErrorParsing() {
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("Error.. Test"));
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("This line has error.."));
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("Test that line has ExcePtion.."));
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("Here is eRRor in line.."));
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("Here is ExceptioNn in line.."));
        Assert.assertTrue(SparkClientUtilities.containsErrorKeyword("Here is ERROR and Exception in line.."));
        Assert.assertFalse(SparkClientUtilities.containsErrorKeyword("No problems in this line"));
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestSparkClient.class);

    private static class SimpleJob implements Job<String> {
        @Override
        public String call(JobContext jc) {
            return "hello";
        }
    }

    private static class ErrorJob implements Job<String> {
        @Override
        public String call(JobContext jc) {
            throw new IllegalStateException("Hello");
        }
    }

    private static class ErrorJobNotSerializable implements Job<String> {
        private static final class NonSerializableException extends Exception {
            private static final long serialVersionUID = 2548414562750016219L;

            private final TestSparkClient.ErrorJobNotSerializable.NonSerializableObject nonSerializableObject;

            private NonSerializableException(String content) {
                super("Hello");
                this.nonSerializableObject = new TestSparkClient.ErrorJobNotSerializable.NonSerializableObject(content);
            }
        }

        private static final class NonSerializableObject {
            String content;

            private NonSerializableObject(String content) {
                this.content = content;
            }
        }

        @Override
        public String call(JobContext jc) throws TestSparkClient.ErrorJobNotSerializable.NonSerializableException {
            throw new TestSparkClient.ErrorJobNotSerializable.NonSerializableException("Hello");
        }
    }

    private static class SparkJob implements Job<Long> {
        @Override
        public Long call(JobContext jc) {
            JavaRDD<Integer> rdd = jc.sc().parallelize(Arrays.asList(1, 2, 3, 4, 5));
            return rdd.count();
        }
    }

    private static class AsyncSparkJob implements Job<Integer> {
        @Override
        public Integer call(JobContext jc) throws Exception {
            JavaRDD<Integer> rdd = jc.sc().parallelize(Arrays.asList(1, 2, 3, 4, 5));
            JavaFutureAction<?> future = jc.monitor(rdd.foreachAsync(new org.apache.spark.api.java.function.VoidFunction<Integer>() {
                @Override
                public void call(Integer l) throws Exception {
                }
            }), null, null);
            future.get(TestSparkClient.TIMEOUT, TimeUnit.SECONDS);
            return 1;
        }
    }

    private static class JarJob implements Job<String> , Function<Integer, String> {
        @Override
        public String call(JobContext jc) {
            return jc.sc().parallelize(Arrays.asList(1)).map(this).collect().get(0);
        }

        @Override
        public String call(Integer i) throws Exception {
            ClassLoader ccl = Thread.currentThread().getContextClassLoader();
            InputStream in = ccl.getResourceAsStream("test.resource");
            byte[] bytes = ByteStreams.toByteArray(in);
            in.close();
            return new String(bytes, 0, bytes.length, "UTF-8");
        }
    }

    private static class FileJob implements Job<String> , Function<Integer, String> {
        private final String fileName;

        FileJob(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public String call(JobContext jc) {
            return jc.sc().parallelize(Arrays.asList(1)).map(this).collect().get(0);
        }

        @Override
        public String call(Integer i) throws Exception {
            InputStream in = new FileInputStream(SparkFiles.get(fileName));
            byte[] bytes = ByteStreams.toByteArray(in);
            in.close();
            return new String(bytes, 0, bytes.length, "UTF-8");
        }
    }

    private static class CounterIncrementJob implements Job<String> , org.apache.spark.api.java.function.VoidFunction<Integer> {
        private SparkCounters counters;

        @Override
        public String call(JobContext jc) {
            counters = new SparkCounters(jc.sc());
            counters.createCounter("group1", "counter1");
            counters.createCounter("group2", "counter2");
            jc.monitor(jc.sc().parallelize(Arrays.asList(1, 2, 3, 4, 5), 5).foreachAsync(this), counters, null);
            return null;
        }

        @Override
        public void call(Integer l) throws Exception {
            counters.getCounter("group1", "counter1").increment(l.longValue());
            counters.getCounter("group2", "counter2").increment(l.longValue());
        }
    }

    private static class SyncRpc implements Job<String> {
        @Override
        public String call(JobContext jc) {
            return "Hello";
        }
    }

    private abstract static class TestFunction {
        abstract void call(SparkClient client) throws Exception;

        void config(Map<String, String> conf) {
        }
    }
}

