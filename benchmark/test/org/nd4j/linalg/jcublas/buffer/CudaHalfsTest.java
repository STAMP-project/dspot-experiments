package org.nd4j.linalg.jcublas.buffer;


import DataBuffer.Type.FLOAT;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 *
 *
 * @author raver119@gmail.com
 */
@Ignore
public class CudaHalfsTest {
    @Test
    public void testSerialization1() throws Exception {
        INDArray array = Nd4j.linspace(1, 5, 10);
        File tempFile = File.createTempFile("alpha", "11");
        tempFile.deleteOnExit();
        // now we serialize halfs, and we expect it to become floats on other side
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(tempFile.getAbsolutePath())))) {
            Nd4j.write(array, dos);
        }
        // loading data back from file
        DataInputStream dis = new DataInputStream(new FileInputStream(tempFile.getAbsoluteFile()));
        INDArray restored = Nd4j.read(dis);
        Assert.assertEquals(array, restored);
    }

    @Test
    public void testSerialization2() throws Exception {
        INDArray array = Nd4j.linspace(1, 5, 10);
        File tempFile = File.createTempFile("alpha", "11");
        tempFile.deleteOnExit();
        // now we serialize halfs, and we expect it to become floats on other side
        try (DataOutputStream dos = new DataOutputStream(Files.newOutputStream(Paths.get(tempFile.getAbsolutePath())))) {
            Nd4j.write(array, dos);
        }
        // loading data back from file
        DataInputStream dis = new DataInputStream(new FileInputStream(tempFile.getAbsoluteFile()));
        DataTypeUtil.setDTypeForContext(FLOAT);
        INDArray exp = Nd4j.linspace(1, 5, 10);
        INDArray restored = Nd4j.read(dis);
        Assert.assertArrayEquals(exp.data().asFloat(), restored.data().asFloat(), 0.1F);
        Assert.assertEquals(FLOAT, exp.data().dataType());
    }

    @Test
    public void test2D1() throws Exception {
        float[][] array = new float[5][5];
        for (int i = 0; i < (array.length); i++) {
            for (int j = 0; j < (array[0].length); j++) {
                array[i][j] = i;
            }
        }
        // crate INDArray
        INDArray data = Nd4j.create(array);
        Assert.assertEquals(25, data.length());
        for (int i = 0; i < (array.length); i++) {
            INDArray row = data.getRow(i);
            for (int x = 0; x < (row.length()); x++) {
                Assert.assertEquals(((float) (i)), row.getFloat(x), 0.1F);
            }
        }
        System.out.println(data);
    }
}

