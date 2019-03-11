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
package alluxio.client.cli.fs.command;


import alluxio.client.cli.fs.AbstractFileSystemShellTest;
import alluxio.client.cli.fs.AbstractShellIntegrationTest;
import alluxio.util.io.PathUtils;
import com.google.common.io.Files;
import java.io.File;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for cross-mount {@link MvCommand}.
 */
public final class DistributedMvCommandTest extends AbstractFileSystemShellTest {
    @Rule
    public TemporaryFolder mFolder = new TemporaryFolder();

    @Test
    public void crossMountMove() throws Exception {
        File file = mFolder.newFile();
        Files.write("hello".getBytes(), file);
        run("mount", "/cross", mFolder.getRoot().getAbsolutePath());
        run("ls", "-f", "/cross");
        run("distributedMv", PathUtils.concatPath("/cross", file.getName()), "/moved");
        mOutput.reset();
        run("cat", "/moved");
        Assert.assertEquals("hello", mOutput.toString());
    }
}

