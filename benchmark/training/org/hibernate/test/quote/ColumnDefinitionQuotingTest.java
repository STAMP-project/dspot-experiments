/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.quote;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class ColumnDefinitionQuotingTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9491")
    public void testExplicitQuoting() {
        withStandardServiceRegistry(false, false, new ColumnDefinitionQuotingTest.TestWork() {
            @Override
            public void doTestWork(StandardServiceRegistry ssr) {
                MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(ColumnDefinitionQuotingTest.E1.class).buildMetadata()));
                metadata.validate();
                PersistentClass entityBinding = metadata.getEntityBinding(ColumnDefinitionQuotingTest.E1.class.getName());
                Column idColumn = extractColumn(entityBinding.getIdentifier().getColumnIterator());
                Assert.assertTrue(isQuoted(idColumn.getSqlType(), ssr));
                Column otherColumn = extractColumn(entityBinding.getProperty("other").getColumnIterator());
                Assert.assertTrue(isQuoted(otherColumn.getSqlType(), ssr));
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9491")
    public void testExplicitQuotingSkippingColumnDef() {
        withStandardServiceRegistry(false, true, new ColumnDefinitionQuotingTest.TestWork() {
            @Override
            public void doTestWork(StandardServiceRegistry ssr) {
                MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(ColumnDefinitionQuotingTest.E1.class).buildMetadata()));
                metadata.validate();
                PersistentClass entityBinding = metadata.getEntityBinding(ColumnDefinitionQuotingTest.E1.class.getName());
                Column idColumn = extractColumn(entityBinding.getIdentifier().getColumnIterator());
                Assert.assertTrue(isQuoted(idColumn.getSqlType(), ssr));
                Column otherColumn = extractColumn(entityBinding.getProperty("other").getColumnIterator());
                Assert.assertTrue(isQuoted(otherColumn.getSqlType(), ssr));
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9491")
    public void testGlobalQuotingNotSkippingColumnDef() {
        withStandardServiceRegistry(true, false, new ColumnDefinitionQuotingTest.TestWork() {
            @Override
            public void doTestWork(StandardServiceRegistry ssr) {
                MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(ColumnDefinitionQuotingTest.E2.class).buildMetadata()));
                metadata.validate();
                PersistentClass entityBinding = metadata.getEntityBinding(ColumnDefinitionQuotingTest.E2.class.getName());
                Column idColumn = extractColumn(entityBinding.getIdentifier().getColumnIterator());
                Assert.assertTrue(isQuoted(idColumn.getSqlType(), ssr));
                Column otherColumn = extractColumn(entityBinding.getProperty("other").getColumnIterator());
                Assert.assertTrue(isQuoted(otherColumn.getSqlType(), ssr));
            }
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9491")
    public void testGlobalQuotingSkippingColumnDef() {
        withStandardServiceRegistry(true, true, new ColumnDefinitionQuotingTest.TestWork() {
            @Override
            public void doTestWork(StandardServiceRegistry ssr) {
                MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(ColumnDefinitionQuotingTest.E2.class).buildMetadata()));
                metadata.validate();
                PersistentClass entityBinding = metadata.getEntityBinding(ColumnDefinitionQuotingTest.E2.class.getName());
                Column idColumn = extractColumn(entityBinding.getIdentifier().getColumnIterator());
                Assert.assertTrue((!(isQuoted(idColumn.getSqlType(), ssr))));
                Column otherColumn = extractColumn(entityBinding.getProperty("other").getColumnIterator());
                Assert.assertTrue((!(isQuoted(otherColumn.getSqlType(), ssr))));
            }
        });
    }

    interface TestWork {
        void doTestWork(StandardServiceRegistry ssr);
    }

    @Entity
    public static class E1 {
        @Id
        @javax.persistence.Column(columnDefinition = "`explicitly quoted`")
        private Integer id;

        @ManyToOne
        @JoinColumn(columnDefinition = "`explicitly quoted`")
        private ColumnDefinitionQuotingTest.E1 other;
    }

    @Entity
    public static class E2 {
        @Id
        @javax.persistence.Column(columnDefinition = "not explicitly quoted")
        private Integer id;

        @ManyToOne
        @JoinColumn(columnDefinition = "not explicitly quoted")
        private ColumnDefinitionQuotingTest.E2 other;
    }
}

