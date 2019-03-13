/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.metamodel;


import java.util.Arrays;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


public class MetamodelTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-12906")
    public void testGetAllCollectionRoles() {
        String[] collectionRoles = entityManagerFactory().getMetamodel().getAllCollectionRoles();
        Arrays.sort(collectionRoles);
        Assert.assertArrayEquals(collectionRoles, new String[]{ (MetamodelTest.EntityWithCollection.class.getName()) + ".collection", (MetamodelTest.EntityWithCollection2.class.getName()) + ".collection2" });
    }

    @Test
    public void testGetCollectionRolesByEntityParticipant() {
        Set<String> collectionRolesByEntityParticipant = entityManagerFactory().getMetamodel().getCollectionRolesByEntityParticipant(MetamodelTest.ElementOfCollection.class.getName());
        Assert.assertEquals(1, collectionRolesByEntityParticipant.size());
        Assert.assertEquals(((MetamodelTest.EntityWithCollection.class.getName()) + ".collection"), collectionRolesByEntityParticipant.iterator().next());
    }

    @Test
    public void testEntityNames() {
        String[] entityNames = entityManagerFactory().getMetamodel().getAllEntityNames();
        Arrays.sort(entityNames);
        Assert.assertArrayEquals(entityNames, new String[]{ MetamodelTest.ElementOfCollection.class.getName(), MetamodelTest.ElementOfCollection2.class.getName(), MetamodelTest.EntityWithCollection.class.getName(), MetamodelTest.EntityWithCollection2.class.getName() });
    }

    @Entity(name = "EntityWithCollection")
    public static class EntityWithCollection {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToMany
        private Set<MetamodelTest.ElementOfCollection> collection;
    }

    @Entity(name = "ElementOfCollection")
    public static class ElementOfCollection {
        @Id
        @GeneratedValue
        private Long id;
    }

    @Entity(name = "EntityWithCollection2")
    public static class EntityWithCollection2 {
        @Id
        @GeneratedValue
        private Long id;

        @ManyToMany
        private Set<MetamodelTest.ElementOfCollection2> collection2;
    }

    @Entity(name = "ElementOfCollection2")
    public static class ElementOfCollection2 {
        @Id
        @GeneratedValue
        private Long id;
    }
}

