package water.rapids;


import hex.CreateFrame;
import java.io.IOException;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.rapids.vals.ValFrame;
import water.util.ArrayUtils;

import static water.MRTask.<init>;


public class SortTest extends TestUtil {
    @Test
    public void testBasicSortRapids() {
        Frame fr = null;
        Frame res = null;
        // Stable sort columns 1 and 2
        String tree = "(sort hex [1 2] [1 1])";
        try {
            // Build a frame which is unsorted on small-count categoricals in columns
            // 0 and 1, and completely sorted on a record-number based column 2.
            // Sort will be on columns 0 and 1, in that order, and is expected stable.
            fr = SortTest.buildFrame(1000, 10);
            fr.insertVec(0, "row", fr.remove(2));
            // 
            Val val = Rapids.exec(tree);
            Assert.assertTrue((val instanceof ValFrame));
            res = val.getFrame();
            res.add("row", res.remove(0));
            new SortTest.CheckSort().doAll(res);
        } finally {
            if (fr != null)
                fr.delete();

            if (res != null)
                res.delete();

        }
    }

    @Test
    public void testBasicSortJava() {
        Frame fr = null;
        Frame res = null;
        try {
            fr = SortTest.buildFrame(1000, 10);
            fr.insertVec(0, "row", fr.remove(2));
            res = Merge.sort(fr, new int[]{ 1, 2 });
            res.add("row", res.remove(0));
            new SortTest.CheckSort().doAll(res);
        } finally {
            if (fr != null)
                fr.delete();

            if (res != null)
                res.delete();

        }
    }

    @Test
    public void testBasicSortJava2() {
        Frame fr = null;
        Frame res = null;
        try {
            fr = SortTest.buildFrame(1000, 10);
            String[] domain = new String[1000];
            for (int i = 0; i < 1000; i++)
                domain[i] = "D" + i;

            fr.vec(0).setDomain(domain);
            res = fr.sort(new int[]{ 0, 1 });
            new SortTest.CheckSort().doAll(res);
        } finally {
            if (fr != null)
                fr.delete();

            if (res != null)
                res.delete();

        }
    }

    // test our sorting with string columns implementation.  The string columns may have NAs.
    // Our sort results are compared with sorting done by R.
    @Test
    public void testSortWithStringsColumns() {
        Random randomVl = new Random();
        double temp = randomVl.nextDouble();
        if (temp >= 0.5)
            testSortWithStrings("smalldata/jira/PUBDEV_5266_merge_strings/PUBDEV_5266_f1_small.csv", "smalldata/jira/PUBDEV_5266_merge_strings/sortedF1_R_C1_C4_small.csv", new int[]{ 0, 3 });
        else
            testSortWithStrings("smalldata/jira/PUBDEV_5266_merge_strings/PUBDEV_5266_f2_small_NAs.csv", "smalldata/jira/PUBDEV_5266_merge_strings/sortedF2_R_C1_C7_C5_small_NAs.csv", new int[]{ 0, 6, 4 });

    }

    // Assert that result is indeed sorted - on all 3 columns, as this is a
    // stable sort.
    private class CheckSort extends MRTask<SortTest.CheckSort> {
        @Override
        public void map(Chunk[] cs) {
            long x0 = cs[0].at8(0);
            long x1 = cs[1].at8(0);
            long x2 = cs[2].at8(0);
            for (int i = 1; i < (cs[0]._len); i++) {
                long y0 = cs[0].at8(i);
                long y1 = cs[1].at8(i);
                long y2 = cs[2].at8(i);
                Assert.assertTrue(((x0 < y0) || ((x0 == y0) && ((x1 < y1) || ((x1 == y1) && (x2 < y2))))));
                x0 = y0;
                x1 = y1;
                x2 = y2;
            }
            // Last row of chunk is sorted relative to 1st row of next chunk
            long row = (cs[0].start()) + (cs[0]._len);
            if (row < (cs[0].vec().length())) {
                long y0 = cs[0].vec().at8(row);
                long y1 = cs[1].vec().at8(row);
                long y2 = cs[2].vec().at8(row);
                Assert.assertTrue(((x0 < y0) || ((x0 == y0) && ((x1 < y1) || ((x1 == y1) && (x2 < y2))))));
            }
        }
    }

