package water.rapids.ast.prims.time;


import Vec.T_NUM;
import Vec.T_STR;
import org.junit.Assert;
import org.junit.Test;
import water.Scope;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.TestFrameBuilder;
import water.rapids.Rapids;


public class AstAsDateTest extends TestUtil {
    @Test
    public void recognizeSingleDigitDay() {
        Frame frame = null;
        Frame convertedFrame = null;
        try {
            Scope.enter();
            frame = Scope.track(new TestFrameBuilder().withName("SingleDigitDayFrame").withColNames("C1").withVecTypes(T_STR).withDataForCol(0, new String[]{ "2013/11/05", "2013/11/5" }).build());
            convertedFrame = Rapids.exec("(as.Date SingleDigitDayFrame '%Y/%m/%d')").getFrame();
            Assert.assertNotNull(convertedFrame);
            Assert.assertEquals(T_NUM, convertedFrame.vec(0).get_type());
            Assert.assertEquals(2, convertedFrame.numRows());
            Assert.assertEquals(convertedFrame.vec(0).at8(0), convertedFrame.vec(0).at8(1));
        } finally {
            Scope.exit();
            if (frame != null)
                frame.remove();

            if (convertedFrame != null)
                convertedFrame.remove();

        }
    }

    @Test
    public void recognizeSingleDigitMonth() {
        Frame frame = null;
        Frame convertedFrame = null;
        try {
            Scope.enter();
            frame = Scope.track(new TestFrameBuilder().withName("SingleDigitDayFrame").withColNames("C1").withVecTypes(T_STR).withDataForCol(0, new String[]{ "2013/01/05", "2013/1/05" }).build());
            convertedFrame = Rapids.exec("(as.Date SingleDigitDayFrame '%Y/%m/%d')").getFrame();
            Assert.assertNotNull(convertedFrame);
            Assert.assertEquals(T_NUM, convertedFrame.vec(0).get_type());
            Assert.assertEquals(2, convertedFrame.numRows());
            Assert.assertEquals(convertedFrame.vec(0).at8(0), convertedFrame.vec(0).at8(1));
        } finally {
            Scope.exit();
            if (frame != null)
                frame.remove();

            if (convertedFrame != null)
                convertedFrame.remove();

        }
    }
}

