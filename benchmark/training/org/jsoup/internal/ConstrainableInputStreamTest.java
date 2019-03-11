package org.jsoup.internal;


import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.jsoup.Jsoup;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class ConstrainableInputStreamTest {
    // todo - move these all to local jetty, don't ignore
    @Test
    public void remainingAfterFirstRead() throws IOException {
        int bufferSize = 5 * 1024;
        int capSize = 100 * 1024;
        String url = "http://direct.infohound.net/tools/large.html";// 280 K

        BufferedInputStream inputStream = Jsoup.connect(url).maxBodySize(capSize).execute().bodyStream();
        Assert.assertTrue((inputStream instanceof ConstrainableInputStream));
        ConstrainableInputStream stream = ((ConstrainableInputStream) (inputStream));
        // simulates parse which does a limited read first
        stream.mark(bufferSize);
        ByteBuffer firstBytes = stream.readToByteBuffer(bufferSize);
        byte[] array = firstBytes.array();
        String firstText = new String(array, "UTF-8");
        Assert.assertTrue(firstText.startsWith("<html><head><title>Large"));
        Assert.assertEquals(bufferSize, array.length);
        boolean fullyRead = (stream.read()) == (-1);
        Assert.assertFalse(fullyRead);
        // reset and read again
        stream.reset();
        ByteBuffer fullRead = stream.readToByteBuffer(0);
        byte[] fullArray = fullRead.array();
        Assert.assertEquals(capSize, fullArray.length);
        String fullText = new String(fullArray, "UTF-8");
        Assert.assertTrue(fullText.startsWith(firstText));
    }

    @Test
    public void noLimitAfterFirstRead() throws IOException {
        int bufferSize = 5 * 1024;
        String url = "http://direct.infohound.net/tools/large.html";// 280 K

        BufferedInputStream inputStream = Jsoup.connect(url).execute().bodyStream();
        Assert.assertTrue((inputStream instanceof ConstrainableInputStream));
        ConstrainableInputStream stream = ((ConstrainableInputStream) (inputStream));
        // simulates parse which does a limited read first
        stream.mark(bufferSize);
        ByteBuffer firstBytes = stream.readToByteBuffer(bufferSize);
        byte[] array = firstBytes.array();
        String firstText = new String(array, "UTF-8");
        Assert.assertTrue(firstText.startsWith("<html><head><title>Large"));
        Assert.assertEquals(bufferSize, array.length);
        // reset and read fully
        stream.reset();
        ByteBuffer fullRead = stream.readToByteBuffer(0);
        byte[] fullArray = fullRead.array();
        Assert.assertEquals(280735, fullArray.length);
        String fullText = new String(fullArray, "UTF-8");
        Assert.assertTrue(fullText.startsWith(firstText));
    }
}

