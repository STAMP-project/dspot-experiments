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


import org.assertj.core.description.Description;
import org.assertj.core.description.EmptyTextDescription;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link Condition#as(Description)}</code>.
 *
 * @author Yvonne Wang
 */
public class Condition_as_Description_Test {
    private static Description description;

    private Condition<Object> condition;

    @Test
    public void should_set_description() {
        condition.as(Condition_as_Description_Test.description);
        Assertions.assertThat(condition.description()).isSameAs(Condition_as_Description_Test.description);
    }

    @Test
    public void should_replace_null_description_by_an_empty_one() {
        condition.as(((Description) (null)));
        Assertions.assertThat(condition.description()).isEqualTo(EmptyTextDescription.emptyDescription());
    }

    @Test
    public void should_return_same_condition() {
        Condition<Object> returnedCondition = condition.as(Condition_as_Description_Test.description);
        Assertions.assertThat(returnedCondition).isSameAs(condition);
    }
}

