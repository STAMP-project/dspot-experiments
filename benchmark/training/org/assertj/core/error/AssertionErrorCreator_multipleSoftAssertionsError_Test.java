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


import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.opentest4j.MultipleFailuresError;


public class AssertionErrorCreator_multipleSoftAssertionsError_Test {
    private AssertionErrorCreator assertionErrorCreator = new AssertionErrorCreator();

    @Test
    public void should_create_MultipleFailuresError_using_reflection() {
        // GIVEN
        List<? extends AssertionError> errors = Lists.list(new AssertionError("error1"), new AssertionError("error2"));
        // WHEN
        AssertionError assertionError = assertionErrorCreator.multipleSoftAssertionsError(errors);
        // THEN
        Assertions.assertThat(assertionError).isInstanceOf(MultipleFailuresError.class).hasMessage(String.format(("Multiple Failures (2 failures)%n" + ("\terror1%n" + "\terror2"))));
        MultipleFailuresError assertionFailedError = ((MultipleFailuresError) (assertionError));
        Assertions.assertThat(assertionFailedError.getFailures()).containsExactlyElementsOf(errors);
    }

    @Test
    public void should_create_SoftAssertionError_when_MultipleFailuresError_could_not_be_created() throws Exception {
        // GIVEN
        List<? extends AssertionError> errors = Lists.list(new AssertionError("error1"), new AssertionError("error2"));
        ConstructorInvoker constructorInvoker = Mockito.mock(ConstructorInvoker.class);
        BDDMockito.given(constructorInvoker.newInstance(ArgumentMatchers.anyString(), ArgumentMatchers.any(Class[].class), ArgumentMatchers.any(Object[].class))).willThrow(Exception.class);
        assertionErrorCreator.constructorInvoker = constructorInvoker;
        // WHEN
        AssertionError assertionError = assertionErrorCreator.multipleSoftAssertionsError(errors);
        // THEN
        Assertions.assertThat(assertionError).isNotInstanceOf(MultipleFailuresError.class).isInstanceOf(SoftAssertionError.class).hasMessage(String.format(("%n" + (("The following 2 assertions failed:%n" + "1) error1%n") + "2) error2%n"))));
    }
}

