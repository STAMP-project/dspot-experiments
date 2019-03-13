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
package org.assertj.core.api;


import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;


public class Assertions_sync_with_Assumptions_Test extends BaseAssertionsTest {
    @Test
    public void standard_assertions_and_assumptions_should_have_the_same_assertions_methods() {
        Method[] assertThatMethods = BaseAssertionsTest.findMethodsWithName(Assertions.class, "assertThat", BaseAssertionsTest.SPECIAL_IGNORED_RETURN_TYPES);
        Method[] assumeThatMethods = BaseAssertionsTest.findMethodsWithName(Assumptions.class, "assumeThat");
        Assertions.assertThat(assertThatMethods).usingElementComparator(BaseAssertionsTest.IGNORING_DECLARING_CLASS_RETURN_TYPE_AND_METHOD_NAME).containsExactlyInAnyOrder(assumeThatMethods);
    }

    @Test
    public void standard_assumptions_and_with_assumptions_should_have_the_same_assertions_methods() {
        Method[] assumptionsMethods = BaseAssertionsTest.findMethodsWithName(Assumptions.class, "assumeThat");
        Method[] withAssumptionsMethods = BaseAssertionsTest.findMethodsWithName(WithAssumptions.class, "assumeThat");
        Assertions.assertThat(withAssumptionsMethods).usingElementComparator(BaseAssertionsTest.IGNORING_DECLARING_CLASS_ONLY).containsExactlyInAnyOrder(assumptionsMethods);
    }
}

