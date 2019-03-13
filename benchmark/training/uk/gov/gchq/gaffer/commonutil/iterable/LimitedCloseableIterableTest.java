/**
 * Copyright 2016-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.commonutil.iterable;


import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.exception.LimitExceededException;


public class LimitedCloseableIterableTest {
    @Test
    public void shouldLimitResultsToFirstItem() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 0;
        final int end = 1;
        // When
        final CloseableIterable<Integer> limitedValues = new LimitedCloseableIterable(values, start, end);
        // Then
        Assert.assertEquals(values.subList(start, end), Lists.newArrayList(limitedValues));
    }

    @Test
    public void shouldLimitResultsToLastItem() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 2;
        final int end = Integer.MAX_VALUE;
        // When
        final CloseableIterable<Integer> limitedValues = new LimitedCloseableIterable(values, start, end);
        // Then
        Assert.assertEquals(values.subList(start, values.size()), Lists.newArrayList(limitedValues));
    }

    @Test
    public void shouldNotLimitResults() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 0;
        final int end = Integer.MAX_VALUE;
        // When
        final CloseableIterable<Integer> limitedValues = new LimitedCloseableIterable(values, start, end);
        // Then
        Assert.assertEquals(values, Lists.newArrayList(limitedValues));
    }

    @Test
    public void shouldReturnNoValuesIfStartIsBiggerThanSize() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 5;
        final int end = Integer.MAX_VALUE;
        // When
        final CloseableIterable<Integer> limitedValues = new LimitedCloseableIterable(values, start, end);
        // Then
        Assert.assertTrue(Lists.newArrayList(limitedValues).isEmpty());
    }

    @Test
    public void shouldThrowExceptionIfStartIsBiggerThanEnd() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 3;
        final int end = 1;
        // When / Then
        try {
            new LimitedCloseableIterable(values, start, end);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionIfDataIsTruncated() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 0;
        final int end = 2;
        final boolean truncate = false;
        // When
        final CloseableIterable<Integer> limitedValues = new LimitedCloseableIterable(values, start, end, truncate);
        // Then
        try {
            for (final Integer i : limitedValues) {
                // Do nothing
            }
            Assert.fail("Exception expected");
        } catch (final LimitExceededException e) {
            Assert.assertEquals((("Limit of " + end) + " exceeded."), e.getMessage());
        }
    }

    @Test
    public void shouldHandleNullIterable() {
        // Given
        final CloseableIterable<Integer> nullIterable = new LimitedCloseableIterable(null, 0, 1, true);
        // Then
        Assert.assertTrue(Lists.newArrayList(nullIterable).isEmpty());
    }

    @Test
    public void shouldHandleLimitEqualToIterableLength() {
        // Given
        final List<Integer> values = Arrays.asList(0, 1, 2, 3);
        final int start = 0;
        final int end = 4;
        final boolean truncate = false;
        // When
        final CloseableIterable<Integer> equalValues = new LimitedCloseableIterable(values, start, end, truncate);
        // Then
        Assert.assertEquals(values, Lists.newArrayList(equalValues));
    }
}

