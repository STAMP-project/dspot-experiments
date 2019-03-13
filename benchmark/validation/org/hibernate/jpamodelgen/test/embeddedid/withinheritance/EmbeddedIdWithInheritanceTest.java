/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.embeddedid.withinheritance;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithMappingFiles;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class EmbeddedIdWithInheritanceTest extends CompilationTest {
    @Test
    @WithClasses({ Ref.class, AbstractRef.class, TestEntity.class })
    @WithMappingFiles("orm.xml")
    public void testEntityContainsEmbeddedIdProperty() {
        TestUtil.assertMetamodelClassGeneratedFor(TestEntity.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(TestEntity.class, "ref", "Property ref should be in metamodel");
    }
}

