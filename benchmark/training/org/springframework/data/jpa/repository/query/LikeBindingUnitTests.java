/**
 * Copyright 2013-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.jpa.repository.query;


import Type.CONTAINING;
import Type.ENDING_WITH;
import Type.STARTING_WITH;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.data.jpa.repository.query.StringQuery.LikeParameterBinding;
import org.springframework.data.repository.query.parser.Part.Type;


/**
 * Unit tests for {@link LikeParameterBinding}.
 *
 * @author Oliver Gierke
 * @author Thomas Darimont
 */
public class LikeBindingUnitTests {
    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullName() {
        new LikeParameterBinding(null, Type.CONTAINING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsEmptyName() {
        new LikeParameterBinding("", Type.CONTAINING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullType() {
        new LikeParameterBinding("foo", null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidType() {
        new LikeParameterBinding("foo", Type.SIMPLE_PROPERTY);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsInvalidPosition() {
        new LikeParameterBinding(0, Type.CONTAINING);
    }

    @Test
    public void setsUpInstanceForName() {
        LikeParameterBinding binding = new LikeParameterBinding("foo", Type.CONTAINING);
        Assert.assertThat(binding.hasName("foo"), CoreMatchers.is(true));
        Assert.assertThat(binding.hasName("bar"), CoreMatchers.is(false));
        Assert.assertThat(binding.hasName(null), CoreMatchers.is(false));
        Assert.assertThat(binding.hasPosition(0), CoreMatchers.is(false));
        Assert.assertThat(binding.getType(), CoreMatchers.is(CONTAINING));
    }

    @Test
    public void setsUpInstanceForIndex() {
        LikeParameterBinding binding = new LikeParameterBinding(1, Type.CONTAINING);
        Assert.assertThat(binding.hasName("foo"), CoreMatchers.is(false));
        Assert.assertThat(binding.hasName(null), CoreMatchers.is(false));
        Assert.assertThat(binding.hasPosition(0), CoreMatchers.is(false));
        Assert.assertThat(binding.hasPosition(1), CoreMatchers.is(true));
        Assert.assertThat(binding.getType(), CoreMatchers.is(CONTAINING));
    }

    @Test
    public void augmentsValueCorrectly() {
        LikeBindingUnitTests.assertAugmentedValue(CONTAINING, "%value%");
        LikeBindingUnitTests.assertAugmentedValue(ENDING_WITH, "%value");
        LikeBindingUnitTests.assertAugmentedValue(STARTING_WITH, "value%");
        Assert.assertThat(prepare(null), CoreMatchers.is(CoreMatchers.nullValue()));
    }
}

