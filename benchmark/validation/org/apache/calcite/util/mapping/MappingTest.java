/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.util.mapping;


import MappingType.PARTIAL_SURJECTION;
import Mappings.TargetMapping;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for mappings.
 *
 * @see Mapping
 * @see Mappings
 */
public class MappingTest {
    public MappingTest() {
    }

    @Test
    public void testMappings() {
        Assert.assertTrue(Mappings.isIdentity(Mappings.createIdentity(0)));
        Assert.assertTrue(Mappings.isIdentity(Mappings.createIdentity(5)));
        Assert.assertFalse(Mappings.isIdentity(Mappings.create(PARTIAL_SURJECTION, 3, 4)));
        Assert.assertFalse(Mappings.isIdentity(Mappings.create(PARTIAL_SURJECTION, 3, 3)));
        Assert.assertFalse(Mappings.isIdentity(Mappings.create(PARTIAL_SURJECTION, 4, 4)));
    }

    /**
     * Unit test for {@link Mappings#createShiftMapping}.
     */
    @Test
    public void testMappingsCreateShiftMapping() {
        Assert.assertEquals("[size=5, sourceCount=20, targetCount=13, elements=[6:3, 7:4, 15:10, 16:11, 17:12]]", Mappings.createShiftMapping(20, 3, 6, 2, 10, 15, 3).toString());
        // no triples makes for a mapping with 0 targets, 20 sources, but still
        // valid
        Mappings.TargetMapping mapping = Mappings.createShiftMapping(20);
        Assert.assertEquals("[size=0, sourceCount=20, targetCount=0, elements=[]]", mapping.toString());
        Assert.assertEquals(20, mapping.getSourceCount());
        Assert.assertEquals(0, mapping.getTargetCount());
    }

    /**
     * Unit test for {@link Mappings#append}.
     */
    @Test
    public void testMappingsAppend() {
        Assert.assertTrue(Mappings.isIdentity(Mappings.append(Mappings.createIdentity(3), Mappings.createIdentity(2))));
        Mapping mapping0 = Mappings.create(PARTIAL_SURJECTION, 5, 3);
        mapping0.set(0, 2);
        mapping0.set(3, 1);
        mapping0.set(4, 0);
        Assert.assertEquals("[size=5, sourceCount=7, targetCount=5, elements=[0:2, 3:1, 4:0, 5:3, 6:4]]", Mappings.append(mapping0, Mappings.createIdentity(2)).toString());
    }

    /**
     * Unit test for {@link Mappings#offsetSource}.
     */
    @Test
    public void testMappingsOffsetSource() {
        final Mappings.TargetMapping mapping = Mappings.target(ImmutableMap.of(0, 5, 1, 7), 2, 8);
        Assert.assertEquals("[size=2, sourceCount=2, targetCount=8, elements=[0:5, 1:7]]", mapping.toString());
        Assert.assertEquals(2, mapping.getSourceCount());
        Assert.assertEquals(8, mapping.getTargetCount());
        final Mappings.TargetMapping mapping1 = Mappings.offsetSource(mapping, 3, 5);
        Assert.assertEquals("[size=2, sourceCount=5, targetCount=8, elements=[3:5, 4:7]]", mapping1.toString());
        Assert.assertEquals(5, mapping1.getSourceCount());
        Assert.assertEquals(8, mapping1.getTargetCount());
        // mapping that extends RHS
        final Mappings.TargetMapping mapping2 = Mappings.offsetSource(mapping, 3, 15);
        Assert.assertEquals("[size=2, sourceCount=15, targetCount=8, elements=[3:5, 4:7]]", mapping2.toString());
        Assert.assertEquals(15, mapping2.getSourceCount());
        Assert.assertEquals(8, mapping2.getTargetCount());
        try {
            final Mappings.TargetMapping mapping3 = Mappings.offsetSource(mapping, 3, 4);
            Assert.fail(("expected exception, got " + mapping3));
        } catch (IllegalArgumentException e) {
            // ok
        }
    }

