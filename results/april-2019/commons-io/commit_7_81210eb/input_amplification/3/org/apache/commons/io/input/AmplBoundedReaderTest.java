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
    public void readMulti_literalMutationNumber24_add1681_add7270() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber24_add1681__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber24_add1681_add7270__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber24_add1681_add7270__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_80 = cbuf[0];
        char char_81 = cbuf[1];
        char char_82 = cbuf[2];
        char char_83 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber24_add1681_add7270__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber7_add1677_add7308() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[8];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber7_add1677__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber7_add1677_add7308__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber7_add1677_add7308__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_72 = cbuf[0];
        char char_73 = cbuf[1];
        char char_74 = cbuf[2];
        char char_75 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber7_add1677_add7308__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationChar20_add1673_add7300() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = '\n';
        }
        int o_readMulti_literalMutationChar20_add1673__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationChar20_add1673_add7300__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar20_add1673_add7300__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_60 = cbuf[0];
        char char_61 = cbuf[1];
        char char_62 = cbuf[2];
        char char_63 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar20_add1673_add7300__12)));
    }

    @Test(timeout = 10000)
    public void readMulti_add30_add1651() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_add30__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_add30_add1651__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1651__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_8 = cbuf[0];
        char char_9 = cbuf[1];
        char char_10 = cbuf[2];
        char char_11 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1651__12)));
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
        char char_52 = cbuf[0];
        char char_53 = cbuf[1];
        char char_54 = cbuf[2];
        char char_55 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (read)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber23_add1655_add7311() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber23_add1655__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber23_add1655_add7311__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber23_add1655_add7311__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_20 = cbuf[0];
        char char_21 = cbuf[1];
        char char_22 = cbuf[2];
        char char_23 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber23_add1655_add7311__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber3_add1671() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 0);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber3_add1671__10 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber3_add1671__10)));
        final int read = mr.read(cbuf, 0, 4);
        char char_52 = cbuf[0];
        char char_53 = cbuf[1];
        char char_54 = cbuf[2];
        char char_55 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber3_add1671__10)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber2_add1657_add7297() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 2);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber2_add1657__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber2_add1657_add7297__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber2_add1657_add7297__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_24 = cbuf[0];
        char char_25 = cbuf[1];
        char char_26 = cbuf[2];
        char char_27 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber2_add1657_add7297__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber13_add1675_add7294() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber13_add1675__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber13_add1675_add7294__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber13_add1675_add7294__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_68 = cbuf[0];
        char char_69 = cbuf[1];
        char char_70 = cbuf[2];
        char char_71 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber13_add1675_add7294__13)));
    }
}

