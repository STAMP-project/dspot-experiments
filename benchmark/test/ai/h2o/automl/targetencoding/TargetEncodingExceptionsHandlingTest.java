package ai.h2o.automl.targetencoding;


import TargetEncoder.DataLeakageHandlingStrategy.KFold;
import TargetEncoder.DataLeakageHandlingStrategy.LeaveOneOut;
import TargetEncoder.DataLeakageHandlingStrategy.None;
import Vec.T_CAT;
import Vec.T_NUM;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;


/**
 * This test is checking for data leakage in case of exception during execution.
 */
public class TargetEncodingExceptionsHandlingTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void exceptionInPrepareEncodingTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "fold_column").withVecTypes(T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).withDataForCol(2, ar(1, 2, 2, 3)).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        String foldColumnName = "fold_column";
        int targetIndex = fr.find(targetColumnName);
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).filterOutNAsFromTargetColumn(fr, targetIndex);
        Map<String, Frame> targetEncodingMap = null;
        try {
            targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, foldColumnName);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }

    @Test
    public void exceptionInPrepareEncodingAtTheEndTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "fold_column").withVecTypes(T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).withDataForCol(2, ar(1, 2, 2, 3)).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        String foldColumnName = "fold_column";
        int targetIndex = fr.find(targetColumnName);
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).groupThenAggregateForNumeratorAndDenominator(ArgumentMatchers.any(Frame.class), ArgumentMatchers.eq(teColumns[0]), ArgumentMatchers.eq(foldColumnName), ArgumentMatchers.eq(targetIndex));
        Map<String, Frame> targetEncodingMap = null;
        try {
            targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, foldColumnName);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }

    @Test
    public void exceptionInApplyEncodingKFOLDInsideCycleTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "fold_column").withVecTypes(T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).withDataForCol(2, ar(1, 2, 2, 3)).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        String foldColumnName = "fold_column";
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Map<String, Frame> targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, foldColumnName);
        Frame resultWithEncoding = null;
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).getOutOfFoldData(ArgumentMatchers.any(Frame.class), ArgumentMatchers.eq(foldColumnName), ArgumentMatchers.anyLong());
        try {
            resultWithEncoding = tecSpy.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, KFold, foldColumnName, false, 0, false, 1234);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (resultWithEncoding != null)
            resultWithEncoding.delete();

        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }

    @Test
    public void exceptionInApplyEncodingKFOLDTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB", "fold_column").withVecTypes(T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).withDataForCol(2, ar(1, 2, 2, 3)).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        String foldColumnName = "fold_column";
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Map<String, Frame> targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, foldColumnName);
        Frame resultWithEncoding = null;
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).removeNumeratorAndDenominatorColumns(ArgumentMatchers.any(Frame.class));
        try {
            resultWithEncoding = tecSpy.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, KFold, foldColumnName, false, 0, false, 1234);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (resultWithEncoding != null)
            resultWithEncoding.delete();

        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }

    @Test
    public void exceptionIsNotCausingKeysLeakageInApplyEncodingLOOTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Map<String, Frame> targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = null;
        // In most cases it is better to throw exception at the end of the logic we are going to test.
        // This way we will create as much objects/frames as possible prior to the exception.
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).removeNumeratorAndDenominatorColumns(ArgumentMatchers.any(Frame.class));
        try {
            resultWithEncoding = tecSpy.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, LeaveOneOut, false, 0, false, 1234);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (resultWithEncoding != null)
            resultWithEncoding.delete();

        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }

    @Test
    public void exceptionIsNotCausingKeysLeakageInApplyEncodingNoneTest() {
        fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "b", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6")).build();
        String[] teColumns = new String[]{ "ColA" };
        String targetColumnName = "ColB";
        TargetEncoder tec = new TargetEncoder(teColumns);
        TargetEncoder tecSpy = Mockito.spy(tec);
        Map<String, Frame> targetEncodingMap = tecSpy.prepareEncodingMap(fr, targetColumnName, null);
        Frame resultWithEncoding = null;
        // In most cases it is better to throw exception at the end of the logic we are going to test.
        // This way we will create as much objects/frames as possible prior to the exception.
        Mockito.doThrow(new IllegalStateException("Fake exception")).when(tecSpy).removeNumeratorAndDenominatorColumns(ArgumentMatchers.any(Frame.class));
        // doThrow(new IllegalStateException("Fake exception")).when(tecSpy).foldColumnIsInEncodingMapCheck(nullable(String.class), any(Frame.class));
        try {
            resultWithEncoding = tecSpy.applyTargetEncoding(fr, targetColumnName, targetEncodingMap, None, false, 0, false, 1234);
            Assert.fail();
        } catch (IllegalStateException ex) {
            Assert.assertEquals("Fake exception", ex.getMessage());
        }
        if (resultWithEncoding != null)
            resultWithEncoding.delete();

        if (targetEncodingMap != null)
            encodingMapCleanUp(targetEncodingMap);

    }
}

