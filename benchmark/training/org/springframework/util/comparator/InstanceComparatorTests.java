/**
 * Copyright 2002-2016 the original author or authors.
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
 * Tests for {@link InstanceComparator}.
 *
 * @author Phillip Webb
 */
public class InstanceComparatorTests {
    private InstanceComparatorTests.C1 c1 = new InstanceComparatorTests.C1();

    private InstanceComparatorTests.C2 c2 = new InstanceComparatorTests.C2();

    private InstanceComparatorTests.C3 c3 = new InstanceComparatorTests.C3();

    private InstanceComparatorTests.C4 c4 = new InstanceComparatorTests.C4();

    @Test
    public void shouldCompareClasses() throws Exception {
        Comparator<Object> comparator = new InstanceComparator(InstanceComparatorTests.C1.class, InstanceComparatorTests.C2.class);
        Assert.assertThat(comparator.compare(c1, c1), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(c1, c2), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c2, c1), CoreMatchers.is(1));
        Assert.assertThat(comparator.compare(c2, c3), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c2, c4), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c3, c4), CoreMatchers.is(0));
    }

    @Test
    public void shouldCompareInterfaces() throws Exception {
        Comparator<Object> comparator = new InstanceComparator(InstanceComparatorTests.I1.class, InstanceComparatorTests.I2.class);
        Assert.assertThat(comparator.compare(c1, c1), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(c1, c2), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(c2, c1), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(c1, c3), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c3, c1), CoreMatchers.is(1));
        Assert.assertThat(comparator.compare(c3, c4), CoreMatchers.is(0));
    }

    @Test
    public void shouldCompareMix() throws Exception {
        Comparator<Object> comparator = new InstanceComparator(InstanceComparatorTests.I1.class, InstanceComparatorTests.C3.class);
        Assert.assertThat(comparator.compare(c1, c1), CoreMatchers.is(0));
        Assert.assertThat(comparator.compare(c3, c4), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c3, null), CoreMatchers.is((-1)));
        Assert.assertThat(comparator.compare(c4, null), CoreMatchers.is(0));
    }

    private static interface I1 {}

    private static interface I2 {}

    private static class C1 implements InstanceComparatorTests.I1 {}

    private static class C2 implements InstanceComparatorTests.I1 {}

    private static class C3 implements InstanceComparatorTests.I2 {}

    private static class C4 implements InstanceComparatorTests.I2 {}
}

