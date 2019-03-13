/**
 * Tencent is pleased to support the open source community by making Angel available.
 *
 * Copyright (C) 2017-2018 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/Apache-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tencent.angel.ml.matrix.psf;


import com.tencent.angel.exception.InvalidParameterException;
import com.tencent.angel.ml.matrix.psf.get.base.GetFunc;
import com.tencent.angel.psagent.matrix.MatrixClient;
import java.util.concurrent.ExecutionException;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.Test;


public class AggrFuncTest {
    private static MatrixClient w2Client = null;

    private static double[] localArray0 = null;

    private static double[] localArray1 = null;

    private static double delta = 1.0E-6;

    private static int dim = -1;

    static {
        PropertyConfigurator.configure("../conf/log4j.properties");
    }

    @Test
    public void testAmax() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Amax(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double max = Double.MIN_VALUE;
        for (double x : AggrFuncTest.localArray1) {
            if (max < (Math.abs(x)))
                max = Math.abs(x);

        }
        Assert.assertEquals(result, max, AggrFuncTest.delta);
    }

    @Test
    public void testAmin() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Amin(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double min = Double.MAX_VALUE;
        for (double x : AggrFuncTest.localArray1) {
            if (min > (Math.abs(x)))
                min = Math.abs(x);

        }
        Assert.assertEquals(result, min, AggrFuncTest.delta);
    }

    @Test
    public void testAsum() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Asum(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double sum = 0.0;
        for (double x : AggrFuncTest.localArray1) {
            sum += Math.abs(x);
        }
        Assert.assertEquals(result, sum, AggrFuncTest.delta);
    }

    @Test
    public void testDot() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Dot(AggrFuncTest.w2Client.getMatrixId(), 0, 1);
        double result = getResult();
        double dot = 0.0;
        for (int i = 0; i < (AggrFuncTest.dim); i++) {
            dot += (AggrFuncTest.localArray0[i]) * (AggrFuncTest.localArray1[i]);
        }
        Assert.assertEquals(result, dot, AggrFuncTest.delta);
    }

    @Test
    public void testMax() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Max(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double max = Double.MIN_VALUE;
        for (double x : AggrFuncTest.localArray1) {
            if (max < x)
                max = x;

        }
        Assert.assertEquals(result, max, AggrFuncTest.delta);
    }

    @Test
    public void testMin() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Min(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double min = Double.MAX_VALUE;
        for (double x : AggrFuncTest.localArray1) {
            if (min > x)
                min = x;

        }
        Assert.assertEquals(result, min, AggrFuncTest.delta);
    }

    @Test
    public void testNnz() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Nnz(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        int count = 0;
        for (double x : AggrFuncTest.localArray1) {
            if ((Math.abs((x - 0.0))) > (AggrFuncTest.delta))
                count++;

        }
        Assert.assertEquals(((int) (result)), count);
    }

    @Test
    public void testNrm2() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Nrm2(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double nrm2 = 0;
        for (double x : AggrFuncTest.localArray1) {
            nrm2 += x * x;
        }
        nrm2 = Math.sqrt(nrm2);
        Assert.assertEquals(result, nrm2, AggrFuncTest.delta);
    }

    @Test
    public void testPull() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new com.tencent.angel.ml.matrix.psf.aggr.Pull(AggrFuncTest.w2Client.getMatrixId(), 1);
        double[] result = getValues();
        for (int i = 0; i < (AggrFuncTest.dim); i++) {
            Assert.assertEquals(result[i], AggrFuncTest.localArray1[i], AggrFuncTest.delta);
        }
    }

    @Test
    public void testSum() throws InvalidParameterException, InterruptedException, ExecutionException {
        GetFunc func = new Sum(AggrFuncTest.w2Client.getMatrixId(), 1);
        double result = getResult();
        double sum = 0.0;
        for (double x : AggrFuncTest.localArray1) {
            sum += x;
        }
        Assert.assertEquals(result, sum, AggrFuncTest.delta);
    }
}

