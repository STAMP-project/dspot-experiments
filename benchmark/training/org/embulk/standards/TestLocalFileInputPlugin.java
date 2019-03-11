package org.embulk.standards;


import LocalFileInputPlugin.PluginTask;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.embulk.EmbulkTestRuntime;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests LocalFileInputPlugin.
 */
public class TestLocalFileInputPlugin {
    @Rule
    public EmbulkTestRuntime runtime = new EmbulkTestRuntime();

    @Rule
    public TemporaryFolder workdir = new TemporaryFolder();

    @Test
    public void testListFiles() throws IOException {
        final LocalFileInputPlugin.PluginTask task = buildTask("foofoo");
        this.workdir.newFile("foofoo1");
        this.workdir.newFile("FooFoo2");
        this.workdir.newFile("barbar3");
        this.workdir.newFolder("foofoo4");
        this.workdir.newFile("foofoo4/foo");
        this.workdir.newFile("foofoo4/bar");
        this.workdir.newFolder("barbar5");
        this.workdir.newFile("barbar5/foo");
        this.workdir.newFile("barbar5/bar");
        this.workdir.newFolder("fooFoo6");
        this.workdir.newFile("fooFoo6/foo");
        this.workdir.newFile("fooFoo6/bar");
        final List<String> files = TestLocalFileInputPlugin.listFiles(task);
        // It intentionally tests in the platform-aware way, not in the platform-oblivious way.
        if (System.getProperty("os.name").contains("Windows")) {
            Assert.assertEquals(6, files.size());
            Assert.assertTrue(files.contains(buildPath("foofoo1")));
            Assert.assertTrue(files.contains(buildPath("FooFoo2")));
            Assert.assertTrue(files.contains(buildPath("foofoo4\\foo")));
            Assert.assertTrue(files.contains(buildPath("foofoo4\\bar")));
            Assert.assertTrue(files.contains(buildPath("fooFoo6\\foo")));
            Assert.assertTrue(files.contains(buildPath("fooFoo6\\bar")));
        } else
            if (System.getProperty("os.name").contains("Mac OS")) {
                Assert.assertEquals(3, files.size());
                Assert.assertTrue(files.contains(buildPath("foofoo1")));
                Assert.assertTrue(files.contains(buildPath("foofoo4/foo")));
                Assert.assertTrue(files.contains(buildPath("foofoo4/bar")));
            } else {
                Assert.assertEquals(3, files.size());
                Assert.assertTrue(files.contains(buildPath("foofoo1")));
                Assert.assertTrue(files.contains(buildPath("foofoo4/foo")));
                Assert.assertTrue(files.contains(buildPath("foofoo4/bar")));
            }

    }

