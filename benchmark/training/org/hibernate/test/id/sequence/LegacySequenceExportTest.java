/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.id.sequence;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.Sequence;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class LegacySequenceExportTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-9936")
    public void testMultipleUsesOfDefaultSequenceName() {
        final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(LegacySequenceExportTest.Entity2.class).buildMetadata()));
        metadata.validate();
        Assert.assertEquals(0, metadata.getDatabase().getAuxiliaryDatabaseObjects().size());
        int count = 0;
        for (Namespace namespace : metadata.getDatabase().getNamespaces()) {
            for (Sequence sequence : namespace.getSequences()) {
                count++;
            }
        }
        Assert.assertEquals(1, count);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9936")
    public void testMultipleUsesOfExplicitSequenceName() {
        final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(LegacySequenceExportTest.Entity4.class).buildMetadata()));
        metadata.validate();
        Assert.assertEquals(0, metadata.getDatabase().getAuxiliaryDatabaseObjects().size());
        int count = 0;
        for (Namespace namespace : metadata.getDatabase().getNamespaces()) {
            for (Sequence sequence : namespace.getSequences()) {
                count++;
            }
        }
        Assert.assertEquals(1, count);
    }

    @Entity(name = "Entity1")
    @Table(name = "Entity1")
    public static class Entity1 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        public Integer id;
    }

    @Entity(name = "Entity2")
    @Table(name = "Entity2")
    public static class Entity2 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        public Integer id;
    }

    @Entity(name = "Entity3")
    @Table(name = "Entity3")
    public static class Entity3 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "my_sequence")
        public Integer id;
    }

    @Entity(name = "Entity4")
    @Table(name = "Entity4")
    public static class Entity4 {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE)
        @SequenceGenerator(name = "my_sequence")
        public Integer id;
    }
}

