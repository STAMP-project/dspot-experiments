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
        char char_112 = cbuf[0];
        char char_113 = cbuf[1];
        char char_114 = cbuf[2];
        char char_115 = cbuf[3];
        mr.close();
        Assert.assertEquals(3, ((int) (o_readMulti_add30__9)));
        Assert.assertEquals(0, ((int) (read)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber1_add1687_add4540() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 4);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber1_add1687__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber1_add1687_add4540__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber1_add1687_add4540__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_96 = cbuf[0];
        char char_97 = cbuf[1];
        char char_98 = cbuf[2];
        char char_99 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber1_add1687_add4540__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationChar15_add1681_add4525() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = '\u0000';
        }
        int o_readMulti_literalMutationChar15_add1681__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationChar15_add1681_add4525__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar15_add1681_add4525__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_84 = cbuf[0];
        char char_85 = cbuf[1];
        char char_86 = cbuf[2];
        char char_87 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar15_add1681_add4525__12)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber11_add1700_add4537() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 1; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber11_add1700__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber11_add1700_add4537__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber11_add1700_add4537__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_120 = cbuf[0];
        char char_121 = cbuf[1];
        char char_122 = cbuf[2];
        char char_123 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber11_add1700_add4537__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_add30_add1697() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_add30__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_add30_add1697__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1697__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_112 = cbuf[0];
        char char_113 = cbuf[1];
        char char_114 = cbuf[2];
        char char_115 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1697__12)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber3_add1665() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 0);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber3_add1665__10 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber3_add1665__10)));
        final int read = mr.read(cbuf, 0, 4);
        char char_44 = cbuf[0];
        char char_45 = cbuf[1];
        char char_46 = cbuf[2];
        char char_47 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber3_add1665__10)));
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

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber2_add1651_add4549() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 2);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber2_add1651__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber2_add1651_add4549__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber2_add1651_add4549__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_16 = cbuf[0];
        char char_17 = cbuf[1];
        char char_18 = cbuf[2];
        char char_19 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber2_add1651_add4549__13)));
    }
}

