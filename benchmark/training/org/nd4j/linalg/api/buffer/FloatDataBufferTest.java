/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
package org.nd4j.linalg.api.buffer;


import AllocationPolicy.STRICT;
import DataBuffer.AllocationMode;
import DataType.FLOAT;
import LearningPolicy.NONE;
import OpExecutioner.ExecutionerType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.nio.ByteBuffer;
import lombok.val;
import org.bytedeco.javacpp.FloatPointer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.Indexer;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.memory.MemoryWorkspace;
import org.nd4j.linalg.api.memory.conf.WorkspaceConfiguration;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.util.SerializationUtils;


/**
 * Float data buffer tests
 *
 * This tests the float buffer data opType
 * Put all buffer related tests here
 *
 * @author Adam Gibson
 */
public class FloatDataBufferTest extends BaseNd4jTest {
    DataType initialType;

    public FloatDataBufferTest(Nd4jBackend backend) {
        super(backend);
        initialType = Nd4j.dataType();
    }

    @Test
    public void testPointerCreation() {
        FloatPointer floatPointer = new FloatPointer(1, 2, 3, 4);
        Indexer indexer = FloatIndexer.create(floatPointer);
        DataBuffer buffer = Nd4j.createBuffer(floatPointer, FLOAT, 4, indexer);
        DataBuffer other = Nd4j.createBuffer(new float[]{ 1, 2, 3, 4 });
        BaseNd4jTest.assertArrayEquals(other.asFloat(), buffer.asFloat(), 0.001F);
    }

