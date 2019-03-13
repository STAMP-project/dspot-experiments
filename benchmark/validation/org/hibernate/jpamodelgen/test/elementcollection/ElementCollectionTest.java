/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpamodelgen.test.elementcollection;


import org.hibernate.jpamodelgen.test.util.CompilationTest;
import org.hibernate.jpamodelgen.test.util.TestForIssue;
import org.hibernate.jpamodelgen.test.util.TestUtil;
import org.hibernate.jpamodelgen.test.util.WithClasses;
import org.hibernate.jpamodelgen.test.util.WithMappingFiles;
import org.junit.Test;


/**
 *
 *
 * @author Hardy Ferentschik
 */
public class ElementCollectionTest extends CompilationTest {
    @Test
    @TestForIssue(jiraKey = "METAGEN-8")
    @WithClasses({ House.class, Room.class })
    public void testElementCollectionOnMap() {
        TestUtil.assertMetamodelClassGeneratedFor(House.class);
        TestUtil.assertMetamodelClassGeneratedFor(Room.class);
        // side effect of METAGEN-8 was that a meta class for String was created!
        TestUtil.assertNoSourceFileGeneratedFor(String.class);
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-19")
    @WithClasses({ Hotel.class, Room.class, Cleaner.class })
    public void testMapKeyClass() {
        TestUtil.assertMetamodelClassGeneratedFor(Hotel.class);
        TestUtil.assertMapAttributesInMetaModelFor(Hotel.class, "roomsByName", String.class, Room.class, "Wrong type in map attribute.");
        TestUtil.assertMapAttributesInMetaModelFor(Hotel.class, "cleaners", Room.class, Cleaner.class, "Wrong type in map attribute.");
    }

    @Test
    @TestForIssue(jiraKey = "METAGEN-22")
    @WithClasses({ Hostel.class, Room.class, Cleaner.class })
    @WithMappingFiles("hostel.xml")
    public void testMapKeyClassXmlConfigured() {
        TestUtil.assertMetamodelClassGeneratedFor(Hostel.class);
        TestUtil.assertMapAttributesInMetaModelFor(Hostel.class, "roomsByName", String.class, Room.class, "Wrong type in map attribute.");
        TestUtil.assertMapAttributesInMetaModelFor(Hostel.class, "cleaners", Room.class, Cleaner.class, "Wrong type in map attribute.");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11004")
    @WithClasses({ OfficeBuilding.class })
    public void testArrayValueElementCollection() {
        TestUtil.assertMetamodelClassGeneratedFor(OfficeBuilding.class);
        TestUtil.assertMapAttributesInMetaModelFor(OfficeBuilding.class, "doorCodes", Integer.class, byte[].class, "Wrong type in map attribute.");
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11871")
    @WithClasses({ Homework.class })
    public void testListAttributeWithGenericTypeForJavaBeanGetter() {
        TestUtil.assertMetamodelClassGeneratedFor(Homework.class);
        TestUtil.assertListAttributeTypeInMetaModelFor(Homework.class, "paths", String.class, "ListAttribute generic type should be String");
    }
}

