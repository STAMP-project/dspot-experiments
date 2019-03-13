package org.jak_linux.dns66;


import Configuration.Item;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructPollfd;
import android.util.Log;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


/**
 * Created by jak on 07/04/17.
 */
@RunWith(PowerMockRunner.class)
public class FileHelperTest {
    Context mockContext;

    AssetManager mockAssets;

    int testResult;

    @Test
    @PrepareForTest({ Environment.class })
    public void testGetItemFile() throws Exception {
        File file = new File("/dir/");
        when(mockContext.getExternalFilesDir(null)).thenReturn(file);
        Configuration.Item item = new Configuration.Item();
        item.location = "http://example.com/";
        Assert.assertEquals(new File("/dir/http%3A%2F%2Fexample.com%2F"), FileHelper.getItemFile(mockContext, item));
        item.location = "https://example.com/";
        Assert.assertEquals(new File("/dir/https%3A%2F%2Fexample.com%2F"), FileHelper.getItemFile(mockContext, item));
        item.location = "file:/myfile";
        Assert.assertNull(FileHelper.getItemFile(mockContext, item));
        mockStatic(Environment.class);
        when(Environment.getExternalStorageDirectory()).thenReturn(new File("/sdcard/"));
        item.location = "file:myfile";
        Assert.assertNull(null, FileHelper.getItemFile(mockContext, item));
        item.location = "ahost.com";
        Assert.assertNull(FileHelper.getItemFile(mockContext, item));
    }

    @Test
    public void testOpenItemFile() throws Exception {
        Configuration.Item item = new Configuration.Item();
        // Test encoding fails
        item.location = "hexample.com";
        Assert.assertNull(FileHelper.openItemFile(mockContext, item));
    }

    @Test
    public void testOpenRead_existingFile() throws Exception {
        FileInputStream stream = mock(FileInputStream.class);
        when(mockContext.openFileInput(ArgumentMatchers.anyString())).thenReturn(stream);
        when(mockAssets.open(ArgumentMatchers.anyString())).thenThrow(new IOException());
        Assert.assertSame(stream, FileHelper.openRead(mockContext, "file"));
    }

    @Test
    public void testOpenRead_fallbackToAsset() throws Exception {
        FileInputStream stream = mock(FileInputStream.class);
        when(mockContext.openFileInput(ArgumentMatchers.anyString())).thenThrow(new FileNotFoundException("Test"));
        when(mockAssets.open(ArgumentMatchers.anyString())).thenReturn(stream);
        Assert.assertSame(stream, FileHelper.openRead(mockContext, "file"));
    }

    @Test
    public void testOpenWrite() throws Exception {
        File file = mock(File.class);
        File file2 = mock(File.class);
        FileOutputStream fos = mock(FileOutputStream.class);
        when(mockContext.getFileStreamPath(ArgumentMatchers.eq("filename"))).thenReturn(file);
        when(mockContext.getFileStreamPath(ArgumentMatchers.eq("filename.bak"))).thenReturn(file2);
        when(mockContext.openFileOutput(ArgumentMatchers.eq("filename"), ArgumentMatchers.anyInt())).thenReturn(fos);
        Assert.assertSame(fos, FileHelper.openWrite(mockContext, "filename"));
        Mockito.verify(file).renameTo(file2);
        Mockito.verify(mockContext).openFileOutput(ArgumentMatchers.eq("filename"), ArgumentMatchers.anyInt());
    }

    @Test
    @PrepareForTest({ Configuration.class })
    public void testLoadDefaultSettings() throws Exception {
        InputStream mockInStream = mock(InputStream.class);
        Configuration mockConfig = mock(Configuration.class);
        when(mockAssets.open(ArgumentMatchers.anyString())).thenReturn(mockInStream);
        when(mockContext.getAssets()).thenReturn(mockAssets);
        mockStatic(Configuration.class);
        doReturn(mockConfig).when(Configuration.class, "read", ArgumentMatchers.any(Reader.class));
        Assert.assertSame(mockConfig, FileHelper.loadDefaultSettings(mockContext));
        Mockito.verify(mockAssets.open(ArgumentMatchers.anyString()));
    }

