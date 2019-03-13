package org.nd4j.linalg;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * Created by Alex on 05/08/2016.
 */
@RunWith(Parameterized.class)
public class InputValidationTests extends BaseNd4jTest {
    public InputValidationTests(Nd4jBackend backend) {
        super(backend);
    }

    // ///////////////////////////////////////////////////////////
    // /////////////////// Broadcast Tests ///////////////////////
    @Test
    public void testInvalidColVectorOp1() {
        INDArray first = Nd4j.create(10, 10);
        INDArray col = Nd4j.create(5, 1);
        try {
            first.muliColumnVector(col);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidColVectorOp2() {
        INDArray first = Nd4j.create(10, 10);
        INDArray col = Nd4j.create(5, 1);
        try {
            first.addColumnVector(col);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidRowVectorOp1() {
        INDArray first = Nd4j.create(10, 10);
        INDArray row = Nd4j.create(1, 5);
        try {
            first.addiRowVector(row);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }

    @Test
    public void testInvalidRowVectorOp2() {
        INDArray first = Nd4j.create(10, 10);
        INDArray row = Nd4j.create(1, 5);
        try {
            first.subRowVector(row);
            Assert.fail("Should have thrown IllegalStateException");
        } catch (IllegalStateException e) {
            // OK
        }
    }
}

