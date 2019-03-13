package water.parser;


import java.util.Random;
import org.junit.Assert;
import org.junit.Test;
import water.fvec.Chunk;


public class FVecParseWriterTest {
    private FVecParseWriter writer;

    @Test
    public void addNumColLosesPrecision() {
        // needs to be fixed: PUBDEV-5840
        writer.addNumCol(0, Math.PI);
        double h2oPI = writer._nvs[0].compress().atd(0);
        Assert.assertEquals(h2oPI, Math.PI, 1.0E-15);
        Assert.assertNotEquals(h2oPI, Math.PI, 0);
    }

    @Test
    public void testBackwardsCompatibility() {
        Random r = new Random(42);
        FVecParseWriter wr2 = FVecParseWriterTest.makeWriter();
        for (int i = 0; i < 2000; i++) {
            double v = r.nextDouble();
            wr2.addNumCol(0, v);
            FVecParseWriterTest.oldAddNumDecompose(v, 0, writer);
        }
        Chunk nc = writer._nvs[0].compress();
        Chunk nc2 = wr2._nvs[0].compress();
        for (int i = 0; i < (nc._len); i++) {
            Assert.assertEquals(nc2.atd(i), nc.atd(i), 0);
        }
    }

    @Test(timeout = 10000)
    public void addNumCol() {
        double[] values = new double[]{ 2.0E19, -123.123, 0.0, 1.0, Math.exp(1), 3 };
        for (double v : values)
            writer.addNumCol(0, v);

        Chunk ds = writer._nvs[0].compress();
        for (int i = 0; i < (values.length); i++)
            Assert.assertEquals((i + "th values differ"), values[i], ds.atd(i), 0);

    }
}