    @Test
    @PrepareForTest({ Log.class, Os.class })
    public void testPoll_retryInterrupt() throws Exception {
        mockStatic(Log.class);
        mockStatic(Os.class);
        when(Os.poll(ArgumentMatchers.any(StructPollfd[].class), ArgumentMatchers.anyInt())).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                // First try fails with EINTR, seconds returns success.
                if (((testResult)++) == 0) {
                    // Android actually sets all OsConstants to 0 when running the
                    // unit tests, so this works, but another constant would have
                    // exactly the same result.
                    throw new ErrnoException("poll", OsConstants.EINTR);
                }
                return 0;
            }
        });
        // poll() will be interrupted first time, so called a second time.
        Assert.assertEquals(0, FileHelper.poll(null, 0));
        Assert.assertEquals(2, testResult);
    }

    @Test
    public void testPoll_interrupted() throws Exception {
        Thread.currentThread().interrupt();
        try {
            FileHelper.poll(null, 0);
            Assert.fail("Did not interrupt");
        } catch (InterruptedException e) {
        }
    }

    @Test
    @PrepareForTest({ Log.class, Os.class })
    public void testPoll_fault() throws Exception {
        mockStatic(Log.class);
        mockStatic(Os.class);
        // Eww, Android is playing dirty and setting all errno values to 0.
        // Hack around it so we can test that aborting the loop works.
        final ErrnoException e = new ErrnoException("foo", 42);
        e.getClass().getDeclaredField("errno").setInt(e, 42);
        when(Os.poll(ArgumentMatchers.any(StructPollfd[].class), ArgumentMatchers.anyInt())).then(new Answer<Integer>() {
            @Override
            public Integer answer(InvocationOnMock invocation) throws Throwable {
                (testResult)++;
                throw e;
            }
        });
        try {
            FileHelper.poll(null, 0);
            Assert.fail("Did not throw");
        } catch (ErrnoException e1) {
            Assert.assertEquals(42, e1.errno);
            Assert.assertSame(e, e1);
        }
        Assert.assertEquals(1, testResult);
    }

    @Test
    @PrepareForTest({ Log.class, Os.class })
    public void testPoll_success() throws Exception {
        mockStatic(Log.class);
        mockStatic(Os.class);
        when(Os.poll(ArgumentMatchers.any(StructPollfd[].class), ArgumentMatchers.anyInt())).then(new FileHelperTest.CountingAnswer(42));
        Assert.assertEquals(42, FileHelper.poll(null, 0));
        Assert.assertEquals(1, testResult);
    }

    @Test
    @PrepareForTest({ Log.class, Os.class })
    public void testCloseOrWarn_fileDescriptor() throws Exception {
        FileDescriptor fd = mock(FileDescriptor.class);
        mockStatic(Log.class);
        mockStatic(Os.class);
        when(Log.e(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Throwable.class))).then(new FileHelperTest.CountingAnswer(null));
        // Closing null should work just fine
        testResult = 0;
        Assert.assertNull(FileHelper.closeOrWarn(((FileDescriptor) (null)), "tag", "msg"));
        Assert.assertEquals(0, testResult);
        // Successfully closing the file should not log.
        testResult = 0;
        Assert.assertNull(FileHelper.closeOrWarn(fd, "tag", "msg"));
        Assert.assertEquals(0, testResult);
        // If closing fails, it should log.
        testResult = 0;
        doThrow(new ErrnoException("close", 0)).when(Os.class, "close", ArgumentMatchers.any(FileDescriptor.class));
        Assert.assertNull(FileHelper.closeOrWarn(fd, "tag", "msg"));
        Assert.assertEquals(1, testResult);
    }

    @Test
    @PrepareForTest(Log.class)
    public void testCloseOrWarn_closeable() throws Exception {
        Closeable closeable = mock(Closeable.class);
        mockStatic(Log.class);
        when(Log.e(ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.any(Throwable.class))).then(new FileHelperTest.CountingAnswer(null));
        // Closing null should work just fine
        testResult = 0;
        Assert.assertNull(FileHelper.closeOrWarn(((Closeable) (null)), "tag", "msg"));
        Assert.assertEquals(0, testResult);
        // Successfully closing the file should not log.
        testResult = 0;
        Assert.assertNull(FileHelper.closeOrWarn(closeable, "tag", "msg"));
        Assert.assertEquals(0, testResult);
        // If closing fails, it should log.
        when(closeable).thenThrow(new IOException("Foobar"));
        testResult = 0;
        Assert.assertNull(FileHelper.closeOrWarn(closeable, "tag", "msg"));
        Assert.assertEquals(1, testResult);
    }

    private class CountingAnswer implements Answer<Object> {
        private final Object result;

        public CountingAnswer(Object result) {
            this.result = result;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            (testResult)++;
            return result;
        }
    }
}

