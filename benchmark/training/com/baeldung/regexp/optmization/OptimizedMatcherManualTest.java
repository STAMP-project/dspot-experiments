package com.baeldung.regexp.optmization;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class OptimizedMatcherManualTest {
    private String action;

    private List<String> items;

    private class TimeWrapper {
        private long time;

        private long mstimePreCompiled;

        private long mstimeNotPreCompiled;
    }

    @Test
    public void givenANotPreCompiledAndAPreCompiledPatternA_whenMatcheItems_thenPreCompiledFasterThanNotPreCompiled() {
        OptimizedMatcherManualTest.TimeWrapper timeObj = new OptimizedMatcherManualTest.TimeWrapper();
        testPatterns("A*", timeObj);
        Assert.assertTrue(((timeObj.mstimePreCompiled) < (timeObj.mstimeNotPreCompiled)));
    }

    @Test
    public void givenANotPreCompiledAndAPreCompiledPatternABC_whenMatcheItems_thenPreCompiledFasterThanNotPreCompiled() {
        OptimizedMatcherManualTest.TimeWrapper timeObj = new OptimizedMatcherManualTest.TimeWrapper();
        testPatterns("A*B*C*", timeObj);
        Assert.assertTrue(((timeObj.mstimePreCompiled) < (timeObj.mstimeNotPreCompiled)));
    }

    @Test
    public void givenANotPreCompiledAndAPreCompiledPatternECWF_whenMatcheItems_thenPreCompiledFasterThanNotPreCompiled() {
        OptimizedMatcherManualTest.TimeWrapper timeObj = new OptimizedMatcherManualTest.TimeWrapper();
        testPatterns("E*C*W*F*", timeObj);
        Assert.assertTrue(((timeObj.mstimePreCompiled) < (timeObj.mstimeNotPreCompiled)));
    }
}