    /**
     * Unit test for {@link Mappings#source(List, int)}
     * and its converse, {@link Mappings#asList(Mappings.TargetMapping)}.
     */
    @Test
    public void testSource() {
        List<Integer> targets = Arrays.asList(3, 1, 4, 5, 8);
        final Mapping mapping = Mappings.source(targets, 10);
        Assert.assertThat(mapping.getTarget(0), CoreMatchers.equalTo(3));
        Assert.assertThat(mapping.getTarget(1), CoreMatchers.equalTo(1));
        Assert.assertThat(mapping.getTarget(2), CoreMatchers.equalTo(4));
        Assert.assertThat(mapping.getTargetCount(), CoreMatchers.equalTo(10));
        Assert.assertThat(mapping.getSourceCount(), CoreMatchers.equalTo(5));
        final List<Integer> integers = Mappings.asList(mapping);
        Assert.assertThat(integers, CoreMatchers.equalTo(targets));
        final Mapping inverse = mapping.inverse();
        Assert.assertThat(inverse.toString(), CoreMatchers.equalTo("[size=5, sourceCount=10, targetCount=5, elements=[1:1, 3:0, 4:2, 5:3, 8:4]]"));
    }

    /**
     * Unit test for {@link Mappings#target(List, int)}.
     */
    @Test
    public void testTarget() {
        List<Integer> sources = Arrays.asList(3, 1, 4, 5, 8);
        final Mapping mapping = Mappings.target(sources, 10);
        Assert.assertThat(mapping.getTarget(3), CoreMatchers.equalTo(0));
        Assert.assertThat(mapping.getTarget(1), CoreMatchers.equalTo(1));
        Assert.assertThat(mapping.getTarget(4), CoreMatchers.equalTo(2));
        try {
            final int target = mapping.getTarget(0);
            Assert.fail(("expected error, got " + target));
        } catch (Mappings e) {
            // ok
        }
        Assert.assertThat(mapping.getTargetCount(), CoreMatchers.equalTo(5));
        Assert.assertThat(mapping.getSourceCount(), CoreMatchers.equalTo(10));
        final List<Integer> integers = Mappings.asList(mapping);
        Assert.assertThat(integers, CoreMatchers.equalTo(Arrays.asList(null, 1, null, 0, 2, 3, null, null, 4, null)));
    }

    /**
     * Unit test for {@link Mappings#bijection(List)}.
     */
    @Test
    public void testBijection() {
        List<Integer> targets = Arrays.asList(3, 0, 1, 2);
        final Mapping mapping = Mappings.bijection(targets);
        Assert.assertThat(mapping.size(), CoreMatchers.equalTo(4));
        Assert.assertThat(mapping.getTarget(0), CoreMatchers.equalTo(3));
        Assert.assertThat(mapping.getTarget(1), CoreMatchers.equalTo(0));
        Assert.assertThat(mapping.getTarget(2), CoreMatchers.equalTo(1));
        Assert.assertThat(mapping.getTarget(3), CoreMatchers.equalTo(2));
        Assert.assertThat(mapping.getTargetOpt(3), CoreMatchers.equalTo(2));
        Assert.assertThat(mapping.getSource(3), CoreMatchers.equalTo(0));
        Assert.assertThat(mapping.getSourceOpt(3), CoreMatchers.equalTo(0));
        try {
            final int target = mapping.getTarget(4);
            Assert.fail(("expected error, got " + target));
        } catch (Mappings e) {
            // ok
        }
        try {
            final int source = mapping.getSource(4);
            Assert.fail(("expected error, got " + source));
        } catch (Mappings e) {
            // ok
        }
        Assert.assertThat(mapping.getTargetCount(), CoreMatchers.equalTo(4));
        Assert.assertThat(mapping.getSourceCount(), CoreMatchers.equalTo(4));
        Assert.assertThat(mapping.toString(), CoreMatchers.equalTo("[3, 0, 1, 2]"));
        Assert.assertThat(mapping.inverse().toString(), CoreMatchers.equalTo("[1, 2, 3, 0]"));
        // empty is OK
        final Mapping empty = Mappings.bijection(Collections.emptyList());
        Assert.assertThat(empty.size(), CoreMatchers.equalTo(0));
        Assert.assertThat(empty.iterator().hasNext(), CoreMatchers.equalTo(false));
        Assert.assertThat(empty.toString(), CoreMatchers.equalTo("[]"));
        try {
            final Mapping x = Mappings.bijection(Arrays.asList(0, 5, 1));
            Assert.fail(("expected error, got " + x));
        } catch (Exception e) {
            // ok
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("target out of range"));
        }
        try {
            final Mapping x = Mappings.bijection(Arrays.asList(1, 0, 1));
            Assert.fail(("expected error, got " + x));
        } catch (Exception e) {
            // ok
            Assert.assertThat(e.getMessage(), CoreMatchers.equalTo("more than one permutation element maps to position 1"));
        }
    }
}

/**
 * End MappingTest.java
 */
