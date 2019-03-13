/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.query.impl.predicates;


import IndexCopyBehavior.NEVER;
import com.hazelcast.internal.serialization.impl.DefaultSerializationServiceBuilder;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class AttributeCanonicalizationTest {
    @Test(expected = IllegalArgumentException.class)
    public void testEmptyComponentIsNotAllowed() {
        PredicateUtils.parseOutCompositeIndexComponents("a,");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDuplicateComponentsAreNotAllowed() {
        PredicateUtils.parseOutCompositeIndexComponents("a,b,a");
    }

    @Test
    public void testAttributes() {
        Assert.assertEquals("foo", PredicateUtils.canonicalizeAttribute("foo"));
        Assert.assertEquals("foo", PredicateUtils.canonicalizeAttribute("this.foo"));
        Assert.assertEquals("this", PredicateUtils.canonicalizeAttribute("this"));
        Assert.assertEquals("foo.this.bar", PredicateUtils.canonicalizeAttribute("foo.this.bar"));
        Assert.assertEquals("foo.bar", PredicateUtils.canonicalizeAttribute("this.foo.bar"));
        Assert.assertEquals("foo.bar", PredicateUtils.canonicalizeAttribute("foo.bar"));
        Assert.assertEquals("__key", PredicateUtils.canonicalizeAttribute("__key"));
        Assert.assertEquals("__key.foo", PredicateUtils.canonicalizeAttribute("__key.foo"));
    }

    @Test
    public void testAbstractPredicate() {
        Assert.assertEquals("foo", new AttributeCanonicalizationTest.TestPredicate("foo").attributeName);
        Assert.assertEquals("foo", new AttributeCanonicalizationTest.TestPredicate("this.foo").attributeName);
        Assert.assertEquals("this", new AttributeCanonicalizationTest.TestPredicate("this").attributeName);
        Assert.assertEquals("foo.this.bar", new AttributeCanonicalizationTest.TestPredicate("foo.this.bar").attributeName);
        Assert.assertEquals("foo.bar", new AttributeCanonicalizationTest.TestPredicate("this.foo.bar").attributeName);
        Assert.assertEquals("foo.bar", new AttributeCanonicalizationTest.TestPredicate("foo.bar").attributeName);
        Assert.assertEquals("__key", new AttributeCanonicalizationTest.TestPredicate("__key").attributeName);
        Assert.assertEquals("__key.foo", new AttributeCanonicalizationTest.TestPredicate("__key.foo").attributeName);
    }

    @Test
    public void testIndexes() {
        Indexes indexes = Indexes.newBuilder(new DefaultSerializationServiceBuilder().build(), NEVER).build();
        AttributeCanonicalizationTest.checkIndex(indexes, "foo", "foo");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo", "this.foo");
        AttributeCanonicalizationTest.checkIndex(indexes, "this", "this");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo.this.bar", "foo.this.bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo.bar", "this.foo.bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo.bar", "foo.bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "__key", "__key");
        AttributeCanonicalizationTest.checkIndex(indexes, "__key.foo", "__key.foo");
    }

    @Test
    public void testCompositeIndexes() {
        Indexes indexes = Indexes.newBuilder(new DefaultSerializationServiceBuilder().build(), NEVER).build();
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar", "foo, bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar", "foo , bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar", "this.foo, bar");
        AttributeCanonicalizationTest.checkIndex(indexes, "this, __key", "this,__key");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar.this.baz", "foo, bar.this.baz");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar.baz", "this.foo, this.bar.baz");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo.bar, baz", "foo.bar, baz");
        AttributeCanonicalizationTest.checkIndex(indexes, "foo, bar, __key.baz", "foo, this.bar, __key.baz");
    }

    private static class TestPredicate extends AbstractPredicate {
        public TestPredicate(String attribute) {
            super(attribute);
        }

        @Override
        public int getId() {
            return 0;
        }

        @Override
        protected boolean applyForSingleAttributeValue(Comparable attributeValue) {
            return false;
        }
    }
}

