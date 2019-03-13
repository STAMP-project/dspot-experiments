package org.nd4j.linalg.api.buffer;


import AllocationPolicy.STRICT;
import DataBuffer.Type.INT;
import DataBuffer.Type.LONG;
import LearningPolicy.NONE;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Tests for INT INDArrays and DataBuffers serialization
 *
 * @author raver119@gmail.com
 */
public class IntDataBufferTests extends BaseNd4jTest {
    public IntDataBufferTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testBasicSerde1() throws Exception {
        DataBuffer dataBuffer = Nd4j.createBuffer(new int[]{ 1, 2, 3, 4, 5 });
        DataBuffer shapeBuffer = Nd4j.getShapeInfoProvider().createShapeInformation(new int[]{ 1, 5 }).getFirst();
        INDArray intArray = Nd4j.createArrayFromShapeBuffer(dataBuffer, shapeBuffer);
        File tempFile = File.createTempFile("test", "test");
        tempFile.deleteOnExit();
        Nd4j.saveBinary(intArray, tempFile);
        InputStream stream = new FileInputStream(tempFile);
        BufferedInputStream bis = new BufferedInputStream(stream);
        DataInputStream dis = new DataInputStream(bis);
        INDArray loaded = Nd4j.read(dis);
        Assert.assertEquals(INT, loaded.data().dataType());
        Assert.assertEquals(LONG, loaded.shapeInfoDataBuffer().dataType());
        Assert.assertEquals(intArray.data().length(), loaded.data().length());
        BaseNd4jTest.assertArrayEquals(intArray.data().asInt(), loaded.data().asInt());
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testOpDiscarded() throws Exception {
        DataBuffer dataBuffer = Nd4j.createBuffer(new int[]{ 1, 2, 3, 4, 5 });
        DataBuffer shapeBuffer = Nd4j.getShapeInfoProvider().createShapeInformation(new int[]{ 1, 5 }).getFirst();
        INDArray intArray = Nd4j.createArrayFromShapeBuffer(dataBuffer, shapeBuffer);
        intArray.add(10.0F);
    }

    @Test
    public void testReallocation() {
        DataBuffer buffer = Nd4j.createBuffer(new int[]{ 1, 2, 3, 4 });
        Assert.assertEquals(4, buffer.capacity());
        int[] old = buffer.asInt();
        buffer.reallocate(6);
        Assert.assertEquals(6, buffer.capacity());
        BaseNd4jTest.assertArrayEquals(old, buffer.asInt());
    }

    @Test
    public void testReallocationWorkspace() {
        WorkspaceConfiguration initialConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024L) * 1024L)).policyAllocation(STRICT).policyLearning(NONE).build();
        MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace(initialConfig, "SOME_ID");
        DataBuffer buffer = Nd4j.createBuffer(new int[]{ 1, 2, 3, 4 });
        int[] old = buffer.asInt();
        Assert.assertTrue(buffer.isAttached());
        Assert.assertEquals(4, buffer.capacity());
        buffer.reallocate(6);
        Assert.assertEquals(6, buffer.capacity());
        BaseNd4jTest.assertArrayEquals(old, buffer.asInt());
        workspace.close();
    }
}

