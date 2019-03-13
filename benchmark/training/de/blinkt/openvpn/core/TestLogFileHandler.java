/**
 * Copyright (c) 2012-2017 Arne Schwabe
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */
package de.blinkt.openvpn.core;


import VpnStatus.LogLevel;
import android.annotation.SuppressLint;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import junit.framework.Assert;
import org.junit.Test;


public class TestLogFileHandler {
    byte[] testUnescaped = new byte[]{ 0, 85, -27, 0, 86, 16, -128, 85, 84 };

    byte[] expectedEscaped = new byte[]{ 85, 0, 0, 0, 9, 0, 86, 0, -27, 0, 86, 1, 16, -128, 86, 0, 84 };

    private TestLogFileHandler.TestingLogFileHandler lfh;

    @Test
    public void testWriteByteArray() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        lfh.setLogFile(byteArrayOutputStream);
        writeEscapedBytes(testUnescaped);
        byte[] result = byteArrayOutputStream.toByteArray();
        Assert.assertTrue(Arrays.equals(expectedEscaped, result));
    }

    @Test
    public void readByteArray() throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(expectedEscaped);
        lfh.readCacheContents(in);
        Assert.assertTrue(Arrays.equals(testUnescaped, lfh.mRestoredByteArray));
    }

    @Test
    public void testMarschal() throws UnsupportedEncodingException {
        LogItem li = new LogItem(LogLevel.DEBUG, 72, "foobar");
        LogItem li2 = marschalAndBack(li);
        testEquals(li, li2);
        Assert.assertEquals(li, li2);
    }

    @Test
    public void testMarschalArgs() throws UnsupportedEncodingException {
        LogItem li = new LogItem(LogLevel.DEBUG, 72, 772, "sinnloser Text", 7723, 723.2F, 7.2);
        LogItem li2 = marschalAndBack(li);
        testEquals(li, li2);
        Assert.assertEquals(li, li2);
    }

    @Test
    public void testMarschalString() throws UnsupportedEncodingException {
        LogItem li = new LogItem(LogLevel.DEBUG, "Nutzlose Nachricht");
        LogItem li2 = marschalAndBack(li);
        testEquals(li, li2);
        Assert.assertEquals(li, li2);
    }

    @SuppressLint("HandlerLeak")
    static class TestingLogFileHandler extends LogFileHandler {
        public byte[] mRestoredByteArray;

        public TestingLogFileHandler() {
            super(null);
        }

        public void setLogFile(OutputStream out) {
            mLogFile = out;
        }

        @Override
        public void readCacheContents(InputStream in) throws IOException {
            super.readCacheContents(in);
        }

        @Override
        protected void restoreLogItem(byte[] buf, int len) {
            mRestoredByteArray = Arrays.copyOf(buf, len);
        }
    }
}

