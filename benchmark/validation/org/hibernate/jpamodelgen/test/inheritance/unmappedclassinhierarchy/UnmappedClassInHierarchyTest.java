/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.inheritance.unmappedclassinhierarchy;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Emmanuel Bernard
 * @author Hardy Ferentschik
 */
public class UnmappedClassInHierarchyTest extends CompilationTest {
    @Test
    @WithClasses({ BaseEntity.class, MappedBase.class, NormalExtendsEntity.class, NormalExtendsMapped.class, SubA.class, SubB.class })
    public void testUnmappedClassInHierarchy() throws Exception {
        TestUtil.assertSuperClassRelationShipInMetamodel(SubA.class, BaseEntity.class);
        TestUtil.assertSuperClassRelationShipInMetamodel(SubB.class, MappedBase.class);
    }
}

