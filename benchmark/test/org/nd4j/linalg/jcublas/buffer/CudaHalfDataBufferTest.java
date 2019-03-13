package org.nd4j.linalg.jcublas.buffer;


import DataBuffer.Type.HALF;
import DataBuffer.TypeEx.FLOAT;
import DataBuffer.TypeEx.FLOAT16;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.factory.Nd4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author raver119@gmail.com
 */
public class CudaHalfDataBufferTest {
    private static Logger logger = LoggerFactory.getLogger(CudaHalfDataBufferTest.class);

    @Test
    public void testConversion1() throws Exception {
        DataBuffer bufferOriginal = new CudaFloatDataBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        DataBuffer bufferHalfs = Nd4j.getNDArrayFactory().convertDataEx(FLOAT, bufferOriginal, FLOAT16);
        DataBuffer bufferRestored = Nd4j.getNDArrayFactory().convertDataEx(FLOAT16, bufferHalfs, FLOAT);
        CudaHalfDataBufferTest.logger.info("Buffer original: {}", Arrays.toString(bufferOriginal.asFloat()));
        CudaHalfDataBufferTest.logger.info("Buffer restored: {}", Arrays.toString(bufferRestored.asFloat()));
        Assert.assertArrayEquals(bufferOriginal.asFloat(), bufferRestored.asFloat(), 0.01F);
    }

    @Test
    public void testSerialization1() throws Exception {
        DataBuffer bufferOriginal = new CudaFloatDataBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        DataBuffer bufferHalfs = Nd4j.getNDArrayFactory().convertDataEx(FLOAT, bufferOriginal, FLOAT16);
        File tempFile = File.createTempFile("alpha", "11");
        tempFile.deleteOnExit();
        // now we serialize halfs, and we expect it to become floats on other side
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(tempFile.getAbsolutePath())))) {
            bufferHalfs.write(dos);
        }
        // loading data back from file
        DataInputStream dis = new DataInputStream(new FileInputStream(tempFile.getAbsoluteFile()));
        DataBuffer bufferRestored = Nd4j.createBuffer(bufferOriginal.length());
        bufferRestored.read(dis);
        Assert.assertArrayEquals(bufferOriginal.asFloat(), bufferRestored.asFloat(), 0.01F);
    }

    @Test
    public void testSerialization2() throws Exception {
        DataBuffer bufferOriginal = new CudaFloatDataBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        DataBuffer bufferHalfs = Nd4j.getNDArrayFactory().convertDataEx(FLOAT, bufferOriginal, FLOAT16);
        DataTypeUtil.setDTypeForContext(HALF);
        File tempFile = File.createTempFile("alpha", "11");
        tempFile.deleteOnExit();
        // now we serialize halfs, and we expect it to become floats on other side
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(tempFile.getAbsolutePath())))) {
            bufferHalfs.write(dos);
        }
        // loading data back from file
        DataInputStream dis = new DataInputStream(new FileInputStream(tempFile.getAbsoluteFile()));
        DataBuffer bufferRestored = Nd4j.createBuffer(bufferOriginal.length());
        bufferRestored.read(dis);
        Assert.assertEquals(bufferRestored.dataType(), HALF);
        DataTypeUtil.setDTypeForContext(DataBuffer.Type.FLOAT);
        DataBuffer bufferConverted = Nd4j.getNDArrayFactory().convertDataEx(FLOAT16, bufferRestored, FLOAT);
        Assert.assertArrayEquals(bufferOriginal.asFloat(), bufferConverted.asFloat(), 0.01F);
    }

    @Test
    public void testSingleConversions() throws Exception {
        CudaFloatDataBuffer bufferOriginal = new CudaFloatDataBuffer(new float[]{ 1.0F, 2.0F, 3.0F, 4.0F, 5.0F });
        short f = bufferOriginal.fromFloat(1.0F);
        float h = bufferOriginal.toFloat(((int) (f)));
        CudaHalfDataBufferTest.logger.info("Short F: {}, Float F: {}", f, h);
        Assert.assertEquals(1.0F, h, 0.001F);
    }
}

