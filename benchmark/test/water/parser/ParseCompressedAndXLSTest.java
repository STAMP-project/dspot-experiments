package water.parser;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


public class ParseCompressedAndXLSTest extends TestUtil {
    @Test
    public void testIris() {
        Key k1 = null;
        Key k2 = null;
        Key k3 = null;
        Key k4 = null;
        try {
            k1 = TestUtil.loadAndParseFile("csv.hex", "smalldata/iris/iris_wheader.csv");
            k2 = TestUtil.loadAndParseFile("xls.hex", "smalldata/iris/iris.xls");
            k3 = TestUtil.loadAndParseFile("gzip.hex", "smalldata/iris/iris_wheader.csv.gz");
            k4 = TestUtil.loadAndParseFile("zip.hex", "smalldata/iris/iris_wheader.csv.zip");
            Value v1 = DKV.get(k1);
            Value v2 = DKV.get(k2);
            Value v3 = DKV.get(k3);
            Value v4 = DKV.get(k4);
            Assert.assertTrue(v1.isBitIdentical(v2));
            Assert.assertTrue(v2.isBitIdentical(v3));
            Assert.assertTrue(v3.isBitIdentical(v4));
        } finally {
            Lockable.delete(k1);
            Lockable.delete(k2);
            Lockable.delete(k3);
            Lockable.delete(k4);
        }
    }
}

