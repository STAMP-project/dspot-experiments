/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.function.udaf.placeholder;


import io.confluent.ksql.function.udaf.TableUdaf;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import static PlaceholderTableUdaf.INSTANCE;


public class PlaceholderTableUdafTest {
    private final TableUdaf<Long, Long> udaf = INSTANCE;

    @Test
    public void shouldInitializeAsNull() {
        Assert.assertThat(udaf.initialize(), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldAggregateToNull() {
        Assert.assertThat(udaf.aggregate(1L, 2L), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldUndoToNull() {
        Assert.assertThat(udaf.undo(1L, 2L), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldMergeToNull() {
        Assert.assertThat(udaf.merge(1L, 2L), Matchers.is(Matchers.nullValue()));
    }
}