    @Test
    public void testGetSet() {
        float[] d1 = new float[]{ 1, 2, 3, 4 };
        DataBuffer d = Nd4j.createBuffer(d1);
        float[] d2 = d.asFloat();
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), d1, d2, 0.1F);
    }

    @Test
    public void testSerialization() {
        DataBuffer buf = Nd4j.createBuffer(5);
        String fileName = "buf.ser";
        File file = new File(fileName);
        file.deleteOnExit();
        SerializationUtils.saveObject(buf, file);
        DataBuffer buf2 = SerializationUtils.readObject(file);
        // assertEquals(buf, buf2);
        BaseNd4jTest.assertArrayEquals(buf.asFloat(), buf2.asFloat(), 1.0E-4F);
        Nd4j.alloc = AllocationMode.DIRECT;
        buf = Nd4j.createBuffer(5);
        file.deleteOnExit();
        SerializationUtils.saveObject(buf, file);
        buf2 = SerializationUtils.readObject(file);
        // assertEquals(buf, buf2);
        BaseNd4jTest.assertArrayEquals(buf.asFloat(), buf2.asFloat(), 1.0E-4F);
    }

    @Test
    public void testDup() {
        float[] d1 = new float[]{ 1, 2, 3, 4 };
        DataBuffer d = Nd4j.createBuffer(d1);
        DataBuffer d2 = d.dup();
        BaseNd4jTest.assertArrayEquals(d.asFloat(), d2.asFloat(), 0.001F);
    }

    @Test
    public void testToNio() {
        DataBuffer buff = Nd4j.createTypedBuffer(new double[]{ 1, 2, 3, 4 }, FLOAT);
        Assert.assertEquals(4, buff.length());
        if ((buff.allocationMode()) == (AllocationMode.HEAP))
            return;

        ByteBuffer nio = buff.asNio();
        Assert.assertEquals(16, nio.capacity());
    }

    @Test
    public void testPut() {
        float[] d1 = new float[]{ 1, 2, 3, 4 };
        DataBuffer d = Nd4j.createBuffer(d1);
        d.put(0, 0.0);
        float[] result = new float[]{ 0, 2, 3, 4 };
        d1 = d.asFloat();
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), d1, result, 0.1F);
    }

    @Test
    public void testGetRange() {
        DataBuffer buffer = Nd4j.linspace(1, 5, 5).data();
        float[] get = buffer.getFloatsAt(0, 3);
        float[] data = new float[]{ 1, 2, 3 };
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), get, data, 0.1F);
        float[] get2 = buffer.asFloat();
        float[] allData = buffer.getFloatsAt(0, ((int) (buffer.length())));
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), get2, allData, 0.1F);
    }

    @Test
    public void testGetOffsetRange() {
        DataBuffer buffer = Nd4j.linspace(1, 5, 5).data();
        float[] get = buffer.getFloatsAt(1, 3);
        float[] data = new float[]{ 2, 3, 4 };
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), get, data, 0.1F);
        float[] allButLast = new float[]{ 2, 3, 4, 5 };
        float[] allData = buffer.getFloatsAt(1, ((int) (buffer.length())));
        BaseNd4jTest.assertArrayEquals(getFailureMessage(), allButLast, allData, 0.1F);
    }

    @Test
    public void testAsBytes() {
        INDArray arr = Nd4j.create(5);
        byte[] d = arr.data().asBytes();
        Assert.assertEquals(getFailureMessage(), (4 * 5), d.length);
        INDArray rand = Nd4j.rand(3, 3);
        rand.data().asBytes();
    }

    @Test
    public void testAssign() {
        DataBuffer assertion = Nd4j.createBuffer(new double[]{ 1, 2, 3 });
        DataBuffer one = Nd4j.createBuffer(new double[]{ 1 });
        DataBuffer twoThree = Nd4j.createBuffer(new double[]{ 2, 3 });
        DataBuffer blank = Nd4j.createBuffer(new double[]{ 0, 0, 0 });
        blank.assign(one, twoThree);
        BaseNd4jTest.assertArrayEquals(assertion.asFloat(), blank.asFloat(), 1.0E-4F);
    }

    @Test
    public void testReadWrite() throws Exception {
        DataBuffer assertion = Nd4j.createBuffer(new double[]{ 1, 2, 3 });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        assertion.write(dos);
        DataBuffer clone = assertion.dup();
        val stream = new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        val header = BaseDataBuffer.readHeader(stream);
        assertion.read(stream, header.getLeft(), header.getMiddle(), header.getRight());
        BaseNd4jTest.assertArrayEquals(assertion.asFloat(), clone.asFloat(), 1.0E-4F);
    }

    @Test
    public void testOffset() {
        DataBuffer create = Nd4j.createBuffer(new float[]{ 1, 2, 3, 4 }, 2);
        Assert.assertEquals(2, create.length());
        Assert.assertEquals(0, create.offset());
        Assert.assertEquals(3, create.getDouble(0), 0.1);
        Assert.assertEquals(4, create.getDouble(1), 0.1);
    }

    @Test
    public void testReallocation() {
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1, 2, 3, 4 });
        Assert.assertEquals(4, buffer.capacity());
        float[] old = buffer.asFloat();
        buffer.reallocate(6);
        float[] newBuf = buffer.asFloat();
        Assert.assertEquals(6, buffer.capacity());
        BaseNd4jTest.assertArrayEquals(old, newBuf, 1.0E-4F);
    }

    @Test
    public void testReallocationWorkspace() {
        WorkspaceConfiguration initialConfig = WorkspaceConfiguration.builder().initialSize(((10 * 1024L) * 1024L)).policyAllocation(STRICT).policyLearning(NONE).build();
        MemoryWorkspace workspace = Nd4j.getWorkspaceManager().getAndActivateWorkspace(initialConfig, "SOME_ID");
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1, 2, 3, 4 });
        Assert.assertTrue(buffer.isAttached());
        float[] old = buffer.asFloat();
        Assert.assertEquals(4, buffer.capacity());
        buffer.reallocate(6);
        Assert.assertEquals(6, buffer.capacity());
        float[] newBuf = buffer.asFloat();
        BaseNd4jTest.assertArrayEquals(old, newBuf, 1.0E-4F);
        workspace.close();
    }

    @Test
    public void testAddressPointer() {
        if ((Nd4j.getExecutioner().type()) != (ExecutionerType.NATIVE_CPU)) {
            return;
        }
        DataBuffer buffer = Nd4j.createBuffer(new float[]{ 1, 2, 3, 4 });
        DataBuffer wrappedBuffer = Nd4j.createBuffer(buffer, 1, 2);
        FloatPointer pointer = ((FloatPointer) (wrappedBuffer.addressPointer()));
        Assert.assertEquals(buffer.getFloat(1), pointer.get(0), 0.1);
        Assert.assertEquals(buffer.getFloat(2), pointer.get(1), 0.1);
        try {
            pointer.asBuffer().get(3);// Try to access element outside pointer capacity.

            Assert.fail("Accessing this address should not be allowed!");
        } catch (IndexOutOfBoundsException e) {
            // do nothing
        }
    }
}

