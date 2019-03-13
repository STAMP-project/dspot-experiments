/**
 * Copyright 2017 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.lib.analysis;


import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.devtools.build.lib.analysis.AspectCollection.AspectCycleOnPathException;
import com.google.devtools.build.lib.analysis.AspectCollection.AspectDeps;
import com.google.devtools.build.lib.packages.Aspect;
import com.google.devtools.build.lib.packages.AspectDescriptor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link AspectCollection}
 */
@RunWith(JUnit4.class)
public class AspectCollectionTest {
    private static final Function<Aspect, AspectDescriptor> ASPECT_TO_DESCRIPTOR = new Function<Aspect, AspectDescriptor>() {
        @Override
        public AspectDescriptor apply(Aspect aspect) {
            return aspect.getDescriptor();
        }
    };

    private static final Function<AspectDeps, AspectDescriptor> ASPECT_PATH_TO_DESCRIPTOR = new Function<AspectDeps, AspectDescriptor>() {
        @Override
        public AspectDescriptor apply(AspectDeps aspectPath) {
            return aspectPath.getAspect();
        }
    };

    /**
     * a3 wants a1 and a2, a1 and a2 want no one, path is a1, a2, a3.
     */
    @Test
    public void linearAspectPath1() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3", "a1", "a2");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a1, a2, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a1, a2), AspectCollectionTest.expectDeps(a1), AspectCollectionTest.expectDeps(a2));
    }

    /**
     * a3 wants a2, a2 wants a1, a1 wants no one, path is a1, a2, a3.
     */
    @Test
    public void linearAspectPath2() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a2");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a1, a2, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a2), AspectCollectionTest.expectDeps(a2, a1), AspectCollectionTest.expectDeps(a1));
    }

    /**
     * a3 wants a1, a1 wants a2,  path is a1, a2, a3, so a2 comes after a1.
     */
    @Test
    public void validateOrder() throws Exception {
        Aspect a1 = createAspect("a1", "a2");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3", "a1");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a1, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a1), AspectCollectionTest.expectDeps(a1));
    }

    /**
     * a3 wants a1, a1 wants a2, a2 wants a1, path is a1, a2, a3, so a2 comes after a1.
     */
    @Test
    public void validateOrder2() throws Exception {
        Aspect a1 = createAspect("a1", "a2");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a1");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a1, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a1), AspectCollectionTest.expectDeps(a1));
    }

    /**
     * a3 wants no one => a1 and a2 must be removed.
     */
    @Test
    public void unneededRemoved() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3));
    }

    /**
     * a3 wants itself.
     */
    @Test
    public void recursive() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3", "a3");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3));
    }

    /**
     * a2 (non-visible aspect) wants itself, a3 wants a2.
     */
    @Test
    public void recursiveNonVisible() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2", "a2");
        Aspect a3 = createAspect("a3", "a2");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a2, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a2), AspectCollectionTest.expectDeps(a2));
    }

    /**
     * Both a2 and a3 are visible, a2 wants a1, a3 wants nothing.
     */
    @Test
    public void twoVisibleAspects() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a1, a2, a3), ImmutableSet.of(a2.getDescriptor(), a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a1, a2, a3), ImmutableList.of(a2, a3), AspectCollectionTest.expectDeps(a3), AspectCollectionTest.expectDeps(a2, a1), AspectCollectionTest.expectDeps(a1));
    }

    /**
     * a2 wants a1, a3 wants a1 and a2, the path is [a2, a1, a2, a3], so a2 occurs twice.
     *
     * First occurrence of a2 would not see a1, but the second would: that is an error.
     */
    @Test
    public void duplicateAspect() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a2", "a1");
        try {
            AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
            Assert.fail();
        } catch (AspectCycleOnPathException e) {
            assertThat(e.getAspect()).isEqualTo(a2.getDescriptor());
            assertThat(e.getPreviousAspect()).isEqualTo(a1.getDescriptor());
        }
    }

    /**
     * a2 wants a1, a3 wants a2, the path is [a2, a1, a2, a3], so a2 occurs twice.
     *
     * First occurrence of a2 would not see a1, but the second would: that is an error.
     */
    @Test
    public void duplicateAspect2() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a2");
        try {
            AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
            Assert.fail();
        } catch (AspectCycleOnPathException e) {
            assertThat(e.getAspect()).isEqualTo(a2.getDescriptor());
            assertThat(e.getPreviousAspect()).isEqualTo(a1.getDescriptor());
        }
    }

    /**
     * a3 wants a1 and a2, a2 does not want a1.
     *  The path is [a2, a1, a2, a3], so a2 occurs twice.
     *  Second occurrence of a2 is consistent with the first.
     */
    @Test
    public void duplicateAspect2a() throws Exception {
        Aspect a1 = createAspect("a1");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3", "a1", "a2");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a2, a1, a3), ImmutableList.of(a3), AspectCollectionTest.expectDeps(a3, a2, a1), AspectCollectionTest.expectDeps(a2), AspectCollectionTest.expectDeps(a1));
    }

    /**
     * a2 wants a1, a3 wants a1 and a2, a1 wants a2. the path is [a2, a1, a2, a3], so a2 occurs twice.
     * First occurrence of a2 does not see a1, but the second does => error.
     */
    @Test
    public void duplicateAspect3() throws Exception {
        Aspect a1 = createAspect("a1", "a2");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a1", "a2");
        try {
            AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
            Assert.fail();
        } catch (AspectCycleOnPathException e) {
            assertThat(e.getAspect()).isEqualTo(a2.getDescriptor());
            assertThat(e.getPreviousAspect()).isEqualTo(a1.getDescriptor());
        }
    }

    /**
     * a2 wants a1, a3 wants a2, a1 wants a2. the path is [a2, a1, a2, a3], so a2 occurs twice.
     * First occurrence of a2 does not see a1, but the second does => error.
     * a1 disappears.
     */
    @Test
    public void duplicateAspect4() throws Exception {
        Aspect a1 = createAspect("a1", "a2");
        Aspect a2 = createAspect("a2", "a1");
        Aspect a3 = createAspect("a3", "a2");
        try {
            AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a3.getDescriptor()));
            Assert.fail();
        } catch (AspectCycleOnPathException e) {
            assertThat(e.getAspect()).isEqualTo(a2.getDescriptor());
            assertThat(e.getPreviousAspect()).isEqualTo(a1.getDescriptor());
        }
    }

    /**
     * a2 and a3 are visible.
     * a3 wants a2, a1 wants a2. The path is [a2, a1, a2, a3], so a2 occurs twice.
     * First occurrence of a2 is consistent with the second.
     * a1 disappears.
     */
    @Test
    public void duplicateAspectVisible() throws Exception {
        Aspect a1 = createAspect("a1", "a2");
        Aspect a2 = createAspect("a2");
        Aspect a3 = createAspect("a3", "a2");
        AspectCollection collection = AspectCollection.create(ImmutableList.of(a2, a1, a2, a3), ImmutableSet.of(a2.getDescriptor(), a3.getDescriptor()));
        AspectCollectionTest.validateAspectCollection(collection, ImmutableList.of(a2, a3), ImmutableList.of(a2, a3), AspectCollectionTest.expectDeps(a3, a2), AspectCollectionTest.expectDeps(a2));
    }
}

