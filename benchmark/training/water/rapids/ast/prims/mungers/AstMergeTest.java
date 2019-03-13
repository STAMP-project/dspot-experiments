package water.rapids.ast.prims.mungers;


import Vec.T_CAT;
import Vec.T_NUM;
import Vec.T_STR;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;
import water.rapids.Val;


/**
 * *
 * This test is written by Andrey Spiridonov in JIRA PUBDEV-5924.
 */
public class AstMergeTest extends TestUtil {
    @Test
    public void mergeWithNaOnTheRightMapsToEverythingTest4() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("leftFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "e", null)).withDataForCol(1, TestUtil.ard((-1), 2, 3, 4, Double.NaN)).build();
            Scope.track(fr);
            Frame holdoutEncodingMap = new TestFrameBuilder().withName("holdoutEncodingMap").withColNames("ColB", "ColC").withVecTypes(T_NUM, T_STR).withDataForCol(0, TestUtil.ard(0, (-1), 2, Double.NaN)).withDataForCol(1, TestUtil.ar("str42", "no", "yes", "WTF")).build();
            Frame answer = new TestFrameBuilder().withColNames("ColB", "ColA", "ColC").withVecTypes(T_NUM, T_CAT, T_STR).withDataForCol(0, TestUtil.ard(Double.NaN, (-1), 2, 3, 4)).withDataForCol(1, TestUtil.ar(null, "a", "b", "c", "e")).withDataForCol(2, TestUtil.ar(null, "no", "yes", null, null)).build();
            Scope.track(answer);
            Scope.track(holdoutEncodingMap);
            String tree = "(merge leftFrame holdoutEncodingMap TRUE FALSE [1.0] [0.0] 'auto')";
            Val val = Rapids.exec(tree);
            Frame result = val.getFrame();
            Scope.track(result);
            System.out.println("\n\nLeft frame: ");
            printFrames(fr);
            System.out.println("\n\nRight frame: ");
            printFrames(holdoutEncodingMap);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(result);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(result);
            System.out.println("\n\nCorrect merged frame should be");
            printFrames(answer);
            assert TestUtil.isBitIdentical(result, answer) : "The two frames are not the same.";
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void mergeWithNaOnTheRightMapsToEverythingTest3() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("leftFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "e", null)).withDataForCol(1, TestUtil.ar(1, 2, 3, 4, 5)).build();
            Scope.track(fr);
            Frame holdoutEncodingMap = new TestFrameBuilder().withName("holdoutEncodingMap").withColNames("ColA", "ColC").withVecTypes(T_CAT, T_STR).withDataForCol(0, TestUtil.ar(null, "c", null, "g")).withDataForCol(1, TestUtil.ar("str42", "no", "yes", "WTF")).build();
            Frame answer = new TestFrameBuilder().withColNames("ColA", "ColB", "ColC").withVecTypes(T_CAT, T_NUM, T_STR).withDataForCol(0, TestUtil.ar(null, "a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(5, 1, 2, 3, 4)).withDataForCol(2, TestUtil.ar(null, null, null, "no", null)).build();
            Scope.track(answer);
            Scope.track(holdoutEncodingMap);
            String tree = "(merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0] 'auto')";
            Val val = Rapids.exec(tree);
            Frame result = val.getFrame();
            Scope.track(result);
            Frame sortedResult = result.sort(new int[]{ 0 });
            Scope.track(sortedResult);
            System.out.println("\n\nLeft frame: ");
            printFrames(fr);
            System.out.println("\n\nRight frame: ");
            printFrames(holdoutEncodingMap);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(sortedResult);
            System.out.println("\n\nCorrect merged frame should be");
            printFrames(answer);
            assert TestUtil.isBitIdentical(sortedResult, answer) : "The two frames are not the same!";
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void mergeWithNaOnTheRightMapsToEverythingTest2() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("leftFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "e", null)).withDataForCol(1, TestUtil.ar(1, 2, 3, 4, 5)).build();
            Scope.track(fr);
            Frame holdoutEncodingMap = new TestFrameBuilder().withName("holdoutEncodingMap").withColNames("ColA", "ColC").withVecTypes(T_CAT, T_STR).withDataForCol(0, TestUtil.ar(null, "c", null)).withDataForCol(1, TestUtil.ar("str42", "no", "yes")).build();
            Frame answer = new TestFrameBuilder().withColNames("ColA", "ColB", "ColC").withVecTypes(T_CAT, T_NUM, T_STR).withDataForCol(0, TestUtil.ar(null, "a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(5, 1, 2, 3, 4)).withDataForCol(2, TestUtil.ar(null, null, null, "no", null)).build();
            Scope.track(answer);
            Scope.track(holdoutEncodingMap);
            String tree = "(merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0] 'auto')";
            Val val = Rapids.exec(tree);
            Frame result = val.getFrame();
            Scope.track(result);
            Frame sortedResult = result.sort(new int[]{ 0 });
            Scope.track(sortedResult);
            System.out.println("\n\nLeft frame: ");
            printFrames(fr);
            System.out.println("\n\nRight frame: ");
            printFrames(holdoutEncodingMap);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(sortedResult);
            System.out.println("\n\nCorrect merged frame should be");
            printFrames(answer);
            assert TestUtil.isBitIdentical(sortedResult, answer) : "The two frames are not the same.";
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void mergeWithNaOnTheRightMapsToEverythingTest0() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("leftFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(1, 2, 3, 4)).build();
            Scope.track(fr);
            Frame holdoutEncodingMap = new TestFrameBuilder().withName("holdoutEncodingMap").withColNames("ColA", "ColC").withVecTypes(T_CAT, T_STR).withDataForCol(0, TestUtil.ar(null, "a", "b")).withDataForCol(1, TestUtil.ar("str42", "no", "yes")).build();
            Frame answer = new TestFrameBuilder().withColNames("ColA", "ColB", "ColC").withVecTypes(T_CAT, T_NUM, T_STR).withDataForCol(0, TestUtil.ar("a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(1, 2, 3, 4)).withDataForCol(2, TestUtil.ar("no", "yes", null, null)).build();
            Scope.track(answer);
            Scope.track(holdoutEncodingMap);
            String tree = "(merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0] 'auto')";
            Val val = Rapids.exec(tree);
            Frame result = val.getFrame();
            Scope.track(result);
            Frame sortedResult = result.sort(new int[]{ 0 });
            Scope.track(sortedResult);
            System.out.println("\n\nLeft frame: ");
            printFrames(fr);
            System.out.println("\n\nRight frame: ");
            printFrames(holdoutEncodingMap);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(sortedResult);
            System.out.println("\n\nCorrect merged frame should be");
            printFrames(answer);
            assert TestUtil.isBitIdentical(sortedResult, answer) : "The two frames are not the same.";
        } finally {
            Scope.exit();
        }
    }

    @Test
    public void mergeWithNaOnTheRightMapsToEverythingTest() {
        Scope.enter();
        try {
            Frame fr = new TestFrameBuilder().withName("leftFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, TestUtil.ar("a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(1, 2, 3, 4)).build();
            Scope.track(fr);
            Frame holdoutEncodingMap = new TestFrameBuilder().withName("holdoutEncodingMap").withColNames("ColA", "ColC").withVecTypes(T_CAT, T_STR).withDataForCol(0, TestUtil.ar(null, "c", null)).withDataForCol(1, TestUtil.ar("str42", "no", "yes")).build();
            Frame answer = new TestFrameBuilder().withColNames("ColA", "ColB", "ColC").withVecTypes(T_CAT, T_NUM, T_STR).withDataForCol(0, TestUtil.ar("a", "b", "c", "e")).withDataForCol(1, TestUtil.ar(1, 2, 3, 4)).withDataForCol(2, TestUtil.ar(null, null, "no", null)).build();
            Scope.track(answer);
            Scope.track(holdoutEncodingMap);
            String tree = "(merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0] 'auto')";
            Val val = Rapids.exec(tree);
            Frame result = val.getFrame();
            Scope.track(result);
            Frame sortedResult = result.sort(new int[]{ 0 });
            Scope.track(sortedResult);
            System.out.println("\n\nLeft frame: ");
            printFrames(fr);
            System.out.println("\n\nRight frame: ");
            printFrames(holdoutEncodingMap);
            System.out.println(("\n\nMerged frame with command (merge leftFrame holdoutEncodingMap TRUE FALSE [0.0] [0.0]" + " 'auto'): "));
            printFrames(sortedResult);
            System.out.println("\n\nCorrect merged frame should be");
            printFrames(answer);
            assert TestUtil.isBitIdentical(sortedResult, answer) : "The two frames are not the same.";
        } finally {
            Scope.exit();
        }
    }
}