    @Test
    public void testListFilesDots() throws IOException {
        // TODO: Mock the current directory.
        try {
            Files.createFile(Paths.get("file1"));
            Files.createFile(Paths.get("file2"));
            Files.createDirectory(Paths.get("dirA"));
            Files.createFile(Paths.get("dirA", "file3"));
            Files.createFile(Paths.get("dirA", "file4"));
            Files.createDirectory(Paths.get("dirB"));
            Files.createFile(Paths.get("dirB", "file5"));
            Files.createFile(Paths.get("dirB", "file6"));
            final LocalFileInputPlugin.PluginTask file1Task = buildRawTask("file1");
            final List<String> file1Files = TestLocalFileInputPlugin.listFiles(file1Task);
            Assert.assertEquals(1, file1Files.size());
            Assert.assertTrue(file1Files.contains("file1"));
            final LocalFileInputPlugin.PluginTask dotSlashFile1Task = buildRawTask((("." + (File.separator)) + "file1"));
            final List<String> dotSlashFile1Files = TestLocalFileInputPlugin.listFiles(dotSlashFile1Task);
            Assert.assertEquals(1, dotSlashFile1Files.size());
            Assert.assertTrue(dotSlashFile1Files.contains((("." + (File.separator)) + "file1")));
            final LocalFileInputPlugin.PluginTask fileTask = buildRawTask("file");
            final List<String> fileFiles = TestLocalFileInputPlugin.listFiles(fileTask);
            Assert.assertEquals(2, fileFiles.size());
            Assert.assertTrue(fileFiles.contains("file1"));
            Assert.assertTrue(fileFiles.contains("file2"));
            final LocalFileInputPlugin.PluginTask dotSlashFileTask = buildRawTask((("." + (File.separator)) + "file"));
            final List<String> dotSlashFileFiles = TestLocalFileInputPlugin.listFiles(dotSlashFileTask);
            Assert.assertEquals(2, dotSlashFileFiles.size());
            Assert.assertTrue(dotSlashFileFiles.contains((("." + (File.separator)) + "file1")));
            Assert.assertTrue(dotSlashFileFiles.contains((("." + (File.separator)) + "file2")));
            final LocalFileInputPlugin.PluginTask dirATask = buildRawTask("dirA");
            final List<String> dirAFiles = TestLocalFileInputPlugin.listFiles(dirATask);
            Assert.assertEquals(2, dirAFiles.size());
            Assert.assertTrue(dirAFiles.contains((("dirA" + (File.separator)) + "file3")));
            Assert.assertTrue(dirAFiles.contains((("dirA" + (File.separator)) + "file4")));
            final LocalFileInputPlugin.PluginTask dotSlashDirATask = buildRawTask((("." + (File.separator)) + "dirA"));
            final List<String> dotSlashDirAFiles = TestLocalFileInputPlugin.listFiles(dotSlashDirATask);
            Assert.assertEquals(2, dotSlashDirAFiles.size());
            Assert.assertTrue(dotSlashDirAFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file3")));
            Assert.assertTrue(dotSlashDirAFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file4")));
            final LocalFileInputPlugin.PluginTask dirTask = buildRawTask("dir");
            final List<String> dirFiles = TestLocalFileInputPlugin.listFiles(dirTask);
            Assert.assertEquals(4, dirFiles.size());
            Assert.assertTrue(dirFiles.contains((("dirA" + (File.separator)) + "file3")));
            Assert.assertTrue(dirFiles.contains((("dirA" + (File.separator)) + "file4")));
            Assert.assertTrue(dirFiles.contains((("dirB" + (File.separator)) + "file5")));
            Assert.assertTrue(dirFiles.contains((("dirB" + (File.separator)) + "file6")));
            final LocalFileInputPlugin.PluginTask dotSlashDirTask = buildRawTask((("." + (File.separator)) + "dir"));
            final List<String> dotSlashDirFiles = TestLocalFileInputPlugin.listFiles(dotSlashDirTask);
            Assert.assertEquals(4, dotSlashDirFiles.size());
            Assert.assertTrue(dotSlashDirFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file3")));
            Assert.assertTrue(dotSlashDirFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file4")));
            Assert.assertTrue(dotSlashDirFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file5")));
            Assert.assertTrue(dotSlashDirFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file6")));
            final LocalFileInputPlugin.PluginTask dotSlashTask = buildRawTask((("." + (File.separator)) + ""));
            final List<String> dotSlashFiles = TestLocalFileInputPlugin.listFiles(dotSlashTask);
            Assert.assertTrue((6 <= (dotSlashFiles.size())));// Other files and directories exist.

            Assert.assertTrue(dotSlashFiles.contains((("." + (File.separator)) + "file1")));
            Assert.assertTrue(dotSlashFiles.contains((("." + (File.separator)) + "file2")));
            Assert.assertTrue(dotSlashFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file3")));
            Assert.assertTrue(dotSlashFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file4")));
            Assert.assertTrue(dotSlashFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file5")));
            Assert.assertTrue(dotSlashFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file6")));
            final LocalFileInputPlugin.PluginTask dotTask = buildRawTask(".");
            final List<String> dotFiles = TestLocalFileInputPlugin.listFiles(dotTask);
            Assert.assertTrue((6 <= (dotFiles.size())));// Other files and directories exist.

            Assert.assertTrue(dotFiles.contains((("." + (File.separator)) + "file1")));
            Assert.assertTrue(dotFiles.contains((("." + (File.separator)) + "file2")));
            Assert.assertTrue(dotFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file3")));
            Assert.assertTrue(dotFiles.contains((((("." + (File.separator)) + "dirA") + (File.separator)) + "file4")));
            Assert.assertTrue(dotFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file5")));
            Assert.assertTrue(dotFiles.contains((((("." + (File.separator)) + "dirB") + (File.separator)) + "file6")));
        } finally {
            Files.deleteIfExists(Paths.get("dirB", "file6"));
            Files.deleteIfExists(Paths.get("dirB", "file5"));
            Files.deleteIfExists(Paths.get("dirB"));
            Files.deleteIfExists(Paths.get("dirA", "file4"));
            Files.deleteIfExists(Paths.get("dirA", "file3"));
            Files.deleteIfExists(Paths.get("dirA"));
            Files.deleteIfExists(Paths.get("file2"));
            Files.deleteIfExists(Paths.get("file1"));
        }
    }

    @Test
    public void testListFilesWithSameCaseDirectoryPrefix() throws IOException {
        final LocalFileInputPlugin.PluginTask task;
        if (System.getProperty("os.name").contains("Windows")) {
            task = buildTask("directory1\\foo");
        } else {
            task = buildTask("directory1/foo");
        }
        this.workdir.newFile("foo");
        this.workdir.newFolder("directory1");
        this.workdir.newFile("directory1/foo1");
        this.workdir.newFile("directory1/foo2");
        this.workdir.newFile("directory1/Foo3");
        this.workdir.newFile("directory1/bar");
        this.workdir.newFolder("directory2");
        this.workdir.newFile("directory2/bar");
        final List<String> files = TestLocalFileInputPlugin.listFiles(task);
        // It intentionally tests in the platform-aware way, not in the platform-oblivious way.
        if (System.getProperty("os.name").contains("Windows")) {
            Assert.assertEquals(3, files.size());
            Assert.assertTrue(files.contains(buildPath("directory1\\foo1")));
            Assert.assertTrue(files.contains(buildPath("directory1\\foo2")));
            Assert.assertTrue(files.contains(buildPath("directory1\\Foo3")));
        } else
            if (System.getProperty("os.name").contains("Mac OS")) {
                Assert.assertEquals(2, files.size());
                Assert.assertTrue(files.contains(buildPath("directory1/foo1")));
                Assert.assertTrue(files.contains(buildPath("directory1/foo2")));
            } else {
                Assert.assertEquals(2, files.size());
                Assert.assertTrue(files.contains(buildPath("directory1/foo1")));
                Assert.assertTrue(files.contains(buildPath("directory1/foo2")));
            }

    }

    @Test
    public void testListFilesWithDifferentCaseDirectoryPrefix() throws IOException {
        final LocalFileInputPlugin.PluginTask task;
        if (System.getProperty("os.name").contains("Windows")) {
            task = buildTask("Directory1\\foo");
        } else {
            task = buildTask("Directory1/foo");
        }
        if ((!(System.getProperty("os.name").contains("Windows"))) && (!(System.getProperty("os.name").contains("Mac OS")))) {
            this.workdir.newFolder("Directory1");
        }
        this.workdir.newFile("foo");
        this.workdir.newFolder("directory1");
        this.workdir.newFile("directory1/foo1");
        this.workdir.newFile("directory1/foo2");
        this.workdir.newFile("directory1/Foo3");
        this.workdir.newFile("directory1/bar");
        this.workdir.newFolder("directory2");
        this.workdir.newFile("directory2/bar");
        final List<String> files = TestLocalFileInputPlugin.listFiles(task);
        // It intentionally tests in the platform-aware way, not in the platform-oblivious way.
        if (System.getProperty("os.name").contains("Windows")) {
            Assert.assertEquals(3, files.size());
            Assert.assertTrue(files.contains(buildPath("directory1\\foo1")));
            Assert.assertTrue(files.contains(buildPath("directory1\\foo2")));
            Assert.assertTrue(files.contains(buildPath("directory1\\Foo3")));
        } else
            if (System.getProperty("os.name").contains("Mac OS")) {
                Assert.assertEquals(0, files.size());
            } else {
                Assert.assertEquals(0, files.size());
            }

    }
}

