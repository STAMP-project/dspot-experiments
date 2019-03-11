package org.jak_linux.dns66.db;


import Configuration.Item;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.jak_linux.dns66.Configuration;
import org.jak_linux.dns66.SingleWriterMultipleReaderFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ Log.class })
public class RuleDatabaseItemUpdateRunnableTest {
    private Context mockContext;

    private File file;

    private SingleWriterMultipleReaderFile singleWriterMultipleReaderFile;

    private HttpURLConnection connection;

    private RuleDatabaseItemUpdateRunnableTest.CountingAnswer finishAnswer;

    private RuleDatabaseItemUpdateRunnableTest.CountingAnswer failAnswer;

    private URL url;

    private RuleDatabaseUpdateTask realTask;

    private RuleDatabaseUpdateTask mockTask;

    private ContentResolver mockResolver;

    @Test
    public void testRun() throws Exception {
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer downloadCount = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(null);
        when(mockTask.doInBackground()).thenCallRealMethod();
        mockTask.context = mockContext;
        mockTask.errors = new ArrayList();
        mockTask.done = new ArrayList();
        mockTask.pending = new ArrayList();
        mockTask.configuration = new Configuration();
        mockTask.configuration.hosts = new Configuration.Hosts();
        mockTask.configuration.hosts.items.add(new Configuration.Item());
        mockTask.configuration.hosts.items.add(new Configuration.Item());
        mockTask.configuration.hosts.items.add(new Configuration.Item());
        mockTask.configuration.hosts.items.add(new Configuration.Item());
        mockTask.configuration.hosts.items.get(0).title = "http-title";
        mockTask.configuration.hosts.items.get(0).state = Item.STATE_DENY;
        mockTask.configuration.hosts.items.get(0).location = "http://foo";
        when(mockTask, "addError", ArgumentMatchers.any(Item.class), ArgumentMatchers.anyString()).thenCallRealMethod();
        when(mockTask, "addDone", ArgumentMatchers.any(Item.class)).thenCallRealMethod();
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = mock(RuleDatabaseItemUpdateRunnable.class);
        itemUpdateRunnable.parentTask = mockTask;
        itemUpdateRunnable.context = mockContext;
        itemUpdateRunnable.item = mockTask.configuration.hosts.items.get(0);
        when(itemUpdateRunnable, "run").thenCallRealMethod();
        when(itemUpdateRunnable, "shouldDownload").thenCallRealMethod();
        when(mockTask.getCommand(ArgumentMatchers.any(Item.class))).thenReturn(itemUpdateRunnable);
        when(itemUpdateRunnable, "downloadFile", ArgumentMatchers.any(File.class), ArgumentMatchers.any(SingleWriterMultipleReaderFile.class), ArgumentMatchers.any(HttpURLConnection.class)).then(downloadCount);
        // Scenario 1: Validate response fails
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        itemUpdateRunnable.run();
        Assert.assertEquals(0, downloadCount.numCalls);
        Assert.assertEquals(1, mockTask.done.size());
        // Scenario 2: Validate response succeeds
        when(itemUpdateRunnable.validateResponse(ArgumentMatchers.any(HttpURLConnection.class))).thenReturn(true);
        when(itemUpdateRunnable.getHttpURLConnection(ArgumentMatchers.any(File.class), ArgumentMatchers.any(SingleWriterMultipleReaderFile.class), ArgumentMatchers.any(URL.class))).thenCallRealMethod();
        when(itemUpdateRunnable.internalOpenHttpConnection(ArgumentMatchers.any(URL.class))).thenReturn(connection);
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        itemUpdateRunnable.run();
        Assert.assertEquals(1, downloadCount.numCalls);
        Assert.assertEquals(2, mockTask.done.size());
        // Scenario 3: Download file throws an exception
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer downloadExceptionCount = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(null) {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                super.answer(invocation);
                throw new IOException("FooBarException");
            }
        };
        when(itemUpdateRunnable, "downloadFile", ArgumentMatchers.any(File.class), ArgumentMatchers.any(SingleWriterMultipleReaderFile.class), ArgumentMatchers.any(HttpURLConnection.class)).then(downloadExceptionCount);
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        itemUpdateRunnable.run();
        Assert.assertEquals(1, downloadExceptionCount.numCalls);
        Assert.assertEquals(1, mockTask.errors.size());
        Assert.assertEquals(3, mockTask.done.size());
        Assert.assertTrue(("http-title in" + (mockTask.errors.get(0))), mockTask.errors.get(0).matches(".*http-title.*"));
    }

    @Test
    public void testRun_content() throws Exception {
        Configuration.Item item = new Configuration.Item();
        item.location = "content://foo";
        item.title = "content-uri";
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = mock(RuleDatabaseItemUpdateRunnable.class);
        itemUpdateRunnable.parentTask = realTask;
        itemUpdateRunnable.context = mockContext;
        itemUpdateRunnable.item = item;
        when(itemUpdateRunnable, "run").thenCallRealMethod();
        when(itemUpdateRunnable, "shouldDownload").thenCallRealMethod();
        when(itemUpdateRunnable.parseUri(ArgumentMatchers.anyString())).thenReturn(mock(Uri.class));
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer downloadCount = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(null);
        when(itemUpdateRunnable, "downloadFile", ArgumentMatchers.any(File.class), ArgumentMatchers.any(SingleWriterMultipleReaderFile.class), ArgumentMatchers.any(HttpURLConnection.class)).then(downloadCount);
        when(mockResolver.openInputStream(ArgumentMatchers.any(Uri.class))).thenReturn(mock(InputStream.class));
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        itemUpdateRunnable.run();
        Assert.assertEquals(0, downloadCount.numCalls);
        Assert.assertEquals(0, realTask.errors.size());
        Assert.assertEquals(0, realTask.done.size());
        Assert.assertEquals(0, realTask.pending.size());
        when(mockResolver, "takePersistableUriPermission", ArgumentMatchers.any(Uri.class), ArgumentMatchers.anyInt()).thenThrow(new SecurityException("FooBar"));
        itemUpdateRunnable.run();
        Assert.assertEquals(0, downloadCount.numCalls);
        Assert.assertEquals(1, realTask.errors.size());
        Assert.assertEquals(0, realTask.done.size());
        Assert.assertEquals(0, realTask.pending.size());
    }

    @Test
    public void testShouldDownload() throws Exception {
        Configuration.Item item = new Configuration.Item();
        item.state = Item.STATE_DENY;
        item.location = "example.com";
        item.title = "host-uri";
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(mockTask, mockContext, item);
        // File should not be downloaded
        Assert.assertFalse(itemUpdateRunnable.shouldDownload());
        // Content URI should
        item.location = "content://foo";
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        // Do not download ignored URIs
        item.state = Item.STATE_IGNORE;
        Assert.assertFalse(itemUpdateRunnable.shouldDownload());
        item.state = Item.STATE_DENY;
        item.location = "https://foo";
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
        item.location = "http://foo";
        Assert.assertTrue(itemUpdateRunnable.shouldDownload());
    }

    @Test
    public void testValidateResponse() throws Exception {
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(realTask, mockContext, mock(Item.class));
        Resources resources = mock(Resources.class);
        when(mockContext.getResources()).thenReturn(resources);
        when(resources.getString(ArgumentMatchers.anyInt(), ArgumentMatchers.anyString(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyString())).thenReturn("%s %s %s");
        when(connection.getResponseCode()).thenReturn(200);
        Assert.assertTrue("200 is OK", itemUpdateRunnable.validateResponse(connection));
        Assert.assertEquals(0, realTask.errors.size());
        when(connection.getResponseCode()).thenReturn(404);
        Assert.assertFalse("404 is not OK", itemUpdateRunnable.validateResponse(connection));
        Assert.assertEquals(1, realTask.errors.size());
        when(connection.getResponseCode()).thenReturn(304);
        Assert.assertFalse("304 is not OK", itemUpdateRunnable.validateResponse(connection));
        Assert.assertEquals(1, realTask.errors.size());
    }

    @Test
    public void testDownloadFile() throws Exception {
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(realTask, mockContext, mock(Item.class));
        byte[] bar = new byte[]{ 'h', 'a', 'l', 'l', 'o' };
        final ByteArrayInputStream bis = new ByteArrayInputStream(bar);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        FileOutputStream fos = mock(FileOutputStream.class);
        when(fos, "write", ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt()).then(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                byte[] buffer = getArgumentAt(0, byte[].class);
                int off = invocation.getArgumentAt(1, Integer.class);
                int len = invocation.getArgumentAt(2, Integer.class);
                bos.write(buffer, off, len);
                return null;
            }
        });
        when(connection.getInputStream()).thenReturn(bis);
        when(singleWriterMultipleReaderFile.startWrite()).thenReturn(fos);
        itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
        Assert.assertArrayEquals(bar, bos.toByteArray());
        Assert.assertEquals(0, failAnswer.numCalls);
        Assert.assertEquals(1, finishAnswer.numCalls);
        bis.reset();
        bos.reset();
    }

    @Test
    public void testDownloadFile_exception() throws Exception {
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(realTask, mockContext, mock(Item.class));
        FileOutputStream fos = mock(FileOutputStream.class);
        InputStream is = mock(InputStream.class);
        when(connection.getInputStream()).thenReturn(is);
        when(singleWriterMultipleReaderFile.startWrite()).thenReturn(fos);
        doThrow(new IOException("foobar")).when(fos, "write", ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        try {
            itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
            Assert.fail("Should have thrown exception");
        } catch (IOException e) {
            Assert.assertEquals("foobar", e.getMessage());
            Assert.assertEquals(1, failAnswer.numCalls);
            Assert.assertEquals(0, finishAnswer.numCalls);
        }
    }

    @Test
    public void testDownloadFile_lastModifiedFail() throws Exception {
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer debugAnswer = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(null);
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer setLastModifiedAnswerTrue = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(true);
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer setLastModifiedAnswerFalse = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(false);
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(realTask, mockContext, mock(Item.class));
        FileOutputStream fos = mock(FileOutputStream.class);
        InputStream is = mock(InputStream.class);
        when(connection.getInputStream()).thenReturn(is);
        when(singleWriterMultipleReaderFile.startWrite()).thenReturn(fos);
        when(is.read(ArgumentMatchers.any(byte[].class))).thenReturn((-1));
        when(Log.d(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).then(debugAnswer);
        // Scenario 0: Connection has no last modified & we cannot set (0, 0)
        when(connection.getLastModified()).thenReturn(0L);
        when(file.setLastModified(ArgumentMatchers.anyLong())).then(setLastModifiedAnswerFalse);
        itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
        Assert.assertEquals(0, failAnswer.numCalls);
        Assert.assertEquals(1, debugAnswer.numCalls);
        Assert.assertEquals(1, finishAnswer.numCalls);
        // Scenario 1: Connect has no last modified & we can set (0, 1);
        when(connection.getLastModified()).thenReturn(0L);
        when(file.setLastModified(ArgumentMatchers.anyLong())).then(setLastModifiedAnswerTrue);
        itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
        Assert.assertEquals(0, failAnswer.numCalls);
        Assert.assertEquals(2, debugAnswer.numCalls);
        Assert.assertEquals(2, finishAnswer.numCalls);
        // Scenario 2: Connect has last modified & we cannot set (1, 0);
        when(connection.getLastModified()).thenReturn(1L);
        when(file.setLastModified(ArgumentMatchers.anyLong())).then(setLastModifiedAnswerFalse);
        itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
        Assert.assertEquals(0, failAnswer.numCalls);
        Assert.assertEquals(3, debugAnswer.numCalls);
        Assert.assertEquals(3, finishAnswer.numCalls);
        // Scenario 4: Connect has last modified & we cannot set (1, 1);
        when(connection.getLastModified()).thenReturn(1L);
        when(file.setLastModified(ArgumentMatchers.anyLong())).then(setLastModifiedAnswerTrue);
        itemUpdateRunnable.downloadFile(file, singleWriterMultipleReaderFile, connection);
        Assert.assertEquals(0, failAnswer.numCalls);
        Assert.assertEquals(3, debugAnswer.numCalls);// as before

        Assert.assertEquals(4, finishAnswer.numCalls);
    }

    @Test
    public void testCopyStream() throws Exception {
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = new RuleDatabaseItemUpdateRunnable(realTask, mockContext, mock(Item.class));
        byte[] bar = new byte[]{ 'h', 'a', 'l', 'l', 'o' };
        ByteArrayInputStream bis = new ByteArrayInputStream(bar);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        itemUpdateRunnable.copyStream(bis, bos);
        Assert.assertArrayEquals(bar, bos.toByteArray());
    }

    @Test
    @PrepareForTest({ Log.class })
    public void testGetHttpURLConnection() throws Exception {
        RuleDatabaseItemUpdateRunnable itemUpdateRunnable = mock(RuleDatabaseItemUpdateRunnable.class);
        when(itemUpdateRunnable.getHttpURLConnection(ArgumentMatchers.any(File.class), ArgumentMatchers.any(SingleWriterMultipleReaderFile.class), ArgumentMatchers.any(URL.class))).thenCallRealMethod();
        when(itemUpdateRunnable.internalOpenHttpConnection(ArgumentMatchers.any(URL.class))).thenReturn(connection);
        when(singleWriterMultipleReaderFile.openRead()).thenReturn(mock(FileInputStream.class));
        Assert.assertSame(connection, itemUpdateRunnable.getHttpURLConnection(file, singleWriterMultipleReaderFile, url));
        // Setting modified.
        RuleDatabaseItemUpdateRunnableTest.CountingAnswer setIfModifiedAnswer = new RuleDatabaseItemUpdateRunnableTest.CountingAnswer(null);
        when(file.lastModified()).thenReturn(42L);
        when(connection, "setIfModifiedSince", ArgumentMatchers.eq(42L)).then(setIfModifiedAnswer);
        Assert.assertSame(connection, itemUpdateRunnable.getHttpURLConnection(file, singleWriterMultipleReaderFile, url));
        Assert.assertEquals(1, setIfModifiedAnswer.numCalls);
        // If we do not have a last modified value, do not set if-modified-since.
        setIfModifiedAnswer.numCalls = 0;
        when(file.lastModified()).thenReturn(0L);
        Assert.assertSame(connection, itemUpdateRunnable.getHttpURLConnection(file, singleWriterMultipleReaderFile, url));
        Assert.assertEquals(0, setIfModifiedAnswer.numCalls);
    }

    private class CountingAnswer implements Answer<Object> {
        private final Object result;

        private int numCalls;

        CountingAnswer(Object result) {
            this.result = result;
            this.numCalls = 0;
        }

        @Override
        public Object answer(InvocationOnMock invocation) throws Throwable {
            (numCalls)++;
            return result;
        }
    }
}

