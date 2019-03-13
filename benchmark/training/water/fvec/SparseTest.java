package water.fvec;


import java.util.Arrays;
import java.util.Random;
import java.util.TreeSet;
import org.junit.Assert;
import org.junit.Test;
import water.TestUtil;


/**
 * Created by tomasnykodym on 3/28/14.
 */
public class SparseTest extends TestUtil {
    @Test
    public void doChunkTest() {
        double[] binary_vals = new double[1024];
        double[] valsZeroSmall;
        double[] valsZero;
        double[] valsNA;
        double[] float_vals;
        double[] double_vals;
        int[] nzs_ary;
        TestUtil.stall_till_cloudsize(1);
        valsZeroSmall = new double[1024];
        valsZero = new double[1024];
        valsNA = new double[1024];
        double[] valsNASmall = new double[1024];
        double[] valsBig = new double[1024];
        double[] valsNABig = new double[1024];
        float_vals = new double[1024];
        double_vals = new double[1024];
        double[] float_vals_na = new double[1024];
        double[] double_vals_na = new double[1024];
        Arrays.fill(float_vals_na, Double.NaN);
        Arrays.fill(double_vals_na, Double.NaN);
        Arrays.fill(valsNA, Double.NaN);
        Arrays.fill(valsNASmall, Double.NaN);
        Arrays.fill(valsNABig, Double.NaN);
        Random rnd = new Random(54321);
        TreeSet<Integer> nzs = new TreeSet<>();
        for (int i = 0; i < 96; i++) {
            int x = rnd.nextInt(valsZero.length);
            if (nzs.add(x)) {
                binary_vals[x] = 1;
                valsNA[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextInt() : 0;
                valsZero[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextInt() : Double.NaN;
                valsZeroSmall[x] = ((rnd.nextDouble()) < 0.95) ? (rnd.nextInt(60000)) - 30000 : Double.NaN;
                valsNASmall[x] = ((rnd.nextDouble()) < 0.95) ? (rnd.nextInt(60000)) - 30000 : 0;
                valsBig[x] = ((rnd.nextDouble()) < 0.95) ? ((double) ((long) ((rnd.nextDouble()) * (Long.MAX_VALUE)))) : Double.NaN;
                valsNABig[x] = ((rnd.nextDouble()) < 0.95) ? ((double) ((long) ((rnd.nextDouble()) * (Long.MAX_VALUE)))) : 0;
                float_vals[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextFloat() : Double.NaN;
                double_vals[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextDouble() : Double.NaN;
                float_vals_na[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextFloat() : 0;
                double_vals_na[x] = ((rnd.nextDouble()) < 0.95) ? rnd.nextDouble() : 0;
            }
        }
        nzs_ary = new int[nzs.size()];
        int k = 0;
        for (Integer i : nzs)
            nzs_ary[(k++)] = i;

        CXIChunk binaryChunk = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, binary_vals, nzs_ary, false)));
        Assert.assertEquals(2, binaryChunk._elem_sz);
        CXIChunk binaryChunkLong = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, binary_vals, nzs_ary, false, (1 << 20))));
        Assert.assertEquals(4, binaryChunkLong._elem_sz);
        SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsZero, nzs_ary, false);
        SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsNA, nzs_ary, true);
        CXIChunk smallZero = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsZeroSmall, nzs_ary, false)));
        Assert.assertEquals(4, smallZero._elem_sz);
        CXIChunk smallZero2 = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsZeroSmall, nzs_ary, false, 60000)));
        Assert.assertEquals(4, smallZero2._elem_sz);
        CXIChunk smallZeroLong = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsZeroSmall, nzs_ary, false, (1 << 20))));
        Assert.assertEquals(8, smallZeroLong._elem_sz);
        CXIChunk smallNA = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsNASmall, nzs_ary, true)));
        Assert.assertEquals(4, smallNA._elem_sz);
        CXIChunk bigZero = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsBig, nzs_ary, false)));
        Assert.assertEquals(12, bigZero._elem_sz);
        CXIChunk bigNAZero = ((CXIChunk) (SparseTest.makeAndTestSparseChunk(CXIChunk.class, valsNABig, nzs_ary, true)));
        Assert.assertEquals(12, bigNAZero._elem_sz);
        CXFChunk floats = ((CXFChunk) (SparseTest.makeAndTestSparseChunk(CXFChunk.class, float_vals, nzs_ary, false)));
        Assert.assertEquals(8, floats._elem_sz);
        CXFChunk doubles = ((CXFChunk) (SparseTest.makeAndTestSparseChunk(CXFChunk.class, double_vals, nzs_ary, false)));
        Assert.assertEquals(12, doubles._elem_sz);
        CXFChunk floats_na = ((CXFChunk) (SparseTest.makeAndTestSparseChunk(CXFChunk.class, float_vals_na, nzs_ary, true)));
        Assert.assertEquals(8, floats_na._elem_sz);
        CXFChunk doubles_na = ((CXFChunk) (SparseTest.makeAndTestSparseChunk(CXFChunk.class, double_vals_na, nzs_ary, true)));
        Assert.assertEquals(12, doubles_na._elem_sz);
    }
}

