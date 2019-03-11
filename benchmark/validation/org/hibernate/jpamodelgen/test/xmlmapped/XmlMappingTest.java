/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.xmlmapped;


import org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor;
import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithProcessorOption;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
// TODO - differentiate needed classes per test better. Right now all test classes are processed for each test (HF)
@WithClasses({ Address.class, Boy.class, Building.class, FakeHero.class, LivingBeing.class, Mammal.class, Superhero.class })
@WithProcessorOption(key = JPAMetaModelEntityProcessor.PERSISTENCE_XML_OPTION, value = "org/hibernate/jpamodelgen/test/xmlmapped/persistence.xml")
public class XmlMappingTest extends CompilationTest {
    @Test
    public void testXmlConfiguredEmbeddedClassGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(Address.class);
    }

    @Test
    public void testXmlConfiguredMappedSuperclassGenerated() {
        TestUtil.assertMetamodelClassGeneratedFor(Building.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Building.class, "address", "address field should exist");
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-17")
    public void testTargetEntityOnOneToOne() {
        TestUtil.assertMetamodelClassGeneratedFor(Boy.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Boy.class, "favoriteSuperhero", "favoriteSuperhero field should exist");
        TestUtil.assertAttributeTypeInMetaModelFor(Boy.class, "favoriteSuperhero", FakeHero.class, "target entity overridden in xml");
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-17")
    public void testTargetEntityOnOneToMany() {
        TestUtil.assertMetamodelClassGeneratedFor(Boy.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Boy.class, "knowsHeroes", "knowsHeroes field should exist");
        TestUtil.assertAttributeTypeInMetaModelFor(Boy.class, "knowsHeroes", FakeHero.class, "target entity overridden in xml");
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-17")
    public void testTargetEntityOnManyToMany() {
        TestUtil.assertMetamodelClassGeneratedFor(Boy.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Boy.class, "savedBy", "savedBy field should exist");
        TestUtil.assertAttributeTypeInMetaModelFor(Boy.class, "savedBy", FakeHero.class, "target entity overridden in xml");
    }

    @Test
    public void testXmlConfiguredElementCollection() {
        TestUtil.assertMetamodelClassGeneratedFor(Boy.class);
        TestUtil.assertPresenceOfFieldInMetamodelFor(Boy.class, "nickNames", "nickNames field should exist");
        TestUtil.assertAttributeTypeInMetaModelFor(Boy.class, "nickNames", String.class, "target class overridden in xml");
    }

    @Test
    public void testClassHierarchy() {
        TestUtil.assertMetamodelClassGeneratedFor(Mammal.class);
        TestUtil.assertMetamodelClassGeneratedFor(LivingBeing.class);
        TestUtil.assertSuperClassRelationShipInMetamodel(Mammal.class, LivingBeing.class);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testNonExistentMappedClassesGetIgnored() throws Exception {
        Class.forName("org.hibernate.jpamodelgen.test.model.Dummy_");
    }
}

