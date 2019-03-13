package water.rapids.ast.prims.time;


import Vec.T_CAT;
import Vec.T_NUM;
import Vec.T_TIME;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.parser.ParseTime;
import water.rapids.Rapids;
import water.rapids.Session;
import water.rapids.Val;
import water.util.Log;


/**
 *
 */
public class AstMomentTest extends TestUtil {
    @Test
    public void generalTest() {
        AstMoment am = new AstMoment();
        Assert.assertEquals(((am.nargs()) - 1), am.args().length);
    }

    @Test
    public void time0Test() {
        Scope.enter();
        try {
            Val result = Rapids.exec("(moment 1970 1 1 0 0 0 0)->$f1");
            Assert.assertTrue(result.isFrame());
            Frame fr = result.getFrame();
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(1, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertEquals(0, fr.vec(0).at8(0));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void badtimeTest() {
        Scope.enter();
        try {
            // Invalid time moment -- should be cast into NaN
            Val result = Rapids.exec("(moment 1970 0 0 0 0 0 0)->$f1");
            Assert.assertTrue(result.isFrame());
            Frame fr = result.getFrame();
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(1, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(0)));
            result = Rapids.exec("(moment 2001 2 29 0 0 0 0)->$f2");
            Assert.assertTrue(Double.isNaN(result.getFrame().vec(0).at(0)));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void vectimeTest() {
        Scope.enter();
        try {
            Session session = new Session();
            new TestFrameBuilder().withName("$fr", session).withColNames("day", "hour", "min").withDataForCol(0, TestUtil.ard(1, 1.1, 1.2, 2, 3)).withDataForCol(1, TestUtil.ard(0, Double.NaN, 11, 13, 15)).withDataForCol(2, TestUtil.ar(0, 0, 30, 0, 0)).build();
            Val result = Rapids.exec("(moment 2016 12 (cols $fr 'day') 0 0 0 0)->$res1", session);
            Assert.assertTrue(result.isFrame());
            Frame fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(5, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            long t0 = ((long) (fr.vec(0).at(0)));
            long t1 = ((long) (fr.vec(0).at(1)));
            long t2 = ((long) (fr.vec(0).at(2)));
            long t3 = ((long) (fr.vec(0).at(3)));
            long t4 = ((long) (fr.vec(0).at(4)));
            Assert.assertEquals(0, (t0 - t1));
            Assert.assertEquals(0, (t1 - t2));
            Assert.assertEquals(((24 * 3600) * 1000), (t3 - t2));
            Assert.assertEquals(((24 * 3600) * 1000), (t4 - t3));
            result = Rapids.exec("(moment 2016 12 1 (cols $fr 'hour') (cols $fr 'min') 0 0)->$res2", session);
            Assert.assertTrue(result.isFrame());
            fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(5, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            double d0 = fr.vec(0).at(0);
            double d1 = fr.vec(0).at(1);
            double d2 = fr.vec(0).at(2);
            double d3 = fr.vec(0).at(3);
            double d4 = fr.vec(0).at(4);
            Assert.assertTrue((("d1 should have been NaN, got " + d1) + " instead"), Double.isNaN(d1));
            Assert.assertEquals(((((11 * 60) + 30) * 60) * 1000), ((long) (d2 - d0)));
            Assert.assertEquals((((13 * 60) * 60) * 1000), ((long) (d3 - d0)));
            Assert.assertEquals((((15 * 60) * 60) * 1000), ((long) (d4 - d0)));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void naTest() {
        Scope.enter();
        try {
            Val result = Rapids.exec("(moment 2000 1 1 0 0 0 NaN)->$f1");
            Assert.assertTrue(result.isFrame());
            Frame fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(1, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(0)));
            Session s = new Session();
            new TestFrameBuilder().withName("$year", s).withColNames("year").withDataForCol(0, TestUtil.ard(2000, 2004, 2008)).build();
            result = Rapids.exec("(moment $year 1 1 0 0 NaN 0)->$f2", s);
            Assert.assertTrue(result.isFrame());
            fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(3, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(0)));
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(1)));
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(2)));
            new TestFrameBuilder().withName("$day", s).withColNames("day").withDataForCol(0, TestUtil.ard(28, 29, 30)).build();
            result = Rapids.exec("(moment 2001 2 $day 0 0 0 0)->$f3", s);
            Assert.assertTrue(result.isFrame());
            fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(3, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertTrue((!(Double.isNaN(fr.vec(0).at(0)))));
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(1)));
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(2)));
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testBadArguments() {
        Scope.enter();
        try {
            Session s = new Session();
            try {
                Rapids.exec("(moment 2000 1 1 0 0 0)->$f1", s);
                Assert.fail("Expected error: Wrong number of arguments");
            } catch (IllegalArgumentException ignored) {
            }
            try {
                Rapids.exec("(moment 2000 1 1 [0] 0 0 0)->$f2", s);
                Assert.fail("Expected error: A NumList is not allowed");
            } catch (IllegalArgumentException ignored) {
            }
            try {
                Rapids.exec("(moment '2000' 1 1 0 0 0 0)->$f3", s);
                Assert.fail("Expected error: A string is not allowed");
            } catch (IllegalArgumentException ignored) {
            }
            new TestFrameBuilder().withName("$test", s).withColNames("day", "month").withVecTypes(T_NUM, T_CAT).withDataForCol(0, TestUtil.ard(5, 10, 15)).withDataForCol(1, TestUtil.ar("April", "May", "June")).build();
            try {
                Rapids.exec("(moment 2010 1 $test 0 0 0 0)->$f4", s);
                Assert.fail("Expected error: frame with >1 columns passed");
            } catch (IllegalArgumentException ignored) {
            }
            try {
                Rapids.exec("(moment 2010 (cols $test 'month') 1 0 0 0 0)->$f5", s);
                Assert.fail("Expected error: non-numeric column used");
            } catch (IllegalArgumentException ignored) {
            }
            new TestFrameBuilder().withName("$frame0", s).withColNames("a").build();
            try {
                Rapids.exec("(moment 2010 1 $frame0 0 0 0 0)->$f6", s);
                Assert.fail("Expected error: 0-rows frame used");
            } catch (IllegalArgumentException ignored) {
            }
            new TestFrameBuilder().withName("$test2", s).withColNames("month").withDataForCol(0, TestUtil.ard(1, 2)).build();
            try {
                Rapids.exec("(moment 2010 (cols $test2 'month') (cols $test 'day') 0 0 0 0)->$f7", s);
                Assert.fail("Expected error: Incompatible vecs: 2 rows and 3 rows");
            } catch (IllegalArgumentException ignored) {
            }
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void testOneRowFrame() {
        Scope.enter();
        try {
            Session s = new Session();
            new TestFrameBuilder().withName("$frame1", s).withColNames("day", "hour").withDataForCol(0, TestUtil.ar(1)).withDataForCol(1, TestUtil.ard(Double.NaN)).build();
            new TestFrameBuilder().withName("$month", s).withColNames("month").withDataForCol(0, TestUtil.ar(2, 3)).build();
            Val result = Rapids.exec("(moment 2010 $month (cols $frame1 'day') 0 0 0 0)->$res1", s);
            Assert.assertTrue(result.isFrame());
            Frame fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(2, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            result = Rapids.exec("(moment 2010 $month 1 (cols $frame1 'hour') 0 0 0)->$res2", s);
            Assert.assertTrue(result.isFrame());
            fr = result.getFrame();
            Scope.track(fr);
            Assert.assertEquals(1, fr.numCols());
            Assert.assertEquals(2, fr.numRows());
            Assert.assertEquals(T_TIME, fr.vec(0).get_type());
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(0)));
            Assert.assertTrue(Double.isNaN(fr.vec(0).at(1)));
        } finally {
            Scope.exit();
        }
    }

    /**
     * [PUBDEV-4044] Verify that {@code moment} function creates the same moments it's
     * supposed to create, even if the input frame has multiple chunks.
     */
    @Test
    public void testMultiChunkedFrame() {
        long seed = new Random().nextLong();
        Log.info(("In testMultiChunkedFrame: using seed " + seed));
        Random rnd = new Random(seed);
        int N = 30000;
        long[] years = new long[N];
        long[] months = new long[N];
        long[] days = new long[N];
        for (int i = 0; i < N; i++) {
            years[i] = 1980 + (rnd.nextInt(50));
            months[i] = 1 + (rnd.nextInt(12));
            days[i] = 1 + (rnd.nextInt(28));
        }
        int nchunks = 30;
        long[] layout = new long[nchunks];
        layout[0] = N;
        for (int i = 1; i < nchunks; i++) {
            layout[i] = N / nchunks;
            layout[0] -= layout[i];
        }
        Scope.enter();
        try {
            Session s = new Session();
            Frame f0 = new TestFrameBuilder().withName("$f0", s).withColNames("year", "month", "day").withDataForCol(0, years).withDataForCol(1, months).withDataForCol(2, days).withChunkLayout(layout).build();
            Scope.track(f0);
            Frame f1 = Rapids.exec("(moment (cols $f0 0) (cols $f0 1) (cols $f0 2) 0 0 0 0)->$f1", s).getFrame();
            Scope.track(f1);
            Assert.assertEquals(1, f1.numCols());
            Assert.assertEquals(N, f1.numRows());
            ParseTime.setTimezone("UTC");// having a global timezone setting is evil...

            Frame f1y = Rapids.exec("(year $f1)->$fy", s).getFrame();
            Frame f1m = Rapids.exec("(month $f1)->$fm", s).getFrame();
            Frame f1d = Rapids.exec("(day $f1)->$fd", s).getFrame();
            Scope.track(f1y, f1m, f1d);
            TestUtil.assertVecEquals(f0.vec(0), f1y.vec(0), 1.0E-10);
            TestUtil.assertVecEquals(f0.vec(1), f1m.vec(0), 1.0E-10);
            TestUtil.assertVecEquals(f0.vec(2), f1d.vec(0), 1.0E-10);
        } finally {
            Scope.exit();
        }
    }
}

