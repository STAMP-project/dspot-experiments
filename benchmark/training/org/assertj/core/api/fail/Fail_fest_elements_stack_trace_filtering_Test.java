/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.fail;


import org.assertj.core.api.Assertions;
import org.assertj.core.api.Fail;
import org.assertj.core.util.StackTraceUtils;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Fail#setRemoveAssertJRelatedElementsFromStackTrace(boolean)}</code>.
 *
 * @author Joel Costigliola
 */
public class Fail_fest_elements_stack_trace_filtering_Test {
    @Test
    public void fest_elements_should_be_removed_from_assertion_error_stack_trace() {
        Fail.setRemoveAssertJRelatedElementsFromStackTrace(true);
        try {
            Assertions.assertThat(5).isLessThan(0);
        } catch (AssertionError assertionError) {
            Assertions.assertThat(StackTraceUtils.hasStackTraceElementRelatedToAssertJ(assertionError)).isFalse();
            return;
        }
        Assertions.failBecauseExceptionWasNotThrown(AssertionError.class);
    }

    @Test
    public void fest_elements_should_be_kept_in_assertion_error_stack_trace() {
        Fail.setRemoveAssertJRelatedElementsFromStackTrace(false);
        try {
            Assertions.assertThat(5).isLessThan(0);
        } catch (AssertionError assertionError) {
            Assertions.assertThat(StackTraceUtils.hasStackTraceElementRelatedToAssertJ(assertionError)).isTrue();
            return;
        }
        Assertions.failBecauseExceptionWasNotThrown(AssertionError.class);
    }
}

