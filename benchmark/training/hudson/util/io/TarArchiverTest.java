/**
 * The MIT License
 *
 * Copyright (c) 2011, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hudson.util.io;


import TaskListener.NULL;
import hudson.FilePath;
import hudson.Functions;
import hudson.Util;
import hudson.util.NullStream;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.jvnet.hudson.test.Issue;


public class TarArchiverTest {
    @Rule
    public TemporaryFolder tmp = new TemporaryFolder();

    /**
     * Makes sure that permissions are properly stored in the tar file.
     */
    @Issue("JENKINS-9397")
    @Test
    public void permission() throws Exception {
        Assume.assumeTrue((!(Functions.isWindows())));
        File tar = File.createTempFile("test", "tar");
        File zip = File.createTempFile("test", "zip");
        FilePath dir = new FilePath(File.createTempFile("test", "dir"));
        try {
            dir.delete();
            dir.child("subdir").mkdirs();
            FilePath f = dir.child("a.txt");
            f.touch(0);
            f.chmod(493);
            f = dir.child("subdir/b.txt");
            f.touch(0);
            f.chmod(420);
            int dirMode = dir.child("subdir").mode();
            dir.tar(Files.newOutputStream(tar.toPath()), "**/*");
            dir.zip(Files.newOutputStream(zip.toPath()));
            FilePath e = dir.child("extract");
            e.mkdirs();
            // extract via the tar command
            TarArchiverTest.run(e, "tar", "xvpf", tar.getAbsolutePath());
            Assert.assertEquals(493, e.child("a.txt").mode());
            Assert.assertEquals(dirMode, e.child("subdir").mode());
            Assert.assertEquals(420, e.child("subdir/b.txt").mode());
            // extract via the zip command
            e.deleteContents();
            TarArchiverTest.run(e, "unzip", zip.getAbsolutePath());
            e = e.listDirectories().get(0);
            Assert.assertEquals(493, e.child("a.txt").mode());
            Assert.assertEquals(dirMode, e.child("subdir").mode());
            Assert.assertEquals(420, e.child("subdir/b.txt").mode());
        } finally {
            tar.delete();
            zip.delete();
            dir.deleteRecursive();
        }
    }

    @Issue("JENKINS-14922")
    @Test
    public void brokenSymlinks() throws Exception {
        Assume.assumeTrue((!(Functions.isWindows())));
        File dir = tmp.getRoot();
        Util.createSymlink(dir, "nonexistent", "link", NULL);
        new FilePath(dir).tar(new NullStream(), "**");
    }

    /**
     * Test backing up an open file
     */
    @Issue("JENKINS-20187")
    @Test
    public void growingFileTar() throws Exception {
        File file = new File(tmp.getRoot(), "growing.file");
        TarArchiverTest.GrowingFileRunnable runnable1 = new TarArchiverTest.GrowingFileRunnable(file);
        Thread t1 = new Thread(runnable1);
        t1.start();
        new FilePath(tmp.getRoot()).tar(new NullStream(), "**");
        runnable1.doFinish();
        t1.join();
    }

    private class GrowingFileRunnable implements Runnable {
        private boolean finish = false;

        private Exception ex = null;

        private File file;

        public GrowingFileRunnable(File file) {
            this.file = file;
        }

        @Override
        public void run() {
            File openFile = file;
            try {
                openFile.createNewFile();
                try (OutputStream fos = Files.newOutputStream(openFile.toPath())) {
                    for (int i = 0; (!(finish)) && (i < 5000000); i++) {
                        // limit the max size, just in case.
                        fos.write(0);
                        // Thread.sleep(5);
                    }
                }
            } catch (Exception e) {
                ex = e;
            }
        }

        public void doFinish() throws Exception {
            finish = true;
            if ((ex) != null) {
                throw ex;
            }
        }
    }
}

