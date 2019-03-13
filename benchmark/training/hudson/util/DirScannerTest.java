/**
 * The MIT License
 *
 * Copyright (c) 2011, Christoph Thelen
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
package hudson.util;


import hudson.FilePath;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 *
 *
 * @author Christoph Thelen
 */
public class DirScannerTest {
    @Rule
    public TemporaryFolder tmpRule = new TemporaryFolder();

    @Test
    public void globShouldUseDefaultExcludes() throws Exception {
        FilePath tmp = new FilePath(tmpRule.getRoot());
        try {
            tmp.child(".gitignore").touch(0);
            FilePath git = tmp.child(".git");
            git.mkdirs();
            git.child("HEAD").touch(0);
            DirScanner glob1 = new DirScanner.Glob("**/*", null);
            DirScanner glob2 = new DirScanner.Glob("**/*", null, true);
            DirScannerTest.MatchingFileVisitor gitdir = new DirScannerTest.MatchingFileVisitor("HEAD");
            DirScannerTest.MatchingFileVisitor gitignore = new DirScannerTest.MatchingFileVisitor(".gitignore");
            glob1.scan(new File(tmp.getRemote()), gitdir);
            glob2.scan(new File(tmp.getRemote()), gitignore);
            Assert.assertFalse(gitdir.found);
            Assert.assertFalse(gitignore.found);
        } finally {
            tmp.deleteRecursive();
        }
    }

    @Test
    public void globShouldIgnoreDefaultExcludesByRequest() throws Exception {
        FilePath tmp = new FilePath(tmpRule.getRoot());
        try {
            tmp.child(".gitignore").touch(0);
            FilePath git = tmp.child(".git");
            git.mkdirs();
            git.child("HEAD").touch(0);
            DirScanner glob = new DirScanner.Glob("**/*", null, false);
            DirScannerTest.MatchingFileVisitor gitdir = new DirScannerTest.MatchingFileVisitor("HEAD");
            DirScannerTest.MatchingFileVisitor gitignore = new DirScannerTest.MatchingFileVisitor(".gitignore");
            glob.scan(new File(tmp.getRemote()), gitdir);
            glob.scan(new File(tmp.getRemote()), gitignore);
            Assert.assertTrue(gitdir.found);
            Assert.assertTrue(gitignore.found);
        } finally {
            tmp.deleteRecursive();
        }
    }

    private static class MatchingFileVisitor extends FileVisitor {
        public boolean found = false;

        public final String filename;

        public MatchingFileVisitor(String filename) {
            this.filename = filename;
        }

        public void visit(File f, String relativePath) throws IOException {
            if (relativePath.endsWith(filename)) {
                found = true;
            }
        }
    }
}

