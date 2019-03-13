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
package org.datavec.api.writable;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import org.datavec.api.transform.metadata.NDArrayMetaData;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Created by Alex on 02/06/2017.
 */
public class TestNDArrayWritableAndSerialization {
    @Test
    public void testIsValid() {
        NDArrayMetaData meta = new NDArrayMetaData("col", new long[]{ 1, 10 });
        NDArrayWritable valid = new NDArrayWritable(Nd4j.create(1, 10));
        NDArrayWritable invalid = new NDArrayWritable(Nd4j.create(1, 5));
        NDArrayWritable invalid2 = new NDArrayWritable(null);
        Assert.assertTrue(meta.isValid(valid));
        Assert.assertFalse(meta.isValid(invalid));
        Assert.assertFalse(meta.isValid(invalid2));
        Assert.assertTrue(meta.isValid(valid.get()));
        Assert.assertFalse(meta.isValid(invalid.get()));
        Assert.assertFalse(meta.isValid(invalid2.get()));
    }

    @Test
    public void testWritableSerializationSingle() throws Exception {
        INDArray arrC = Nd4j.rand(new int[]{ 1, 10 }, 'c');
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput da = new DataOutputStream(baos);
        NDArrayWritable wC = new NDArrayWritable(arrC);
        wC.write(da);
        byte[] b = baos.toByteArray();
        NDArrayWritable w2C = new NDArrayWritable();
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInput din = new DataInputStream(bais);
        w2C.readFields(din);
        Assert.assertEquals(arrC, w2C.get());
    }

    @Test
    public void testWritableSerialization() throws Exception {
        INDArray arrC = Nd4j.rand(new int[]{ 10, 20 }, 'c');
        INDArray arrF = Nd4j.rand(new int[]{ 10, 20 }, 'f');
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput da = new DataOutputStream(baos);
        NDArrayWritable wC = new NDArrayWritable(arrC);
        NDArrayWritable wF = new NDArrayWritable(arrF);
        wC.write(da);
        wF.write(da);
        byte[] b = baos.toByteArray();
        NDArrayWritable w2C = new NDArrayWritable();
        NDArrayWritable w2F = new NDArrayWritable();
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        DataInput din = new DataInputStream(bais);
        w2C.readFields(din);
        w2F.readFields(din);
        Assert.assertEquals(arrC, w2C.get());
        Assert.assertEquals(arrF, w2F.get());
    }

    @Test
    public void testWritableEqualsHashCodeOrdering() throws Exception {
        // NDArrayWritable implements WritableComparable - we need to make sure this operates as expected...
        // First: check C vs. F order, same contents
        INDArray arrC = Nd4j.rand(new int[]{ 10, 20 }, 'c');
        INDArray arrF = arrC.dup('f');
        NDArrayWritable wC = new NDArrayWritable(arrC);
        NDArrayWritable wF = new NDArrayWritable(arrF);
        Assert.assertEquals(wC, wF);
        Assert.assertEquals(wC.hashCode(), wF.hashCode());
        int compare = wC.compareTo(wF);
        Assert.assertEquals(0, compare);
        // Check order conventions:
        // Null first
        // Then smallest rank first
        // Then smallest length first
        // Then sort by shape
        // Then sort by contents, element-wise
        Assert.assertEquals((-1), new NDArrayWritable(null).compareTo(new NDArrayWritable(Nd4j.create(1))));
        Assert.assertEquals((-1), compareTo(new NDArrayWritable(Nd4j.create(1, 1, 1))));
        Assert.assertEquals((-1), compareTo(new NDArrayWritable(Nd4j.create(1, 2))));
        Assert.assertEquals((-1), compareTo(new NDArrayWritable(Nd4j.create(3, 1))));
        Assert.assertEquals((-1), compareTo(new NDArrayWritable(Nd4j.create(new double[]{ 1.0, 2.0, 3.1 }))));
        Assert.assertEquals(1, new NDArrayWritable(Nd4j.create(1)).compareTo(new NDArrayWritable(null)));
        Assert.assertEquals(1, compareTo(new NDArrayWritable(Nd4j.create(1, 1))));
        Assert.assertEquals(1, compareTo(new NDArrayWritable(Nd4j.create(1, 1))));
        Assert.assertEquals(1, compareTo(new NDArrayWritable(Nd4j.create(1, 3))));
        Assert.assertEquals(1, compareTo(new NDArrayWritable(Nd4j.create(new double[]{ 1.0, 2.0, 3.0 }))));
    }
}

