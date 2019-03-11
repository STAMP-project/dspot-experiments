/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.cli;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 */
public class PullDependenciesTest {
    private static final String EXTENSION_A_COORDINATE = "groupX:extension_A:123";

    private static final String EXTENSION_B_COORDINATE = "groupY:extension_B:456";

    private static final String HADOOP_CLIENT_2_3_0_COORDINATE = "org.apache.hadoop:hadoop-client:2.3.0";

    private static final String HADOOP_CLIENT_2_4_0_COORDINATE = "org.apache.hadoop:hadoop-client:2.4.0";

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File localRepo;// a mock local repository that stores jars


    private final Artifact extension_A = new DefaultArtifact(PullDependenciesTest.EXTENSION_A_COORDINATE);

    private final Artifact extension_B = new DefaultArtifact(PullDependenciesTest.EXTENSION_B_COORDINATE);

    private final Artifact hadoop_client_2_3_0 = new DefaultArtifact(PullDependenciesTest.HADOOP_CLIENT_2_3_0_COORDINATE);

    private final Artifact hadoop_client_2_4_0 = new DefaultArtifact(PullDependenciesTest.HADOOP_CLIENT_2_4_0_COORDINATE);

    private PullDependencies pullDependencies;

    private File rootExtensionsDir;

    private File rootHadoopDependenciesDir;

    private HashMap<Artifact, List<String>> extensionToJars;// map Artifact to its associated jars' names


    /**
     * If --clean is not specified and root extension directory already exists, skip creating.
     */
    @Test
    public void testPullDependencies_root_extension_dir_exists() {
        rootExtensionsDir.mkdir();
        pullDependencies.run();
    }

    /**
     * A file exists on the root extension directory path, but it's not a directory, throw exception.
     */
    @Test(expected = RuntimeException.class)
    public void testPullDependencies_root_extension_dir_bad_state() throws IOException {
        Assert.assertTrue(rootExtensionsDir.createNewFile());
        pullDependencies.run();
    }

    /**
     * If --clean is not specified and hadoop dependencies directory already exists, skip creating.
     */
    @Test
    public void testPullDependencies_root_hadoop_dependencies_dir_exists() {
        rootHadoopDependenciesDir.mkdir();
        pullDependencies.run();
    }

    /**
     * A file exists on the root hadoop dependencies directory path, but it's not a directory, throw exception.
     */
    @Test(expected = RuntimeException.class)
    public void testPullDependencies_root_hadoop_dependencies_dir_bad_state() throws IOException {
        Assert.assertTrue(rootHadoopDependenciesDir.createNewFile());
        pullDependencies.run();
    }

    @Test
    public void testPullDependencies() {
        rootExtensionsDir.mkdir();
        rootHadoopDependenciesDir.mkdir();
        // Because --clean is specified, pull-deps will first remove existing root extensions and hadoop dependencies
        pullDependencies.clean = true;
        pullDependencies.run();
        final File[] actualExtensions = rootExtensionsDir.listFiles();
        Arrays.sort(actualExtensions);
        Assert.assertEquals(2, actualExtensions.length);
        Assert.assertEquals(extension_A.getArtifactId(), actualExtensions[0].getName());
        Assert.assertEquals(extension_B.getArtifactId(), actualExtensions[1].getName());
        final File[] jarsUnderExtensionA = actualExtensions[0].listFiles();
        Arrays.sort(jarsUnderExtensionA);
        Assert.assertArrayEquals(getExpectedJarFiles(extension_A), jarsUnderExtensionA);
        final File[] jarsUnderExtensionB = actualExtensions[1].listFiles();
        Arrays.sort(jarsUnderExtensionB);
        Assert.assertArrayEquals(getExpectedJarFiles(extension_B), jarsUnderExtensionB);
        final File[] actualHadoopDependencies = rootHadoopDependenciesDir.listFiles();
        Arrays.sort(actualHadoopDependencies);
        Assert.assertEquals(1, actualHadoopDependencies.length);
        Assert.assertEquals(hadoop_client_2_3_0.getArtifactId(), actualHadoopDependencies[0].getName());
        final File[] versionDirsUnderHadoopClient = actualHadoopDependencies[0].listFiles();
        Assert.assertEquals(2, versionDirsUnderHadoopClient.length);
        Arrays.sort(versionDirsUnderHadoopClient);
        Assert.assertEquals(hadoop_client_2_3_0.getVersion(), versionDirsUnderHadoopClient[0].getName());
        Assert.assertEquals(hadoop_client_2_4_0.getVersion(), versionDirsUnderHadoopClient[1].getName());
        final File[] jarsUnder2_3_0 = versionDirsUnderHadoopClient[0].listFiles();
        Arrays.sort(jarsUnder2_3_0);
        Assert.assertArrayEquals(getExpectedJarFiles(hadoop_client_2_3_0), jarsUnder2_3_0);
        final File[] jarsUnder2_4_0 = versionDirsUnderHadoopClient[1].listFiles();
        Arrays.sort(jarsUnder2_4_0);
        Assert.assertArrayEquals(getExpectedJarFiles(hadoop_client_2_4_0), jarsUnder2_4_0);
    }
}