    @Test
    public void testSortTimes() throws IOException {
        Scope.enter();
        Frame fr = null;
        Frame sorted = null;
        try {
            fr = TestUtil.parse_test_file("sort_crash.csv");
            sorted = fr.sort(new int[]{ 0 });
            Scope.track(fr);
            Scope.track(sorted);
            SortTest.testSort(sorted, fr, 0);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testSortOverflows() throws IOException {
        Scope.enter();
        Frame fr = null;
        Frame sorted = null;
        try {
            fr = ArrayUtils.frame(TestUtil.ar("Long", "Double"), TestUtil.ard(Long.MAX_VALUE, Double.MAX_VALUE), TestUtil.ard(((Long.MIN_VALUE) + 10), Double.MIN_VALUE), TestUtil.ard(Long.MAX_VALUE, Double.MAX_VALUE), TestUtil.ard((-1152921504), Double.MIN_VALUE));
            int colIndex = 0;
            sorted = fr.sort(new int[]{ colIndex });// sort Long/integer first

            Scope.track(fr);
            Scope.track(sorted);
            SortTest.testSort(sorted, fr, colIndex);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testSortOverflows2() throws IOException {
        Scope.enter();
        Frame fr;
        Frame sorted1;
        Frame sorted2;
        final long ts = 1485333188427000000L;
        try {
            Vec dz = Vec.makeZero(1000);
            Vec z = dz.makeZero();// make a vec consisting of C0LChunks

            Vec v = new MRTask() {
                @Override
                public void map(Chunk[] cs) {
                    for (Chunk c : cs)
                        for (int r = 0; r < (c._len); r++)
                            c.set(r, ((r + ts) + (c.start())));


                }
            }.doAll(z)._fr.vecs()[0];
            Scope.track(dz);
            Scope.track(z);
            Scope.track(v);
            Vec rand = dz.makeRand(12345678);
            fr = new Frame(v, rand);
            sorted1 = fr.sort(new int[]{ 1 });
            sorted2 = sorted1.sort(new int[]{ 0 });
            for (long i = 0; i < (fr.numRows()); i++) {
                Assert.assertTrue(((fr.vec(0).at8(i)) == (sorted2.vec(0).at8(i))));
            }
            Scope.track(fr);
            Scope.track(sorted1);
            Scope.track(sorted2);
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testSortIntegersFloats() throws IOException {
        // test small integers sort
        SortTest.testSortOneColumn("smalldata/synthetic/smallIntFloats.csv.zip", 0, false, false);
        // test small float sort
        SortTest.testSortOneColumn("smalldata/synthetic/smallIntFloats.csv.zip", 1, false, false);
        // test integer frame
        SortTest.testSortOneColumn("smalldata/synthetic/integerFrame.csv", 0, false, false);
        // test integer frame with NAs
        SortTest.testSortOneColumn("smalldata/synthetic/integerFrame.csv", 0, true, false);
        // test double frame
        SortTest.testSortOneColumn("smalldata/synthetic/doubleFrame.csv", 0, false, false);
        // test double frame with NAs
        SortTest.testSortOneColumn("smalldata/synthetic/doubleFrame.csv", 0, true, false);
        // test integer frame where overflow will occur for col.max()-col.min()
        // TestSortOneColumn("smalldata/synthetic/bigIntFloatsOverflows.csv.zip", 0, false, false);
        // test integer frame where overflow will occur for col.max()-col.min(), with NAs
        // TestSortOneColumn("smalldata/synthetic/bigIntFloatsOverflows.csv.zip", 0, true, false);
        // test double frame where overflow will occur for col.max()-col.min()
        // TestSortOneColumn("smalldata/synthetic/bigIntFloatsOverflows.csv.zip", 1, false, false);
        // test double frame where overflow will occur for col.max()-col.min(), with NAs
        // TestSortOneColumn("smalldata/synthetic/bigIntFloatsOverflows.csv.zip", 1, true, false);
    }

    @Test
    public void testSortIntegersDescend() throws IOException {
        Scope.enter();
        Frame fr;
        Frame sortedInt;
        try {
            fr = TestUtil.parse_test_file("smalldata/synthetic/integerFrame.csv");
            sortedInt = fr.sort(new int[]{ 0 }, new int[]{ -1 });
            Scope.track(fr);
            Scope.track(sortedInt);
            long numRows = fr.numRows();
            assert numRows == (sortedInt.numRows());
            for (long index = 1; index < numRows; index++) {
                Assert.assertTrue(((sortedInt.vec(0).at8(index)) >= (sortedInt.vec(0).at8(index))));
            }
        } finally {
            Scope.exit();
        }
    }

    /**
     * *
     * This simple test just want to test and make sure that processing the final frames by a batch does
     * not leak memories.  The accuracy of the sort is tested elsewhere.
     */
    @Test
    public void testSortOOM() throws IOException {
        Scope.enter();
        Frame fr;
        Frame sortedInt;
        try {
            CreateFrame cf = new CreateFrame();
            cf.rows = 9000000;
            cf.cols = 2;
            cf.categorical_fraction = 0;
            cf.integer_fraction = 1;
            cf.binary_fraction = 0;
            cf.time_fraction = 0;
            cf.string_fraction = 0;
            cf.binary_ones_fraction = 0;
            cf.integer_range = 1;
            cf.has_response = false;
            cf.seed = 1234;
            fr = cf.execImpl().get();
            sortedInt = fr.sort(new int[]{ 0 }, new int[]{ -1 });
            Scope.track(fr);
            Scope.track(sortedInt);
            assert (fr.numRows()) == (sortedInt.numRows());
        } finally {
            Scope.exit();
        }
    }
}

