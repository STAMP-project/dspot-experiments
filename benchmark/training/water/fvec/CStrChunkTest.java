package water.fvec;


import Vec.Writer;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import water.IcedUtils;
import water.TestUtil;
import water.parser.BufferedString;

import static Vec.T_STR;


public class CStrChunkTest extends TestUtil {
    @Test
    public void test_addStr() {
        for (int l = 0; l < 2; ++l) {
            NewChunk nc = new NewChunk(null, 0);
            BufferedString[] vals = new BufferedString[1000001];
            for (int i = 0; i < (vals.length); i++) {
                vals[i] = new BufferedString(("Foo" + i));
            }
            if (l == 1)
                nc.addNA();

            for (BufferedString v : vals)
                nc.addStr(v);

            nc.addNA();
            Chunk cc = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc._len);
            Assert.assertTrue((cc instanceof CStrChunk));
            if (l == 1)
                Assert.assertTrue(cc.isNA(0));

            if (l == 1)
                Assert.assertTrue(cc.isNA_abs(0));

            BufferedString tmpStr = new BufferedString();
            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.atStr(tmpStr, (l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc.atStr_abs(tmpStr, (l + i)));

            Assert.assertTrue(cc.isNA(((vals.length) + l)));
            Assert.assertTrue(cc.isNA_abs(((vals.length) + l)));
            Chunk cc2 = IcedUtils.deepCopy(cc);
            Assert.assertEquals((((vals.length) + 1) + l), cc2._len);
            Assert.assertTrue((cc2 instanceof CStrChunk));
            if (l == 1)
                Assert.assertTrue(cc2.isNA(0));

            if (l == 1)
                Assert.assertTrue(cc2.isNA_abs(0));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.atStr(tmpStr, (l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.atStr_abs(tmpStr, (l + i)));

            Assert.assertTrue(cc2.isNA(((vals.length) + l)));
            Assert.assertTrue(cc2.isNA_abs(((vals.length) + l)));
            nc = cc.extractRows(new NewChunk(null, 0), 0, nc.len());
            Assert.assertEquals((((vals.length) + 1) + l), nc.len());
            if (l == 1)
                Assert.assertTrue(nc.isNA(0));

            if (l == 1)
                Assert.assertTrue(nc.isNA_abs(0));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.atStr(tmpStr, (l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], nc.atStr_abs(tmpStr, (l + i)));

            Assert.assertTrue(nc.isNA(((vals.length) + l)));
            Assert.assertTrue(nc.isNA_abs(((vals.length) + l)));
            cc2 = nc.compress();
            Assert.assertEquals((((vals.length) + 1) + l), cc._len);
            Assert.assertTrue((cc2 instanceof CStrChunk));
            if (l == 1)
                Assert.assertTrue(cc2.isNA(0));

            if (l == 1)
                Assert.assertTrue(cc2.isNA_abs(0));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.atStr(tmpStr, (l + i)));

            for (int i = 0; i < (vals.length); ++i)
                Assert.assertEquals(vals[i], cc2.atStr_abs(tmpStr, (l + i)));

            Assert.assertTrue(cc2.isNA(((vals.length) + l)));
            Assert.assertTrue(cc2.isNA_abs(((vals.length) + l)));
            Assert.assertTrue(Arrays.equals(cc._mem, cc2._mem));
        }
    }

    @Test
    public void test_writer() {
        Frame frame = null;
        try {
            frame = TestUtil.parse_test_file("smalldata/junit/iris.csv");
            // Create a label vector
            byte[] typeArr = new byte[]{ T_STR };
            Vec labels = frame.lastVec().makeCons(1, 0, null, typeArr)[0];
            Vec.Writer writer = labels.open();
            int rowCnt = ((int) (frame.lastVec().length()));
            // adding labels in reverse order
            for (int r = 0; r < rowCnt; r++)
                writer.set(((rowCnt - r) - 1), ("Foo" + (r + 1)));

            writer.close();
            // Append label vector and spot check
            frame.add("Labels", labels);
            Assert.assertTrue("Failed to create a new String based label column", ((frame.lastVec().atStr(new BufferedString(), 42).compareTo(new BufferedString("Foo108"))) == 0));
        } finally {
            if (frame != null)
                frame.delete();

        }
    }

    @Test
    public void test_sparse() {
        NewChunk nc = new NewChunk(null, 0);
        for (int i = 0; i < 100; i++)
            nc.addNA();

        nc.addStr(new BufferedString("foo"));
        nc.addNA();
        nc.addStr(new BufferedString("bar"));
        Chunk c = nc.compress();
        Assert.assertTrue("first 100 entries are NA", ((c.isNA(0)) && (c.isNA(99))));
        Assert.assertTrue("Sparse string has values", c.atStr(new BufferedString(), 100).equalsAsciiString("foo"));
        Assert.assertTrue("NA", c.isNA(101));
        final BufferedString bufferedString = c.atStr(new BufferedString(), 102);
        Assert.assertTrue(("Sparse string has values: expected `bar`, got " + bufferedString), bufferedString.equalsAsciiString("bar"));
    }

    @Test
    public void test_lstrip() {
        final List<String> content = Arrays.asList("   empty left", "empty right   ", "some string", "", "mystring", "  xxx  ", "cray tweet");
        CStrChunkTest.TextChunk sut = new CStrChunkTest.TextChunk(content);
        sut.lstrip();
        Assert.assertEquals("empty left", sut.at(0));
        Assert.assertEquals("empty right   ", sut.at(1));
        Assert.assertEquals("some string", sut.at(2));
        Assert.assertEquals("", sut.at(3));
        Assert.assertEquals("mystring", sut.at(4));
        Assert.assertEquals("xxx  ", sut.at(5));
        Assert.assertEquals("cray tweet", sut.at(6));
    }

    @Test
    public void test_rstrip() {
        CStrChunkTest.TextChunk sut = new CStrChunkTest.TextChunk(Arrays.asList("", "    ", "   empty left", "empty right   ", "some string", "mystring", "  xxx  ", "cray tweet"));
        sut.rstrip();
        Assert.assertEquals("", sut.at(0));
        Assert.assertEquals("", sut.at(1));
        Assert.assertEquals("   empty left", sut.at(2));
        Assert.assertEquals("empty right", sut.at(3));
        Assert.assertEquals("some string", sut.at(4));
        Assert.assertEquals("mystring", sut.at(5));
        Assert.assertEquals("  xxx", sut.at(6));
        Assert.assertEquals("cray tweet", sut.at(7));
    }

    @Test
    public void test_rstrip_was_failing() {
        CStrChunkTest.TextChunk sut = new CStrChunkTest.TextChunk(Arrays.asList(""));
        sut.rstrip();
        Assert.assertEquals("", sut.at(0));
    }

    class TextChunk {
        CStrChunk cc;

        TextChunk(Iterable<String> content) {
            NewChunk nc = newChunk();
            for (String s : content)
                nc.addStr(s);

            updateFrom(nc);
        }

        private NewChunk newChunk() {
            return new NewChunk(null, 0);
        }

        private void updateFrom(NewChunk nc) {
            cc = ((CStrChunk) (nc.compress()));
        }

        String at(int i) {
            return cc.atStr(new BufferedString(), i).toString();
        }

        void lstrip() {
            updateFrom(cc.asciiLStrip(newChunk(), " "));
        }

        void rstrip() {
            updateFrom(cc.asciiRStrip(newChunk(), " \u0000"));
        }
    }
}

