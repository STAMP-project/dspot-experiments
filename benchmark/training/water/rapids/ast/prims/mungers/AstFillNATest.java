package water.rapids.ast.prims.mungers;


import Vec.T_NUM;
import hex.CreateFrame;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Session;
import water.rapids.Val;
import water.rapids.vals.ValFrame;


public class AstFillNATest extends TestUtil {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void TestFillNA() {
        Scope.enter();
        try {
            Session sess = new Session();
            Frame fr = Scope.track(new TestFrameBuilder().withName("$fr", sess).withColNames("C1", "C2", "C3").withVecTypes(T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1, Double.NaN, Double.NaN, Double.NaN, Double.NaN)).withDataForCol(1, TestUtil.ard(Double.NaN, 1, Double.NaN, Double.NaN, Double.NaN)).withDataForCol(2, TestUtil.ard(Double.NaN, Double.NaN, 1, Double.NaN, Double.NaN)).build());
            Val val = Rapids.exec("(h2o.fillna $fr 'forward' 0 2)", sess);
            Assert.assertTrue((val instanceof ValFrame));
            Frame res = Scope.track(val.getFrame());
            // check the first column. should be all unique indexes in order
            TestUtil.assertVecEquals(res.vec(0), TestUtil.dvec(1.0, 1.0, 1.0, Double.NaN, Double.NaN), 0.0);
            TestUtil.assertVecEquals(res.vec(1), TestUtil.dvec(Double.NaN, 1.0, 1.0, 1.0, Double.NaN), 0.0);
            TestUtil.assertVecEquals(res.vec(2), TestUtil.dvec(Double.NaN, Double.NaN, 1.0, 1.0, 1.0), 0.0);
        } finally {
            Scope.exit();
        }
    }

    /**
     * This test will build a big frame spanning across multiple chunks.  It will perform fillna backwards for both
     * columnwise and row-wise.  It will grab the result as the answer frame from the single thread implementation
     * of fillna backwards.  Finally, it will compare the two frames and they should be the same.
     *
     * A collection of maxlen is considered.
     */
    @Test
    public void testBackwardRowColLarge() {
        Scope.enter();
        try {
            long seed = 12345;
            Session sess = new Session();
            CreateFrame cf = new CreateFrame();
            cf.rows = 10000;
            cf.cols = 20;
            cf.binary_fraction = 0;
            cf.integer_fraction = 0.3;
            cf.categorical_fraction = 0.3;
            cf.has_response = false;
            cf.missing_fraction = 0.7;
            cf.integer_range = 10000;
            cf.factors = 100;
            cf.seed = seed;
            Frame testFrame = cf.execImpl().get();
            Scope.track(testFrame);
            int[] maxLenArray = new int[]{ 0, 1, 2, 17, 10000000 };
            for (int maxlen : maxLenArray) {
                // first perform col fillna
                String rapidStringCol = ((("(h2o.fillna " + (testFrame._key)) + " 'backward' 0 ") + maxlen) + ")";
                assertFillNACorrect(testFrame, rapidStringCol, maxlen, false, sess);
                String rapidStringRow = ((("(h2o.fillna " + (testFrame._key)) + " 'backward' 1 ") + maxlen) + ")";
                assertFillNACorrect(testFrame, rapidStringRow, maxlen, true, sess);
            }
        } finally {
            Scope.exit();
        }
    }

    /**
     * Purpose here is to carry out short tests to make sure the code works and the single thread code works.
     */
    @Test
    public void testBackwardMethodRowAll() {
        Scope.enter();
        try {
            Session sess = new Session();
            Frame frAllNA = Scope.track(new TestFrameBuilder().withName("$fr", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN)).withDataForCol(1, TestUtil.ard(Double.NaN)).withDataForCol(2, TestUtil.ard(Double.NaN)).withDataForCol(3, TestUtil.ard(Double.NaN)).withDataForCol(4, TestUtil.ard(Double.NaN)).withDataForCol(5, TestUtil.ard(Double.NaN)).withDataForCol(6, TestUtil.ard(Double.NaN)).build());
            assertNFillNACorrect(sess, frAllNA, frAllNA, 0, "(h2o.fillna $fr 'backward' 1 0)", true);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, frAllNA, frAllNA, 100, "(h2o.fillna $fr 'backward' 1 100)", true);// h2o.fillna with maxlen 100

            // correct answers
            Frame fr1NA = Scope.track(new TestFrameBuilder().withName("$fr2", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN)).withDataForCol(1, TestUtil.ard(Double.NaN)).withDataForCol(2, TestUtil.ard(Double.NaN)).withDataForCol(3, TestUtil.ard(Double.NaN)).withDataForCol(4, TestUtil.ard(Double.NaN)).withDataForCol(5, TestUtil.ard(Double.NaN)).withDataForCol(6, TestUtil.ard(1.234)).build());
            Frame fr1NA1na = Scope.track(new TestFrameBuilder().withName("$fr1NA1na", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN)).withDataForCol(1, TestUtil.ard(Double.NaN)).withDataForCol(2, TestUtil.ard(Double.NaN)).withDataForCol(3, TestUtil.ard(Double.NaN)).withDataForCol(4, TestUtil.ard(Double.NaN)).withDataForCol(5, TestUtil.ard(1.234)).withDataForCol(6, TestUtil.ard(1.234)).build());
            Frame fr1NA3na = Scope.track(new TestFrameBuilder().withName("$fr1NA3na", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN)).withDataForCol(1, TestUtil.ard(Double.NaN)).withDataForCol(2, TestUtil.ard(Double.NaN)).withDataForCol(3, TestUtil.ard(1.234)).withDataForCol(4, TestUtil.ard(1.234)).withDataForCol(5, TestUtil.ard(1.234)).withDataForCol(6, TestUtil.ard(1.234)).build());
            Frame fr1NA100na = Scope.track(new TestFrameBuilder().withName("$fr1NA100na", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1.234)).withDataForCol(1, TestUtil.ard(1.234)).withDataForCol(2, TestUtil.ard(1.234)).withDataForCol(3, TestUtil.ard(1.234)).withDataForCol(4, TestUtil.ard(1.234)).withDataForCol(5, TestUtil.ard(1.234)).withDataForCol(6, TestUtil.ard(1.234)).build());
            assertNFillNACorrect(sess, fr1NA, fr1NA, 0, "(h2o.fillna $fr2 'backward' 1 0)", true);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, fr1NA, fr1NA1na, 1, "(h2o.fillna $fr2 'backward' 1 1)", true);// h2o.fillna with maxlen 1

            assertNFillNACorrect(sess, fr1NA, fr1NA3na, 3, "(h2o.fillna $fr2 'backward' 1 3)", true);// h2o.fillna with maxlen 3

            assertNFillNACorrect(sess, fr1NA, fr1NA100na, 100, "(h2o.fillna $fr2 'backward' 1 100)", true);// h2o.fillna with maxlen 100

            // frame with multiple numbers and NA blocks
            Frame frMultipleNA = Scope.track(new TestFrameBuilder().withName("$frMultipleNA", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(Double.NaN)).withDataForCol(2, TestUtil.ard(2)).withDataForCol(3, TestUtil.ard(Double.NaN)).withDataForCol(4, TestUtil.ard(Double.NaN)).withDataForCol(5, TestUtil.ard(3)).withDataForCol(6, TestUtil.ard(Double.NaN)).withDataForCol(7, TestUtil.ard(Double.NaN)).withDataForCol(8, TestUtil.ard(Double.NaN)).withDataForCol(9, TestUtil.ard(4)).withDataForCol(10, TestUtil.ard(Double.NaN)).withDataForCol(11, TestUtil.ard(Double.NaN)).withDataForCol(12, TestUtil.ard(Double.NaN)).withDataForCol(13, TestUtil.ard(Double.NaN)).withDataForCol(14, TestUtil.ard(5)).withDataForCol(15, TestUtil.ard(Double.NaN)).withDataForCol(16, TestUtil.ard(Double.NaN)).withDataForCol(17, TestUtil.ard(Double.NaN)).withDataForCol(18, TestUtil.ard(Double.NaN)).withDataForCol(19, TestUtil.ard(Double.NaN)).withDataForCol(20, TestUtil.ard(6)).withDataForCol(21, TestUtil.ard(Double.NaN)).withDataForCol(22, TestUtil.ard(Double.NaN)).withDataForCol(23, TestUtil.ard(Double.NaN)).withDataForCol(24, TestUtil.ard(Double.NaN)).withDataForCol(25, TestUtil.ard(Double.NaN)).withDataForCol(26, TestUtil.ard(Double.NaN)).withDataForCol(27, TestUtil.ard(7)).withDataForCol(28, TestUtil.ard(Double.NaN)).build());
            // correct answer
            Frame frMultipleNA1Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA1Fill", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(2)).withDataForCol(2, TestUtil.ard(2)).withDataForCol(3, TestUtil.ard(Double.NaN)).withDataForCol(4, TestUtil.ard(3)).withDataForCol(5, TestUtil.ard(3)).withDataForCol(6, TestUtil.ard(Double.NaN)).withDataForCol(7, TestUtil.ard(Double.NaN)).withDataForCol(8, TestUtil.ard(4)).withDataForCol(9, TestUtil.ard(4)).withDataForCol(10, TestUtil.ard(Double.NaN)).withDataForCol(11, TestUtil.ard(Double.NaN)).withDataForCol(12, TestUtil.ard(Double.NaN)).withDataForCol(13, TestUtil.ard(5)).withDataForCol(14, TestUtil.ard(5)).withDataForCol(15, TestUtil.ard(Double.NaN)).withDataForCol(16, TestUtil.ard(Double.NaN)).withDataForCol(17, TestUtil.ard(Double.NaN)).withDataForCol(18, TestUtil.ard(Double.NaN)).withDataForCol(19, TestUtil.ard(6)).withDataForCol(20, TestUtil.ard(6)).withDataForCol(21, TestUtil.ard(Double.NaN)).withDataForCol(22, TestUtil.ard(Double.NaN)).withDataForCol(23, TestUtil.ard(Double.NaN)).withDataForCol(24, TestUtil.ard(Double.NaN)).withDataForCol(25, TestUtil.ard(Double.NaN)).withDataForCol(26, TestUtil.ard(7)).withDataForCol(27, TestUtil.ard(7)).withDataForCol(28, TestUtil.ard(Double.NaN)).build());
            Frame frMultipleNA2Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA2Fill", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(2)).withDataForCol(2, TestUtil.ard(2)).withDataForCol(3, TestUtil.ard(3)).withDataForCol(4, TestUtil.ard(3)).withDataForCol(5, TestUtil.ard(3)).withDataForCol(6, TestUtil.ard(Double.NaN)).withDataForCol(7, TestUtil.ard(4)).withDataForCol(8, TestUtil.ard(4)).withDataForCol(9, TestUtil.ard(4)).withDataForCol(10, TestUtil.ard(Double.NaN)).withDataForCol(11, TestUtil.ard(Double.NaN)).withDataForCol(12, TestUtil.ard(5)).withDataForCol(13, TestUtil.ard(5)).withDataForCol(14, TestUtil.ard(5)).withDataForCol(15, TestUtil.ard(Double.NaN)).withDataForCol(16, TestUtil.ard(Double.NaN)).withDataForCol(17, TestUtil.ard(Double.NaN)).withDataForCol(18, TestUtil.ard(6)).withDataForCol(19, TestUtil.ard(6)).withDataForCol(20, TestUtil.ard(6)).withDataForCol(21, TestUtil.ard(Double.NaN)).withDataForCol(22, TestUtil.ard(Double.NaN)).withDataForCol(23, TestUtil.ard(Double.NaN)).withDataForCol(24, TestUtil.ard(Double.NaN)).withDataForCol(25, TestUtil.ard(7)).withDataForCol(26, TestUtil.ard(7)).withDataForCol(27, TestUtil.ard(7)).withDataForCol(28, TestUtil.ard(Double.NaN)).build());
            Frame frMultipleNA3Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA3Fill", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(2)).withDataForCol(2, TestUtil.ard(2)).withDataForCol(3, TestUtil.ard(3)).withDataForCol(4, TestUtil.ard(3)).withDataForCol(5, TestUtil.ard(3)).withDataForCol(6, TestUtil.ard(4)).withDataForCol(7, TestUtil.ard(4)).withDataForCol(8, TestUtil.ard(4)).withDataForCol(9, TestUtil.ard(4)).withDataForCol(10, TestUtil.ard(5)).withDataForCol(11, TestUtil.ard(5)).withDataForCol(12, TestUtil.ard(5)).withDataForCol(13, TestUtil.ard(5)).withDataForCol(14, TestUtil.ard(5)).withDataForCol(15, TestUtil.ard(6)).withDataForCol(16, TestUtil.ard(6)).withDataForCol(17, TestUtil.ard(6)).withDataForCol(18, TestUtil.ard(6)).withDataForCol(19, TestUtil.ard(6)).withDataForCol(20, TestUtil.ard(6)).withDataForCol(21, TestUtil.ard(7)).withDataForCol(22, TestUtil.ard(7)).withDataForCol(23, TestUtil.ard(7)).withDataForCol(24, TestUtil.ard(7)).withDataForCol(25, TestUtil.ard(7)).withDataForCol(26, TestUtil.ard(7)).withDataForCol(27, TestUtil.ard(7)).withDataForCol(28, TestUtil.ard(Double.NaN)).build());
            Frame frMultipleNA100Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA100Fill", sess).withVecTypes(T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM, T_NUM).withDataForCol(0, TestUtil.ard(1)).withDataForCol(1, TestUtil.ard(2)).withDataForCol(2, TestUtil.ard(2)).withDataForCol(3, TestUtil.ard(3)).withDataForCol(4, TestUtil.ard(3)).withDataForCol(5, TestUtil.ard(3)).withDataForCol(6, TestUtil.ard(4)).withDataForCol(7, TestUtil.ard(4)).withDataForCol(8, TestUtil.ard(4)).withDataForCol(9, TestUtil.ard(5)).withDataForCol(10, TestUtil.ard(5)).withDataForCol(11, TestUtil.ard(5)).withDataForCol(12, TestUtil.ard(5)).withDataForCol(13, TestUtil.ard(5)).withDataForCol(14, TestUtil.ard(6)).withDataForCol(15, TestUtil.ard(6)).withDataForCol(16, TestUtil.ard(6)).withDataForCol(17, TestUtil.ard(6)).withDataForCol(18, TestUtil.ard(6)).withDataForCol(19, TestUtil.ard(6)).withDataForCol(20, TestUtil.ard(7)).withDataForCol(21, TestUtil.ard(7)).withDataForCol(22, TestUtil.ard(7)).withDataForCol(23, TestUtil.ard(7)).withDataForCol(24, TestUtil.ard(7)).withDataForCol(25, TestUtil.ard(7)).withDataForCol(26, TestUtil.ard(7)).withDataForCol(27, TestUtil.ard(Double.NaN)).build());
            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA, 0, "(h2o.fillna $frMultipleNA 'backward' 1 0)", true);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA1Fill, 1, "(h2o.fillna $frMultipleNA 'backward' 1 1)", true);// h2o.fillna with maxlen 1

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA2Fill, 2, "(h2o.fillna $frMultipleNA 'backward' 1 2)", true);// h2o.fillna with maxlen 2

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA3Fill, 3, "(h2o.fillna $frMultipleNA 'backward' 1 3)", true);// h2o.fillna with maxlen 3

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA100Fill, 100, "(h2o.fillna $frMultipleNA 'backward' 1 100)", true);// h2o.fillna with maxlen 100

        } finally {
            Scope.exit();
        }
    }

    /**
     * Purpose here is to carry out short tests to make sure the code works and the single thread code works.
     */
    @Test
    public void testBackwardMethodColAll() {
        Scope.enter();
        try {
            Session sess = new Session();
            Frame frAllNA = Scope.track(new TestFrameBuilder().withName("$fr", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN)).build());
            assertNFillNACorrect(sess, frAllNA, frAllNA, 0, "(h2o.fillna $fr 'backward' 0 0)", false);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, frAllNA, frAllNA, 100, "(h2o.fillna $fr 'backward' 0 100)", false);// h2o.fillna with maxlen 100

            // correct answers
            Frame fr1NA = Scope.track(new TestFrameBuilder().withName("$fr2", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 1.234)).build());
            Frame fr1NA1Ans = Scope.track(new TestFrameBuilder().withName("$fr1NA1Ans", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 1.234, 1.234)).build());
            Frame fr1NA3Ans = Scope.track(new TestFrameBuilder().withName("$fr1NA3Ans", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 1.234, 1.234, 1.234, 1.234)).build());
            Frame fr1NA100Ans = Scope.track(new TestFrameBuilder().withName("$fr1NA100Ans", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1.234, 1.234, 1.234, 1.234, 1.234, 1.234, 1.234, 1.234, 1.234, 1.234)).build());
            assertNFillNACorrect(sess, fr1NA, fr1NA, 0, "(h2o.fillna $fr 'backward' 0 0)", false);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, fr1NA, fr1NA1Ans, 1, "(h2o.fillna $fr 'backward' 0 1)", false);// h2o.fillna with maxlen 1

            assertNFillNACorrect(sess, fr1NA, fr1NA3Ans, 3, "(h2o.fillna $fr 'backward' 0 3)", false);// h2o.fillna with maxlen 3

            assertNFillNACorrect(sess, fr1NA, fr1NA100Ans, 100, "(h2o.fillna $fr 'backward' 0 100)", false);// h2o.fillna with maxlen 100

            // frame with multiple numbers and NA blocks
            Frame frMultipleNA = Scope.track(new TestFrameBuilder().withName("$frMultipleNA", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, Double.NaN, 2, Double.NaN, Double.NaN, 3, Double.NaN, Double.NaN, 4, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 5, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 6, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 7, Double.NaN)).build());
            // correct answer
            Frame frMultipleNA1Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA1Fill", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, 2, 2, Double.NaN, 3, 3, Double.NaN, 4, 4, Double.NaN, Double.NaN, Double.NaN, 5, 5, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 6, 6, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 7, 7, Double.NaN)).build());
            Frame frMultipleNA2Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA2Fill", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, 2, 2, 3, 3, 3, 4, 4, 4, Double.NaN, Double.NaN, 5, 5, 5, Double.NaN, Double.NaN, Double.NaN, 6, 6, 6, Double.NaN, Double.NaN, Double.NaN, Double.NaN, 7, 7, 7, Double.NaN)).build());
            Frame frMultipleNA3Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA3Fill", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, 2, 2, 3, 3, 3, 4, 4, 4, Double.NaN, 5, 5, 5, 5, Double.NaN, Double.NaN, 6, 6, 6, 6, Double.NaN, Double.NaN, Double.NaN, 7, 7, 7, 7, Double.NaN)).build());
            Frame frMultipleNA100Fill = Scope.track(new TestFrameBuilder().withName("$frMultipleNA100Fill", sess).withColNames("C1").withVecTypes(T_NUM).withDataForCol(0, TestUtil.ard(1, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, Double.NaN)).build());
            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA, 0, "(h2o.fillna $frMultipleNA 'backward' 0 0)", false);// h2o.fillna with maxlen 0

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA1Fill, 1, "(h2o.fillna $frMultipleNA 'backward' 0 1)", false);// h2o.fillna with maxlen 1

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA2Fill, 2, "(h2o.fillna $frMultipleNA 'backward' 0 2)", false);// h2o.fillna with maxlen 2

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA3Fill, 3, "(h2o.fillna $frMultipleNA 'backward' 0 3)", false);// h2o.fillna with maxlen 3

            assertNFillNACorrect(sess, frMultipleNA, frMultipleNA100Fill, 100, "(h2o.fillna $frMultipleNA 'backward' 0 100)", false);// h2o.fillna with maxlen 100

        } finally {
            Scope.exit();
        }
    }
}

