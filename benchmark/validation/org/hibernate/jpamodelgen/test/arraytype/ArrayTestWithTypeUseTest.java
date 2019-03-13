/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.arraytype;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class ArrayTestWithTypeUseTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "HHH-12011")
    @WithClasses(TestEntity.class)
    public void testArrayWithBeanValidation() {
        TestUtil.assertMetamodelClassGeneratedFor(TestEntity.class);
        // Primitive Arrays
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "primitiveAnnotatedArray", byte[].class, "Wrong type for field.");
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "primitiveArray", byte[].class, "Wrong type for field.");
        // Primitive non-array
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "primitiveAnnotated", Byte.class, "Wrong type for field.");
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "primitive", Byte.class, "Wrong type for field.");
        // Non-primitive Arrays
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "nonPrimitiveAnnotatedArray", Byte[].class, "Wrong type for field.");
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "nonPrimitiveArray", Byte[].class, "Wrong type for field.");
        // Non-primitive non-array
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "nonPrimitiveAnnotated", Byte.class, "Wrong type for field.");
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "nonPrimitive", Byte.class, "Wrong type for field.");
        // Custom class type array
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "customAnnotatedArray", TestEntity.CustomType[].class, "Wrong type for field.");
        TestUtil.assertAttributeTypeInMetaModelFor(TestEntity.class, "customArray", TestEntity.CustomType[].class, "Wrong type for field.");
    }
}

