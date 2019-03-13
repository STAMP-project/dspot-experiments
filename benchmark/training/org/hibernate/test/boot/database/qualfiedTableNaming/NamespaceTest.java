/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.boot.database.qualfiedTableNaming;


import Namespace.Name;
import org.hamcrest.CoreMatchers;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11625")
public class NamespaceTest {
    private static final String EXPECTED_CATALOG_PHYSICAL_NAME = "catalog";

    private static final String EXPECTED_SCHEMA_PHYSICAL_NAME = "schema";

    private final Database mockDatabase = Mockito.mock(Database.class);

    private Name name;

    @Test
    public void testPhysicalNameSchemaAndCatalog() {
        Namespace namespace = new Namespace(mockDatabase.getPhysicalNamingStrategy(), mockDatabase.getJdbcEnvironment(), name);
        final Namespace.Name physicalName = namespace.getPhysicalName();
        Assert.assertThat(physicalName.getSchema().getText(), CoreMatchers.is(NamespaceTest.EXPECTED_SCHEMA_PHYSICAL_NAME));
        Assert.assertThat(physicalName.getCatalog().getText(), CoreMatchers.is(NamespaceTest.EXPECTED_CATALOG_PHYSICAL_NAME));
    }

    public static class TestNamingStrategy implements PhysicalNamingStrategy {
        @Override
        public Identifier toPhysicalCatalogName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return new Identifier(NamespaceTest.EXPECTED_CATALOG_PHYSICAL_NAME, false);
        }

        @Override
        public Identifier toPhysicalSchemaName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return new Identifier(NamespaceTest.EXPECTED_SCHEMA_PHYSICAL_NAME, false);
        }

        @Override
        public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return name;
        }

        @Override
        public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return null;
        }

        @Override
        public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment jdbcEnvironment) {
            return name;
        }
    }
}

