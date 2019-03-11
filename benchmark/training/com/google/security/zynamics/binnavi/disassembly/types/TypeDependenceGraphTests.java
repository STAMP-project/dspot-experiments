/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.disassembly.types;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.security.zynamics.binnavi.yfileswrap.disassembly.types.TypeDependenceGraph;
import com.google.security.zynamics.binnavi.yfileswrap.disassembly.types.TypeDependenceGraph.DependenceResult;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static BaseTypeCategory.STRUCT;


/**
 * Test suite for the type dependence graph class.
 */
@RunWith(JUnit4.class)
public class TypeDependenceGraphTests {
    private TestTypeSystem typeSystem;

    private TypeDependenceGraph dependenceGraph;

    @Test
    public void testAddMember() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.addMember(typeSystem.simpleStruct, typeSystem.intType).getAffectedTypes();
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testAddMemberNoDependency() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.addMember(typeSystem.doubleNestedStruct, typeSystem.intType).getAffectedTypes();
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullMember() {
        final TypeDependenceGraph dependenceGraph = new TypeDependenceGraph(ImmutableList.<BaseType>builder().build(), ImmutableList.<TypeMember>builder().build());
        dependenceGraph.addMember(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testAddNullType() {
        final TypeDependenceGraph dependenceGraph = new TypeDependenceGraph(ImmutableList.<BaseType>builder().build(), ImmutableList.<TypeMember>builder().build());
        dependenceGraph.addType(null);
    }

    @Test
    public void testAddType() {
        final BaseType newCompoundType = new BaseType(100, "new_compound_type", 0, false, STRUCT);
        dependenceGraph.addType(newCompoundType);
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.addMember(newCompoundType, typeSystem.simpleStruct).getAffectedTypes();
        Assert.assertEquals(ImmutableSet.<BaseType>of(newCompoundType), affectedTypes);
        final ImmutableSet<BaseType> newAffectedTypes = dependenceGraph.addMember(typeSystem.simpleStruct, typeSystem.intType).getAffectedTypes();
        Assert.assertEquals(ImmutableSet.<BaseType>of(newCompoundType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), newAffectedTypes);
    }

    @Test
    public void testDeleteFooType() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteType(typeSystem.doubleNestedStruct);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testDeleteIntType() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteType(typeSystem.intType);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.intType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.simpleUnion, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testDeleteMember() {
        final TypeMember member = typeSystem.simpleStruct.iterator().next();
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteMember(member);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testDeleteMemberTwoDependencies() {
        final TypeMember member = typeSystem.nestedStruct.iterator().next();
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteMember(member);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testDeleteSimpleStruct() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteType(typeSystem.simpleStruct);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
    }

    @Test(expected = NullPointerException.class)
    public void testDeleteNullType() {
        dependenceGraph.deleteType(null);
    }

    @Test
    public void testDeleteUintType() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.deleteType(typeSystem.uintType);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.uintType, typeSystem.uintArrayType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.simpleUnion, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testIsContainedIn() {
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.simpleStruct, typeSystem.intType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.simpleStruct, typeSystem.uintType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.simpleStruct, typeSystem.uintArrayType));
    }

    @Test
    public void testIsContainedInDoubleNested() {
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.intType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.uintType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.uintArrayType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.uintPointerType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.simpleStruct));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.doubleNestedStruct, typeSystem.nestedStruct));
    }

    @Test
    public void testIsContainedInNested() {
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.nestedStruct, typeSystem.intType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.nestedStruct, typeSystem.uintType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.nestedStruct, typeSystem.uintArrayType));
        Assert.assertTrue(dependenceGraph.isTypeContainedIn(typeSystem.nestedStruct, typeSystem.simpleStruct));
    }

    @Test(expected = NullPointerException.class)
    public void testNullInstantiation() {
        new TypeDependenceGraph(null, null);
    }

    @Test
    public void testRecursiveType() {
        final DependenceResult result = dependenceGraph.addMember(typeSystem.simpleStruct, typeSystem.doubleNestedStruct);
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void testRecursiveTypeWithSelf() {
        final DependenceResult result = dependenceGraph.addMember(typeSystem.simpleStruct, typeSystem.simpleStruct);
        Assert.assertFalse(result.isValid());
    }

    @Test
    public void testUpdateDoubleNestedStructMembers() {
        final BaseType newMemberType = typeSystem.intType;
        final BaseType parentType = typeSystem.doubleNestedStruct;
        for (final TypeMember member : parentType) {
            final ImmutableSet<BaseType> affectedTypes = dependenceGraph.updateMember(parentType, member.getBaseType(), newMemberType).getAffectedTypes();
            Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
        }
    }

    @Test
    public void testUpdateIntType() {
        final Set<BaseType> affectedTypes = dependenceGraph.updateType(typeSystem.intType);
        Assert.assertEquals(Sets.newHashSet(typeSystem.intType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.simpleUnion, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testUpdateSimpleStructMembers() {
        for (final TypeMember member : typeSystem.simpleStruct) {
            final Set<BaseType> affectedTypes = dependenceGraph.updateMember(member.getParentType(), member.getBaseType(), typeSystem.intType).getAffectedTypes();
            Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNullMember() {
        dependenceGraph.updateMember(null, null, null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNullType() {
        dependenceGraph.updateType(null);
    }

    @Test
    public void testUpdateTypeTransitive() {
        // "NestedStruct" is only transitively reachable from "unsigned int" so this test is
        // conceptually different from testUpdateType().
        final Set<BaseType> affectedTypes = dependenceGraph.updateType(typeSystem.uintType);
        Assert.assertEquals(Sets.newHashSet(typeSystem.uintType, typeSystem.uintArrayType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.simpleUnion, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testUpdateUintType() {
        final ImmutableSet<BaseType> affectedTypes = dependenceGraph.updateType(typeSystem.uintType);
        Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.uintType, typeSystem.uintArrayType, typeSystem.simpleStruct, typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.simpleUnion, typeSystem.complexUnion), affectedTypes);
    }

    @Test
    public void testUpdateNestedStructMembers() {
        for (final TypeMember member : typeSystem.nestedStruct) {
            final ImmutableSet<BaseType> affectedTypes = dependenceGraph.updateMember(member.getParentType(), member.getBaseType(), typeSystem.intType).getAffectedTypes();
            Assert.assertEquals(ImmutableSet.<BaseType>of(typeSystem.nestedStruct, typeSystem.doubleNestedStruct, typeSystem.complexUnion), affectedTypes);
        }
    }
}

