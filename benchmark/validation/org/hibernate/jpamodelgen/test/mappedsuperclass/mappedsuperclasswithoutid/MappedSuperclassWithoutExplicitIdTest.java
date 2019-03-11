/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.mappedsuperclass.mappedsuperclasswithoutid;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class MappedSuperclassWithoutExplicitIdTest extends CompilationTest {
    @Test
    @WithClasses({ ConcreteProduct.class, Product.class, Shop.class })
    public void testRightAccessTypeForMappedSuperclass() {
        TestUtil.assertMetamodelClassGeneratedFor(ConcreteProduct.class);
        TestUtil.assertMetamodelClassGeneratedFor(Product.class);
        TestUtil.assertMetamodelClassGeneratedFor(Shop.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Product.class, "shop", "The many to one attribute shop is missing");
    }
}

