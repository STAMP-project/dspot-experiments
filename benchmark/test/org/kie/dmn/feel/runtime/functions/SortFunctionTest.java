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
package org.kie.dmn.feel.runtime.functions;


import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;


public class SortFunctionTest {
    private SortFunction sortFunction;

    @Test
    public void invokeListParamNull() {
        FunctionTestUtil.assertResultError(sortFunction.invoke(null), InvalidParametersEvent.class);
    }

    @Test
    public void invokeListEmpty() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Collections.emptyList()), Collections.emptyList());
    }

    @Test
    public void invokeListSingleItem() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Collections.singletonList(10)), Collections.singletonList(10));
    }

    @Test
    public void invokeListTypeHeterogenous() {
        FunctionTestUtil.assertResultError(sortFunction.invoke(Arrays.asList(10, "test", BigDecimal.TEN)), InvalidParametersEvent.class);
    }

    @Test
    public void invokeList() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(Arrays.asList(10, 4, 5, 12)), Arrays.asList(4, 5, 10, 12));
        FunctionTestUtil.assertResultList(sortFunction.invoke(Arrays.asList("a", "c", "b")), Arrays.asList("a", "b", "c"));
    }

    @Test
    public void invokeWithSortFunctionNull() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), null), Arrays.asList(4, 5, 10, 12));
    }

    @Test
    public void invokeWithSortFunction() {
        FunctionTestUtil.assertResultList(sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getBooleanFunction(true)), Arrays.asList(12, 5, 4, 10));
        FunctionTestUtil.assertResultList(sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getBooleanFunction(false)), Arrays.asList(10, 4, 5, 12));
    }

    @Test
    public void invokeExceptionInSortFunction() {
        FunctionTestUtil.assertResultError(sortFunction.invoke(null, Arrays.asList(10, 4, 5, 12), getFunctionThrowingException()), InvalidParametersEvent.class);
    }
}

