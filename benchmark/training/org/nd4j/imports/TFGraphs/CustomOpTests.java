package org.nd4j.imports.TFGraphs;


import DataType.FLOAT;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.factory.Nd4j;


public class CustomOpTests {
    @Test
    public void testPad() {
        INDArray in = Nd4j.create(FLOAT, 1, 28, 28, 264);
        INDArray pad = Nd4j.createFromArray(new int[][]{ new int[]{ 0, 0 }, new int[]{ 0, 1 }, new int[]{ 0, 1 }, new int[]{ 0, 0 } });
        INDArray out = Nd4j.create(FLOAT, 1, 29, 29, 264);
        DynamicCustomOp op = // constant mode, with no constant specified
        DynamicCustomOp.builder("pad").addInputs(in, pad).addOutputs(out).addIntegerArguments(0).build();
        val outShape = Nd4j.getExecutioner().calculateOutputShape(op);
        Assert.assertEquals(1, outShape.size());
        Assert.assertArrayEquals(new long[]{ 1, 29, 29, 264 }, outShape.get(0).getShape());
        Nd4j.getExecutioner().exec(op);// Crash here

    }

    @Test
    public void testResizeBilinearEdgeCase() {
        INDArray in = Nd4j.ones(FLOAT, 1, 1, 1, 3);
        INDArray size = Nd4j.createFromArray(8, 8);
        INDArray out = Nd4j.create(FLOAT, 1, 8, 8, 3);
        DynamicCustomOp op = // 1 = center. Though TF works with align_corners == false or true
        DynamicCustomOp.builder("resize_bilinear").addInputs(in, size).addOutputs(out).addIntegerArguments(1).build();
        Nd4j.getExecutioner().exec(op);
        INDArray exp = Nd4j.ones(FLOAT, 1, 8, 8, 3);
        Assert.assertEquals(exp, out);
    }
}

