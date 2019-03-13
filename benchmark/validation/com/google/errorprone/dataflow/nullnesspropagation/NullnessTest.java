/**
 * Copyright 2014 The Error Prone Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.errorprone.dataflow.nullnesspropagation;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link Nullness}.
 *
 * @author eaftan@google.com (Eddie Aftandilian)
 */
@RunWith(JUnit4.class)
public class NullnessTest {
    @Test
    public void testLeastUpperBound() {
        assertThat(Nullness.NULLABLE.leastUpperBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULLABLE.leastUpperBound(Nullness.NULL)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULLABLE.leastUpperBound(Nullness.NONNULL)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULLABLE.leastUpperBound(Nullness.BOTTOM)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULL.leastUpperBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULL.leastUpperBound(Nullness.NULL)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.NULL.leastUpperBound(Nullness.NONNULL)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULL.leastUpperBound(Nullness.BOTTOM)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.NONNULL.leastUpperBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NONNULL.leastUpperBound(Nullness.NULL)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NONNULL.leastUpperBound(Nullness.NONNULL)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.NONNULL.leastUpperBound(Nullness.BOTTOM)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.BOTTOM.leastUpperBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.BOTTOM.leastUpperBound(Nullness.NULL)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.BOTTOM.leastUpperBound(Nullness.NONNULL)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.BOTTOM.leastUpperBound(Nullness.BOTTOM)).isEqualTo(Nullness.BOTTOM);
    }

    @Test
    public void testGreatestLowerBound() {
        assertThat(Nullness.NULLABLE.greatestLowerBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULLABLE.greatestLowerBound(Nullness.NULL)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.NULLABLE.greatestLowerBound(Nullness.NONNULL)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.NULLABLE.greatestLowerBound(Nullness.BOTTOM)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.NULL.greatestLowerBound(Nullness.NULLABLE)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.NULL.greatestLowerBound(Nullness.NULL)).isEqualTo(Nullness.NULL);
        assertThat(Nullness.NULL.greatestLowerBound(Nullness.NONNULL)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.NULL.greatestLowerBound(Nullness.BOTTOM)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.NONNULL.greatestLowerBound(Nullness.NULLABLE)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.NONNULL.greatestLowerBound(Nullness.NULL)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.NONNULL.greatestLowerBound(Nullness.NONNULL)).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.NONNULL.greatestLowerBound(Nullness.BOTTOM)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.BOTTOM.greatestLowerBound(Nullness.NULLABLE)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.BOTTOM.greatestLowerBound(Nullness.NULL)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.BOTTOM.greatestLowerBound(Nullness.NONNULL)).isEqualTo(Nullness.BOTTOM);
        assertThat(Nullness.BOTTOM.greatestLowerBound(Nullness.BOTTOM)).isEqualTo(Nullness.BOTTOM);
    }

    @Test
    public void testDeducedValueWhenNotEqual() {
        assertThat(Nullness.NULLABLE.deducedValueWhenNotEqual()).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.NULL.deducedValueWhenNotEqual()).isEqualTo(Nullness.NONNULL);
        assertThat(Nullness.NONNULL.deducedValueWhenNotEqual()).isEqualTo(Nullness.NULLABLE);
        assertThat(Nullness.BOTTOM.deducedValueWhenNotEqual()).isEqualTo(Nullness.BOTTOM);
    }
}

