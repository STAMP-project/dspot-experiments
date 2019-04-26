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
    public void readMulti_literalMutationNumber24_add1675_add6946() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber24_add1675__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber24_add1675_add6946__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber24_add1675_add6946__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_68 = cbuf[0];
        char char_69 = cbuf[1];
        char char_70 = cbuf[2];
        char char_71 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber24_add1675_add6946__13)));
    }

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
        char char_124 = cbuf[0];
        char char_125 = cbuf[1];
        char char_126 = cbuf[2];
        char char_127 = cbuf[3];
        mr.close();
        Assert.assertEquals(3, ((int) (o_readMulti_add30__9)));
        Assert.assertEquals(0, ((int) (read)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber4_add1685_add6986() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 1);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber4_add1685__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber4_add1685_add6986__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber4_add1685_add6986__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_88 = cbuf[0];
        char char_89 = cbuf[1];
        char char_90 = cbuf[2];
        char char_91 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber4_add1685_add6986__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber1_add1687_add6972() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 4);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber1_add1687__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber1_add1687_add6972__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber1_add1687_add6972__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_92 = cbuf[0];
        char char_93 = cbuf[1];
        char char_94 = cbuf[2];
        char char_95 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber1_add1687_add6972__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber7_add1671_add7020() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[8];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber7_add1671__10 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationNumber7_add1671_add7020__13 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber7_add1671_add7020__13)));
        final int read = mr.read(cbuf, 0, 4);
        char char_60 = cbuf[0];
        char char_61 = cbuf[1];
        char char_62 = cbuf[2];
        char char_63 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber7_add1671_add7020__13)));
    }

    @Test(timeout = 10000)
    public void readMulti_add30_add1699() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_add30__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_add30_add1699__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1699__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_124 = cbuf[0];
        char char_125 = cbuf[1];
        char char_126 = cbuf[2];
        char char_127 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_add30_add1699__12)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationNumber21_add1661_add6989() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'X';
        }
        int o_readMulti_literalMutationNumber21_add1661__9 = mr.read(cbuf, 1, 4);
        int o_readMulti_literalMutationNumber21_add1661_add6989__13 = mr.read(cbuf, 1, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber21_add1661_add6989__13)));
        final int read = mr.read(cbuf, 1, 4);
        char char_32 = cbuf[0];
        char char_33 = cbuf[1];
        char char_34 = cbuf[2];
        char char_35 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber21_add1661_add6989__13)));
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
        char char_40 = cbuf[0];
        char char_41 = cbuf[1];
        char char_42 = cbuf[2];
        char char_43 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationNumber3_add1665__10)));
    }

    @Test(timeout = 10000)
    public void readMulti_literalMutationChar19_add1691_add6969() throws IOException {
        BoundedReader mr = new BoundedReader(sr, 3);
        char[] cbuf = new char[4];
        for (int i = 0; i < (cbuf.length); i++) {
            cbuf[i] = 'W';
        }
        int o_readMulti_literalMutationChar19_add1691__9 = mr.read(cbuf, 0, 4);
        int o_readMulti_literalMutationChar19_add1691_add6969__12 = mr.read(cbuf, 0, 4);
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar19_add1691_add6969__12)));
        final int read = mr.read(cbuf, 0, 4);
        char char_100 = cbuf[0];
        char char_101 = cbuf[1];
        char char_102 = cbuf[2];
        char char_103 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (o_readMulti_literalMutationChar19_add1691_add6969__12)));
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
        char char_40 = cbuf[0];
        char char_41 = cbuf[1];
        char char_42 = cbuf[2];
        char char_43 = cbuf[3];
        mr.close();
        Assert.assertEquals(0, ((int) (read)));
    }
}

