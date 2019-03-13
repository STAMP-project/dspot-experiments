package org.zeromq.util;


import ZMQ.CHARSET;
import java.util.Arrays;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ZDataTest {
    @Test
    public void testPrint() {
        byte[] buf = new byte[10];
        Arrays.fill(buf, ((byte) (170)));
        ZData data = new ZData(buf);
        data.print(System.out, "ZData: ");
    }

    @Test
    public void testPrintNonPrintable() {
        byte[] buf = new byte[12];
        Arrays.fill(buf, ((byte) (4)));
        ZData data = new ZData(buf);
        data.print(System.out, "ZData: ");
    }

    @Test
    public void testToString() {
        ZData data = new ZData("test".getBytes(CHARSET));
        String string = data.toString();
        Assert.assertThat(string, CoreMatchers.is("test"));
    }

    @Test
    public void testToStringNonPrintable() {
        byte[] buf = new byte[2];
        Arrays.fill(buf, ((byte) (4)));
        ZData data = new ZData(buf);
        String string = data.toString();
        Assert.assertThat(string, CoreMatchers.is("0404"));
    }

    @Test
    public void testStreq() {
        ZData data = new ZData("test".getBytes(CHARSET));
        Assert.assertThat(data.streq("test"), CoreMatchers.is(true));
    }

    @Test
    public void testEquals() {
        ZData data = new ZData("test".getBytes(CHARSET));
        ZData other = new ZData("test".getBytes(CHARSET));
        Assert.assertThat(data.equals(other), CoreMatchers.is(true));
    }
}

