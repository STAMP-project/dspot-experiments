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
package org.assertj.core.data;


import org.assertj.core.api.Assertions;
import org.assertj.core.internal.ErrorMessages;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Percentage#withPercentage(Number)}.
 *
 * @author Alexander Bischof
 */
public class Percentage_withPercentage_with_Double_Test {
    @SuppressWarnings("null")
    @Test
    public void should_throw_error_if_value_is_null() {
        Assertions.assertThatNullPointerException().isThrownBy(() -> {
            Double value = null;
            withPercentage(value);
        });
    }

    @Test
    public void should_throw_error_if_value_is_negative() {
        double negative = -1.0;
        Assertions.assertThatIllegalArgumentException().isThrownBy(() -> withPercentage(negative)).withMessage(ErrorMessages.percentageValueIsInRange(negative));
    }

    @Test
    public void should_create_Percentage() {
        Double value = 0.8;
        Assertions.assertThat(Percentage.withPercentage(value).value).isEqualTo(value);
        value = 200.0;
        Assertions.assertThat(Percentage.withPercentage(value).value).isEqualTo(value);
    }
}

