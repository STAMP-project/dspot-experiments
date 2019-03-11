package water;


import org.junit.Assert;
import org.junit.Test;
import water.fvec.Vec;


public class ExternalFrameUtilsTest {
    static final int[] EMPTY_ARI = new int[0];

    @Test
    public void testGetElemSizes() {
        Assert.assertArrayEquals("Size of single primitive element is [1]", TestUtil.ari(1), ExternalFrameUtils.getElemSizes(TestUtil.ar(ExternalFrameUtils.EXPECTED_BYTE), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("Size of two single primitive elements is [1,1]", TestUtil.ari(1, 1), ExternalFrameUtils.getElemSizes(TestUtil.ar(ExternalFrameUtils.EXPECTED_INT, ExternalFrameUtils.EXPECTED_BYTE), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("Size of a single vec is size of specified length", TestUtil.ari(1234), ExternalFrameUtils.getElemSizes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(1234)));
        Assert.assertArrayEquals("Size of a two vec should match passed vector lenghts ", TestUtil.ari(1234, 4567), ExternalFrameUtils.getElemSizes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR, ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(1234, 4567)));
        Assert.assertArrayEquals("Size of a two vec and primitive element should match passed vector lenghts and 1", TestUtil.ari(1234, 1, 4567), ExternalFrameUtils.getElemSizes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR, ExternalFrameUtils.EXPECTED_BYTE, ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(1234, 4567)));
    }

    @Test
    public void testVecTypesFromExpectedTypes() {
        ExternalFrameUtilsTest.assertNumericTypes(ExternalFrameUtils.EXPECTED_BYTE, ExternalFrameUtils.EXPECTED_CHAR, ExternalFrameUtils.EXPECTED_SHORT, ExternalFrameUtils.EXPECTED_INT, ExternalFrameUtils.EXPECTED_LONG, ExternalFrameUtils.EXPECTED_FLOAT, ExternalFrameUtils.EXPECTED_DOUBLE);
        Assert.assertArrayEquals("Boolean type is mapped to vector numeric type", TestUtil.ar(Vec.T_NUM), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_BOOL), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("String type is mapped to vector string type", TestUtil.ar(Vec.T_STR), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_STRING), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("String type is mapped to vector string type", TestUtil.ar(Vec.T_TIME), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_TIMESTAMP), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("Two primitive types are mapped to vector numeric types", TestUtil.ar(Vec.T_NUM, Vec.T_STR), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_BYTE, ExternalFrameUtils.EXPECTED_STRING), ExternalFrameUtilsTest.EMPTY_ARI));
        Assert.assertArrayEquals("Vector type is mapped to array of vector numeric types", TestUtil.ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(3)));
        Assert.assertArrayEquals("Two vector types are mapped to array of vector numeric types", TestUtil.ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_NUM), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR, ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(3, 2)));
        Assert.assertArrayEquals("Mixed types are mapped properly", TestUtil.ar(Vec.T_NUM, Vec.T_NUM, Vec.T_NUM, Vec.T_STR, Vec.T_NUM, Vec.T_NUM), ExternalFrameUtils.vecTypesFromExpectedTypes(TestUtil.ar(ExternalFrameUtils.EXPECTED_VECTOR, ExternalFrameUtils.EXPECTED_STRING, ExternalFrameUtils.EXPECTED_VECTOR), TestUtil.ari(3, 2)));
    }
}

