package water.rapids.ast.prims.timeseries;


import hex.CreateFrame;
import org.junit.Assert;
import org.junit.Test;
import water.DKV;
import water.TestUtil;
import water.fvec.Frame;
import water.parser.BufferedString;
import water.rapids.Rapids;
import water.rapids.Val;


public class TimeSeriesTests extends TestUtil {
    private static Frame f = null;

    private static Frame fr1 = null;

    private static Frame fr2 = null;

    private static CreateFrame cf = null;

    @Test
    public void testIsax() {
        Val res1 = Rapids.exec((("(cumsum " + (TimeSeriesTests.f._key)) + " 1)"));// 

        TimeSeriesTests.fr1 = res1.getFrame();
        DKV.put(TimeSeriesTests.fr1);
        Val res2 = Rapids.exec((("(isax " + (TimeSeriesTests.fr1._key)) + " 10 10 0)"));// 10 words 10 max cardinality 0 optimize card

        TimeSeriesTests.fr2 = res2.getFrame();
        String expected = "0^10_0^10_0^10_0^10_5^10_7^10_8^10_9^10_9^10_8^10";
        final String actual = TimeSeriesTests.fr2.vec(0).atStr(new BufferedString(), 0).toString();
        Assert.assertEquals(expected, actual);
    }
}

