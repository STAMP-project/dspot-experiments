package water.fvec;


import H2O.STORE;
import Vec.T_CAT;
import Vec.T_NUM;
import Vec.T_STR;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;

import static Vec.T_NUM;


/**
 * Tests for Frame.java
 */
public class FrameTest extends TestUtil {
    @Test
    public void testNonEmptyChunks() {
        try {
            Scope.enter();
            final Frame train1 = Scope.track(new TestFrameBuilder().withName("testFrame").withColNames("ColA", "Response").withVecTypes(T_NUM, T_CAT).withDataForCol(0, TestUtil.ard(1, 2, 3, 4, 0)).withDataForCol(1, TestUtil.ar("A", "B", "C", "A", "B")).withChunkLayout(1, 0, 0, 2, 1, 0, 1).build());
            Assert.assertEquals(4, train1.anyVec().nonEmptyChunks());
            final Frame train2 = Scope.track(new TestFrameBuilder().withName("testFrame").withColNames("ColA", "Response").withVecTypes(T_NUM, T_CAT).withDataForCol(0, TestUtil.ard(1, 2, 3, 4, 0)).withDataForCol(1, TestUtil.ar("A", "B", "C", "A", "B")).withChunkLayout(1, 2, 1, 1).build());
            Assert.assertEquals(4, train2.anyVec().nonEmptyChunks());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testRemoveColumn() {
        Scope.enter();
        Frame testData = TestUtil.parse_test_file(Key.make("test_deep_select_1"), "smalldata/sparse/created_frame_binomial.svm.zip");
        Set<Vec> removedVecs = new HashSet<>();
        try {
            // dataset to split
            int initialSize = testData.numCols();
            removedVecs.add(testData.remove((-1)));
            Assert.assertEquals(initialSize, testData.numCols());
            removedVecs.add(testData.remove(0));
            Assert.assertEquals((initialSize - 1), testData.numCols());
            Assert.assertEquals("C2", testData._names[0]);
            removedVecs.add(testData.remove((initialSize - 2)));
            Assert.assertEquals((initialSize - 2), testData.numCols());
            Assert.assertEquals(("C" + (initialSize - 1)), testData._names[(initialSize - 3)]);
            removedVecs.add(testData.remove(42));
            Assert.assertEquals((initialSize - 3), testData.numCols());
            Assert.assertEquals("C43", testData._names[41]);
            Assert.assertEquals("C45", testData._names[42]);
        } finally {
            Scope.exit();
            for (Vec v : removedVecs)
                if (v != null)
                    v.remove();


            testData.delete();
            STORE.clear();
        }
    }

    /**
     * This test is testing deepSlice functionality and shows that we can use zero-based indexes for slicing
     * // TODO if confirmed go and correct comments for Frame.deepSlice() method
     */
    @Test
    public void testRowDeepSlice() {
        Scope.enter();
        try {
            long[] numericalCol = TestUtil.ar(1, 2, 3, 4);
            Frame input = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_STR, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "d")).withDataForCol(1, numericalCol).withChunkLayout(numericalCol.length).build();
            // Single number row slice
            Frame sliced = input.deepSlice(new long[]{ 1 }, null);
            Assert.assertEquals(1, sliced.numRows());
            Assert.assertEquals("b", sliced.vec(0).stringAt(0));
            Assert.assertEquals(2, sliced.vec(1).at(0), 1.0E-5);
            // checking that 0-based indexing is allowed as well
            // We are slicing here particular indexes of rows : 0 and 3
            Frame slicedRange = input.deepSlice(new long[]{ 0, 3 }, null);
            printOutFrameAsTable(slicedRange, false, slicedRange.numRows());
            Assert.assertEquals(2, slicedRange.numRows());
            TestUtil.assertStringVecEquals(TestUtil.svec("a", "d"), slicedRange.vec(0));
            TestUtil.assertVecEquals(TestUtil.vec(1, 4), slicedRange.vec(1), 1.0E-5);
            // TODO add test for new long[]{-4} values
        } finally {
            Scope.exit();
        }
    }

    // deep select filters out all defined values of the chunk and the only left ones are NAs, eg.: c(1, NA, NA) -> c(NA, NA)
    @Test
    public void testDeepSelectNAs() {
        Scope.enter();
        try {
            String[] data = /* undefined */
            new String[2/* defined */
             + 17];
            data[0] = "A";
            data[((data.length) - 1)] = "Z";
            double[] pred = new double[data.length];
            Arrays.fill(pred, 1.0);
            pred[0] = 0;
            pred[((data.length) - 1)] = 0;
            Frame input = // single chunk
            new TestFrameBuilder().withName("testFrame").withColNames("ColA", "predicate").withVecTypes(T_STR, T_NUM).withDataForCol(0, data).withDataForCol(1, pred).withChunkLayout(data.length).build();
            Scope.track(input);
            Frame result = new Frame.DeepSelect().doAll(T_STR, input).outputFrame();
            Scope.track(result);
            Assert.assertEquals(((data.length) - 2), result.numRows());
            for (int i = 0; i < ((data.length) - 2); i++)
                Assert.assertTrue((("Value in row " + i) + " is NA"), result.vec(0).isNA(i));

        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testFinalizePartialFrameRemovesTrailingChunks() {
        final String fName = "part_frame";
        final long[] layout = new long[]{ 0, 1, 0, 3, 2, 0, 0, 0 };
        try {
            Scope.enter();
            Key<Frame> fKey = Key.make(fName);
            Frame f = new Frame(fKey);
            f.preparePartialFrame(new String[]{ "C0" });
            Scope.track(f);
            f.update();
            for (int i = 0; i < (layout.length); i++) {
                FrameTestUtil.createNC(fName, i, ((int) (layout[i])), new byte[]{ T_NUM });
            }
            f = DKV.get(fName).get();
            f.finalizePartialFrame(layout, new String[][]{ null }, new byte[]{ T_NUM });
            final long[] expectedESPC = new long[]{ 0, 0, 1, 1, 4, 6 };
            Assert.assertArrayEquals(expectedESPC, f.anyVec().espc());
            Frame f2 = Scope.track(new MRTask() {
                @Override
                public void map(Chunk c, NewChunk nc) {
                    for (int i = 0; i < (c._len); i++)
                        nc.addNum(c.atd(i));

                }
            }.doAll(T_NUM, f).outputFrame());
            // the ESPC is the same
            Assert.assertArrayEquals(expectedESPC, f2.anyVec().espc());
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void deepCopyFrameTest() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_CAT).withDataForCol(0, TestUtil.ar("a", "b")).withDataForCol(1, TestUtil.ar("c", "d")).build();
            Frame newFrame = fr.deepCopy(Key.make().toString());
            fr.delete();
            TestUtil.assertStringVecEquals(newFrame.vec("ColB"), TestUtil.cvec("c", "d"));
        } finally {
            Scope.exit();
        }
    }
}

