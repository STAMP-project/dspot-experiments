/**
 * Copyright (c) 2010-2018. Axon Framework
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axonframework.common.annotation;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.axonframework.common.Priority;
import org.junit.Assert;
import org.junit.Test;


public class PriorityAnnotationComparatorTest {
    private PriorityAnnotationComparator<Object> testSubject;

    @Test
    public void testCompareDifferentPriorities() {
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.LowPrio(), new PriorityAnnotationComparatorTest.HighPrio())) > 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.LowPrio(), new PriorityAnnotationComparatorTest.NeutralPrio())) > 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.NeutralPrio(), new PriorityAnnotationComparatorTest.HighPrio())) > 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.HighPrio(), new PriorityAnnotationComparatorTest.LowPrio())) < 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.NeutralPrio(), new PriorityAnnotationComparatorTest.LowPrio())) < 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.HighPrio(), new PriorityAnnotationComparatorTest.NeutralPrio())) < 0));
    }

    @Test
    public void testCompareSamePriorities() {
        Assert.assertEquals(0, testSubject.compare(new PriorityAnnotationComparatorTest.LowPrio(), new PriorityAnnotationComparatorTest.LowPrio()));
        Assert.assertEquals(0, testSubject.compare(new PriorityAnnotationComparatorTest.NeutralPrio(), new PriorityAnnotationComparatorTest.NeutralPrio()));
        Assert.assertEquals(0, testSubject.compare(new PriorityAnnotationComparatorTest.HighPrio(), new PriorityAnnotationComparatorTest.HighPrio()));
    }

    @Test
    public void testUndefinedConsideredNeutralPriority() {
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.UndefinedPrio(), new PriorityAnnotationComparatorTest.HighPrio())) > 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.UndefinedPrio(), new PriorityAnnotationComparatorTest.LowPrio())) < 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.UndefinedPrio(), new PriorityAnnotationComparatorTest.NeutralPrio())) == 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.HighPrio(), new PriorityAnnotationComparatorTest.UndefinedPrio())) < 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.LowPrio(), new PriorityAnnotationComparatorTest.UndefinedPrio())) > 0));
        Assert.assertTrue(((testSubject.compare(new PriorityAnnotationComparatorTest.NeutralPrio(), new PriorityAnnotationComparatorTest.UndefinedPrio())) == 0));
    }

    @Test
    public void sortComparisonResultsInCorrectSortOrder() {
        PriorityAnnotationComparatorTest.HighPrio highPrio = new PriorityAnnotationComparatorTest.HighPrio();
        PriorityAnnotationComparatorTest.LowPrio lowPrio = new PriorityAnnotationComparatorTest.LowPrio();
        List<Object> result = Arrays.asList(new PriorityAnnotationComparatorTest.NeutralPrio(), new PriorityAnnotationComparatorTest.UndefinedPrio(), highPrio, lowPrio);
        for (int i = 0; i < 100; i++) {
            Collections.shuffle(result);
            result.sort(testSubject);
            Assert.assertEquals(highPrio, result.get(0));
            Assert.assertEquals(lowPrio, result.get(3));
        }
    }

    @Priority(Priority.LAST)
    private static class LowPrio {}

    @Priority(Priority.FIRST)
    private static class HighPrio {}

    @Priority(Priority.NEUTRAL)
    private static class NeutralPrio {}

    private static class UndefinedPrio {}
}

