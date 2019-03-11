package ai.h2o.automl.targetencoding;


import TargetEncoder.DataLeakageHandlingStrategy.KFold;
import Vec.T_CAT;
import Vec.T_NUM;
import Vec.T_STR;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.Key;
import water.TestUtil;


public class TargetEncodingImmutabilityTest extends TestUtil {
    private Frame fr = null;

    @Test
    public void deepCopyTest() {
        fr = new TestFrameBuilder().withName("trainingFrame").withColNames("colA").withVecTypes(T_STR).withDataForCol(0, ar("a", "b", "c")).build();
        Frame trainCopy = fr.deepCopy(Key.make().toString());
        DKV.put(trainCopy);
        Assert.assertTrue(isBitIdentical(fr, trainCopy));
        trainCopy.vec(0).set(0, "d");
        Assert.assertFalse(isBitIdentical(fr, trainCopy));
        trainCopy.delete();
    }

    @Test
    public void ensureImmutabilityOfTheOriginalDatasetDuringApplicationOfTE() {
        String teColumnName = "ColA";
        String targetColumnName = "ColC";
        String foldColumn = "fold_column";
        Frame training = new TestFrameBuilder().withName("trainingFrame").withColNames(teColumnName, targetColumnName, foldColumn).withVecTypes(T_CAT, T_CAT, T_NUM).withDataForCol(0, ar("a", "b", "c", "d", "e", "b", "b")).withDataForCol(1, ar("2", "6", "6", "6", "6", "2", "2")).withDataForCol(2, ar(1, 2, 2, 3, 1, 2, 1)).build();
        String[] teColumns = new String[]{ teColumnName };
        TargetEncoder tec = new TargetEncoder(teColumns);
        Map<String, Frame> targetEncodingMap = tec.prepareEncodingMap(training, targetColumnName, foldColumn);
        Frame trainCopy = training.deepCopy(Key.make().toString());
        DKV.put(trainCopy);
        Frame resultWithEncoding = tec.applyTargetEncoding(training, targetColumnName, targetEncodingMap, KFold, foldColumn, false, 0, false, 1234);
        Assert.assertTrue(isBitIdentical(training, trainCopy));
        training.delete();
        trainCopy.delete();
        encodingMapCleanUp(targetEncodingMap);
        resultWithEncoding.delete();
    }
}

