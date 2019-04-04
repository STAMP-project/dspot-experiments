package org.apache.commons.io.input;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class AmplUnixLineEndingInputStreamTest {
    @Test(timeout = 10000)
    public void malformed() throws Exception {
        String o_malformed__1 = roundtrip("a\rbc", false);
        Assert.assertEquals("abc", o_malformed__1);
    }

    private String roundtrip(String msg) throws IOException {
        return roundtrip(msg, true);
    }

    private String roundtrip(String msg, boolean ensure) throws IOException {
        ByteArrayInputStream baos = new ByteArrayInputStream(msg.getBytes("UTF-8"));
        UnixLineEndingInputStream lf = new UnixLineEndingInputStream(baos, ensure);
        byte[] buf = new byte[100];
        final int read = lf.read(buf);
        lf.close();
        return new String(buf, 0, read, "UTF-8");
    }
}

