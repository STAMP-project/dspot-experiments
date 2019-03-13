package org.nd4j.linalg.aggregates;


import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.nd4j.linalg.BaseNd4jTest;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.aggregates.impl.AggregateCBOW;
import org.nd4j.linalg.api.ops.aggregates.impl.AggregateSkipGram;
import org.nd4j.linalg.api.ops.aggregates.impl.HierarchicSoftmax;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.factory.Nd4jBackend;


/**
 * This tests pack covers simple gradient checks for AggregateSkipGram, CBOW and HierarchicSoftmax
 *
 * @author raver119@gmail.com
 */
@Slf4j
@RunWith(Parameterized.class)
public class HierarchicSoftmaxTests extends BaseNd4jTest {
    public HierarchicSoftmaxTests(Nd4jBackend backend) {
        super(backend);
    }

    @Test
    public void testHSGradient1() throws Exception {
        INDArray syn0 = Nd4j.ones(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.ones(10, 10).assign(0.02F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray neu1e = Nd4j.create(10);
        INDArray expSyn0 = Nd4j.create(10).assign(0.01F);
        INDArray expSyn1 = Nd4j.create(10).assign(0.020005);
        INDArray expNeu1e = Nd4j.create(10).assign(1.0E-5F);
        int idxSyn0 = 1;
        int idxSyn1 = 1;
        int code = 0;
        double lr = 0.001;
        HierarchicSoftmax op = new HierarchicSoftmax(syn0.getRow(idxSyn0), syn1.getRow(idxSyn1), expTable, neu1e, code, lr);
        Nd4j.getExecutioner().exec(op);
        INDArray syn0row = syn0.getRow(idxSyn0);
        INDArray syn1row = syn1.getRow(idxSyn1);
        // expected gradient is 0.0005
        // expected neu1 = 0.00001
        // expected syn1 = 0.020005
        Assert.assertEquals(expNeu1e, neu1e);
        Assert.assertEquals(expSyn1, syn1row);
        // we hadn't modified syn0 at all yet
        Assert.assertEquals(expSyn0, syn0row);
    }

    @Test
    public void testSGGradient1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        double lr = 0.001;
        int idxSyn0 = 0;
        INDArray expSyn0 = Nd4j.create(10).assign(0.01001F);
        INDArray expSyn1_1 = Nd4j.create(10).assign(0.020005);
        INDArray syn0row = syn0.getRow(idxSyn0);
        log.info("syn0row before: {}", Arrays.toString(syn0row.dup().data().asFloat()));
        AggregateSkipGram op = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, null, idxSyn0, new int[]{ 1 }, new int[]{ 0 }, 0, 0, 10, lr, 1L, 10);
        Nd4j.getExecutioner().exec(op);
        log.info("syn0row after: {}", Arrays.toString(syn0row.dup().data().asFloat()));
        Assert.assertEquals(expSyn0, syn0row);
        Assert.assertEquals(expSyn1_1, syn1.getRow(1));
    }

    @Test
    public void testSGGradient2() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        double lr = 0.001;
        int idxSyn0 = 0;
        INDArray expSyn0 = Nd4j.create(10).assign(0.01F);
        INDArray expSyn1_1 = Nd4j.create(10).assign(0.020005);// gradient is 0.00005

        INDArray expSyn1_2 = Nd4j.create(10).assign(0.019995F);// gradient is -0.00005

