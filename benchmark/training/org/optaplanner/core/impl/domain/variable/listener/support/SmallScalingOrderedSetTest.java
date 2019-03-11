/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.domain.variable.listener.support;


import SmallScalingOrderedSet.LIST_SIZE_THRESHOLD;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

import static SmallScalingOrderedSet.LIST_SIZE_THRESHOLD;


public class SmallScalingOrderedSetTest {
    @Test
    public void addRemoveAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet();
        Assert.assertTrue(set.add("s1"));
        Assert.assertFalse(set.add("s1"));
        Assert.assertTrue(set.add("s2"));
        Assert.assertFalse(set.add("s1"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertTrue(set.remove("s2"));
        Assert.assertFalse(set.remove("s2"));
        Assert.assertTrue(set.add("s2"));
        Assert.assertEquals(2, set.size());
        Assert.assertTrue(set.contains("s1"));
        Assert.assertTrue(set.contains("s2"));
        for (int i = 0; i < ((LIST_SIZE_THRESHOLD) - 3); i++) {
            set.add(("filler " + i));
        }
        Assert.assertFalse(set.add("s2"));
        Assert.assertTrue(set.add("s3"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertEquals(LIST_SIZE_THRESHOLD, set.size());
        Assert.assertTrue(set.add("s4"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertFalse(set.add("s3"));
        Assert.assertFalse(set.add("s4"));
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) + 1), set.size());
        Assert.assertTrue(set.remove("s4"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertFalse(set.add("s3"));
        Assert.assertEquals(LIST_SIZE_THRESHOLD, set.size());
        Assert.assertTrue(set.add("s5"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertFalse(set.add("s3"));
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) + 1), set.size());
        Assert.assertTrue(set.add("s6"));
        Assert.assertFalse(set.add("s2"));
        Assert.assertFalse(set.add("s3"));
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) + 2), set.size());
        Assert.assertTrue(set.contains("s1"));
        Assert.assertTrue(set.contains("s2"));
        Assert.assertTrue(set.contains("s3"));
        Assert.assertFalse(set.contains("s4"));
        Assert.assertTrue(set.contains("s5"));
        Assert.assertTrue(set.contains("s6"));
    }

    @Test
    public void addAllAroundThreshold() {
        SmallScalingOrderedSet<String> set = new SmallScalingOrderedSet();
        Assert.assertTrue(set.addAll(Arrays.asList("s1", "s2", "s3")));
        Assert.assertEquals(3, set.size());
        Assert.assertTrue(set.addAll(Arrays.asList("s1", "s3", "s4", "s5")));
        Assert.assertFalse(set.addAll(Arrays.asList("s1", "s2", "s4")));
        Assert.assertEquals(5, set.size());
        Assert.assertTrue(set.contains("s1"));
        Assert.assertTrue(set.contains("s2"));
        Assert.assertTrue(set.contains("s3"));
        Assert.assertTrue(set.contains("s4"));
        Assert.assertTrue(set.contains("s5"));
        for (int i = 0; i < ((LIST_SIZE_THRESHOLD) - 7); i++) {
            set.add(("filler " + i));
        }
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) - 2), set.size());
        Assert.assertTrue(set.addAll(Arrays.asList("s6", "s7", "s2", "s3", "s8", "s9")));
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) + 2), set.size());
        Assert.assertTrue(set.remove("s1"));
        Assert.assertTrue(set.remove("s5"));
        Assert.assertEquals(LIST_SIZE_THRESHOLD, set.size());
        Assert.assertTrue(set.addAll(Arrays.asList("s1", "s2", "s10")));
        Assert.assertEquals(((LIST_SIZE_THRESHOLD) + 2), set.size());
        Assert.assertTrue(set.contains("s1"));
        Assert.assertTrue(set.contains("s2"));
        Assert.assertTrue(set.contains("s3"));
        Assert.assertTrue(set.contains("s4"));
        Assert.assertFalse(set.contains("s5"));
        Assert.assertTrue(set.contains("s6"));
        Assert.assertTrue(set.contains("s7"));
        Assert.assertTrue(set.contains("s8"));
        Assert.assertTrue(set.contains("s9"));
        Assert.assertTrue(set.contains("s10"));
    }
}

