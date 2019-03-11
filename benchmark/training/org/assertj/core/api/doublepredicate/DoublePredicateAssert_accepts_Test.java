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
package org.assertj.core.api.doublepredicate;


import PredicateDescription.GIVEN;
import java.util.function.DoublePredicate;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.DoublePredicateAssertBaseTest;
import org.assertj.core.error.ElementsShouldMatch;
import org.assertj.core.util.FailureMessages;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Filip Hrisafov
 */
public class DoublePredicateAssert_accepts_Test extends DoublePredicateAssertBaseTest {
    @Test
    public void should_fail_when_predicate_is_null() {
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(((DoublePredicate) (null))).accepts(1.0, 2.0, 3.0)).withMessage(FailureMessages.actualIsNull());
    }

    @Test
    public void should_fail_when_predicate_does_not_accept_all_values() {
        DoublePredicate predicate = ( val) -> val <= 2;
        double[] matchValues = new double[]{ 1.0, 2.0, 3.0 };
        Assertions.assertThatExceptionOfType(AssertionError.class).isThrownBy(() -> assertThat(predicate).accepts(matchValues)).withMessage(ElementsShouldMatch.elementsShouldMatch(matchValues, 3.0, GIVEN).create());
    }

    @Test
    public void should_pass_when_predicate_accepts_all_values() {
        DoublePredicate predicate = ( val) -> val <= 2;
        Assertions.assertThat(predicate).accepts(1.0, 2.0);
    }
}