        INDArray syn0row = syn0.getRow(idxSyn0);
        log.info("syn1row2 before: {}", Arrays.toString(syn1.getRow(2).dup().data().asFloat()));
        AggregateSkipGram op = new AggregateSkipGram(syn0, syn1, null, expTable, null, idxSyn0, new int[]{ 1, 2 }, new int[]{ 0, 1 }, 0, 0, 10, lr, 1L, 10);
        Nd4j.getExecutioner().exec(op);
        /* Since expTable contains all-equal values, and only difference for ANY index is code being 0 or 1, syn0 row will stay intact,
        because neu1e will be full of 0.0f, and axpy will have no actual effect
         */
        Assert.assertEquals(expSyn0, syn0row);
        // syn1 row 1 modified only once
        BaseNd4jTest.assertArrayEquals(expSyn1_1.data().asFloat(), syn1.getRow(1).dup().data().asFloat(), 1.0E-7F);
        log.info("syn1row2 after: {}", Arrays.toString(syn1.getRow(2).dup().data().asFloat()));
        // syn1 row 2 modified only once
        BaseNd4jTest.assertArrayEquals(expSyn1_2.data().asFloat(), syn1.getRow(2).dup().data().asFloat(), 1.0E-7F);
    }

    /**
     * This particular test does nothing: neither HS or Neh is executed
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testSGGradientNoOp() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = null;
        double lr = 0.001;
        int idxSyn0 = 0;
        INDArray expSyn0 = Nd4j.create(10).assign(0.01F);
        INDArray expSyn1 = syn1.dup();
        AggregateSkipGram op = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, table, idxSyn0, new int[]{  }, new int[]{  }, 0, 0, 10, lr, 1L, 10);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(expSyn0, syn0.getRow(idxSyn0));
        Assert.assertEquals(expSyn1, syn1);
    }

    @Test
    public void testSGGradientNegative1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = Nd4j.create(100000);
        double lr = 0.001;
        INDArray expSyn0 = Nd4j.create(10).assign(0.01F);
        int idxSyn0 = 1;
        log.info("syn0row1 after: {}", Arrays.toString(syn0.getRow(idxSyn0).dup().data().asFloat()));
        AggregateSkipGram op = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, table, idxSyn0, new int[]{  }, new int[]{  }, 1, 3, 10, lr, 2L, 10);
        Nd4j.getExecutioner().exec(op);
        log.info("syn0row1 after: {}", Arrays.toString(syn0.getRow(idxSyn0).dup().data().asFloat()));
        // we expect syn0 to be equal, since 2 rounds with +- gradients give the same output value for neu1e
        Assert.assertEquals(expSyn0, syn0.getRow(idxSyn0));
    }

    @Test
    public void testCBOWGradient1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        double lr = 0.025;
        INDArray syn0row_before_0 = syn0.getRow(0).dup();
        INDArray syn0row_before_1 = syn0.getRow(1).dup();
        INDArray syn0row_before_2 = syn0.getRow(2).dup();
        AggregateCBOW op = new AggregateCBOW(syn0, syn1, null, expTable, null, 0, new int[]{ 0, 1, 2 }, new int[]{ 4, 5 }, new int[]{ 1, 1 }, 0, 0, 10, lr, 2L, 10);
        Nd4j.getExecutioner().exec(op);
        INDArray syn0row_0 = syn0.getRow(0);
        INDArray syn0row_1 = syn0.getRow(1);
        INDArray syn0row_2 = syn0.getRow(2);
        INDArray syn1row_4 = syn1.getRow(4);
        INDArray syn1row_5 = syn1.getRow(5);
        INDArray syn1row_6 = syn1.getRow(6);
        INDArray expSyn0row_0 = Nd4j.create(10).assign(0.0095F);
        INDArray expSyn1row_4 = Nd4j.create(10).assign(0.019875F);
        INDArray expSyn1row_6 = Nd4j.create(10).assign(0.02F);
        Assert.assertNotEquals(syn0row_before_0, syn0row_0);
        Assert.assertNotEquals(syn0row_before_1, syn0row_1);
        Assert.assertNotEquals(syn0row_before_2, syn0row_2);
        // neu1 is expected to be 0.01
        // dot is expected to be 0.002
        // g is expected -0.0125 for both rounds: both codes are 1, so (1 - 1 - 0.5) * 0.025 = -0.0125
        // neu1e is expected to be -0.00025 after first round ( g * syn1 + neu1e) (-0.0125 * 0.02 + 0.000)
        // neu1e is expected to be -0.00050 after second round (-0.0125 * 0.02 + -0.00025)
        // syn1 is expected to be 0.019875 after first round (g * neu1 + syn1)  (-0.0125 * 0.01 + 0.02 )
        // syn1 is expected to be 0.019875 after second round (g * neu1 + syn1)  (-0.0125 * 0.01 + 0.02 ) NOTE: each of round uses it's own syn1 index
        // syn0 is expected to be 0.0095f after op (syn0 += neu1e) (0.01 += -0.0005)
        log.info("syn1row4[0]: {}", syn1row_4.getFloat(0));
        Assert.assertEquals(expSyn0row_0, syn0row_0);
        Assert.assertEquals(expSyn0row_0, syn0row_1);
        Assert.assertEquals(expSyn0row_0, syn0row_2);
        Assert.assertEquals(expSyn1row_4, syn1row_4);
        Assert.assertEquals(expSyn1row_4, syn1row_5);
        Assert.assertEquals(expSyn1row_6, syn1row_6);
    }

    @Test
    public void testCBOWGradientNoOp1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.ones(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = Nd4j.create(100000);
        double lr = 0.025;
        INDArray expSyn0 = syn0.dup();
        INDArray expSyn1 = syn1.dup();
        INDArray expSyn1Neg = syn1Neg.dup();
        AggregateCBOW op = new AggregateCBOW(syn0, syn1, syn1Neg, expTable, table, 0, new int[]{  }, new int[]{  }, new int[]{  }, 0, 0, 10, lr, 2L, 10);
        Nd4j.getExecutioner().exec(op);
        Assert.assertEquals(expSyn0, syn0);
        Assert.assertEquals(expSyn1, syn1);
        Assert.assertEquals(expSyn1Neg, syn1Neg);
    }

    @Test
    public void testCBOWGradientNegative1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.create(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = Nd4j.create(100000);
        double lr = 0.025;
        INDArray syn0dup = syn0.dup();
        INDArray syn1dup = syn1.dup();
        INDArray syn1NegDup = syn1Neg.dup();
        INDArray expSyn0_row0 = Nd4j.create(10).assign(0.0096265625);
        INDArray expSyn0_row3 = Nd4j.create(10).assign(0.01F);
        INDArray expSyn1Neg_row6 = Nd4j.create(10).assign(0.030125F);
        AggregateCBOW op = new AggregateCBOW(syn0, syn1, syn1Neg, expTable, table, 0, new int[]{ 0, 1, 2 }, new int[]{  }, new int[]{  }, 2, 6, 10, lr, 2L, 10);
        Nd4j.getExecutioner().exec(op);
        Assert.assertNotEquals(syn0dup, syn0);
        Assert.assertNotEquals(syn1NegDup, syn1Neg);
        Assert.assertEquals(syn1dup, syn1);
        // neu1 is expected to be 0.01
        // dot is expected to be 0.003 (dot += 0.01 * 0.03) for round 1 & 2.
        // dot is expected to be 0.002987 for round 3 (because syn1Neg for idx 8 is modified at round 2)
        // g is expected to be 0.0125 for the first round (code is 1)
        // g is expected to be -0.0125 for the second round (code is 0)
        // g is expected to be -0.0125 for the third round (code is 0)
        // neu1e is expected to be 0.000375 after first round (0.0125 * 0.03 + 0.00)
        // neu1e is expected to be 0.00 after second round (-0.0125 * 0.03 + 0.000375)
        // neu1e is expected to be -0.0003734375 after third round (-0.0125 * 0.029875 + 0.00)
        // syn1Neg idx6 is expected to be 0.030125 after first round (0.0125 * 0.01 + 0.03)
        // syn1Neg idx8 is expected to be 0.029875 after second round (-0.0125 * 0.01 + 0.03)
        // syn1Neg idx8 is expected to be 0.02975 after third round (-0.0125 * 0.01 + 0.029875)
        // syn0 idx0 is expected to be 0.00 after training (0.01 += -0.0003734375)
        log.info("syn1neg_row6 after: {}", Arrays.toString(syn1Neg.getRow(6).dup().data().asFloat()));
        // checking target first
        Assert.assertEquals(expSyn1Neg_row6, syn1Neg.getRow(6));
        Assert.assertEquals(expSyn0_row0, syn0.getRow(0));
        Assert.assertEquals(expSyn0_row0, syn0.getRow(1));
        Assert.assertEquals(expSyn0_row0, syn0.getRow(2));
        // these rows shouldn't change
        Assert.assertEquals(expSyn0_row3, syn0.getRow(3));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(4));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(5));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(6));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(7));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(8));
        Assert.assertEquals(expSyn0_row3, syn0.getRow(9));
    }

    @Test
    public void testCBOWInference1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.create(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = Nd4j.create(100000);
        double lr = 0.025;
        INDArray syn0dup = syn0.dup();
        INDArray syn1dup = syn1.dup();
        INDArray syn1NegDup = syn1Neg.dup();
        INDArray inference = Nd4j.create(10).assign(0.04F);
        INDArray dup = inference.dup();
        INDArray expInference = Nd4j.create(10).assign(0.0395F);
        log.info("Empty vector: {}", Arrays.toString(inference.data().asFloat()));
        /* surrounding words are 0 and 1 */
        AggregateCBOW op = new AggregateCBOW(syn0, syn1, null, expTable, null, 0, new int[]{ 0, 1 }, new int[]{ 4, 5 }, new int[]{ 1, 1 }, 0, 0, 10, lr, 2L, 10, 0, false, inference);
        Nd4j.getExecutioner().exec(op);
        /* syn0, syn1 and syn1Neg should stay intact during inference */
        Assert.assertEquals(syn0dup, syn0);
        Assert.assertEquals(syn1dup, syn1);
        Assert.assertEquals(syn1NegDup, syn1Neg);
        /**
         * neu1 is expected to be 0.02
         * syn1 is expected to be 0.02
         * dot is expected to be 0.04 ( 0.02 * 0.02 * 10)
         * g is expected to be -0.0125 for BOTH rounds, since we're not changing syn1 values during inference
         * neu1e is expected to be -0.00025 at first round (-0.0125 * 0.02 + 0.00)
         * neu1e is expected to be -0.0005 at second round (-0.0125 * 0.02 + -0.00025)
         * inference is expected to be 0.0395 after training (0.04 + -0.0005)
         */
        Assert.assertNotEquals(dup, inference);
        log.info("Inferred vector: {}", Arrays.toString(inference.data().asFloat()));
        Assert.assertEquals(expInference, inference);
    }

    @Test
    public void testSGInference1() throws Exception {
        INDArray syn0 = Nd4j.create(10, 10).assign(0.01F);
        INDArray syn1 = Nd4j.create(10, 10).assign(0.02F);
        INDArray syn1Neg = Nd4j.create(10, 10).assign(0.03F);
        INDArray expTable = Nd4j.create(10000).assign(0.5F);
        INDArray table = Nd4j.create(100000);
        double lr = 0.025;
        INDArray syn0dup = syn0.dup();
        INDArray syn1dup = syn1.dup();
        INDArray syn1NegDup = syn1Neg.dup();
        INDArray inference = Nd4j.create(10).assign(0.04F);
        INDArray dup = inference.dup();
        INDArray expInference = Nd4j.create(10).assign(0.0395F);
        AggregateSkipGram op = new AggregateSkipGram(syn0, syn1, syn1Neg, expTable, null, 0, new int[]{ 1, 2 }, new int[]{ 1, 1 }, 0, 0, 10, lr, 1L, 10, inference);
        Nd4j.getExecutioner().exec(op);
        /* syn0, syn1 and syn1Neg should stay intact during inference */
        Assert.assertEquals(syn0dup, syn0);
        Assert.assertEquals(syn1dup, syn1);
        Assert.assertEquals(syn1NegDup, syn1Neg);
        Assert.assertNotEquals(dup, inference);
        /**
         * dot is expected to be 0.008 for both rounds
         * g is expected to be -0.0125 for both rounds, since we don't update syn0/syn1 before end of SG round
         * neu1e is expected to be -0.00025 after first round (-0.0125 * 0.02 + 0.00)
         * neu1e is expected to be -0.0005 after first round (-0.0125 * 0.02 + -0.00025)
         * inferenceVector is expected to be 0.0395 after training (0.04 + -0.0005)
         */
        Assert.assertEquals(expInference, inference);
    }
}

