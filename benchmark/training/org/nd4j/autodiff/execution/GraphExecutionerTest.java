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
package org.nd4j.autodiff.execution;


import OutputMode.EXPLICIT;
import OutputMode.IMPLICIT;
import OutputMode.VARIABLE_SPACE;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.nd4j.OpValidationSuite;
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
    public void testEquality1() {
        OpValidationSuite.ignoreFailing();// Failing 2019/01/24

        GraphExecutioner executionerA = new BasicGraphExecutioner();
        GraphExecutioner executionerB = new NativeGraphExecutioner();
        SameDiff sameDiff = SameDiff.create();
        INDArray ones = Nd4j.ones(4);
        SDVariable sdVariable = sameDiff.var("ones", ones);
        SDVariable scalarOne = sameDiff.var("scalar", Nd4j.scalar(1.0));
        SDVariable result = sdVariable.add(scalarOne);
        SDVariable total = sameDiff.sum(result, Integer.MAX_VALUE);
        log.info("TOTAL: {}; Id: {}", total.getVarName(), total);
        INDArray[] resB = executionerB.executeGraph(sameDiff, GraphExecutionerTest.configVarSpace);
        // Variables: ones, scalar, result, total
        Assert.assertEquals(sameDiff.variables().size(), resB.length);
        Assert.assertEquals(Nd4j.ones(4), resB[0]);
        Assert.assertEquals(Nd4j.scalar(1), resB[1]);
        Assert.assertEquals(Nd4j.create(new float[]{ 2.0F, 2.0F, 2.0F, 2.0F }), resB[2]);
        Assert.assertEquals(Nd4j.scalar(8.0), resB[3]);
    }

    /**
     * Implicit should return tree edges. So, one variable
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testEquality2() {
        OpValidationSuite.ignoreFailing();// Failing 2019/01/24

        GraphExecutioner executionerA = new BasicGraphExecutioner();
        GraphExecutioner executionerB = new NativeGraphExecutioner();
        SameDiff sameDiff = SameDiff.create();
        INDArray ones = Nd4j.ones(4);
        SDVariable sdVariable = sameDiff.var("ones", ones);
        SDVariable scalarOne = sameDiff.var("add1", Nd4j.scalar(1.0));
        SDVariable result = sdVariable.add(scalarOne);
        SDVariable total = sameDiff.sum(result, Integer.MAX_VALUE);
        // log.info("ID: {}",sameDiff.getGraph().getVertex(1).getValue().getId());
        INDArray[] resB = executionerB.executeGraph(sameDiff, GraphExecutionerTest.configImplicit);
        Assert.assertEquals(1, resB.length);
        Assert.assertEquals(Nd4j.scalar(8.0), resB[0]);
        // INDArray resA = executionerA.executeGraph(sameDiff)[0];
        // assertEquals(resA, resB);
    }
}

