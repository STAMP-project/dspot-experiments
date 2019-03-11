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
package org.assertj.core.api.future;


import java.util.concurrent.Future;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.FutureAssertBaseTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


public class FutureAssert_isDone_Test extends FutureAssertBaseTest {
    @Test
    public void should_fail_if_actual_is_not_done() {
        Future<?> actual = Mockito.mock(Future.class);
        Mockito.when(actual.isDone()).thenReturn(false);
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(actual).isDone()).withMessageContaining("to be done");
    }
}

