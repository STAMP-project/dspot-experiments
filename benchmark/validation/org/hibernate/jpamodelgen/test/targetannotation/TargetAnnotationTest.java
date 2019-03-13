/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.targetannotation;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class TargetAnnotationTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-30")
    @WithClasses({ Address.class, AddressImpl.class, House.class })
    public void testEmbeddableWithTargetAnnotation() {
        TestUtil.assertMetamodelClassGeneratedFor(House.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(House.class, "address", "the metamodel should have a member 'address'");
        TestUtil.assertAttributeTypeInMetaModelFor(House.class, "address", AddressImpl.class, "The target annotation set the type to AddressImpl");
    }
}

