/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.drill.exec.record;


import TypeProtos.MajorType;
import TypeProtos.MinorType.LIST;
import TypeProtos.MinorType.MAP;
import org.apache.drill.categories.VectorTest;
import org.apache.drill.common.types.TypeProtos;
import org.apache.drill.common.types.Types;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(VectorTest.class)
public class TestMaterializedField {
    private static final String PARENT_NAME = "parent";

    private static final String PARENT_SECOND_NAME = "parent2";

    private static final String CHILD_NAME = "child";

    private static final String CHILD_SECOND_NAME = "child2";

    private static final MajorType PARENT_TYPE = Types.repeated(MAP);

    private static final MajorType PARENT_SECOND_TYPE = Types.repeated(LIST);

    private static final MajorType CHILD_TYPE = Types.repeated(MAP);

    private static final MajorType CHILD_SECOND_TYPE = Types.repeated(MAP);

    // set of (name, type) tuples representing a test case
    private static final Object[][] matrix = new Object[][]{ new Object[]{ TestMaterializedField.PARENT_SECOND_NAME, TestMaterializedField.PARENT_TYPE }, new Object[]{ TestMaterializedField.PARENT_NAME, TestMaterializedField.PARENT_SECOND_TYPE }, new Object[]{ TestMaterializedField.CHILD_SECOND_NAME, TestMaterializedField.CHILD_TYPE }, new Object[]{ TestMaterializedField.CHILD_NAME, TestMaterializedField.CHILD_SECOND_TYPE } };

    private MaterializedField parent;

    private MaterializedField child;

    @Test
    public void testClone() {
        final MaterializedField cloneParent = parent.clone();
        final boolean isParentEqual = parent.equals(cloneParent);
        Assert.assertTrue("Cloned parent does not match the original", isParentEqual);
        final MaterializedField cloneChild = child.clone();
        final boolean isChildEqual = child.equals(cloneChild);
        Assert.assertTrue("Cloned child does not match the original", isChildEqual);
        for (final MaterializedField field : new MaterializedField[]{ parent, child }) {
            for (Object[] args : TestMaterializedField.matrix) {
                final String path = args[0].toString();
                final TypeProtos.MajorType type = MajorType.class.cast(args[1]);
                final MaterializedField clone = field.withPathAndType(path, type);
                final boolean isPathEqual = path.equals(clone.getName());
                Assert.assertTrue("Cloned path does not match the original", isPathEqual);
                final boolean isTypeEqual = type.equals(clone.getType());
                Assert.assertTrue("Cloned type does not match the original", isTypeEqual);
                final boolean isChildrenEqual = field.getChildren().equals(clone.getChildren());
                Assert.assertTrue("Cloned children do not match the original", isChildrenEqual);
            }
        }
    }
}

