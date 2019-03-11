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


import java.io.PrintWriter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link Throwables#getStackTrace(Throwable)}.
 *
 * @author Daniel Zlotin
 */
public class Throwables_getStackTrace_Test {
    @Test
    public void calls_printStackTrace_with_temp_PrintWriter() {
        final Throwable mock = Mockito.mock(Throwable.class);
        Throwables.getStackTrace(mock);
        Mockito.verify(mock, Mockito.times(1)).printStackTrace(ArgumentMatchers.isA(PrintWriter.class));
    }

    @Test
    public void should_return_stacktrace_as_String() {
        final Throwable throwable = new Throwable("some message");
        Assertions.assertThat(Throwables.getStackTrace(throwable)).isInstanceOf(String.class).contains("java.lang.Throwable: some message").containsPattern("\tat .*\\(Throwables_getStackTrace_Test.java:\\d");
    }
}

