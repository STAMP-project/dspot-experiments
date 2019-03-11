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
package org.apache.calcite.util;


import SqlTypeName.DOUBLE;
import com.google.common.collect.ImmutableList;
import org.apache.calcite.jdbc.JavaTypeFactoryImpl;
import org.apache.calcite.rel.core.Project;
import org.apache.calcite.rel.type.RelDataType;
import org.apache.calcite.rel.type.RelDataTypeFactory;
import org.apache.calcite.rex.RexBuilder;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for {@link Permutation}.
 */
public class PermutationTestCase {
    // ~ Constructors -----------------------------------------------------------
    public PermutationTestCase() {
    }

    // ~ Methods ----------------------------------------------------------------
    @Test
    public void testOne() {
        final Permutation perm = new Permutation(4);
        Assert.assertEquals("[0, 1, 2, 3]", perm.toString());
        Assert.assertEquals(4, perm.size());
        perm.set(0, 2);
        Assert.assertEquals("[2, 1, 0, 3]", perm.toString());
        perm.set(1, 0);
        Assert.assertEquals("[2, 0, 1, 3]", perm.toString());
        final Permutation invPerm = perm.inverse();
        Assert.assertEquals("[1, 2, 0, 3]", invPerm.toString());
        // changing perm doesn't change inverse
        perm.set(0, 0);
        Assert.assertEquals("[0, 2, 1, 3]", perm.toString());
        Assert.assertEquals("[1, 2, 0, 3]", invPerm.toString());
    }

    @Test
    public void testTwo() {
        final Permutation perm = new Permutation(new int[]{ 3, 2, 0, 1 });
        Assert.assertFalse(perm.isIdentity());
        Assert.assertEquals("[3, 2, 0, 1]", perm.toString());
        Permutation perm2 = ((Permutation) (perm.clone()));
        Assert.assertEquals("[3, 2, 0, 1]", perm2.toString());
        Assert.assertTrue(perm.equals(perm2));
        Assert.assertTrue(perm2.equals(perm));
        perm.set(2, 1);
        Assert.assertEquals("[3, 2, 1, 0]", perm.toString());
        Assert.assertFalse(perm.equals(perm2));
        // clone not affected
        Assert.assertEquals("[3, 2, 0, 1]", perm2.toString());
        perm2.set(2, 3);
        Assert.assertEquals("[0, 2, 3, 1]", perm2.toString());
    }

    @Test
    public void testInsert() {
        Permutation perm = new Permutation(new int[]{ 3, 0, 4, 2, 1 });
        perm.insertTarget(2);
        Assert.assertEquals("[4, 0, 5, 3, 1, 2]", perm.toString());
        // insert at start
        perm = new Permutation(new int[]{ 3, 0, 4, 2, 1 });
        perm.insertTarget(0);
        Assert.assertEquals("[4, 1, 5, 3, 2, 0]", perm.toString());
        // insert at end
        perm = new Permutation(new int[]{ 3, 0, 4, 2, 1 });
        perm.insertTarget(5);
        Assert.assertEquals("[3, 0, 4, 2, 1, 5]", perm.toString());
        // insert into empty
        perm = new Permutation(new int[]{  });
        perm.insertTarget(0);
        Assert.assertEquals("[0]", perm.toString());
    }

    @Test
    public void testEmpty() {
        final Permutation perm = new Permutation(0);
        Assert.assertTrue(perm.isIdentity());
        Assert.assertEquals("[]", perm.toString());
        Assert.assertTrue(perm.equals(perm));
        Assert.assertTrue(perm.equals(perm.inverse()));
        try {
            perm.set(1, 0);
            Assert.fail("expected exception");
        } catch (ArrayIndexOutOfBoundsException e) {
            // success
        }
        try {
            perm.set((-1), 2);
            Assert.fail("expected exception");
        } catch (ArrayIndexOutOfBoundsException e) {
            // success
        }
    }

    @Test
    public void testProjectPermutation() {
        final RelDataTypeFactory typeFactory = new JavaTypeFactoryImpl();
        final RexBuilder builder = new RexBuilder(typeFactory);
        final RelDataType doubleType = typeFactory.createSqlType(DOUBLE);
        // A project with [1, 1] is not a permutation, so should return null
        final Permutation perm = Project.getPermutation(2, ImmutableList.of(builder.makeInputRef(doubleType, 1), builder.makeInputRef(doubleType, 1)));
        Assert.assertThat(perm, CoreMatchers.nullValue());
        // A project with [0, 1, 0] is not a permutation, so should return null
        final Permutation perm1 = Project.getPermutation(2, ImmutableList.of(builder.makeInputRef(doubleType, 0), builder.makeInputRef(doubleType, 1), builder.makeInputRef(doubleType, 0)));
        Assert.assertThat(perm1, CoreMatchers.nullValue());
        // A project of [1, 0] is a valid permutation!
        final Permutation perm2 = Project.getPermutation(2, ImmutableList.of(builder.makeInputRef(doubleType, 1), builder.makeInputRef(doubleType, 0)));
        Assert.assertThat(perm2, Is.is(new Permutation(new int[]{ 1, 0 })));
    }
}

/**
 * End PermutationTestCase.java
 */
