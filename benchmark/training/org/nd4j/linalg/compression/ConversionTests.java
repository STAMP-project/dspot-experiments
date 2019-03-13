package org.nd4j.linalg.compression;


import DataBuffer.Type.DOUBLE;
import DataBuffer.Type.FLOAT;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class ConversionTests extends BaseNd4jTest {
    public ConversionTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testDoubleToFloats1() {
        val dtype = Nd4j.dataType();
        Nd4j.setDataType(DOUBLE);
        val arrayX = Nd4j.create(10).assign(1.0);
        Nd4j.setDataType(FLOAT);
        val arrayY = Nd4j.create(10).assign(1.0);
        val converted = arrayX.convertToFloats();
        val exp = Nd4j.create(10).assign(2.0);
        converted.addi(arrayY);
        Assert.assertEquals(exp, converted);
        Nd4j.setDataType(dtype);
    }

    @Test
    public void testFloatsToDoubles1() {
        val dtype = Nd4j.dataType();
        Nd4j.setDataType(FLOAT);
        val arrayX = Nd4j.create(10).assign(1.0);
        Nd4j.setDataType(DOUBLE);
        val arrayY = Nd4j.create(10).assign(1.0);
        val converted = arrayX.convertToDoubles();
        val exp = Nd4j.create(10).assign(2.0);
        converted.addi(arrayY);
        Assert.assertEquals(exp, converted);
        Nd4j.setDataType(dtype);
    }
}

