package com.amaze.filemanager.filesystem.ssh;


import RuntimeEnvironment.application;
import android.os.Environment;
import com.amaze.filemanager.filesystem.HybridFile;
import com.amaze.filemanager.utils.OpenMode;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class ListFilesOnSshdTest extends AbstractSftpServerTest {
    @Test
    public void testNormalListDirs() throws InterruptedException {
        for (String s : new String[]{ "sysroot", "srv", "var", "tmp", "bin", "lib", "usr" }) {
            new File(Environment.getExternalStorageDirectory(), s).mkdir();
        }
        performVerify();
    }

    @Test
    public void testListDirsAndSymlinks() throws Exception {
        File sysroot = new File(Environment.getExternalStorageDirectory(), "sysroot");
        sysroot.mkdir();
        for (String s : new String[]{ "srv", "var", "tmp" }) {
            File subdir = new File(sysroot, s);
            subdir.mkdir();
            Files.createSymbolicLink(Paths.get(new File(Environment.getExternalStorageDirectory(), s).getAbsolutePath()), Paths.get(subdir.getAbsolutePath()));
        }
        for (String s : new String[]{ "bin", "lib", "usr" }) {
            new File(Environment.getExternalStorageDirectory(), s).mkdir();
        }
        performVerify();
    }

    @Test
    public void testListDirsAndFilesAndSymlinks() throws Exception {
        File sysroot = new File(Environment.getExternalStorageDirectory(), "sysroot");
        sysroot.mkdir();
        for (String s : new String[]{ "srv", "var", "tmp" }) {
            File subdir = new File(sysroot, s);
            subdir.mkdir();
            Files.createSymbolicLink(Paths.get(new File(Environment.getExternalStorageDirectory(), s).getAbsolutePath()), Paths.get(subdir.getAbsolutePath()));
        }
        for (String s : new String[]{ "bin", "lib", "usr" }) {
            new File(Environment.getExternalStorageDirectory(), s).mkdir();
        }
        for (int i = 1; i <= 4; i++) {
            File f = new File(Environment.getExternalStorageDirectory(), (i + ".txt"));
            FileOutputStream out = new FileOutputStream(f);
            out.write(i);
            out.close();
            Files.createSymbolicLink(Paths.get(new File(Environment.getExternalStorageDirectory(), (("symlink" + i) + ".txt")).getAbsolutePath()), Paths.get(f.getAbsolutePath()));
        }
        List<String> dirs = new ArrayList<>();
        List<String> files = new ArrayList<>();
        HybridFile file = new HybridFile(OpenMode.SFTP, "ssh://testuser:testpassword@127.0.0.1:22222");
        CountDownLatch waiter = new CountDownLatch(15);
        file.forEachChildrenFile(application, false, ( fileFound) -> {
            if (!(fileFound.getName().endsWith(".txt"))) {
                assertTrue(((fileFound.getPath()) + " not seen as directory"), fileFound.isDirectory());
                dirs.add(fileFound.getName());
            } else {
                assertFalse(((fileFound.getPath()) + " not seen as file"), fileFound.isDirectory());
                files.add(fileFound.getName());
            }
            waiter.countDown();
        });
        waiter.await();
        Assert.assertEquals(7, dirs.size());
        Assert.assertThat(dirs, Matchers.hasItems("sysroot", "srv", "var", "tmp", "bin", "lib", "usr"));
        Assert.assertThat(files, Matchers.hasItems("1.txt", "2.txt", "3.txt", "4.txt", "symlink1.txt", "symlink2.txt", "symlink3.txt", "symlink4.txt"));
    }
}

