package org.nd4j.linalg.api.ndarray;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;
import org.nd4j.linalg.indexing.NDArrayIndex;


/**
 * Created by Alex on 28/04/2016.
 */
@RunWith(Parameterized.class)
public class TestSerialization extends BaseNd4jTest {
    public TestSerialization(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testSerializationFullArrayNd4jWriteRead() throws Exception {
        int length = 100;
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', 10, 10);
        INDArray arrF = Nd4j.linspace(1, length, length).reshape('f', 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            Nd4j.write(arrC, dos);
        }
        byte[] bytesC = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            Nd4j.write(arrF, dos);
        }
        byte[] bytesF = baos.toByteArray();
        INDArray arr2C;
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytesC))) {
            arr2C = Nd4j.read(dis);
        }
        INDArray arr2F;
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytesF))) {
            arr2F = Nd4j.read(dis);
        }
        Assert.assertEquals(arrC, arr2C);
        Assert.assertEquals(arrF, arr2F);
    }

    @Test
    public void testSerializationFullArrayJava() throws Exception {
        int length = 100;
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', 10, 10);
        INDArray arrF = Nd4j.linspace(1, length, length).reshape('f', 10, 10);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(arrC);
        }
        byte[] bytesC = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(arrF);
        }
        byte[] bytesF = baos.toByteArray();
        INDArray arr2C;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytesC))) {
            arr2C = ((INDArray) (ois.readObject()));
        }
        INDArray arr2F;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytesF))) {
            arr2F = ((INDArray) (ois.readObject()));
        }
        Assert.assertEquals(arrC, arr2C);
        Assert.assertEquals(arrF, arr2F);
    }

    @Test
    public void testSerializationOnViewsNd4jWriteRead() throws Exception {
        int length = 100;
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', 10, 10);
        INDArray arrF = Nd4j.linspace(1, length, length).reshape('f', 10, 10);
        INDArray subC = arrC.get(NDArrayIndex.interval(5, 10), NDArrayIndex.interval(5, 10));
        INDArray subF = arrF.get(NDArrayIndex.interval(5, 10), NDArrayIndex.interval(5, 10));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            Nd4j.write(subC, dos);
        }
        byte[] bytesC = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        try (DataOutputStream dos = new DataOutputStream(baos)) {
            Nd4j.write(subF, dos);
        }
        byte[] bytesF = baos.toByteArray();
        INDArray arr2C;
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytesC))) {
            arr2C = Nd4j.read(dis);
        }
        INDArray arr2F;
        try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytesF))) {
            arr2F = Nd4j.read(dis);
        }
        Assert.assertEquals(subC, arr2C);
        Assert.assertEquals(subF, arr2F);
    }

    @Test
    public void testSerializationOnViewsJava() throws Exception {
        int length = 100;
        INDArray arrC = Nd4j.linspace(1, length, length).reshape('c', 10, 10);
        INDArray arrF = Nd4j.linspace(1, length, length).reshape('f', 10, 10);
        INDArray subC = arrC.get(NDArrayIndex.interval(5, 10), NDArrayIndex.interval(5, 10));
        INDArray subF = arrF.get(NDArrayIndex.interval(5, 10), NDArrayIndex.interval(5, 10));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(subC);
        }
        byte[] bytesC = baos.toByteArray();
        baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(subF);
        }
        byte[] bytesF = baos.toByteArray();
        INDArray arr2C;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytesC))) {
            arr2C = ((INDArray) (ois.readObject()));
        }
        INDArray arr2F;
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytesF))) {
            arr2F = ((INDArray) (ois.readObject()));
        }
        Assert.assertEquals(subC, arr2C);
        Assert.assertEquals(subF, arr2F);
    }
}

