package ai.h2o.automl.targetencoding;


import TargetEncoder.DataLeakageHandlingStrategy.LeaveOneOut;
import Vec.T_CAT;
import Vec.T_NUM;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.fvec.Vec;


public class TargetEncodingLeaveOneOutStrategyTest extends TestUtil {
    private Frame fr = null;

    // TODO see PUBDEV-5941 regarding chunk layout
    @Test
    public void deletionDependsOnTheChunkLayoutTest() {
        fr = // fails if we set one single chunk `.withChunkLayout(3)`
        new TestFrameBuilder().withName("testFrame").withColNames("ColA").withVecTypes(T_CAT).withDataForCol(0, ar("a", "b", "a")).withChunkLayout(1, 2).build();
        Vec zeroVec = Vec.makeZero(fr.numRows());
        String nameOfAddedColumn = "someName";
        fr.add(nameOfAddedColumn, zeroVec);
        zeroVec.remove();
        fr.vec(nameOfAddedColumn).at(1);
    }

    @Test
    public void targetEncoderLOOHoldoutDivisionByZeroTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColC";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, "ColB", targetColumnName).withVecTypes(T_CAT, T_NUM, T_CAT).withDataForCol(0, ar("a", "b", "c", "d", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 5, 4)).withDataForCol(2, ar("2", "6", "6", "2", "6", "6")).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, false, 0.0, false, 1234);
        // For level `c` and `d` we got only one row... so after leave one out subtraction we get `0` for denominator. We need to use different formula(value) for the result.
        Assert.assertEquals(0.66666, resultWithEncoding.vec("ColA_te").at(4), 1.0E-5);
        Assert.assertEquals(0.66666, resultWithEncoding.vec("ColA_te").at(5), 1.0E-5);
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    @Test
    public void naValuesWithLOOStrategyTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColB";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", null, null, null)).withDataForCol(1, ar("2", "6", "6", "2", "6")).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        printOutColumnsMeta(fr);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncodings = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, false, 0.0, true, 1234);
        Vec expected = dvec(0.6, 0.6, 0.5, 1, 0.5);
        printOutFrameAsTable(resultWithEncodings);
        assertVecEquals(expected, resultWithEncodings.vec("ColA_te"), 1.0E-5);
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncodings.delete();
    }

    @Test
    public void emptyStringsAndNAsAreTreatedAsDifferentCategoriesTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColB";
        fr = // null and "" are different categories even though they look the same in printout
        new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "", "", null)).withDataForCol(1, ar("2", "6", "6", "2", "6")).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        printOutColumnsMeta(fr);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, false, 0.0, false, 1234);
        printOutFrameAsTable(resultWithEncoding);
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    // Test that NA and empty strings create same encoding. Imputed average is slightly different for some reason
    @Test
    public void comparisonBetweenNAsAndNonEmptyStringForLOOStrategyTest() {
        String teColumnName = "ColA";
        String targetColumnName = "ColB";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", null, null, null)).withDataForCol(1, ar("2", "6", "6", "2", "6")).build();
        Frame fr2 = new TestFrameBuilder().withName("testFrame2").withColNames(teColumnName, targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "na", "na", "na")).withDataForCol(1, ar("2", "6", "6", "2", "6")).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, false, 0.0, true, 1234);
        Map<String, Frame> targetEncodingMap2 = tec.prepareEncodingMap(fr2, targetColumnName, null);
        Frame resultWithEncoding2 = tec.applyTargetEncoding(fr2, targetColumnName, targetEncodingMap2, LeaveOneOut, false, 0.0, true, 1234);
        Frame sortedResult = resultWithEncoding.sort(new int[]{ 2 }, new int[]{ 2 });
        Frame sortedResult2 = resultWithEncoding2.sort(new int[]{ 2 }, new int[]{ 2 });
        assertVecEquals(sortedResult.vec("ColA_te"), sortedResult2.vec("ColA_te"), 1.0E-5);
        encodingMapCleanUp(targetEncodingMap);
        encodingMapCleanUp(targetEncodingMap2);
        fr2.delete();
        sortedResult.delete();
        sortedResult2.delete();
        resultWithEncoding.delete();
        resultWithEncoding2.delete();
    }

    // Test that empty strings create same encodings as nonempty strings
    @Test
    public void comparisonBetweenEmptyStringAndNonEmptyStringForLOOStrategyTest() {
        String targetColumnName = "ColB";
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "", "", "")).withDataForCol(1, ar("2", "6", "2", "2", "6")).build();
        Frame fr2 = new TestFrameBuilder().withName("testFrame2").withColNames("ColA", targetColumnName).withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "na", "na", "na")).withDataForCol(1, ar("2", "6", "2", "2", "6")).build();
        BlendingParams params = new BlendingParams(20, 10);
        String[] teColumns = new String[]{ "ColA" };
        TargetEncoder tec = new TargetEncoder(teColumns, params);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, true, 0.0, false, 1234);
        Map<String, Frame> targetEncodingMap2 = tec.prepareEncodingMap(fr2, targetColumnName, null);
        Frame resultWithEncoding2 = tec.applyTargetEncoding(fr2, targetColumnName, targetEncodingMap2, LeaveOneOut, true, 0.0, false, 1234);
        printOutFrameAsTable(resultWithEncoding);
        printOutFrameAsTable(resultWithEncoding2);
        assertVecEquals(resultWithEncoding.vec("ColA_te"), resultWithEncoding2.vec("ColA_te"), 1.0E-5);
        encodingMapCleanUp(targetEncodingMap);
        encodingMapCleanUp(targetEncodingMap2);
        fr2.delete();
        resultWithEncoding.delete();
        resultWithEncoding2.delete();
    }

    @Test
    public void targetEncoderLOOHoldoutSubtractCurrentRowTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "numerator", "denominator", "target").withVecTypes(T_CAT, T_NUM, T_NUM, T_CAT).withDataForCol(0, ar("a", "b", "b", "b", "a", "b")).withDataForCol(1, ard(1, 1, 4, 7, 4, 2)).withDataForCol(2, ard(1, 1, 4, 7, 4, 6)).withDataForCol(3, ar("2", "6", "6", "6", "6", null)).build();
        String[] teColumns = new String[]{ "" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Frame res = tec.subtractTargetValueForLOO(fr, "target");
        // We check here that for  `target column = NA` we do not subtract anything and for other cases we subtract current row's target value
        Vec vecNotSubtracted = vec(1, 0, 3, 6, 3, 2);
        assertVecEquals(vecNotSubtracted, res.vec(1), 1.0E-5);
        Vec vecSubtracted = vec(0, 0, 3, 6, 3, 6);
        assertVecEquals(vecSubtracted, res.vec(2), 1.0E-5);
        vecNotSubtracted.remove();
        vecSubtracted.remove();
        res.delete();
    }

    @Test
    public void targetEncoderLOOHoldoutApplyingTest() {
        String targetColumn = "ColC";
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", targetColumn).withVecTypes(T_CAT, T_NUM, T_CAT).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 4)).withDataForCol(2, ar("2", "6", "6", "6", "6")).build();
        String[] teColumns = new String[]{ "ColA" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumn, null);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumn, targetEncodingMap, LeaveOneOut, false, 0, false, 1234);
        Vec expected = vec(1, 0, 1, 1, 1);
        assertVecEquals(expected, resultWithEncoding.vec(3), 1.0E-5);
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    // Test if presence of the fold column breaks logic
    @Test
    public void targetEncoderLOOHoldoutApplyingWithFoldColumnTest() {
        String targetColumn = "ColC";
        String foldColumnName = "fold_column";
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", targetColumn, foldColumnName).withVecTypes(T_CAT, T_NUM, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 4)).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2)).build();
        String[] teColumns = new String[]{ "ColA" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumn, foldColumnName);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumn, targetEncodingMap, LeaveOneOut, foldColumnName, false, 0, false, 1234);
        Vec expected = vec(1, 0, 1, 1, 1);
        assertVecEquals(expected, resultWithEncoding.vec(4), 1.0E-5);
        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    @Test
    public void targetEncoderLOOApplyWithNoiseTest() {
        String targetColumn = "ColC";
        String foldColumnName = "fold_column";
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", targetColumn, foldColumnName).withVecTypes(T_CAT, T_NUM, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ard(1, 1, 4, 7, 4)).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2)).build();
        String[] teColumns = new String[]{ "ColA" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumn, foldColumnName);
        // If we do not pass noise_level as parameter then it will be calculated according to the type of target column. For categorical target column it defaults to 1e-2
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumn, targetEncodingMap, LeaveOneOut, foldColumnName, false, true, 1234);
        Vec expected = vec(1, 0, 1, 1, 1);
        double expectedDifferenceDueToNoise = 0.01;
        assertVecEquals(expected, resultWithEncoding.vec(4), expectedDifferenceDueToNoise);// TODO is it ok that encoding contains negative values?

        expected.remove();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }

    // ------------------------ Multiple columns for target encoding -------------------------------------------------//
    @Test
    public void LOOHoldoutMultipleTEColumnsWithFoldColumnTest() {
        String targetColumnName = "ColC";
        String foldColumnName = "fold_column";
        TestFrameBuilder frameBuilder = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", targetColumnName, foldColumnName).withVecTypes(T_CAT, T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b", "a")).withDataForCol(1, ar("d", "e", "d", "e", "e")).withDataForCol(2, ar("2", "6", "6", "6", "6")).withDataForCol(3, ar(1, 2, 2, 3, 2));
        fr = frameBuilder.withName("testFrame").build();
        String[] teColumns = new String[]{ "ColA", "ColB" };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(fr, targetColumnName, foldColumnName);
        Frame resultWithEncoding = tec.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, foldColumnName, false, 0.0, false, 1234);
        Frame sortedBy1 = resultWithEncoding.sort(new int[]{ 1 });
        Vec encodingForColumnA_Multiple = sortedBy1.vec(4);
        Frame sortedBy0 = resultWithEncoding.sort(new int[]{ 0 });
        Vec encodingForColumnB_Multiple = sortedBy0.vec(5);
        // Let's check it with Single TE version of the algorithm. So we rely here on a correctness of the single-column encoding.
        // For the first encoded column
        Frame frA = frameBuilder.withName("testFrameA").build();
        String[] indexForColumnA = new String[]{ "ColA" };
        TargetEncoder tecA = new TargetEncoder(indexForColumnA);
        Map<String, Frame> targetEncodingMapForColumn1 = tecA.prepareEncodingMap(frA, targetColumnName, foldColumnName);
        Frame resultWithEncodingForColumn1 = tecA.applyTargetEncoding(frA, targetColumnName, targetEncodingMapForColumn1, LeaveOneOut, foldColumnName, false, 0, false, 1234);
        Frame sortedSingleColumn1ByColA = resultWithEncodingForColumn1.sort(new int[]{ 0 });
        Vec encodingForColumnA_Single = sortedSingleColumn1ByColA.vec(4);
        assertVecEquals(encodingForColumnA_Single, encodingForColumnA_Multiple, 1.0E-5);
        // For the second encoded column
        Frame frB = frameBuilder.withName("testFrameB").build();
        String[] indexForColumnB = new String[]{ "ColB" };
        TargetEncoder tecB = new TargetEncoder(indexForColumnB);
        Map<String, Frame> targetEncodingMapForColumn2 = tecB.prepareEncodingMap(frB, targetColumnName, foldColumnName);
        Frame resultWithEncodingForColumn2 = tecB.applyTargetEncoding(frB, targetColumnName, targetEncodingMapForColumn2, LeaveOneOut, foldColumnName, false, 0, false, 1234);
        Frame sortedSingleColumn2ByColA = resultWithEncodingForColumn2.sort(new int[]{ 0 });
        Vec encodingForColumnB_Single = sortedSingleColumn2ByColA.vec(4);
        assertVecEquals(encodingForColumnB_Single, encodingForColumnB_Multiple, 1.0E-5);
        sortedBy0.delete();
        sortedBy1.delete();
        sortedSingleColumn1ByColA.delete();
        sortedSingleColumn2ByColA.delete();
        encodingMapCleanUp(targetEncodingMap);
        encodingMapCleanUp(targetEncodingMapForColumn1);
        encodingMapCleanUp(targetEncodingMapForColumn2);
        frA.delete();
        frB.delete();
        resultWithEncoding.delete();
        resultWithEncodingForColumn1.delete();
        resultWithEncodingForColumn2.delete();
    }
}

