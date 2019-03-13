/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil.stream;


import com.google.common.collect.Iterables;
import java.util.LinkedHashSet;
import java.util.stream.IntStream;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.LimitedInMemorySortedIterable;


public class GafferCollectorTest {
    @Test
    public void shouldCollectToLinkedHashSet() {
        // Given
        final IntStream stream = IntStream.range(0, 100);
        // When
        final Iterable<Integer> iterable = stream.boxed().collect(GafferCollectors.toLinkedHashSet());
        // Then
        MatcherAssert.assertThat(iterable, IsInstanceOf.instanceOf(LinkedHashSet.class));
        MatcherAssert.assertThat(Iterables.size(iterable), IsEqual.equalTo(100));
    }

    @Test
    public void shouldCollectToLimitedSortedSet() {
        // Given
        final IntStream stream = IntStream.range(0, 100);
        final int limit = 50;
        final boolean deduplicate = true;
        // When
        final LimitedInMemorySortedIterable<Integer> result = stream.boxed().collect(GafferCollectors.toLimitedInMemorySortedIterable(Integer::compareTo, limit, deduplicate));
        // Then
        Assert.assertEquals(50, result.size());
    }
}

