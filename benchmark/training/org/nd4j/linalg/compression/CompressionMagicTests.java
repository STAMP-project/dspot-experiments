package org.nd4j.linalg.compression;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@RunWith(Parameterized.class)
public class CompressionMagicTests extends BaseNd4jTest {
    public CompressionMagicTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testMagicDecompression1() throws Exception {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "GZIP");
        compressed.muli(1.0);
        Assert.assertEquals(array, compressed);
    }

    @Test
    public void testMagicDecompression2() throws Exception {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "FLOAT16");
        compressed.muli(1.0);
        BaseNd4jTest.assertArrayEquals(array.data().asFloat(), compressed.data().asFloat(), 0.1F);
    }

    @Test
    public void testMagicDecompression3() throws Exception {
        INDArray array = Nd4j.linspace(1, 2500, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "INT16");
        compressed.muli(1.0);
        Assert.assertEquals(array, compressed);
    }

    @Test
    public void testMagicDecompression4() throws Exception {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "GZIP");
        for (int cnt = 0; cnt < (array.length()); cnt++) {
            float a = array.getFloat(cnt);
            float c = compressed.getFloat(cnt);
            Assert.assertEquals(a, c, 0.01F);
        }
    }

    @Test
    public void testDupSkipDecompression1() {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "GZIP");
        INDArray newArray = compressed.dup();
        Assert.assertTrue(newArray.isCompressed());
        Nd4j.getCompressor().decompressi(compressed);
        Nd4j.getCompressor().decompressi(newArray);
        Assert.assertEquals(array, compressed);
        Assert.assertEquals(array, newArray);
    }

    @Test
    public void testDupSkipDecompression2() {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "GZIP");
        INDArray newArray = compressed.dup('c');
        Assert.assertTrue(newArray.isCompressed());
        Nd4j.getCompressor().decompressi(compressed);
        Nd4j.getCompressor().decompressi(newArray);
        Assert.assertEquals(array, compressed);
        Assert.assertEquals(array, newArray);
    }

    @Test
    public void testDupSkipDecompression3() {
        INDArray array = Nd4j.linspace(1, 100, 2500);
        INDArray compressed = Nd4j.getCompressor().compress(array, "GZIP");
        INDArray newArray = compressed.dup('f');
        Assert.assertFalse(newArray.isCompressed());
        Nd4j.getCompressor().decompressi(compressed);
        // Nd4j.getCompressor().decompressi(newArray);
        Assert.assertEquals(array, compressed);
        Assert.assertEquals(array, newArray);
        Assert.assertEquals('f', newArray.ordering());
        Assert.assertEquals('c', compressed.ordering());
    }
}

