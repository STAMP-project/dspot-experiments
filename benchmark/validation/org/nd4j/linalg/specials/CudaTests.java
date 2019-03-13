package org.nd4j.linalg.specials;


import DataBuffer.Type;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ops.executioner.GridExecutioner;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


@Slf4j
@RunWith(Parameterized.class)
public class CudaTests extends BaseNd4jTest {
    Type initialType;

    public CudaTests(Nd4jBackend backend) {
        super(backend);
        this.initialType = Nd4j.dataType();
    }

    @Test
    public void testMGrid_1() {
        if (!((Nd4j.getExecutioner()) instanceof GridExecutioner))
            return;

        val arrayA = Nd4j.create(128, 128);
        val arrayB = Nd4j.create(128, 128);
        val arrayC = Nd4j.create(128, 128);
        arrayA.muli(arrayB);
        val executioner = ((GridExecutioner) (Nd4j.getExecutioner()));
        Assert.assertEquals(1, executioner.getQueueLength());
        arrayA.addi(arrayC);
        Assert.assertEquals(1, executioner.getQueueLength());
    }

    @Test
    public void testMGrid_2() {
        if (!((Nd4j.getExecutioner()) instanceof GridExecutioner))
            return;

        val exp = Nd4j.create(128, 128).assign(2.0);
        Nd4j.getExecutioner().commit();
        val arrayA = Nd4j.create(128, 128);
        val arrayB = Nd4j.create(128, 128);
        arrayA.muli(arrayB);
        val executioner = ((GridExecutioner) (Nd4j.getExecutioner()));
        Assert.assertEquals(1, executioner.getQueueLength());
        arrayA.addi(2.0F);
        Assert.assertEquals(0, executioner.getQueueLength());
        Nd4j.getExecutioner().commit();
        Assert.assertEquals(exp, arrayA);
    }
}

