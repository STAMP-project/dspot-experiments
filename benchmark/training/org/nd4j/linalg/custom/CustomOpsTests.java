package org.nd4j.linalg.custom;


import OpStatus.ND4J_STATUS_OK;
import ScatterUpdate.UpdateOp;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.linalg.api.ops.CustomOp;
import org.nd4j.linalg.api.ops.DynamicCustomOp;
import org.nd4j.linalg.api.ops.custom.ScatterUpdate;
import org.nd4j.linalg.api.ops.executioner.OpStatus;
import org.nd4j.linalg.exception.ND4JIllegalStateException;
import org.nd4j.linalg.factory.Nd4j;


/**
 * This class holds various CustomOps tests
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class CustomOpsTests {
    @Test
    public void testNonInplaceOp1() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.create(10, 10);
        val arrayZ = Nd4j.create(10, 10);
        arrayX.assign(3.0);
        arrayY.assign(1.0);
        val exp = Nd4j.create(10, 10).assign(4.0);
        CustomOp op = DynamicCustomOp.builder("add").addInputs(arrayX, arrayY).addOutputs(arrayZ).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayZ);
    }

    /**
     * This test works inplace, but without inplace declaration
     */
    @Test
    public void testNonInplaceOp2() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.create(10, 10);
        arrayX.assign(3.0);
        arrayY.assign(1.0);
        val exp = Nd4j.create(10, 10).assign(4.0);
        CustomOp op = DynamicCustomOp.builder("add").addInputs(arrayX, arrayY).addOutputs(arrayX).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testFloor() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        arrayX.assign(3.0);
        val exp = Nd4j.create(10, 10).assign(3.0);
        CustomOp op = DynamicCustomOp.builder("floor").addInputs(arrayX).addOutputs(arrayX).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testInplaceOp1() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.create(10, 10);
        arrayX.assign(4.0);
        arrayY.assign(2.0);
        val exp = Nd4j.create(10, 10).assign(6.0);
        CustomOp op = DynamicCustomOp.builder("add").addInputs(arrayX, arrayY).callInplace(true).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayX);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testNoneInplaceOp3() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.create(10, 10);
        arrayX.assign(4.0);
        arrayY.assign(2.0);
        val exp = Nd4j.create(10, 10).assign(6.0);
        CustomOp op = DynamicCustomOp.builder("add").addInputs(arrayX, arrayY).callInplace(false).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayX);
    }

    @Test
    public void testInplaceOp2() throws Exception {
        val arrayX = Nd4j.create(10, 10);
        val arrayY = Nd4j.create(10, 10);
        val arrayZ = Nd4j.create(10, 10);
        arrayX.assign(3.0);
        arrayY.assign(1.0);
        val exp = Nd4j.create(10, 10).assign(4.0);
        val expZ = Nd4j.create(10, 10);
        CustomOp op = DynamicCustomOp.builder("add").addInputs(arrayX, arrayY).addOutputs(arrayZ).callInplace(true).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, arrayX);
        Assert.assertEquals(expZ, arrayZ);
    }

    @Test
    public void testMergeMax1() throws Exception {
        val array0 = Nd4j.create(new double[]{ 1, 0, 0, 0, 0 });
        val array1 = Nd4j.create(new double[]{ 0, 2, 0, 0, 0 });
        val array2 = Nd4j.create(new double[]{ 0, 0, 3, 0, 0 });
        val array3 = Nd4j.create(new double[]{ 0, 0, 0, 4, 0 });
        val array4 = Nd4j.create(new double[]{ 0, 0, 0, 0, 5 });
        val z = Nd4j.create(5);
        val exp = Nd4j.create(new double[]{ 1, 2, 3, 4, 5 });
        CustomOp op = DynamicCustomOp.builder("mergemax").addInputs(array0, array1, array2, array3, array4).addOutputs(z).callInplace(false).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, z);
    }

    @Test
    public void testMergeMaxF() throws Exception {
        val array0 = Nd4j.rand('f', 5, 2).add(1);// some random array with +ve numbers

        val array1 = array0.dup('f').add(5);
        array1.put(0, 0, 0);// array1 is always bigger than array0 except at 0,0

        // expected value of maxmerge
        val exp = array1.dup('f');
        exp.putScalar(0, 0, array0.getDouble(0, 0));
        val zF = Nd4j.zeros(array0.shape(), 'f');
        CustomOp op = DynamicCustomOp.builder("mergemax").addInputs(array0, array1).addOutputs(zF).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, zF);
    }

    @Test
    public void testMergeMaxMixedOrder_Subtract() {
        val exp = Nd4j.create(new int[]{ 2, 2 }, 'c').assign(5.0);
        Nd4j.getExecutioner().commit();
        val array0 = Nd4j.create(new int[]{ 2, 2 }, 'f');// some random array with +ve numbers

        val array1 = array0.dup('c').addi(5.0);
        Nd4j.getExecutioner().commit();
        Assert.assertEquals(exp, array1);
    }

    @Test
    public void testMergeMaxSameOrder_Subtract() {
        val exp = Nd4j.create(new int[]{ 2, 2 }, 'c').assign(5.0);
        Nd4j.getExecutioner().commit();
        val array0 = Nd4j.create(new int[]{ 2, 2 }, 'c');// some random array with +ve numbers

        val array1 = array0.dup('c').addi(5);
        Nd4j.getExecutioner().commit();
        Assert.assertEquals(exp, array1);
    }

    @Test
    public void testMergeMaxMixedOrder() {
        val array0 = Nd4j.rand('f', 5, 2).addi(1);// some random array with +ve numbers

        val array1 = array0.dup().addi(5);
        array1.put(0, 0, 0);// array1 is always bigger than array0 except at 0,0

        // expected value of maxmerge
        val exp = array1.dup();
        exp.putScalar(0, 0, array0.getDouble(0, 0));
        val zF = Nd4j.zeros(array0.shape(), 'f');
        CustomOp op = DynamicCustomOp.builder("mergemax").addInputs(array0, array1).addOutputs(zF).build();
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(exp, zF);
    }

    @Test
    public void testOutputShapes1() {
        val array0 = Nd4j.rand('f', 5, 2).addi(1);// some random array with +ve numbers

        val array1 = array0.dup().addi(5);
        array1.put(0, 0, 0);// array1 is always bigger than array0 except at 0,0

        // expected value of maxmerge
        val exp = array1.dup();
        exp.putScalar(0, 0, array0.getDouble(0, 0));
        CustomOp op = DynamicCustomOp.builder("mergemax").addInputs(array0, array1).build();
        val shapes = Nd4j.getExecutioner().calculateOutputShape(op);
        Assert.assertEquals(1, shapes.size());
        Assert.assertArrayEquals(new long[]{ 5, 2 }, shapes.get(0));
    }

    @Test
    public void testScatterUpdate1() throws Exception {
        val matrix = Nd4j.create(5, 5);
        val updates = Nd4j.create(2, 5).assign(1.0);
        int[] dims = new int[]{ 1 };
        int[] indices = new int[]{ 1, 3 };
        val exp0 = Nd4j.create(1, 5).assign(0);
        val exp1 = Nd4j.create(1, 5).assign(1);
        ScatterUpdate op = new ScatterUpdate(matrix, updates, indices, dims, UpdateOp.ADD);
        Nd4j.getExecutioner().exec(op);
        log.info("Matrix: {}", matrix);
        Assert.assertEquals(exp0, matrix.getRow(0));
        Assert.assertEquals(exp1, matrix.getRow(1));
        Assert.assertEquals(exp0, matrix.getRow(2));
        Assert.assertEquals(exp1, matrix.getRow(3));
        Assert.assertEquals(exp0, matrix.getRow(4));
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testScatterUpdate2() throws Exception {
        val matrix = Nd4j.create(5, 5);
        val updates = Nd4j.create(2, 5).assign(1.0);
        int[] dims = new int[]{ 0 };
        int[] indices = new int[]{ 0, 1 };
        val exp0 = Nd4j.create(1, 5).assign(0);
        val exp1 = Nd4j.create(1, 5).assign(1);
        ScatterUpdate op = new ScatterUpdate(matrix, updates, indices, dims, UpdateOp.ADD);
    }

    @Test(expected = ND4JIllegalStateException.class)
    public void testScatterUpdate3() throws Exception {
        val matrix = Nd4j.create(5, 5);
        val updates = Nd4j.create(2, 5).assign(1.0);
        int[] dims = new int[]{ 1 };
        int[] indices = new int[]{ 0, 6 };
        val exp0 = Nd4j.create(1, 5).assign(0);
        val exp1 = Nd4j.create(1, 5).assign(1);
        ScatterUpdate op = new ScatterUpdate(matrix, updates, indices, dims, UpdateOp.ADD);
    }

    @Test
    public void testOpStatus1() throws Exception {
        Assert.assertEquals(ND4J_STATUS_OK, OpStatus.byNumber(0));
    }
}

