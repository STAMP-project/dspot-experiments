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
package org.assertj.core.util;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Throwables#removeAssertJRelatedElementsFromStackTrace(Throwable)}</code>.
 *
 * @author Joel Costigliola
 */
public class Throwables_removeAssertJElementFromStackTrace_Test {
    @Test
    public void should_add_stack_trace_of_current_thread() {
        try {
            throw new Throwables_removeAssertJElementFromStackTrace_Test.AssertJThrowable();
        } catch (Throwables_removeAssertJElementFromStackTrace_Test.AssertJThrowable throwable) {
            Assertions.assertThat(StackTraceUtils.hasStackTraceElementRelatedToAssertJ(throwable)).isTrue();
            Throwables.removeAssertJRelatedElementsFromStackTrace(throwable);
            Assertions.assertThat(StackTraceUtils.hasStackTraceElementRelatedToAssertJ(throwable)).isFalse();
        }
    }

    private static class AssertJThrowable extends Throwable {
        private static final long serialVersionUID = 1L;
    }
}

