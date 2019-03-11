/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.client.cli.fs;


import FileSystem.Factory;
import alluxio.AlluxioURI;
import alluxio.cli.fs.FileSystemShell;
import alluxio.client.file.FileSystem;
import alluxio.conf.ServerConfiguration;
import alluxio.master.LocalAlluxioJobCluster;
import alluxio.master.MultiMasterLocalAlluxioCluster;
import alluxio.testutils.BaseIntegrationTest;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests that the job service is available to the shell when running in fault-tolerant mode.
 */
public final class JobServiceFaultToleranceShellTest extends BaseIntegrationTest {
    private MultiMasterLocalAlluxioCluster mLocalAlluxioCluster;

    private LocalAlluxioJobCluster mLocalAlluxioJobCluster;

    private ByteArrayOutputStream mOutput;

    @Test
    public void distributedMv() throws Exception {
        FileSystem fs = Factory.create(ServerConfiguration.global());
        try (OutputStream out = fs.createFile(new AlluxioURI("/test"))) {
            out.write("Hello".getBytes());
        }
        try (FileSystemShell shell = new FileSystemShell(ServerConfiguration.global())) {
            int exitCode = shell.run("distributedMv", "/test", "/test2");
            Assert.assertEquals(("Command failed, output: " + (mOutput.toString())), 0, exitCode);
        }
        Assert.assertTrue(fs.exists(new AlluxioURI("/test2")));
    }
}

