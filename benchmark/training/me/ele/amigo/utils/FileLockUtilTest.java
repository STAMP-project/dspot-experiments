package me.ele.amigo.utils;


import FileLockUtil.ExclusiveFileLock;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


// ? ????? testHoldLock() testWaitLockToRelease()????????***????***???
public class FileLockUtilTest {
    @Test
    public void testHoldLock() throws Exception {
        String pid = FileLockUtilTest.getPid();
        File file = getTestFile();
        Assert.assertEquals(true, file.exists());
        FileLockUtil.ExclusiveFileLock lock = FileLockUtil.getFileLock(file);
        lock.lock();
        System.out.printf("got file lock...\n");
        byte[] data = lock.readFully();
        int count = holdCount;
        while ((count--) > 0) {
            System.out.printf("process %s is holding the file lock\n", pid);
            System.out.flush();
            Thread.sleep(1000);
        } 
        lock.release();
        Assert.assertEquals(file.length(), data.length);
    }

    final int waitCount = 9;

    final int holdCount = 5;

    @Test
    public void testWaitLockToRelease() throws Exception {
        File file = getTestFile();
        Assert.assertEquals(true, file.exists());
        FileLockUtil.ExclusiveFileLock lock = FileLockUtil.getFileLock(file);
        System.out.println(("running in process " + (FileLockUtilTest.getPid())));
        int count = waitCount;
        while ((count--) > 0) {
            try {
                if (lock.tryLock()) {
                    System.out.printf("got file lock...\n");
                    break;
                }
                System.out.printf("file lock is hold by another process, try again later...\n");
                System.out.flush();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.printf("unable to get file lock, try again later...\n");
                System.out.flush();
                if (count == 0) {
                    throw new IOException("failed to lock file after tried 80 times");
                }
            }
            Thread.sleep(1000);
        } 
        byte[] data = lock.readFully();
        lock.release();
        Assert.assertEquals(file.length(), data.length);
    }
}

