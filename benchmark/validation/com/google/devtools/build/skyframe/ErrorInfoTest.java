/**
 * Copyright 2014 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.skyframe;


import Order.COMPILE_ORDER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.devtools.build.lib.collect.nestedset.NestedSetBuilder;
import java.io.IOException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static Transience.PERSISTENT;
import static Transience.TRANSIENT;


/**
 * Tests for the non-trivial creation logic of {@link ErrorInfo}.
 */
@RunWith(JUnit4.class)
public class ErrorInfoTest {
    /**
     * Dummy SkyFunctionException implementation for the sake of testing.
     */
    private static class DummySkyFunctionException extends SkyFunctionException {
        private final boolean isCatastrophic;

        public DummySkyFunctionException(Exception cause, boolean isTransient, boolean isCatastrophic) {
            super(cause, (isTransient ? TRANSIENT : PERSISTENT));
            this.isCatastrophic = isCatastrophic;
        }

        @Override
        public boolean isCatastrophic() {
            return isCatastrophic;
        }
    }

    @Test
    public void testFromException_NonTransient() {
        /* isDirectlyTransient= */
        /* isTransitivelyTransient= */
        runTestFromException(false, false);
    }

    @Test
    public void testFromException_DirectlyTransient() {
        /* isDirectlyTransient= */
        /* isTransitivelyTransient= */
        runTestFromException(true, false);
    }

    @Test
    public void testFromException_TransitivelyTransient() {
        /* isDirectlyTransient= */
        /* isTransitivelyTransient= */
        runTestFromException(false, true);
    }

    @Test
    public void testFromException_DirectlyAndTransitivelyTransient() {
        /* isDirectlyTransient= */
        /* isTransitivelyTransient= */
        runTestFromException(true, true);
    }

    @Test
    public void testFromCycle() {
        CycleInfo cycle = new CycleInfo(ImmutableList.of(GraphTester.toSkyKey("PATH, 1234")), ImmutableList.of(GraphTester.toSkyKey("CYCLE, 4321")));
        ErrorInfo errorInfo = ErrorInfo.fromCycle(cycle);
        assertThat(errorInfo.getRootCauses()).isEmpty();
        assertThat(errorInfo.getException()).isNull();
        assertThat(errorInfo.getRootCauseOfException()).isNull();
        assertThat(errorInfo.isTransitivelyTransient()).isFalse();
        assertThat(errorInfo.isCatastrophic()).isFalse();
    }

    @Test
    public void testFromChildErrors() {
        CycleInfo cycle = new CycleInfo(ImmutableList.of(GraphTester.toSkyKey("PATH, 1234")), ImmutableList.of(GraphTester.toSkyKey("CYCLE, 4321")));
        ErrorInfo cycleErrorInfo = ErrorInfo.fromCycle(cycle);
        Exception exception1 = new IOException("ehhhhh");
        SkyKey causeOfException1 = GraphTester.toSkyKey("CAUSE1, 1234");
        ErrorInfoTest.DummySkyFunctionException dummyException1 = /* isTransient= */
        /* isCatastrophic= */
        new ErrorInfoTest.DummySkyFunctionException(exception1, true, false);
        ErrorInfo exceptionErrorInfo1 = /* isTransitivelyTransient= */
        ErrorInfo.fromException(new com.google.devtools.build.skyframe.SkyFunctionException.ReifiedSkyFunctionException(dummyException1, causeOfException1), false);
        // N.B this ErrorInfo will be catastrophic.
        Exception exception2 = new IOException("blahhhhh");
        SkyKey causeOfException2 = GraphTester.toSkyKey("CAUSE2, 5678");
        ErrorInfoTest.DummySkyFunctionException dummyException2 = /* isTransient= */
        /* isCatastrophic= */
        new ErrorInfoTest.DummySkyFunctionException(exception2, false, true);
        ErrorInfo exceptionErrorInfo2 = /* isTransitivelyTransient= */
        ErrorInfo.fromException(new com.google.devtools.build.skyframe.SkyFunctionException.ReifiedSkyFunctionException(dummyException2, causeOfException2), false);
        SkyKey currentKey = GraphTester.toSkyKey("CURRENT, 9876");
        ErrorInfo errorInfo = ErrorInfo.fromChildErrors(currentKey, ImmutableList.of(cycleErrorInfo, exceptionErrorInfo1, exceptionErrorInfo2));
        assertThat(errorInfo.getRootCauses()).containsExactly(causeOfException1, causeOfException2);
        // For simplicity we test the current implementation detail that we choose the first non-null
        // (exception, cause) pair that we encounter. This isn't necessarily a requirement of the
        // interface, but it makes the test convenient and is a way to document the current behavior.
        assertThat(errorInfo.getException()).isSameAs(exception1);
        assertThat(errorInfo.getRootCauseOfException()).isSameAs(causeOfException1);
        assertThat(errorInfo.getCycleInfo()).containsExactly(new CycleInfo(ImmutableList.of(currentKey, Iterables.getOnlyElement(cycle.getPathToCycle())), cycle.getCycle()));
        assertThat(errorInfo.isTransitivelyTransient()).isTrue();
        assertThat(errorInfo.isCatastrophic()).isTrue();
    }

    @Test
    public void testCannotCreateErrorInfoWithoutExceptionOrCycle() {
        try {
            /* exception= */
            /* rootCauseOfException= */
            new ErrorInfo(NestedSetBuilder.<SkyKey>emptySet(COMPILE_ORDER), null, null, ImmutableList.<CycleInfo>of(), false, false, false);
        } catch (IllegalStateException e) {
            // Brittle, but confirms we failed for the right reason.
            assertThat(e).hasMessage("At least one of exception and cycles must be non-null/empty, respectively");
        }
    }

    @Test
    public void testCannotCreateErrorInfoWithExceptionButNoRootCause() {
        try {
            /* rootCauseOfException= */
            new ErrorInfo(NestedSetBuilder.<SkyKey>emptySet(COMPILE_ORDER), new IOException("foo"), null, ImmutableList.<CycleInfo>of(), false, false, false);
        } catch (IllegalStateException e) {
            // Brittle, but confirms we failed for the right reason.
            assertThat(e).hasMessageThat().startsWith("exception and rootCauseOfException must both be null or non-null");
        }
    }
}

