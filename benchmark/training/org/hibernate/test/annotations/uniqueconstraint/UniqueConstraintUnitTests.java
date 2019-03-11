/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.uniqueconstraint;


import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import org.hibernate.AnnotationException;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.mapping.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class UniqueConstraintUnitTests extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-8026")
    public void testUnNamedConstraints() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final Metadata metadata = addAnnotatedClass(UniqueConstraintUnitTests.UniqueNoNameB.class).buildMetadata();
            Table tableA = null;
            Table tableB = null;
            for (Table table : metadata.collectTableMappings()) {
                if (table.getName().equals("UniqueNoNameA")) {
                    tableA = table;
                } else
                    if (table.getName().equals("UniqueNoNameB")) {
                        tableB = table;
                    }

            }
            Assert.assertTrue("Could not find the expected tables.", ((tableA != null) && (tableB != null)));
            Assert.assertFalse(tableA.getUniqueKeyIterator().next().getName().equals(tableB.getUniqueKeyIterator().next().getName()));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Test
    @TestForIssue(jiraKey = "HHH-8537")
    public void testNonExistentColumn() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final Metadata metadata = addAnnotatedClass(UniqueConstraintUnitTests.UniqueNoNameB.class).buildMetadata();
        } catch (NullPointerException e) {
            Assert.fail("The @UniqueConstraint with a non-existent column name should have resulted in an AnnotationException");
        } catch (AnnotationException e) {
            // expected
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @javax.persistence.Table(name = "UniqueNoNameA", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
    public static class UniqueNoNameA {
        @Id
        @GeneratedValue
        public long id;

        public String name;
    }

    @Entity
    @javax.persistence.Table(name = "UniqueNoNameB", uniqueConstraints = { @UniqueConstraint(columnNames = { "name" }) })
    public static class UniqueNoNameB {
        @Id
        @GeneratedValue
        public long id;

        public String name;
    }

    @Entity
    public static class UniqueColumnDoesNotExist {
        @Id
        public Integer id;

        @ElementCollection(fetch = FetchType.EAGER)
        @CollectionTable(name = "tbl_strings", joinColumns = @JoinColumn(name = "fk", nullable = false), uniqueConstraints = @UniqueConstraint(columnNames = { "fk", "doesnotexist" }))
        @Column(name = "string", nullable = false)
        public Set<String> strings = new HashSet<String>();
    }
}

