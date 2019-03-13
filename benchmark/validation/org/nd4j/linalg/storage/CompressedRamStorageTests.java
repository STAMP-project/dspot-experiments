package org.nd4j.linalg.storage;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.storage.CompressedRamStorage;


/**
 *
 *
 * @author raver119@gmail.com
 */
@RunWith(Parameterized.class)
public class CompressedRamStorageTests extends BaseNd4jTest {
    private CompressedRamStorage<Integer> halfsStorageInplace;

    private CompressedRamStorage<Integer> halfsStorageNIP;

    private CompressedRamStorage<Integer> noopStorageInplace;

    private CompressedRamStorage<Integer> noopStorageNIP;

    public CompressedRamStorageTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testFP16StorageInplace1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        INDArray exp = array.dup();
        halfsStorageInplace.store(1, array);
        Assert.assertTrue(array.isCompressed());
        INDArray dec = halfsStorageInplace.get(1);
        Assert.assertEquals(exp, dec);
    }

    @Test
    public void testFP16StorageNIP1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        INDArray exp = array.dup();
        halfsStorageNIP.store(1, array);
        Assert.assertFalse(array.isCompressed());
        INDArray dec = halfsStorageNIP.get(1);
        Assert.assertEquals(exp, dec);
    }

    @Test
    public void testNoOpStorageInplace1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        INDArray exp = array.dup();
        noopStorageInplace.store(1, array);
        Assert.assertTrue(array.isCompressed());
        INDArray dec = noopStorageInplace.get(1);
        Assert.assertEquals(exp, dec);
    }

    @Test
    public void testNoOpStorageNIP1() throws Exception {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        INDArray exp = array.dup();
        noopStorageNIP.store(1, array);
        Assert.assertFalse(array.isCompressed());
        INDArray dec = noopStorageNIP.get(1);
        Assert.assertEquals(exp, dec);
    }
}

