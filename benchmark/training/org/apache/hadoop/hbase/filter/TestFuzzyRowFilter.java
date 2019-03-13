/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hbase.filter;


import FuzzyRowFilter.SatisfiesCode.NEXT_EXISTS;
import FuzzyRowFilter.SatisfiesCode.YES;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.SmallTests;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category({ FilterTests.class, SmallTests.class })
public class TestFuzzyRowFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestFuzzyRowFilter.class);

    @Test
    public void testSatisfiesNoUnsafeForward() {
        Assert.assertEquals(YES, FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, ((byte) (-128)), 1, 0, 1 }, 0, 5, new byte[]{ 1, 0, 1 }, new byte[]{ 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, ((byte) (-128)), 2, 0, 1 }, 0, 5, new byte[]{ 1, 0, 1 }, new byte[]{ 0, 1, 0 }));
        Assert.assertEquals(YES, FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, 2, 1, 3, 3 }, 0, 5, new byte[]{ 1, 2, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, // row to check
        // fuzzy row
        FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, 1, 1, 3, 0 }, 0, 5, new byte[]{ 1, 2, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));// mask

        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, 1, 1, 3, 0 }, 0, 5, new byte[]{ 1, ((byte) (245)), 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(false, new byte[]{ 1, 2, 1, 0, 1 }, 0, 5, new byte[]{ 0, 1, 2 }, new byte[]{ 1, 0, 0 }));
    }

    @Test
    public void testSatisfiesForward() {
        Assert.assertEquals(YES, FuzzyRowFilter.satisfies(false, new byte[]{ 1, ((byte) (-128)), 1, 0, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(false, new byte[]{ 1, ((byte) (-128)), 2, 0, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));
        Assert.assertEquals(YES, FuzzyRowFilter.satisfies(false, new byte[]{ 1, 2, 1, 3, 3 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, // row to check
        // fuzzy row
        FuzzyRowFilter.satisfies(false, new byte[]{ 1, 1, 1, 3, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));// mask

        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(false, new byte[]{ 1, 1, 1, 3, 0 }, new byte[]{ 1, ((byte) (245)), 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(false, new byte[]{ 1, 2, 1, 0, 1 }, new byte[]{ 0, 1, 2 }, new byte[]{ 0, -1, -1 }));
    }

    @Test
    public void testSatisfiesReverse() {
        Assert.assertEquals(YES, FuzzyRowFilter.satisfies(true, new byte[]{ 1, ((byte) (-128)), 1, 0, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 1, ((byte) (-128)), 2, 0, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 2, 3, 1, 1, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));
        Assert.assertEquals(YES, FuzzyRowFilter.satisfies(true, new byte[]{ 1, 2, 1, 3, 3 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 1, ((byte) (245)), 1, 3, 0 }, new byte[]{ 1, 1, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 1, 3, 1, 3, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 2, 1, 1, 1, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfies(true, new byte[]{ 1, 2, 1, 0, 1 }, new byte[]{ 0, 1, 2 }, new byte[]{ 0, -1, -1 }));
    }

    @Test
    public void testSatisfiesNoUnsafeReverse() {
        Assert.assertEquals(YES, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, ((byte) (-128)), 1, 0, 1 }, 0, 5, new byte[]{ 1, 0, 1 }, new byte[]{ 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, ((byte) (-128)), 2, 0, 1 }, 0, 5, new byte[]{ 1, 0, 1 }, new byte[]{ 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 2, 3, 1, 1, 1 }, 0, 5, new byte[]{ 1, 0, 1 }, new byte[]{ 0, 1, 0 }));
        Assert.assertEquals(YES, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, 2, 1, 3, 3 }, 0, 5, new byte[]{ 1, 2, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, ((byte) (245)), 1, 3, 0 }, 0, 5, new byte[]{ 1, 1, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, 3, 1, 3, 0 }, 0, 5, new byte[]{ 1, 2, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 2, 1, 1, 1, 0 }, 0, 5, new byte[]{ 1, 2, 0, 3 }, new byte[]{ 0, 0, 1, 0 }));
        Assert.assertEquals(NEXT_EXISTS, FuzzyRowFilter.satisfiesNoUnsafe(true, new byte[]{ 1, 2, 1, 0, 1 }, 0, 5, new byte[]{ 0, 1, 2 }, new byte[]{ 1, 0, 0 }));
    }

    @Test
    public void testGetNextForFuzzyRuleForward() {
        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 2 }, new byte[]{ 0, -1, -1 }, new byte[]{ 1, 2, 1, 0, 1 }, new byte[]{ 2, 1, 2 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 2 }, new byte[]{ 0, -1, -1 }, new byte[]{ 1, 1, 2, 0, 1 }, new byte[]{ 1, 1, 2, 0, 2 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 2, 0 }, new byte[]{ 0, -1, 0, -1, 0 }, new byte[]{ 1, 0, 2, 0, 1 }, new byte[]{ 1, 1, 0, 2 });// expected next

        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }, new byte[]{ 1, ((byte) (128)), 2, 0, 1 }, new byte[]{ 1, ((byte) (129)), 1 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, 0, 1 }, new byte[]{ 5, 1, 1, 1 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, 0, 1, 1 }, new byte[]{ 5, 1, 0, 1, 2 });
        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 0 }, new byte[]{ 0, -1, 0, 0 }, new byte[]{ 5, 1, ((byte) (255)), 1 }, new byte[]{ 5, 1, ((byte) (255)), 2 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, ((byte) (255)), 1 }, new byte[]{ 6, 1, 0, 1 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, ((byte) (255)), 0 }, new byte[]{ 5, 1, ((byte) (255)), 1 });// expected next

        TestFuzzyRowFilter.assertNext(false, new byte[]{ 5, 1, 1, 0 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 5, 1, ((byte) (255)), 1 }, new byte[]{ 5, 1, ((byte) (255)), 2 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 1, 1, 1 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 1, 1, 2, 2 }, new byte[]{ 1, 1, 2, 3 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 1, 1, 1 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 1, 1, 3, 2 }, new byte[]{ 1, 1, 3, 3 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 1, 1, 1 }, new byte[]{ 0, 0, 0, 0 }, new byte[]{ 1, 1, 2, 3 }, new byte[]{ 1, 1, 2, 4 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 1, 1, 1 }, new byte[]{ 0, 0, 0, 0 }, new byte[]{ 1, 1, 3, 2 }, new byte[]{ 1, 1, 3, 3 });
        TestFuzzyRowFilter.assertNext(false, new byte[]{ 1, 1, 0, 0 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 0, 1, 3, 2 }, new byte[]{ 1, 1 });
        // No next for this one
        Assert.assertNull(// row to check
        // fuzzy row
        FuzzyRowFilter.getNextForFuzzyRule(new byte[]{ 2, 3, 1, 1, 1 }, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }));// mask

        Assert.assertNull(FuzzyRowFilter.getNextForFuzzyRule(new byte[]{ 1, ((byte) (245)), 1, 3, 0 }, new byte[]{ 1, 1, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertNull(FuzzyRowFilter.getNextForFuzzyRule(new byte[]{ 1, 3, 1, 3, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
        Assert.assertNull(FuzzyRowFilter.getNextForFuzzyRule(new byte[]{ 2, 1, 1, 1, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
    }

    @Test
    public void testGetNextForFuzzyRuleReverse() {
        // fuzzy row
        // mask
        // current
        // TODO: should be {1, 1, 3} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 2 }, new byte[]{ 0, -1, -1 }, new byte[]{ 1, 2, 1, 0, 1 }, new byte[]{ 1, 1, 2, ((byte) (255)), ((byte) (255)) });// expected next

        // fuzzy row
        // mask
        // current
        // TODO: should be {1, 1, 1, 3} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 2, 0 }, new byte[]{ 0, -1, 0, -1, 0 }, new byte[]{ 1, 2, 1, 3, 1 }, new byte[]{ 1, 1, 0, 2, 0 });// expected next

        // TODO: should be {1, (byte) 128, 2} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }, new byte[]{ 1, ((byte) (128)), 2, 0, 1 }, new byte[]{ 1, ((byte) (128)), 1, ((byte) (255)), ((byte) (255)) });
        // TODO: should be {5, 1, 0, 2} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, 0, 2, 1 }, new byte[]{ 5, 1, 0, 1, ((byte) (255)) });
        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 0 }, new byte[]{ 0, -1, 0, 0 }, new byte[]{ 5, 1, ((byte) (255)), 1 }, new byte[]{ 5, 1, ((byte) (255)), 0 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, 0, 1 }, new byte[]{ 4, 1, ((byte) (255)), 1 });// expected next

        // fuzzy row
        // mask
        // current
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, ((byte) (255)), 0 }, new byte[]{ 5, 1, ((byte) (254)), 1 });// expected next

        // TODO: should be {1, 0} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 1, 0, 0 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 2, 1, 3, 2 }, new byte[]{ 1, 1, 0, 0 });
        // fuzzy row
        // mask
        // row to check
        // TODO: should be {1, (byte) 0xFF, 2} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }, new byte[]{ 2, 3, 1, 1, 1 }, new byte[]{ 1, 0, 1, ((byte) (255)), ((byte) (255)) });
        // TODO: should be {1, 1, (byte) 255, 4} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 1, 0, 3 }, new byte[]{ -1, -1, 0, -1 }, new byte[]{ 1, ((byte) (245)), 1, 3, 0 }, new byte[]{ 1, 1, 0, 3, ((byte) (255)) });
        // TODO: should be 1, 2, (byte) 255, 4 ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }, new byte[]{ 1, 3, 1, 3, 0 }, new byte[]{ 1, 2, 0, 3, ((byte) (255)) });
        // TODO: should be {1, 2, (byte) 255, 4} ?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }, new byte[]{ 2, 1, 1, 1, 0 }, new byte[]{ 1, 2, 0, 3, ((byte) (255)) });
        // TODO: should be null?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 0, 1 }, new byte[]{ -1, 0, -1 }, new byte[]{ 1, ((byte) (128)), 2 }, new byte[]{ 1, ((byte) (128)), 1 });
        // TODO: should be null?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 0, 1, 0, 1 }, new byte[]{ 0, -1, 0, -1 }, new byte[]{ 5, 1, 0, 2 }, new byte[]{ 5, 1, 0, 1 });
        // TODO: should be null?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 5, 1, 1, 0 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 5, 1, ((byte) (255)), 1 }, new byte[]{ 5, 1, ((byte) (255)), 0 });
        // TODO: should be null?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 1, 1, 1 }, new byte[]{ -1, -1, 0, 0 }, new byte[]{ 1, 1, 2, 2 }, new byte[]{ 1, 1, 2, 1 });
        // TODO: should be null?
        TestFuzzyRowFilter.assertNext(true, new byte[]{ 1, 1, 1, 1 }, new byte[]{ 0, 0, 0, 0 }, new byte[]{ 1, 1, 2, 3 }, new byte[]{ 1, 1, 2, 2 });
        Assert.assertNull(FuzzyRowFilter.getNextForFuzzyRule(true, new byte[]{ 1, 1, 1, 3, 0 }, new byte[]{ 1, 2, 0, 3 }, new byte[]{ -1, -1, 0, -1 }));
    }
}

