package org.apache.commons.io.input;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import org.junit.Assert;
import org.junit.Test;


public class AmplBoundedReaderTest {
    private final Reader sr = new BufferedReader(new StringReader("01234567890"));

    private final Reader shortReader = new BufferedReader(new StringReader("01"));

    @Test(timeout = 10000)
    public void readMulti_add30() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_add30__9 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(3, ((int) (o_readMulti_add30__9)));
        final int read = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (read)));
        char char_8 = cbuf[0];
        char char_9 = cbuf[1];
        char char_10 = cbuf[2];
        char char_11 = cbuf[3];
        mr.close();
        Assert.assertEquals(3, ((int) (o_readMulti_add30__9)));
        Assert.assertEquals(0, ((int) (read)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber3() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 0);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        final int read = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (read)));
        char char_44 = cbuf[0];
        char char_45 = cbuf[1];
        char char_46 = cbuf[2];
        char char_47 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (read)));
    }
}

