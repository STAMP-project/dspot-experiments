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
package org.assertj.core.error;


import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.opentest4j.AssertionFailedError;


public class AssertionErrorCreator_assertionError_Test {
    private AssertionErrorCreator assertionErrorCreator = new AssertionErrorCreator();

    @Test
    public void should_create_AssertionFailedError_using_reflection() {
        // GIVEN
        String actual = "actual";
        String expected = "expected";
        String message = "error message";
        // WHEN
        AssertionError assertionError = assertionErrorCreator.assertionError(message, actual, expected);
        // THEN
        Assertions.assertThat(assertionError).isInstanceOf(AssertionFailedError.class).hasMessage(message);
        AssertionFailedError assertionFailedError = ((AssertionFailedError) (assertionError));
        Assertions.assertThat(assertionFailedError.getActual().getValue()).isSameAs(actual);
        Assertions.assertThat(assertionFailedError.getExpected().getValue()).isSameAs(expected);
    }

    @Test
    public void should_create_AssertionError_when_AssertionFailedError_could_not_be_created() throws Exception {
        // GIVEN
        String message = "error message";
        ConstructorInvoker constructorInvoker = Mockito.mock(ConstructorInvoker.class);
        BDDMockito.given(constructorInvoker.newInstance(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class[].class), ArgumentMatchers.any(Object[].class))).willThrow(Exception.class);
        assertionErrorCreator.constructorInvoker = constructorInvoker;
        // WHEN
        AssertionError assertionError = assertionErrorCreator.assertionError(message, "actual", "expected");
        // THEN
        Assertions.assertThat(assertionError).isNotInstanceOf(AssertionFailedError.class).hasMessage(message);
    }
}

