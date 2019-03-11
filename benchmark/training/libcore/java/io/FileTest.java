/**
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.io;


import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import junit.framework.TestCase;


public class FileTest extends TestCase {
    // RoboVM note: Darwin has a path length limit of 1024 characters while
    // Ubuntu allows as many as 4096.
    public static final int PATH_MAX = ((System.getProperty("os.name").contains("iOS")) || (System.getProperty("os.name").contains("Mac"))) ? 1024 : 4096;

    // Rather than test all methods, assume that if createTempFile creates a long path and
    // exists can see it, the code for coping with long paths (shared by all methods) works.
    public void test_longPath() throws Exception {
        File base = FileTest.createTemporaryDirectory();
        TestCase.assertTrue(FileTest.createDeepStructure(base).exists());
    }

    // readlink(2) is a special case,.
    public void test_longReadlink() throws Exception {
        File base = FileTest.createTemporaryDirectory();
        File target = FileTest.createDeepStructure(base);
        File source = new File(base, "source");
        TestCase.assertFalse(source.exists());
        TestCase.assertTrue(target.exists());
        TestCase.assertTrue(((target.getCanonicalPath().length()) > ((FileTest.PATH_MAX) - 256)));
        FileTest.ln_s(target, source);
        TestCase.assertTrue(source.exists());
        TestCase.assertEquals(target.getCanonicalPath(), source.getCanonicalPath());
    }

    // TODO: File.list is a special case too, but I haven't fixed it yet, and the new code,
    // like the old code, will die of a native buffer overrun if we exercise it.
    public void test_emptyFilename() throws Exception {
        // The behavior of the empty filename is an odd mixture.
        File f = new File("");
        // Mostly it behaves like an invalid path...
        TestCase.assertFalse(f.canExecute());
        TestCase.assertFalse(f.canRead());
        TestCase.assertFalse(f.canWrite());
        try {
            f.createNewFile();
            TestCase.fail("expected IOException");
        } catch (IOException expected) {
        }
        TestCase.assertFalse(f.delete());
        f.deleteOnExit();
        TestCase.assertFalse(f.exists());
        TestCase.assertEquals("", f.getName());
        TestCase.assertEquals(null, f.getParent());
        TestCase.assertEquals(null, f.getParentFile());
        TestCase.assertEquals("", f.getPath());
        TestCase.assertFalse(f.isAbsolute());
        TestCase.assertFalse(f.isDirectory());
        TestCase.assertFalse(f.isFile());
        TestCase.assertFalse(f.isHidden());
        TestCase.assertEquals(0, f.lastModified());
        TestCase.assertEquals(0, f.length());
        TestCase.assertEquals(null, f.list());
        TestCase.assertEquals(null, f.list(null));
        TestCase.assertEquals(null, f.listFiles());
        TestCase.assertEquals(null, f.listFiles(((FileFilter) (null))));
        TestCase.assertEquals(null, f.listFiles(((FilenameFilter) (null))));
        TestCase.assertFalse(f.mkdir());
        TestCase.assertFalse(f.mkdirs());
        TestCase.assertFalse(f.renameTo(f));
        TestCase.assertFalse(f.setLastModified(123));
        TestCase.assertFalse(f.setExecutable(true));
        TestCase.assertFalse(f.setReadOnly());
        TestCase.assertFalse(f.setReadable(true));
        TestCase.assertFalse(f.setWritable(true));
        // ...but sometimes it behaves like "user.dir".
        String cwd = System.getProperty("user.dir");
        TestCase.assertEquals(new File(cwd), f.getAbsoluteFile());
        TestCase.assertEquals(cwd, f.getAbsolutePath());
        // TODO: how do we test these without hard-coding assumptions about where our temporary
        // directory is? (In practice, on Android, our temporary directory is accessed through
        // a symbolic link, so the canonical file/path will be different.)
        // assertEquals(new File(cwd), f.getCanonicalFile());
        // assertEquals(cwd, f.getCanonicalPath());
    }

    // http://b/2486943 - between eclair and froyo, we added a call to
    // isAbsolute from the File constructor, potentially breaking subclasses.
    public void test_subclassing() throws Exception {
        class MyFile extends File {
            private String field;

            MyFile(String s) {
                super(s);
                field = "";
            }

            @Override
            public boolean isAbsolute() {
                field.length();
                return super.isAbsolute();
            }
        }
        new MyFile("");
    }

    // http://b/3047893 - getCanonicalPath wasn't actually resolving symbolic links.
    public void test_getCanonicalPath() throws Exception {
        // This assumes you can create symbolic links in the temporary directory. This isn't
        // true on Android if you're using /sdcard. It will work in /data/local though.
        File base = FileTest.createTemporaryDirectory();
        File target = new File(base, "target");
        target.createNewFile();// The RI won't follow a dangling symlink, which seems like a bug!

        File linkName = new File(base, "link");
        FileTest.ln_s(target, linkName);
        TestCase.assertEquals(target.getCanonicalPath(), linkName.getCanonicalPath());
        // .../subdir/shorter -> .../target (using a link to ../target).
        File subdir = new File(base, "subdir");
        TestCase.assertTrue(subdir.mkdir());
        linkName = new File(subdir, "shorter");
        FileTest.ln_s("../target", linkName.toString());
        TestCase.assertEquals(target.getCanonicalPath(), linkName.getCanonicalPath());
        // .../l -> .../subdir/longer (using a relative link to subdir/longer).
        linkName = new File(base, "l");
        FileTest.ln_s("subdir/longer", linkName.toString());
        File longer = new File(base, "subdir/longer");
        longer.createNewFile();// The RI won't follow a dangling symlink, which seems like a bug!

        TestCase.assertEquals(longer.getCanonicalPath(), linkName.getCanonicalPath());
        // .../double -> .../target (via a link into subdir and a link back out).
        linkName = new File(base, "double");
        FileTest.ln_s("subdir/shorter", linkName.toString());
        TestCase.assertEquals(target.getCanonicalPath(), linkName.getCanonicalPath());
    }

    public void test_createNewFile() throws Exception {
        File f = File.createTempFile("FileTest", "tmp");
        TestCase.assertFalse(f.createNewFile());// EEXIST -> false

        TestCase.assertFalse(f.getParentFile().createNewFile());// EEXIST -> false, even if S_ISDIR

        try {
            new File(f, "poop").createNewFile();// ENOTDIR -> throw

            TestCase.fail();
        } catch (IOException expected) {
        }
        try {
            new File("").createNewFile();// ENOENT -> throw

            TestCase.fail();
        } catch (IOException expected) {
        }
    }

    public void test_rename() throws Exception {
        File f = File.createTempFile("FileTest", "tmp");
        TestCase.assertFalse(f.renameTo(new File("")));
        TestCase.assertFalse(new File("").renameTo(f));
        TestCase.assertFalse(f.renameTo(new File(".")));
        TestCase.assertTrue(f.renameTo(f));
    }

    public void test_getAbsolutePath() throws Exception {
        String originalUserDir = System.getProperty("user.dir");
        try {
            File f = new File("poop");
            System.setProperty("user.dir", "/a");
            TestCase.assertEquals("/a/poop", f.getAbsolutePath());
            System.setProperty("user.dir", "/b");
            TestCase.assertEquals("/b/poop", f.getAbsolutePath());
        } finally {
            System.setProperty("user.dir", originalUserDir);
        }
    }

    public void test_getSpace() throws Exception {
        TestCase.assertTrue(((new File("/").getFreeSpace()) >= 0));
        TestCase.assertTrue(((new File("/").getTotalSpace()) >= 0));
        TestCase.assertTrue(((new File("/").getUsableSpace()) >= 0));
    }

    public void test_mkdirs() throws Exception {
        // Set up a directory to test in.
        File base = FileTest.createTemporaryDirectory();
        // mkdirs returns true only if it _creates_ a directory.
        // So we get false for a directory that already exists...
        TestCase.assertTrue(base.exists());
        TestCase.assertFalse(base.mkdirs());
        // But true if we had to create something.
        File a = new File(base, "a");
        TestCase.assertFalse(a.exists());
        TestCase.assertTrue(a.mkdirs());
        TestCase.assertTrue(a.exists());
        // Test the recursive case where we need to create multiple parents.
        File b = new File(a, "b");
        File c = new File(b, "c");
        File d = new File(c, "d");
        TestCase.assertTrue(a.exists());
        TestCase.assertFalse(b.exists());
        TestCase.assertFalse(c.exists());
        TestCase.assertFalse(d.exists());
        TestCase.assertTrue(d.mkdirs());
        TestCase.assertTrue(a.exists());
        TestCase.assertTrue(b.exists());
        TestCase.assertTrue(c.exists());
        TestCase.assertTrue(d.exists());
        // Test the case where the 'directory' exists as a file.
        File existsAsFile = new File(base, "existsAsFile");
        existsAsFile.createNewFile();
        TestCase.assertTrue(existsAsFile.exists());
        TestCase.assertFalse(existsAsFile.mkdirs());
        // Test the case where the parent exists as a file.
        File badParent = new File(existsAsFile, "sub");
        TestCase.assertTrue(existsAsFile.exists());
        TestCase.assertFalse(badParent.exists());
        TestCase.assertFalse(badParent.mkdirs());
    }
}

