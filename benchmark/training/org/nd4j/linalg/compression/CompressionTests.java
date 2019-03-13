package org.nd4j.linalg.compression;


import CompressionType.LOSSY;
import DataBuffer.Type.COMPRESSED;
import DataBuffer.Type.FLOAT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.convolution.Convolution;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class CompressionTests extends BaseNd4jTest {
    public CompressionTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testCompressionView() {
        BasicNDArrayCompressor compressor = BasicNDArrayCompressor.getInstance().setDefaultCompression("UINT8");
        INDArray compressed = compressor.compress(Nd4j.create(1, 1, 1));
        INDArray reshaped = compressed.reshape(1, 1, 1, 1);
        // After a reshape, "reshaped" is no longer marked as compressed, but the underlying data buffer is of opType compressed
        // Because of that mismatch, this will fail with an illegal state exception:
        Convolution.im2col(reshaped, new int[]{ 1, 1 }, new int[]{ 1, 1 }, new int[]{ 1, 1 });
    }

    @Test
    public void testCompressionDescriptorSerde() {
        CompressionDescriptor descriptor = new CompressionDescriptor();
        descriptor.setCompressedLength(4);
        descriptor.setOriginalElementSize(4);
        descriptor.setNumberOfElements(4);
        descriptor.setCompressionAlgorithm("GZIP");
        descriptor.setOriginalLength(4);
        descriptor.setCompressionType(LOSSY);
        ByteBuffer toByteBuffer = descriptor.toByteBuffer();
        CompressionDescriptor fromByteBuffer = CompressionDescriptor.fromByteBuffer(toByteBuffer);
        Assert.assertEquals(descriptor, fromByteBuffer);
    }

    @Test
    public void testGzipInPlaceCompression() {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Nd4j.getCompressor().setDefaultCompression("GZIP");
        Nd4j.getCompressor().compressi(array);
        Assert.assertTrue(array.isCompressed());
        Nd4j.getCompressor().decompressi(array);
        TestCase.assertFalse(array.isCompressed());
    }

    @Test
    public void testFP16Compression1() {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("INT8");
        BasicNDArrayCompressor.getInstance().printAvailableCompressors();
        INDArray compr = BasicNDArrayCompressor.getInstance().compress(array);
        Assert.assertEquals(COMPRESSED, compr.data().dataType());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(5.0F, decomp.getFloat(4), 0.01F);
    }

    @Test
    public void testFP16Compression2() {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        DataBuffer exp = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("FLOAT16");
        DataBuffer compr = BasicNDArrayCompressor.getInstance().compress(buffer);
        Assert.assertEquals(COMPRESSED, compr.dataType());
        DataBuffer decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(5.0F, decomp.getFloat(4), 0.01F);
    }

    @Test
    public void testFP16Compression3() {
        INDArray buffer = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("FLOAT16");
        INDArray compr = BasicNDArrayCompressor.getInstance().compress(buffer);
        Assert.assertEquals(false, buffer.isCompressed());
        Assert.assertEquals(true, compr.isCompressed());
        Assert.assertEquals(COMPRESSED, compr.data().dataType());
        // assertNotEquals(exp, compr);
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(false, decomp.isCompressed());
        Assert.assertEquals(FLOAT, decomp.data().dataType());
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testUint8Compression1() {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        DataBuffer exp = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("UINT8");
        DataBuffer compr = BasicNDArrayCompressor.getInstance().compress(buffer);
        Assert.assertEquals(COMPRESSED, compr.dataType());
        DataBuffer decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(5.0F, decomp.getFloat(4), 0.01F);
    }

    @Test
    public void testUint8Compression2() {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 1005.0F });
        DataBuffer exp = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 1005.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("UINT8");
        DataBuffer compr = BasicNDArrayCompressor.getInstance().compress(buffer);
        Assert.assertEquals(COMPRESSED, compr.dataType());
        DataBuffer decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(255.0F, decomp.getFloat(4), 0.01F);
    }

    @Test
    public void testInt8Compression1() {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 1005.0F, -3.7F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("INT8");
        DataBuffer compr = BasicNDArrayCompressor.getInstance().compress(buffer);
        Assert.assertEquals(COMPRESSED, compr.dataType());
        DataBuffer decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(127.0F, decomp.getFloat(4), 0.01F);
        Assert.assertEquals((-3.0F), decomp.getFloat(5), 0.01F);
    }

    @Test
    public void testGzipCompression1() {
        INDArray array = Nd4j.linspace(1, 10000, 20000);
        INDArray exp = array.dup();
        BasicNDArrayCompressor.getInstance().setDefaultCompression("GZIP");
        INDArray compr = BasicNDArrayCompressor.getInstance().compress(array);
        Assert.assertEquals(COMPRESSED, compr.data().dataType());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(exp, array);
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testNoOpCompression1() {
        INDArray array = Nd4j.linspace(1, 10000, 20000);
        INDArray exp = Nd4j.linspace(1, 10000, 20000);
        INDArray mps = Nd4j.linspace(1, 10000, 20000);
        BasicNDArrayCompressor.getInstance().setDefaultCompression("NOOP");
        INDArray compr = BasicNDArrayCompressor.getInstance().compress(array);
        Assert.assertEquals(COMPRESSED, compr.data().dataType());
        Assert.assertTrue(compr.isCompressed());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(FLOAT, decomp.data().dataType());
        TestCase.assertFalse(decomp.isCompressed());
        TestCase.assertFalse(((decomp.data()) instanceof CompressedDataBuffer));
        TestCase.assertFalse(((exp.data()) instanceof CompressedDataBuffer));
        TestCase.assertFalse(exp.isCompressed());
        TestCase.assertFalse(((array.data()) instanceof CompressedDataBuffer));
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testFP8Compression1() {
        INDArray array = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("FLOAT8");
        BasicNDArrayCompressor.getInstance().printAvailableCompressors();
        INDArray compr = BasicNDArrayCompressor.getInstance().compress(array);
        Assert.assertEquals(COMPRESSED, compr.data().dataType());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compr);
        Assert.assertEquals(1.0F, decomp.getFloat(0), 0.01F);
        Assert.assertEquals(2.0F, decomp.getFloat(1), 0.01F);
        Assert.assertEquals(3.0F, decomp.getFloat(2), 0.01F);
        Assert.assertEquals(4.0F, decomp.getFloat(3), 0.01F);
        Assert.assertEquals(5.0F, decomp.getFloat(4), 0.01F);
    }

    @Test
    public void testJVMCompression1() throws Exception {
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("FLOAT16");
        INDArray compressed = BasicNDArrayCompressor.getInstance().compress(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Assert.assertNotEquals(null, compressed.data());
        Assert.assertNotEquals(null, compressed.shapeInfoDataBuffer());
        Assert.assertTrue(compressed.isCompressed());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compressed);
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testJVMCompression2() throws Exception {
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("INT8");
        INDArray compressed = BasicNDArrayCompressor.getInstance().compress(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Assert.assertNotEquals(null, compressed.data());
        Assert.assertNotEquals(null, compressed.shapeInfoDataBuffer());
        Assert.assertTrue(compressed.isCompressed());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compressed);
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testJVMCompression3() throws Exception {
        INDArray exp = Nd4j.create(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        BasicNDArrayCompressor.getInstance().setDefaultCompression("NOOP");
        INDArray compressed = BasicNDArrayCompressor.getInstance().compress(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        Assert.assertNotEquals(null, compressed.data());
        Assert.assertNotEquals(null, compressed.shapeInfoDataBuffer());
        Assert.assertTrue(compressed.isCompressed());
        INDArray decomp = BasicNDArrayCompressor.getInstance().decompress(compressed);
        Assert.assertEquals(exp, decomp);
    }

    @Test
    public void testThresholdCompressionZ() throws Exception {
        INDArray initial = Nd4j.create(1, 16384);
        for (int i = 0; i < 96; i++)
            initial.putScalar((i * 20), 1.0F);

        INDArray exp = Nd4j.create(1, 16384);
        for (int i = 0; i < 96; i++)
            exp.putScalar((i * 20), 0.1F);

        INDArray exp_d = Nd4j.create(1, 16384);
        for (int i = 0; i < 96; i++)
            exp_d.putScalar((i * 20), 0.9F);

        NDArrayCompressor compressor = Nd4j.getCompressor().getCompressor("THRESHOLD");
        compressor.configure(0.9);
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.9);
        Assert.assertEquals(exp, initial);
        log.info("Compressed length: {}", compressed.data().length());
        // log.info("Compressed: {}", Arrays.toString(compressed.data().asInt()));
        INDArray decompressed = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(compressed, decompressed);
        log.info("Decompressed length: {}", decompressed.lengthLong());
        Assert.assertEquals(exp_d, decompressed);
    }

    @Test
    public void testThresholdCompression1() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 0.0, 0.0, 0.001, -0.001, 0.0, 0.0 });
        INDArray exp_0 = Nd4j.create(6);
        INDArray exp_1 = initial.dup();
        NDArrayCompressor compressor = Nd4j.getCompressor().getCompressor("THRESHOLD");
        compressor.configure(0.001);
        INDArray compressed = compressor.compress(initial);
        log.info("Initial array: {}", Arrays.toString(initial.data().asFloat()));
        Assert.assertEquals(exp_0, initial);
        INDArray decompressed = compressor.decompress(compressed);
        Assert.assertEquals(exp_1, decompressed);
    }

    @Test
    public void testThresholdCompression2() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 1.0, 2.0, 0.0, 0.0, -1.0, -1.0 });
        INDArray exp_0 = Nd4j.create(new double[]{ 1.0 - 0.001, 2.0 - 0.001, 0.0, 0.0, (-1.0) + 0.001, (-1.0) + 0.001 });
        INDArray exp_1 = Nd4j.create(new double[]{ 0.001, 0.001, 0.0, 0.0, -0.001, -0.001 });
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        // NDArray compressed = Nd4j.getCompressor().compress(initial, "THRESHOLD");
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.001F);
        log.info("Initial array: {}", Arrays.toString(initial.data().asFloat()));
        Assert.assertEquals(exp_0, initial);
        INDArray decompressed = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(compressed, decompressed);
        log.info("Decompressed array: {}", Arrays.toString(decompressed.data().asFloat()));
        Assert.assertEquals(exp_1, decompressed);
    }

    @Test
    public void testThresholdCompression3() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ -1.0, -2.0, 0.0, 0.0, 1.0, 1.0 });
        INDArray exp_0 = Nd4j.create(new double[]{ (-1.0) + 0.001, (-2.0) + 0.001, 0.0, 0.0, 1.0 - 0.001, 1.0 - 0.001 });
        INDArray exp_1 = Nd4j.create(new double[]{ -0.001, -0.001, 0.0, 0.0, 0.001, 0.001 });
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.001F);
        INDArray copy = compressed.unsafeDuplication();
        log.info("Initial array: {}", Arrays.toString(initial.data().asFloat()));
        Assert.assertEquals(exp_0, initial);
        INDArray decompressed = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(compressed, decompressed);
        log.info("Decompressed array: {}", Arrays.toString(decompressed.data().asFloat()));
        Assert.assertEquals(exp_1, decompressed);
        INDArray decompressed_copy = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(copy, decompressed_copy);
        TestCase.assertFalse((decompressed == decompressed_copy));
        Assert.assertEquals(decompressed, decompressed_copy);
    }

    @Test
    public void testThresholdCompression4() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 1.0E-4, -1.0E-4, 0.0, 0.0, 1.0E-4, -1.0E-4 });
        INDArray exp_0 = initial.dup();
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.001F);
        log.info("Initial array: {}", Arrays.toString(initial.data().asFloat()));
        Assert.assertEquals(exp_0, initial);
        Assert.assertNull(compressed);
    }

    @Test
    public void testThresholdCompression5() throws Exception {
        INDArray initial = Nd4j.ones(1000);
        INDArray exp_0 = initial.dup();
        Nd4j.getExecutioner().commit();
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 1.0F, 100);
        Assert.assertEquals(104, compressed.data().length());
        Assert.assertNotEquals(exp_0, initial);
        Assert.assertEquals(900, initial.sumNumber().doubleValue(), 0.01);
    }

    @Test
    public void testThresholdCompression6() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 1.0, 2.0, 0.0, 0.0, -1.0, -1.0 });
        INDArray exp_0 = Nd4j.create(new double[]{ 1.0 - 0.001, 2.0 - 0.001, 0.0, 0.0, (-1.0) + 0.001, (-1.0) + 0.001 });
        INDArray exp_1 = Nd4j.create(new double[]{ 0.001, 0.001, 0.0, 0.0, -0.001, -0.001 });
        INDArray exp_2 = Nd4j.create(new double[]{ 0.002, 0.002, 0.0, 0.0, -0.002, -0.002 });
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        // NDArray compressed = Nd4j.getCompressor().compress(initial, "THRESHOLD");
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.001F);
        log.info("Initial array: {}", Arrays.toString(initial.data().asFloat()));
        Assert.assertEquals(exp_0, initial);
        INDArray decompressed = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(compressed, decompressed);
        log.info("Decompressed array: {}", Arrays.toString(decompressed.data().asFloat()));
        Assert.assertEquals(exp_1, decompressed);
        Nd4j.getExecutioner().thresholdDecode(compressed, decompressed);
        Assert.assertEquals(exp_2, decompressed);
    }

    @Test
    public void testThresholdSerialization1() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ -1.0, -2.0, 0.0, 0.0, 1.0, 1.0 });
        INDArray exp_0 = Nd4j.create(new double[]{ (-1.0) + 0.001, (-2.0) + 0.001, 0.0, 0.0, 1.0 - 0.001, 1.0 - 0.001 });
        INDArray exp_1 = Nd4j.create(new double[]{ -0.001, -0.001, 0.0, 0.0, 0.001, 0.001 });
        // Nd4j.getCompressor().getCompressor("THRESHOLD").configure(1e-3);
        INDArray compressed = Nd4j.getExecutioner().thresholdEncode(initial, 0.001F);
        Assert.assertEquals(exp_0, initial);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Nd4j.write(baos, compressed);
        INDArray serialized = Nd4j.read(new ByteArrayInputStream(baos.toByteArray()));
        INDArray decompressed_copy = Nd4j.create(initial.length());
        Nd4j.getExecutioner().thresholdDecode(serialized, decompressed_copy);
        Assert.assertEquals(exp_1, decompressed_copy);
    }

    @Test
    public void testBitmapEncoding1() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 0.0, 0.0, 0.001, -0.001, 0.0, 0.0 });
        INDArray exp_0 = Nd4j.create(6);
        INDArray exp_1 = initial.dup();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        log.info("Encoded: {}", Arrays.toString(enc.data().asInt()));
        Assert.assertEquals(exp_0, initial);
        Assert.assertEquals(5, enc.data().length());
        log.info("Encoded: {}", Arrays.toString(enc.data().asInt()));
        INDArray target = Nd4j.create(6);
        Nd4j.getExecutioner().bitmapDecode(enc, target);
        log.info("Target: {}", Arrays.toString(target.data().asFloat()));
        Assert.assertEquals(exp_1, target);
    }

    @Test
    public void testBitmapEncoding1_1() throws Exception {
        INDArray initial = Nd4j.create(15);
        INDArray exp_0 = Nd4j.create(6);
        INDArray exp_1 = initial.dup();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        // assertEquals(exp_0, initial);
        Assert.assertEquals(5, enc.data().length());
        initial = Nd4j.create(31);
        enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        Assert.assertEquals(6, enc.data().length());
        initial = Nd4j.create(32);
        enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        Assert.assertEquals(7, enc.data().length());
    }

    @Test
    public void testBitmapEncoding2() throws Exception {
        INDArray initial = Nd4j.create(40000000);
        INDArray target = Nd4j.create(initial.length());
        initial.addi(0.001);
        long time1 = System.currentTimeMillis();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        long time2 = System.currentTimeMillis();
        Nd4j.getExecutioner().bitmapDecode(enc, target);
        long time3 = System.currentTimeMillis();
        log.info("Encode time: {}", (time2 - time1));
        log.info("Decode time: {}", (time3 - time2));
    }

    @Test
    public void testBitmapEncoding3() throws Exception {
        INDArray initial = Nd4j.create(new double[]{ 0.0, -6.0E-4, 0.001, -0.001, 0.0, 0.0 });
        INDArray exp_0 = Nd4j.create(new double[]{ 0.0, -1.0E-4, 0.0, 0.0, 0.0, 0.0 });
        INDArray exp_1 = Nd4j.create(new double[]{ 0.0, -5.0E-4, 0.001, -0.001, 0.0, 0.0 });
        DataBuffer ib = Nd4j.getDataBufferFactory().createInt(5);
        INDArray enc = Nd4j.createArrayFromShapeBuffer(ib, initial.shapeInfoDataBuffer());
        long elements = Nd4j.getExecutioner().bitmapEncode(initial, enc, 0.001);
        log.info("Encoded: {}", Arrays.toString(enc.data().asInt()));
        BaseNd4jTest.assertArrayEquals(new int[]{ 6, 6, 981668463, 1, 655372 }, enc.data().asInt());
        Assert.assertEquals(3, elements);
        Assert.assertEquals(exp_0, initial);
        INDArray target = Nd4j.create(6);
        Nd4j.getExecutioner().bitmapDecode(enc, target);
        log.info("Target: {}", Arrays.toString(target.data().asFloat()));
        Assert.assertEquals(exp_1, target);
    }

    @Test
    public void testBitmapEncoding4() throws Exception {
        Nd4j.getRandom().setSeed(119);
        INDArray initial = Nd4j.rand(1, 10000, 0, 1, Nd4j.getRandom());
        INDArray exp_1 = initial.dup();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.1);
        Nd4j.getExecutioner().bitmapDecode(enc, initial);
        Assert.assertEquals(exp_1, initial);
    }

    @Test
    public void testBitmapEncoding5() throws Exception {
        Nd4j.getRandom().setSeed(119);
        INDArray initial = Nd4j.rand(1, 10000, (-1), (-0.5), Nd4j.getRandom());
        INDArray exp_0 = initial.dup().addi(0.1);
        INDArray exp_1 = initial.dup();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.1);
        Assert.assertEquals(exp_0, initial);
        Nd4j.getExecutioner().bitmapDecode(enc, initial);
        Assert.assertEquals(exp_1, initial);
    }

    @Test
    public void testBitmapEncoding6() throws Exception {
        Nd4j.getRandom().setSeed(119);
        INDArray initial = Nd4j.rand(1, 100000, (-1), 1, Nd4j.getRandom());
        INDArray exp_1 = initial.dup();
        INDArray enc = Nd4j.getExecutioner().bitmapEncode(initial, 0.001);
        // assertEquals(exp_0, initial);
        Nd4j.getExecutioner().bitmapDecode(enc, initial);
        Assert.assertEquals(exp_1, initial);
    }
}

