package ai.h2o.automl.targetencoding;


import TargetEncoder.DataLeakageHandlingStrategy.None;
import Vec.T_CAT;
import Vec.T_NUM;
import java.util.Map;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;


public class TargetEncodingNoneStrategyTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void targetEncoderNoneHoldoutApplyingTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColC";
        String foldColumn = "fold_column";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, "ColB", targetColumnName, foldColumn).withVecTypes(T_CAT, T_NUM, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 4)).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2)).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, foldColumn);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, None, foldColumn, false, 0, false, 1234);
        Vec vec = resultWithEncoding.vec(4);
        Vec expected = dvec(0.5, 0.5, 1, 1, 1);
        assertVecEquals(expected, vec, 1.0E-5);
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    @Test
    public void holdoutTypeNoneApplyWithNoiseTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColC";
        String foldColumn = "fold_column";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, "ColB", targetColumnName, foldColumn).withVecTypes(T_CAT, T_NUM, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 4)).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2)).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, foldColumn);
        printOutFrameAsTable(targetEncodingMap.get(teColumnName));
        // If we do not pass noise_level as parameter then it will be calculated according to the type of target column. For categorical target column it defaults to 1e-2
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, None, foldColumn, false, false, 1234);
        printOutFrameAsTable(resultWithEncoding);
        double expectedDifferenceDueToNoise = 0.01;
        Vec vec = resultWithEncoding.vec(4);
        Vec expected = dvec(0.5, 0.5, 1, 1, 1);
        assertVecEquals(expected, vec, expectedDifferenceDueToNoise);
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    @Test
    public void endToEndTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColC";
        Frame training = new TestFrameBuilder().withName("trainingFrame").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "c", "d", "e", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6", "6", "2", "2")).build();
        Frame holdout = new TestFrameBuilder().withName("holdoutFrame").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ar("2", "6", "6", "6", "6")).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(holdout, targetColumnName, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(training, targetColumnName, targetEncodingMap, None, false, 0, false, 1234);
        printOutFrameAsTable(resultWithEncoding);
        Vec vec = resultWithEncoding.vec(2);
        Vec expected = dvec(0.8, 0.8, 0.8, 0.5, 1, 1, 1);
        assertVecEquals(expected, vec, 1.0E-5);
        training.delete();
        holdout.delete();
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    // ------------------------ Multiple columns for target encoding -------------------------------------------------//
    @Test
    public void NoneHoldoutMultipleTEColumnsWithFoldColumnTest() {
        String targetColumnName = "ColC";
        String foldColumn = "fold_column";
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", targetColumnName, foldColumn).withVecTypes(T_CAT, T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ar("d", "e", "d", "e", "e")).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2)).build();
        String[] teColumns = new String[]{ "ColA", "ColB" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, foldColumn);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, None, foldColumn, false, 0, false, 1234);
        Vec expected = dvec(0.5, 1, 0.5, 1, 1);
        assertVecEquals(expected, resultWithEncoding.vec(4), 1.0E-5);
        Vec expected2 = dvec(0.5, 0.5, 1, 1, 1);
        assertVecEquals(expected2, resultWithEncoding.vec(5), 1.0E-5);
        expected.remove();
        expected2.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }
}

