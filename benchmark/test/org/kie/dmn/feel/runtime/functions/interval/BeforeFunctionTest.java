/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.dmn.feel.runtime.functions.interval;


import Range.RangeBoundary;
import java.math.BigDecimal;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;


public class BeforeFunctionTest {
    private BeforeFunction beforeFunction;

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(beforeFunction.invoke(((Comparable) (null)), ((Comparable) ("b"))), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(beforeFunction.invoke(((Comparable) ("a")), ((Comparable) (null))), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError(beforeFunction.invoke("a", BigDecimal.valueOf(2)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamSingles() {
        FunctionTestUtil.assertResult(beforeFunction.invoke("a", "b"), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke("a", "a"), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke("b", "a"), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(BigDecimal.valueOf(2), BigDecimal.valueOf(1)), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(BigDecimal.valueOf(1), BigDecimal.valueOf(2)), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(BigDecimal.valueOf(1), BigDecimal.valueOf(1)), Boolean.FALSE);
    }

    @Test
    public void invokeParamSingleAndRange() {
        FunctionTestUtil.assertResult(beforeFunction.invoke("a", new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke("f", new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke("a", new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.OPEN, "a", "f", RangeBoundary.CLOSED)), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke("a", new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "b", "f", RangeBoundary.CLOSED)), Boolean.TRUE);
    }

    @Test
    public void invokeParamRangeAndSingle() {
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), "f"), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), "a"), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.OPEN), "f"), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), "g"), Boolean.TRUE);
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "g", "k", RangeBoundary.CLOSED)), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "f", "k", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.OPEN, "f", "k", RangeBoundary.CLOSED)), Boolean.TRUE);
        FunctionTestUtil.assertResult(beforeFunction.invoke(new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.OPEN), new org.kie.dmn.feel.runtime.impl.RangeImpl(RangeBoundary.CLOSED, "f", "k", RangeBoundary.CLOSED)), Boolean.TRUE);
    }
}

