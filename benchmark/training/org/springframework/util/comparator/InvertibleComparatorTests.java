/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.util.comparator;


import java.util.Comparator;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link InvertibleComparator}.
 *
 * @author Keith Donald
 * @author Chris Beams
 * @author Phillip Webb
 */
@Deprecated
public class InvertibleComparatorTests {
    private final Comparator<Integer> comparator = new ComparableComparator();

    @Test(expected = IllegalArgumentException.class)
    public void shouldNeedComparator() throws Exception {
        new InvertibleComparator(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNeedComparatorWithAscending() throws Exception {
        new InvertibleComparator(null, true);
    }

    @Test
    public void shouldDefaultToAscending() throws Exception {
        InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator(comparator);
        Assert.assertThat(invertibleComparator.isAscending(), CoreMatchers.is(true));
        Assert.assertThat(invertibleComparator.compare(1, 2), CoreMatchers.is((-1)));
    }

    @Test
    public void shouldInvert() throws Exception {
        InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator(comparator);
        Assert.assertThat(invertibleComparator.isAscending(), CoreMatchers.is(true));
        Assert.assertThat(invertibleComparator.compare(1, 2), CoreMatchers.is((-1)));
        invertibleComparator.invertOrder();
        Assert.assertThat(invertibleComparator.isAscending(), CoreMatchers.is(false));
        Assert.assertThat(invertibleComparator.compare(1, 2), CoreMatchers.is(1));
    }

    @Test
    public void shouldCompareAscending() throws Exception {
        InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator(comparator, true);
        Assert.assertThat(invertibleComparator.compare(1, 2), CoreMatchers.is((-1)));
    }

    @Test
    public void shouldCompareDescending() throws Exception {
        InvertibleComparator<Integer> invertibleComparator = new InvertibleComparator(comparator, false);
        Assert.assertThat(invertibleComparator.compare(1, 2), CoreMatchers.is(1));
    }
}

