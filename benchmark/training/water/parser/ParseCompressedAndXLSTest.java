package water.parser;


import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;
import water.fvec.Frame;
import water.fvec.NFSFileVec;
import water.fvec.Vec;

import static ParseSetup.NO_HEADER;


public class ParseCompressedAndXLSTest extends TestUtil {
    @Test
    public void testIris() {
        Frame k1 = null;
        Frame k2 = null;
        Frame k3 = null;
        Frame k4 = null;
        try {
            k1 = TestUtil.parse_test_file("smalldata/junit/iris.csv");
            k2 = TestUtil.parse_test_file("smalldata/junit/iris.xls");
            k3 = TestUtil.parse_test_file("smalldata/junit/iris.csv.gz");
            k4 = TestUtil.parse_test_file("smalldata/junit/iris.csv.zip");
            Assert.assertTrue(TestUtil.isBitIdentical(k1, k2));
            Assert.assertTrue(TestUtil.isBitIdentical(k2, k3));
            Assert.assertTrue(TestUtil.isBitIdentical(k3, k4));
        } finally {
            if (k1 != null)
                k1.delete();

            if (k2 != null)
                k2.delete();

            if (k3 != null)
                k3.delete();

            if (k4 != null)
                k4.delete();

        }
    }

    @Test
    public void testXLS() {
        Frame k1 = null;
        try {
            k1 = TestUtil.parse_test_file("smalldata/junit/benign.xls");
            Assert.assertEquals(14, k1.numCols());
            Assert.assertEquals(203, k1.numRows());
            k1.delete();
            k1 = TestUtil.parse_test_file("smalldata/junit/pros.xls");
            Assert.assertEquals(9, k1.numCols());
            Assert.assertEquals(380, k1.numRows());
        } finally {
            if (k1 != null)
                k1.delete();

        }
    }

    @Test
    public void testXLSBadArgs() {
        Frame k1 = null;
        try {
            NFSFileVec nfs = TestUtil.makeNfsFileVec("smalldata/airlines/AirlinesTest.csv.zip");
            byte[] ctypes = new byte[12];
            for (int i = 0; i < 12; i++)
                ctypes[i] = Vec.T_NUM;

            ParseSetup setup = // sep; ascii '4'
            // singleQuotes
            // check header
            // ncols
            new ParseSetup(DefaultParserProviders.XLS_INFO, ((byte) (52)), true, NO_HEADER, 12, new String[]{ "fYear", "fMonth", "fDayofMonth", "fDayOfWeek", "DepTime", "ArrTime", "UniqueCarrier", "Origin", "Dest", "Distance", "IsDepDelayed", "IsDepDelayed_REC" }, ctypes, null, null, null);
            try {
                k1 = ParseDataset.parse(Key.make(), new Key[]{ nfs._key }, true, setup, true)._job.get();
                Assert.assertTrue("Should have thrown ParseException since file isn't XLS file", false);// fail - should've thrown

                k1.delete();
            } catch (Throwable t) {
                Assert.assertTrue(((t instanceof ParseDataset.H2OParseException) || ((t.getCause()) instanceof ParseDataset.H2OParseException)));
            }
        } finally {
            if (k1 != null)
                k1.delete();

        }
    }
}

