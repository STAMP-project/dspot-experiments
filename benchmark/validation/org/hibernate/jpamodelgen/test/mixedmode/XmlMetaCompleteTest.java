/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.mixedmode;


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
public class XmlMetaCompleteTest extends CompilationTest {
    @Test
    @WithClasses(Person.class)
    @WithMappingFiles("orm.xml")
    public void testXmlConfiguredEmbeddedClassGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(Person.class);
        TestUtil.assertAbsenceOfFieldInMetamodelFor(Person.class, "name");
    }
}

