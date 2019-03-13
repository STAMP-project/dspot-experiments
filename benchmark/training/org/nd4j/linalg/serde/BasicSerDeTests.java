package org.nd4j.linalg.serde;


import DataBuffer.Type;
import DataBuffer.Type.DOUBLE;
import DataBuffer.Type.FLOAT;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.buffer.DataBuffer;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Created by raver119 on 21.12.16.
 */
@RunWith(Parameterized.class)
@Slf4j
public class BasicSerDeTests extends BaseNd4jTest {
    public BasicSerDeTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testBasicDataTypeSwitch1() throws Exception {
        DataBuffer.Type initialType = Nd4j.dataType();
        Nd4j.setDataType(FLOAT);
        INDArray array = Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6 });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Nd4j.write(bos, array);
        Nd4j.setDataType(DOUBLE);
        INDArray restored = Nd4j.read(new ByteArrayInputStream(bos.toByteArray()));
        TestCase.assertEquals(Nd4j.create(new float[]{ 1, 2, 3, 4, 5, 6 }), restored);
        TestCase.assertEquals(8, restored.data().getElementSize());
        TestCase.assertEquals(8, restored.shapeInfoDataBuffer().getElementSize());
        Nd4j.setDataType(initialType);
    }
}

