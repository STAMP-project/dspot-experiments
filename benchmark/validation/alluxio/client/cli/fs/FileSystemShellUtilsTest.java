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


import alluxio.ConfigurationTestUtils;
import alluxio.Constants;
import alluxio.cli.Command;
import alluxio.cli.fs.FileSystemShell;
import alluxio.cli.fs.FileSystemShellUtils;
import alluxio.client.file.FileSystem;
import alluxio.client.file.FileSystemContext;
import alluxio.conf.ServerConfiguration;
import alluxio.exception.AlluxioException;
import alluxio.testutils.LocalAlluxioClusterResource;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.reflections.Reflections;


/**
 * Unit tests on {@link FileSystemShellUtils}.
 *
 * Note that the test case for {@link FileSystemShellUtils#validatePath(String)} is already covered
 * in {@link FileSystemShellUtils#getFilePath(String)}. Hence only getFilePathTest is specified.
 */
public final class FileSystemShellUtilsTest {
    public static final String TEST_DIR = "/testDir";

    @Rule
    public LocalAlluxioClusterResource mLocalAlluxioClusterResource = new LocalAlluxioClusterResource.Builder().build();

    private FileSystem mFileSystem = null;

    @Test
    public void getFilePath() throws IOException {
        String[] paths = new String[]{ (Constants.HEADER) + "localhost:19998/dir", (Constants.HEADER_FT) + "localhost:19998/dir", "/dir", "dir" };
        String expected = "/dir";
        for (String path : paths) {
            String result = FileSystemShellUtils.getFilePath(path, ConfigurationTestUtils.defaults());
            Assert.assertEquals(expected, result);
        }
    }

    /**
     * Type of file system.
     */
    public enum FsType {

        TFS,
        LOCAL;}

    @Test
    public void getPath() throws AlluxioException, IOException {
        for (FileSystemShellUtilsTest.FsType fsType : FileSystemShellUtilsTest.FsType.values()) {
            String rootDir = resetFsHierarchy(fsType);
            List<String> tl1 = getPaths((rootDir + "/foo"), fsType);
            Assert.assertEquals(tl1.size(), 1);
            Assert.assertEquals(tl1.get(0), (rootDir + "/foo"));
            // Trailing slash
            List<String> tl2 = getPaths((rootDir + "/foo/"), fsType);
            Assert.assertEquals(tl2.size(), 1);
            Assert.assertEquals(tl2.get(0), (rootDir + "/foo"));
            // Wildcard
            List<String> tl3 = getPaths((rootDir + "/foo/*"), fsType);
            Assert.assertEquals(tl3.size(), 2);
            Assert.assertEquals(tl3.get(0), (rootDir + "/foo/foobar1"));
            Assert.assertEquals(tl3.get(1), (rootDir + "/foo/foobar2"));
            // Trailing slash + wildcard
            List<String> tl4 = getPaths((rootDir + "/foo/*/"), fsType);
            Assert.assertEquals(tl4.size(), 2);
            Assert.assertEquals(tl4.get(0), (rootDir + "/foo/foobar1"));
            Assert.assertEquals(tl4.get(1), (rootDir + "/foo/foobar2"));
            // Multiple wildcards
            List<String> tl5 = getPaths((rootDir + "/*/foo*"), fsType);
            Assert.assertEquals(tl5.size(), 3);
            Assert.assertEquals(tl5.get(0), (rootDir + "/bar/foobar3"));
            Assert.assertEquals(tl5.get(1), (rootDir + "/foo/foobar1"));
            Assert.assertEquals(tl5.get(2), (rootDir + "/foo/foobar2"));
        }
    }

    @Test
    public void match() {
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "/a/*"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "/a/*/"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "/a/*/c"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "/a/*/*"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "/a/*/*/"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c/", "/a/*/*/"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c/", "/a/*/*"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/foo/bar/foobar/", "/foo*/*"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/foo/bar/foobar/", "/*/*/foobar"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c/", "/b/*"), false);
        Assert.assertEquals(FileSystemShellUtils.match("/", "/*/*"), false);
        Assert.assertEquals(FileSystemShellUtils.match("/a/b/c", "*"), true);
        Assert.assertEquals(FileSystemShellUtils.match("/", "/*"), true);
    }

    @Test
    public void getIntArgTest() throws Exception {
        Option opt = Option.builder("t").longOpt("test").numberOfArgs(1).required(false).build();
        CommandLine cmdLine = getCmdLine(opt, "--test", "1");
        Assert.assertEquals("Should get long form", 1, FileSystemShellUtils.getIntArg(cmdLine, opt, 0));
        cmdLine = getCmdLine(opt, "-t", "5");
        Assert.assertEquals("Should get short form", 5, FileSystemShellUtils.getIntArg(cmdLine, opt, 0));
        cmdLine = getCmdLine(opt);
        Assert.assertEquals("Should not get arg", 0, FileSystemShellUtils.getIntArg(cmdLine, opt, 0));
    }

    @Test
    public void loadCommands() {
        Map<String, Command> map = FileSystemShellUtils.loadCommands(FileSystemContext.create(ServerConfiguration.global()));
        String pkgName = Command.class.getPackage().getName();
        Reflections reflections = new Reflections(pkgName);
        Set<Class<? extends Command>> cmdSet = reflections.getSubTypesOf(Command.class);
        for (Map.Entry<String, Command> entry : map.entrySet()) {
            Assert.assertEquals(entry.getValue().getCommandName(), entry.getKey());
            Assert.assertEquals(cmdSet.contains(entry.getValue().getClass()), true);
        }
        int expectSize = 0;
        for (Class<? extends Command> cls : cmdSet) {
            if ((cls.getPackage().getName().equals(((FileSystemShell.class.getPackage().getName()) + ".command"))) && (!(Modifier.isAbstract(cls.getModifiers())))) {
                expectSize++;
            }
        }
        Assert.assertEquals(expectSize, map.size());
    }
}

