/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hbm.uk;


import org.hamcrest.CoreMatchers;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.dialect.unique.DefaultUniqueDelegate;
import org.hibernate.dialect.unique.UniqueDelegate;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.test.hbm.index.JournalingSchemaToolingTarget;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class UniqueDelegateTest extends BaseUnitTestCase {
    private static int getColumnDefinitionUniquenessFragmentCallCount = 0;

    private static int getTableCreationUniqueConstraintsFragmentCallCount = 0;

    private static int getAlterTableToAddUniqueKeyCommandCallCount = 0;

    private static int getAlterTableToDropUniqueKeyCommandCallCount = 0;

    private StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-10203")
    public void testUniqueDelegateConsulted() {
        final Metadata metadata = addResource("org/hibernate/test/hbm/uk/person_unique.hbm.xml").buildMetadata();
        final JournalingSchemaToolingTarget target = new JournalingSchemaToolingTarget();
        new org.hibernate.tool.schema.internal.SchemaCreatorImpl(ssr).doCreation(metadata, false, target);
        Assert.assertThat(UniqueDelegateTest.getAlterTableToAddUniqueKeyCommandCallCount, CoreMatchers.equalTo(1));
        Assert.assertThat(UniqueDelegateTest.getColumnDefinitionUniquenessFragmentCallCount, CoreMatchers.equalTo(1));
        Assert.assertThat(UniqueDelegateTest.getTableCreationUniqueConstraintsFragmentCallCount, CoreMatchers.equalTo(1));
        new org.hibernate.tool.schema.internal.SchemaDropperImpl(ssr).doDrop(metadata, false, target);
        // unique keys are not dropped explicitly
        Assert.assertThat(UniqueDelegateTest.getAlterTableToAddUniqueKeyCommandCallCount, CoreMatchers.equalTo(1));
        Assert.assertThat(UniqueDelegateTest.getColumnDefinitionUniquenessFragmentCallCount, CoreMatchers.equalTo(1));
        Assert.assertThat(UniqueDelegateTest.getTableCreationUniqueConstraintsFragmentCallCount, CoreMatchers.equalTo(1));
    }

    public static class MyDialect extends H2Dialect {
        private UniqueDelegateTest.MyUniqueDelegate myUniqueDelegate;

        public MyDialect() {
            this.myUniqueDelegate = new UniqueDelegateTest.MyUniqueDelegate(this);
        }

        @Override
        public UniqueDelegate getUniqueDelegate() {
            return myUniqueDelegate;
        }
    }

    public static class MyUniqueDelegate extends DefaultUniqueDelegate {
        /**
         * Constructs DefaultUniqueDelegate
         *
         * @param dialect
         * 		The dialect for which we are handling unique constraints
         */
        public MyUniqueDelegate(Dialect dialect) {
            super(dialect);
        }

        @Override
        public String getColumnDefinitionUniquenessFragment(Column column) {
            (UniqueDelegateTest.getColumnDefinitionUniquenessFragmentCallCount)++;
            return super.getColumnDefinitionUniquenessFragment(column);
        }

        @Override
        public String getTableCreationUniqueConstraintsFragment(Table table) {
            (UniqueDelegateTest.getTableCreationUniqueConstraintsFragmentCallCount)++;
            return super.getTableCreationUniqueConstraintsFragment(table);
        }

        @Override
        public String getAlterTableToAddUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
            (UniqueDelegateTest.getAlterTableToAddUniqueKeyCommandCallCount)++;
            return super.getAlterTableToAddUniqueKeyCommand(uniqueKey, metadata);
        }

        @Override
        public String getAlterTableToDropUniqueKeyCommand(UniqueKey uniqueKey, Metadata metadata) {
            (UniqueDelegateTest.getAlterTableToDropUniqueKeyCommandCallCount)++;
            return super.getAlterTableToDropUniqueKeyCommand(uniqueKey, metadata);
        }
    }
}

