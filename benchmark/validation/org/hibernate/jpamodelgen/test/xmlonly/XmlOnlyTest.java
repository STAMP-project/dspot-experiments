/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.xmlonly;


import org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor;
import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithProcessorOption;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
@WithClasses({ Car.class, Course.class, Option.class, Period.class, Teacher.class, Tire.class, XmlOnly.class })
@WithProcessorOption(key = JPAMetaModelEntityProcessor.PERSISTENCE_XML_OPTION, value = "org/hibernate/jpamodelgen/test/xmlonly/persistence.xml")
public class XmlOnlyTest extends CompilationTest {
    @Test
    public void testMetaModelGeneratedForXmlConfiguredEntity() {
        TestUtil.assertMetamodelClassGeneratedFor(XmlOnly.class);
    }

    @Test
    public void testMetaModelGeneratedForManyToManyFieldAccessWithoutTargetEntity() {
        TestUtil.assertPresenceOfFieldInMetamodelFor(Course.class, "qualifiedTeachers", "Type should be inferred from field");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Teacher.class, "qualifiedFor", "Type should be inferred from field");
    }

    @Test
    public void testMetaModelGeneratedForOneToManyPropertyAccessWithoutTargetEntity() {
        TestUtil.assertPresenceOfFieldInMetamodelFor(Car.class, "tires", "Type should be inferred from field");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Tire.class, "car", "Type should be inferred from field");
    }

    @Test
    public void testMetaModelGeneratedForEmbeddable() {
        TestUtil.assertPresenceOfFieldInMetamodelFor(Option.class, "period", "Embedded expected");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Period.class, "start", "Embedded expected");
        TestUtil.assertPresenceOfFieldInMetamodelFor(Period.class, "end", "Embedded expected");
    }
}

