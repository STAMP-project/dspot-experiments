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
import org.junit.Test;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.FunctionTestUtil;
import org.kie.dmn.feel.runtime.impl.RangeImpl;


public class OverlapsFunctionTest {
    private OverlapsFunction overlapsFunction;

    @Test
    public void invokeParamIsNull() {
        FunctionTestUtil.assertResultError(overlapsFunction.invoke(((Range) (null)), ((Range) (new RangeImpl()))), InvalidParametersEvent.class);
        FunctionTestUtil.assertResultError(overlapsFunction.invoke(((Range) (new RangeImpl())), ((Range) (null))), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamsCantBeCompared() {
        FunctionTestUtil.assertResultError(overlapsFunction.invoke(new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new RangeImpl(RangeBoundary.CLOSED, 1, 2, RangeBoundary.CLOSED)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeParamRangeAndRange() {
        FunctionTestUtil.assertResult(overlapsFunction.invoke(new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(overlapsFunction.invoke(new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new RangeImpl(RangeBoundary.CLOSED, "c", "k", RangeBoundary.CLOSED)), Boolean.TRUE);
        FunctionTestUtil.assertResult(overlapsFunction.invoke(new RangeImpl(RangeBoundary.CLOSED, "c", "k", RangeBoundary.CLOSED), new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED)), Boolean.FALSE);
        FunctionTestUtil.assertResult(overlapsFunction.invoke(new RangeImpl(RangeBoundary.CLOSED, "a", "f", RangeBoundary.CLOSED), new RangeImpl(RangeBoundary.OPEN, "a", "k", RangeBoundary.CLOSED)), Boolean.FALSE);
    }
}

