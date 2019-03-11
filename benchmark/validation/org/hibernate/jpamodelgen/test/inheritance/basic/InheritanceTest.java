/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.inheritance.basic;


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
public class InheritanceTest extends CompilationTest {
    @Test
    @WithClasses({ AbstractEntity.class, Area.class, Building.class, Customer.class, House.class, Person.class, User.class })
    public void testInheritance() throws Exception {
        // entity inheritance
        TestUtil.assertSuperClassRelationShipInMetamodel(Customer.class, User.class);
        // mapped super class
        TestUtil.assertSuperClassRelationShipInMetamodel(House.class, Building.class);
        TestUtil.assertSuperClassRelationShipInMetamodel(Building.class, Area.class);
        // METAGEN-29
        TestUtil.assertSuperClassRelationShipInMetamodel(Person.class, AbstractEntity.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(AbstractEntity.class, "id", "Property 'id' should exist");
        TestUtil.assertPresenceOfFieldInMetamodelFor(AbstractEntity.class, "foo", "Property should exist - METAGEN-29");
        TestUtil.assertAttributeTypeInMetaModelFor(AbstractEntity.class, "foo", Object.class, "Object is the upper bound of foo ");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Person.class, "name", "Property 'name' should exist");
    }
}

