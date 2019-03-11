/**
 * Copyright 2011-2015 John Ericksen
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
package org.parceler.internal;


import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import javax.inject.Inject;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.bootstrap.Bootstrap;
import org.junit.Assert;
import org.junit.Test;


@Bootstrap
public class ASTTypeHierarchyIteratorTest {
    static class A extends ASTTypeHierarchyIteratorTest.B {}

    static class B extends ASTTypeHierarchyIteratorTest.C {}

    static class C {}

    private ASTType a;

    private ASTType b;

    private ASTType c;

    @Inject
    ASTClassFactory astClassFactory;

    @Test
    public void testRegularIterator() {
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.<ASTType>of());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(a, iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(b, iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(c, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(null, iterator.next());
    }

    @Test
    public void testSkipIterator() {
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.of(b, c));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(b, iterator.next());
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(c, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(null, iterator.next());
    }

    @Test
    public void testSkipIncludingObjectIterator() {
        Iterator<ASTType> iterator = new ASTTypeHierarchyIterator(a, ImmutableSet.of(c));
        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals(c, iterator.next());
        Assert.assertFalse(iterator.hasNext());
        Assert.assertEquals(null, iterator.next());
    }
}

