package ai.h2o.automl.targetencoding;


import Vec.T_CAT;
import Vec.T_NUM;
import java.io.File;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class TargetEncodingTargetColumnTest extends TestUtil {
    private Frame fr = null;

    // groupByTEColumnAndAggregateTest
    @Test
    public void binaryCategoricalTargetColumnWorksTest() {
        String tmpName = null;
        String tmpName2 = null;
        Frame fr2 = null;
        Frame parsedFrame = null;
        Frame parsedFrame2 = null;
        try {
            fr = // we need extra column because parsing from file will fail for csv with one column. For timeseries do we need more then one column?
            new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, ar("NO", "YES", "NO")).withDataForCol(1, ar(1, 2, 3)).build();
            fr2 = new TestFrameBuilder().withName("testFrame2").withColNames("ColA2", "ColB2").withVecTypes(T_CAT, T_NUM).withDataForCol(0, ar("YES", "NO", "NO")).withDataForCol(1, ar(1, 2, 3)).build();
            tmpName = UUID.randomUUID().toString();
            Frame.export(fr, tmpName, fr._key.toString(), true, 1);
            tmpName2 = UUID.randomUUID().toString();
            Frame.export(fr2, tmpName2, fr2._key.toString(), true, 1);
            try {
                Thread.sleep(1000);// TODO why we need to wait? otherwise `parsedFrame` is empty.

            } catch (InterruptedException ex) {
            }
            parsedFrame = parse_test_file(Key.make("parsed"), tmpName);
            parsedFrame2 = parse_test_file(Key.make("parsed2"), tmpName2);
            String[] domains = parsedFrame.vec(0).domain();
            String[] domains2 = parsedFrame2.vec(0).domain();
            Assert.assertArrayEquals(domains, domains2);
        } finally {
            new File(tmpName).delete();
            new File(tmpName2).delete();
            fr.delete();
            fr2.delete();
            parsedFrame.delete();
            parsedFrame2.delete();
        }
    }

    // BecauseRepresentationForCategoricalsIsNumericalInside frame
    @Test
    public void weCanSumTargetColumnTest() {
        String tmpName = null;
        Frame parsedFrame = null;
        try {
            fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_NUM).withDataForCol(0, ar("NO", "YES", "NO")).withDataForCol(1, ar(1, 2, 3)).build();
            tmpName = UUID.randomUUID().toString();
            Frame.export(fr, tmpName, fr._key.toString(), true, 1);
            try {
                Thread.sleep(1000);// TODO why we need to wait? otherwise `parsedFrame` is empty.

            } catch (InterruptedException ex) {
            }
            parsedFrame = parse_test_file(Key.make("parsed"), tmpName);
            Assert.assertEquals(0, parsedFrame.vec(0).at8(0));
            Assert.assertEquals(1, parsedFrame.vec(0).at8(1));
        } finally {
            new File(tmpName).delete();
            fr.delete();
            parsedFrame.delete();
        }
    }

    // Test that we can do sum and count on binary categorical column due to numerical representation under the hood.
    // TODO move to Target encoding test
    @Test
    public void groupThenAggregateWithoutFoldsForBinaryTargetTest() {
        String tmpName = null;
        Frame parsedFrame = null;
        try {
            fr = new TestFrameBuilder().withName("testFrame").withColNames("ColA", "ColB").withVecTypes(T_CAT, T_CAT).withDataForCol(0, ar("a", "a", "b")).withDataForCol(1, ar("NO", "YES", "NO")).build();
            tmpName = UUID.randomUUID().toString();
            Frame.export(fr, tmpName, fr._key.toString(), true, 1);
            try {
                Thread.sleep(1000);// TODO why we need to wait? otherwise `parsedFrame` is empty.

            } catch (InterruptedException ex) {
            }
            parsedFrame = parse_test_file(Key.make("parsed"), tmpName, true);
            String[] teColumns = new String[]{ "ColA" };
            TargetEncoder tec = new TargetEncoder(teColumns);
            Frame res = tec.groupThenAggregateForNumeratorAndDenominator(parsedFrame, teColumns[0], null, 1);
            Vec expectedSumColumn = vec(1, 0);
            Vec expectedCountColumn = vec(2, 1);
            assertVecEquals(expectedSumColumn, res.vec(1), 1.0E-5);
            assertVecEquals(expectedCountColumn, res.vec(2), 1.0E-5);
            expectedSumColumn.remove();
            expectedCountColumn.remove();
            res.delete();
        } finally {
            new File(tmpName).delete();
            fr.delete();
            parsedFrame.delete();
        }
    }

    @Test
    public void categoricalTargetHasCardinalityOfTwoTest() {
        String targetColumnName = "ColC";
        fr = new TestFrameBuilder().withName("testFrame").withColNames(targetColumnName).withVecTypes(T_CAT).withDataForCol(0, ar("2", "6", "6", "6", "6", "2")).build();
        Assert.assertEquals(2, fr.vec(0).cardinality());
        fr.delete();
    }
}

