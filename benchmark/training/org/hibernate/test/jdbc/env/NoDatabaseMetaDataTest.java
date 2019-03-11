/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.jdbc.env;


import AvailableSettings.DIALECT;
import java.sql.DatabaseMetaData;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.ExtractedDatabaseMetaData;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Gail Badner
 */
public class NoDatabaseMetaDataTest extends BaseUnitTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10515")
    public void testNoJdbcMetadataDefaultDialect() {
        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting("hibernate.temp.use_jdbc_metadata_defaults", "false").build();
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        ExtractedDatabaseMetaData extractedDatabaseMetaData = jdbcEnvironment.getExtractedDatabaseMetaData();
        Assert.assertNull(extractedDatabaseMetaData.getConnectionCatalogName());
        Assert.assertNull(extractedDatabaseMetaData.getConnectionSchemaName());
        Assert.assertTrue(extractedDatabaseMetaData.getTypeInfoSet().isEmpty());
        Assert.assertTrue(extractedDatabaseMetaData.getExtraKeywords().isEmpty());
        Assert.assertFalse(extractedDatabaseMetaData.supportsNamedParameters());
        Assert.assertFalse(extractedDatabaseMetaData.supportsRefCursors());
        Assert.assertFalse(extractedDatabaseMetaData.supportsScrollableResults());
        Assert.assertFalse(extractedDatabaseMetaData.supportsGetGeneratedKeys());
        Assert.assertFalse(extractedDatabaseMetaData.supportsBatchUpdates());
        Assert.assertFalse(extractedDatabaseMetaData.supportsDataDefinitionInTransaction());
        Assert.assertFalse(extractedDatabaseMetaData.doesDataDefinitionCauseTransactionCommit());
        Assert.assertNull(extractedDatabaseMetaData.getSqlStateType());
        Assert.assertFalse(extractedDatabaseMetaData.doesLobLocatorUpdateCopy());
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10515")
    public void testNoJdbcMetadataDialectOverride() {
        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySetting("hibernate.temp.use_jdbc_metadata_defaults", "false").applySetting(DIALECT, NoDatabaseMetaDataTest.TestDialect.class.getName()).build();
        JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
        ExtractedDatabaseMetaData extractedDatabaseMetaData = jdbcEnvironment.getExtractedDatabaseMetaData();
        Assert.assertNull(extractedDatabaseMetaData.getConnectionCatalogName());
        Assert.assertNull(extractedDatabaseMetaData.getConnectionSchemaName());
        Assert.assertTrue(extractedDatabaseMetaData.getTypeInfoSet().isEmpty());
        Assert.assertTrue(extractedDatabaseMetaData.getExtraKeywords().isEmpty());
        Assert.assertTrue(extractedDatabaseMetaData.supportsNamedParameters());
        Assert.assertFalse(extractedDatabaseMetaData.supportsRefCursors());
        Assert.assertFalse(extractedDatabaseMetaData.supportsScrollableResults());
        Assert.assertFalse(extractedDatabaseMetaData.supportsGetGeneratedKeys());
        Assert.assertFalse(extractedDatabaseMetaData.supportsBatchUpdates());
        Assert.assertFalse(extractedDatabaseMetaData.supportsDataDefinitionInTransaction());
        Assert.assertFalse(extractedDatabaseMetaData.doesDataDefinitionCauseTransactionCommit());
        Assert.assertNull(extractedDatabaseMetaData.getSqlStateType());
        Assert.assertFalse(extractedDatabaseMetaData.doesLobLocatorUpdateCopy());
        StandardServiceRegistryBuilder.destroy(serviceRegistry);
    }

    public static class TestDialect extends Dialect {
        @Override
        public boolean supportsNamedParameters(DatabaseMetaData databaseMetaData) {
            return true;
        }
    }
}

