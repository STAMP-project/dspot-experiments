package org.nd4j.autodiff.execution;


import OutputMode.EXPLICIT;
import OutputMode.IMPLICIT;
import OutputMode.VARIABLE_SPACE;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.autodiff.execution.conf.ExecutorConfiguration;
import org.nd4j.autodiff.samediff.SDVariable;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;


/**
 * Comparative tests for native executioner vs sequential execution
 *
 * @author raver119@gmail.com
 */
@Slf4j
public class GraphExecutionerTest {
    protected static ExecutorConfiguration configVarSpace = ExecutorConfiguration.builder().outputMode(VARIABLE_SPACE).build();

    protected static ExecutorConfiguration configExplicit = ExecutorConfiguration.builder().outputMode(EXPLICIT).build();

    protected static ExecutorConfiguration configImplicit = ExecutorConfiguration.builder().outputMode(IMPLICIT).build();

    /**
     * VarSpace should dump everything. 4 variables in our case
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEquality1() throws Exception {
        GraphExecutioner executionerA = new BasicGraphExecutioner();
        GraphExecutioner executionerB = new NativeGraphExecutioner();
        SameDiff sameDiff = SameDiff.create();
        INDArray ones = Nd4j.ones(4);
        SDVariable sdVariable = sameDiff.var("ones", ones);
        SDVariable scalarOne = sameDiff.var("add1", Nd4j.scalar(1.0));
        SDVariable result = sdVariable.addi(scalarOne);
        SDVariable total = sameDiff.sum(result, Integer.MAX_VALUE);
        log.info("TOTAL: {}; Id: {}", total.getVarName(), total);
        INDArray[] resB = executionerB.executeGraph(sameDiff, GraphExecutionerTest.configVarSpace);
        Assert.assertEquals(6, resB.length);
        Assert.assertEquals(Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F }), resB[4]);
        Assert.assertEquals(Nd4j.scalar(1), resB[1]);
        Assert.assertEquals(Nd4j.scalar(8.0), resB[5]);
    }

    /**
     * Implicit should return tree edges. So, one variable
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEquality2() throws Exception {
        GraphExecutioner executionerA = new BasicGraphExecutioner();
        GraphExecutioner executionerB = new NativeGraphExecutioner();
        SameDiff sameDiff = SameDiff.create();
        INDArray ones = Nd4j.ones(4);
        SDVariable sdVariable = sameDiff.var("ones", ones);
        SDVariable scalarOne = sameDiff.var("add1", Nd4j.scalar(1.0));
        SDVariable result = sdVariable.addi(scalarOne);
        SDVariable total = sameDiff.sum(result, Integer.MAX_VALUE);
        // log.info("ID: {}",sameDiff.getGraph().getVertex(1).getValue().getId());
        INDArray[] resB = executionerB.executeGraph(sameDiff, GraphExecutionerTest.configImplicit);
        Assert.assertEquals(1, resB.length);
        Assert.assertEquals(Nd4j.scalar(8.0), resB[0]);
        // INDArray resA = executionerA.executeGraph(sameDiff)[0];
        // assertEquals(resA, resB);
    }
}

