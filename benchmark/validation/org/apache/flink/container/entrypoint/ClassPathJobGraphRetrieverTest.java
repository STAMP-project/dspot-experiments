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
package org.apache.flink.container.entrypoint;


import CoreOptions.DEFAULT_PARALLELISM;
import JarsOnClassPath.INSTANCE;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import org.apache.flink.api.common.JobID;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.SavepointRestoreSettings;
import org.apache.flink.util.FlinkException;
import org.apache.flink.util.TestLogger;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@link ClassPathJobGraphRetriever}.
 */
public class ClassPathJobGraphRetrieverTest extends TestLogger {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final String[] PROGRAM_ARGUMENTS = new String[]{ "--arg", "suffix" };

    @Test
    public void testJobGraphRetrieval() throws FlinkException {
        final int parallelism = 42;
        final Configuration configuration = new Configuration();
        configuration.setInteger(DEFAULT_PARALLELISM, parallelism);
        final JobID jobId = new JobID();
        final ClassPathJobGraphRetriever classPathJobGraphRetriever = new ClassPathJobGraphRetriever(jobId, SavepointRestoreSettings.none(), ClassPathJobGraphRetrieverTest.PROGRAM_ARGUMENTS, TestJob.class.getCanonicalName());
        final JobGraph jobGraph = classPathJobGraphRetriever.retrieveJobGraph(configuration);
        Assert.assertThat(jobGraph.getName(), Matchers.is(Matchers.equalTo(((TestJob.class.getCanonicalName()) + "-suffix"))));
        Assert.assertThat(jobGraph.getMaximumParallelism(), Matchers.is(parallelism));
        Assert.assertEquals(jobGraph.getJobID(), jobId);
    }

    @Test
    public void testJobGraphRetrievalFromJar() throws FileNotFoundException, FlinkException {
        final File testJar = TestJob.getTestJobJar();
        final ClassPathJobGraphRetriever classPathJobGraphRetriever = // No class name specified, but the test JAR "is" on the class path
        new ClassPathJobGraphRetriever(new JobID(), SavepointRestoreSettings.none(), ClassPathJobGraphRetrieverTest.PROGRAM_ARGUMENTS, null, () -> Collections.singleton(testJar));
        final JobGraph jobGraph = classPathJobGraphRetriever.retrieveJobGraph(new Configuration());
        Assert.assertThat(jobGraph.getName(), Matchers.is(Matchers.equalTo(((TestJob.class.getCanonicalName()) + "-suffix"))));
    }

    @Test
    public void testJobGraphRetrievalJobClassNameHasPrecedenceOverClassPath() throws FileNotFoundException, FlinkException {
        final File testJar = new File("non-existing");
        final ClassPathJobGraphRetriever classPathJobGraphRetriever = // Both a class name is specified and a JAR "is" on the class path
        // The class name should have precedence.
        new ClassPathJobGraphRetriever(new JobID(), SavepointRestoreSettings.none(), ClassPathJobGraphRetrieverTest.PROGRAM_ARGUMENTS, TestJob.class.getCanonicalName(), () -> Collections.singleton(testJar));
        final JobGraph jobGraph = classPathJobGraphRetriever.retrieveJobGraph(new Configuration());
        Assert.assertThat(jobGraph.getName(), Matchers.is(Matchers.equalTo(((TestJob.class.getCanonicalName()) + "-suffix"))));
    }

    @Test
    public void testSavepointRestoreSettings() throws FlinkException {
        final Configuration configuration = new Configuration();
        final SavepointRestoreSettings savepointRestoreSettings = SavepointRestoreSettings.forPath("foobar", true);
        final JobID jobId = new JobID();
        final ClassPathJobGraphRetriever classPathJobGraphRetriever = new ClassPathJobGraphRetriever(jobId, savepointRestoreSettings, ClassPathJobGraphRetrieverTest.PROGRAM_ARGUMENTS, TestJob.class.getCanonicalName());
        final JobGraph jobGraph = classPathJobGraphRetriever.retrieveJobGraph(configuration);
        Assert.assertThat(jobGraph.getSavepointRestoreSettings(), Matchers.is(Matchers.equalTo(savepointRestoreSettings)));
        Assert.assertEquals(jobGraph.getJobID(), jobId);
    }

    @Test
    public void testJarFromClassPathSupplierSanityCheck() {
        Iterable<File> jarFiles = INSTANCE.get();
        // Junit executes this test, so it should be returned as part of JARs on the class path
        Assert.assertThat(jarFiles, Matchers.hasItem(Matchers.hasProperty("name", Matchers.containsString("junit"))));
    }

    @Test
    public void testJarFromClassPathSupplier() throws IOException {
        final File file1 = temporaryFolder.newFile();
        final File file2 = temporaryFolder.newFile();
        final File directory = temporaryFolder.newFolder();
        // Mock java.class.path property. The empty strings are important as the shell scripts
        // that prepare the Flink class path often have such entries.
        final String classPath = ClassPathJobGraphRetrieverTest.javaClassPath("", "", "", file1.getAbsolutePath(), "", directory.getAbsolutePath(), "", file2.getAbsolutePath(), "", "");
        Iterable<File> jarFiles = ClassPathJobGraphRetrieverTest.setClassPathAndGetJarsOnClassPath(classPath);
        Assert.assertThat(jarFiles, Matchers.contains(file1, file2));
    }
}

