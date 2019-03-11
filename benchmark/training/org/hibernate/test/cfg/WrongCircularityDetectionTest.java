/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.cfg;


import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * This test illustrates the problem when two related (in terms of joins)
 * classes have the same table name in different schemas.
 *
 * @author Didier Villevalois
 */
@TestForIssue(jiraKey = "HHH-7134")
public class WrongCircularityDetectionTest extends BaseUnitTestCase {
    @Test
    public void testNoCircularityDetection() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final Metadata metadata = addAnnotatedClass(WrongCircularityDetectionTest.Entity2.class).buildMetadata();
            Table entity1Table = metadata.getEntityBinding(WrongCircularityDetectionTest.Entity1.class.getName()).getTable();
            Table entity2Table = metadata.getEntityBinding(WrongCircularityDetectionTest.Entity2.class.getName()).getTable();
            Assert.assertTrue(entity1Table.getName().equals(entity2Table.getName()));
            Assert.assertFalse(entity1Table.getSchema().equals(entity2Table.getSchema()));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @Inheritance(strategy = InheritanceType.JOINED)
    @javax.persistence.Table(schema = "schema1", name = "entity")
    public static class Entity1 {
        private String id;

        @Id
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Entity
    @javax.persistence.Table(schema = "schema2", name = "entity")
    public static class Entity2 extends WrongCircularityDetectionTest.Entity1 {
        private String value;

        @Basic
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}

