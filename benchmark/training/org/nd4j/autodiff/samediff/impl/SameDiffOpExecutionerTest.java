package org.nd4j.autodiff.samediff.impl;


import OpExecutioner.ProfilingMode.ALL;
import org.junit.Test;
import org.nd4j.autodiff.samediff.SameDiff;
import org.nd4j.autodiff.samediff.SameDiffOpExecutioner;
import org.nd4j.linalg.factory.Nd4j;


public class SameDiffOpExecutionerTest {
    @Test
    public void testupdateGraphFromProfiler() {
        SameDiffOpExecutioner sameDiffOpExecutioner = new SameDiffOpExecutioner();
        Nd4j.getExecutioner().setProfilingMode(ALL);
        Nd4j.getExecutioner().exec(new org.nd4j.linalg.api.ops.impl.transforms.Sigmoid(Nd4j.scalar(1.0)));
        SameDiff sameDiff = sameDiffOpExecutioner.getSameDiff();
    }
}

