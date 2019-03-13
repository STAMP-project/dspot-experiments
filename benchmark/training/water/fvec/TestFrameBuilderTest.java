package water.fvec;


import Vec.T_CAT;
import Vec.T_NUM;
import Vec.T_STR;
import Vec.T_TIME;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.parser.BufferedString;


public class TestFrameBuilderTest extends TestUtil {
    private static double DELTA = 1.0E-5;

    @Test
    public void testEmpty() {
        Frame fr = new TestFrameBuilder().build();
        Assert.assertEquals(fr.vecs().length, 0);
        Assert.assertEquals(fr.numRows(), 0);
        Assert.assertEquals(fr.numCols(), 0);
        Assert.assertNull(fr.anyVec());// because we don't have any vectors

        fr.remove();
    }

    @Test
    public void testName() {
        Frame fr = new TestFrameBuilder().withName("FrameName").build();
        Assert.assertEquals(fr._key.toString(), "FrameName");
        Assert.assertEquals(fr.vecs().length, 0);
        Assert.assertEquals(fr.numRows(), 0);
        Assert.assertEquals(fr.numCols(), 0);
        Assert.assertNull(fr.anyVec());// because we don't have any vectors

        fr.remove();
    }

    @Test
    public void testVecTypes() {
        Frame fr = new TestFrameBuilder().withVecTypes(T_CAT, T_NUM, T_TIME, T_STR).build();
        Assert.assertArrayEquals(fr.names(), TestUtil.ar("col_0", "col_1", "col_2", "col_3"));
        Assert.assertEquals(fr.vecs().length, 4);
        Assert.assertEquals(fr.numRows(), 0);
        Assert.assertEquals(fr.numCols(), 4);
        Assert.assertEquals(fr.vec(0).get_type(), T_CAT);
        Assert.assertEquals(fr.vec(1).get_type(), T_NUM);
        Assert.assertEquals(fr.vec(2).get_type(), T_TIME);
        Assert.assertEquals(fr.vec(3).get_type(), T_STR);
        fr.remove();
    }

    /**
     * This test throws exception because size of specified vectors and size of specified names differ
     */
    @Test(expected = IllegalArgumentException.class)
    public void testWrongVecNameSize() {
        Frame fr = new TestFrameBuilder().withVecTypes(T_CAT, T_NUM, T_TIME, T_STR).withColNames("A", "B").build();
        fr.remove();
    }

    @Test
    public void testColNames() {
        Frame fr = new TestFrameBuilder().withVecTypes(T_CAT, T_NUM, T_TIME, T_STR).withColNames("A", "B", "C", "D").build();
        Assert.assertEquals(fr.vecs().length, 4);
        Assert.assertEquals(fr.numRows(), 0);
        Assert.assertEquals(fr.numCols(), 4);
        Assert.assertArrayEquals(fr.names(), TestUtil.ar("A", "B", "C", "D"));
        fr.remove();
    }

    @Test
    public void testDefaultChunks() {
        Frame fr = new TestFrameBuilder().withVecTypes(T_CAT, T_NUM, T_TIME, T_STR).withColNames("A", "B", "C", "D").build();
        Assert.assertArrayEquals(fr.anyVec().espc(), TestUtil.ar(0, 0));// no data

        Assert.assertEquals(fr.anyVec().nChunks(), 1);// 1 empty chunk

        fr.remove();
    }

    /**
     * This test throws exception because it expects more data ( via chunk layout) than is actually available
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetChunksToMany() {
        Frame fr = // we are requesting 7 rows to be able to create 4 chunks, but we have 0 rows
        new TestFrameBuilder().withVecTypes(T_CAT, T_NUM, T_TIME, T_STR).withColNames("A", "B", "C", "D").withChunkLayout(2, 2, 2, 1).build();
        fr.remove();
    }

    @Test
    public void testSetChunks() {
        final Frame fr = new TestFrameBuilder().withName("frameName").withColNames("ColA", "ColB").withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(Double.NaN, 1, 2, 3, 4, 5.6, 7)).withDataForCol(1, TestUtil.ar("A", "B", "C", null, "F", "I", "J")).withChunkLayout(2, 2, 2, 1).build();
        Assert.assertEquals(fr.anyVec().nChunks(), 4);
        Assert.assertArrayEquals(fr.anyVec().espc(), new long[]{ 0, 2, 4, 6, 7 });
        // check data in the frame
        Assert.assertEquals(fr.vec(0).at(0), Double.NaN, TestFrameBuilderTest.DELTA);
        Assert.assertEquals(fr.vec(0).at(5), 5.6, TestFrameBuilderTest.DELTA);
        Assert.assertEquals(fr.vec(0).at(6), 7, TestFrameBuilderTest.DELTA);
        BufferedString strBuf = new BufferedString();
        Assert.assertEquals(fr.vec(1).atStr(strBuf, 0).toString(), "A");
        Assert.assertNull(fr.vec(1).atStr(strBuf, 3));
        Assert.assertEquals(fr.vec(1).atStr(strBuf, 6).toString(), "J");
        fr.remove();
    }

    /**
     * This test throws exception because the data has different length
     */
    @Test(expected = IllegalArgumentException.class)
    public void testDataDifferentSize() {
        final Frame fr = // 3 elements
        // 2 elements
        new TestFrameBuilder().withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(Double.NaN, 1)).withDataForCol(1, TestUtil.ar("A", "B", "C")).build();
        fr.remove();
    }

    @Test
    public void withRandomIntDataForColTest() {
        long seed = 44L;
        int size = 1000;
        int min = 1;
        int max = 5;
        Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withRandomIntDataForCol(0, size, min, max, seed).build();
        printOutFrameAsTable(fr, false, size);
        Vec generatedVec = fr.vec(0);
        for (int i = 0; i < size; i++) {
            Assert.assertTrue((((generatedVec.at(i)) <= max) && ((generatedVec.at(i)) >= min)));
        }
        fr.delete();
    }

    @Test
    public void withRandomDoubleDataForColTest() {
        long seed = 44L;
        int size = 1000;
        int min = 1;
        int max = 5;
        Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withRandomDoubleDataForCol(0, size, min, max, seed).build();
        printOutFrameAsTable(fr, false, size);
        Vec generatedVec = fr.vec(0);
        for (int i = 0; i < size; i++) {
            Assert.assertTrue((((generatedVec.at(i)) <= max) && ((generatedVec.at(i)) >= min)));
        }
        fr.delete();
    }

    @Test
    public void numRowsIsWorkingForRandomlyGeneratedColumnsTest() {
        long seed = 44L;
        Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_NUM).withRandomDoubleDataForCol(0, 1000, 1, 5, seed).build();
        long numberOfRowsGenerated = fr.numRows();
        Assert.assertEquals(1000, numberOfRowsGenerated);
        fr.delete();
    }

    @Test
    public void withRandomBinaryDataForColTest() {
        long seed = 44L;
        Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_CAT).withRandomBinaryDataForCol(0, 1000, seed).build();
        Assert.assertEquals(2, fr.vec("ColA").cardinality());
        fr.delete();
    }
}

