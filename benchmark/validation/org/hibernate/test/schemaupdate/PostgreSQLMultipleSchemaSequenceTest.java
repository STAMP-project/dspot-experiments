/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import AvailableSettings.URL;
import TargetType.DATABASE;
import TargetType.SCRIPT;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.test.util.DdlTransactionIsolatorTestingImpl;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(PostgreSQL81Dialect.class)
public class PostgreSQLMultipleSchemaSequenceTest extends BaseUnitTestCase {
    private File output;

    @Test
    @TestForIssue(jiraKey = "HHH-5538")
    public void test() {
        StandardServiceRegistry ssr1 = new StandardServiceRegistryBuilder().build();
        final String extraSchemaName = "extra_schema_sequence_validation";
        try {
            final MetadataImplementor metadata1 = ((MetadataImplementor) (addAnnotatedClass(PostgreSQLMultipleSchemaSequenceTest.Box.class).buildMetadata()));
            try {
                new SchemaExport().setOutputFile(output.getAbsolutePath()).create(EnumSet.of(DATABASE, SCRIPT), metadata1);
                final ConnectionProvider connectionProvider1 = ssr1.getService(ConnectionProvider.class);
                DdlTransactionIsolatorTestingImpl ddlTransactionIsolator1 = new DdlTransactionIsolatorTestingImpl(ssr1, new org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess(connectionProvider1));
                try (Statement statement = getIsolatedConnection().createStatement()) {
                    statement.execute(String.format("DROP SCHEMA IF EXISTS %s CASCADE", extraSchemaName));
                    statement.execute(String.format("CREATE SCHEMA %s", extraSchemaName));
                    try (ResultSet resultSet = statement.executeQuery("SELECT NEXTVAL('SEQ_TEST')")) {
                        while (resultSet.next()) {
                            Long sequenceValue = resultSet.getLong(1);
                            Assert.assertEquals(Long.valueOf(1L), sequenceValue);
                        } 
                    }
                } catch (SQLException e) {
                    Assert.fail(e.getMessage());
                }
                StandardServiceRegistry ssr2 = new StandardServiceRegistryBuilder().applySetting(URL, (((Environment.getProperties().get(URL)) + "?currentSchema=") + extraSchemaName)).build();
                try {
                    final MetadataImplementor metadata2 = ((MetadataImplementor) (addAnnotatedClass(PostgreSQLMultipleSchemaSequenceTest.Box.class).buildMetadata()));
                    try {
                        new SchemaExport().setOutputFile(output.getAbsolutePath()).create(EnumSet.of(DATABASE, SCRIPT), metadata2);
                    } finally {
                        final ConnectionProvider connectionProvider2 = ssr2.getService(ConnectionProvider.class);
                        DdlTransactionIsolatorTestingImpl ddlTransactionIsolator2 = new DdlTransactionIsolatorTestingImpl(ssr2, new org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess(connectionProvider2));
                        try (Statement statement = getIsolatedConnection().createStatement()) {
                            try (ResultSet resultSet = statement.executeQuery("SELECT NEXTVAL('SEQ_TEST')")) {
                                while (resultSet.next()) {
                                    Long sequenceValue = resultSet.getLong(1);
                                    Assert.assertEquals(Long.valueOf(1L), sequenceValue);
                                } 
                            }
                            statement.execute(String.format("DROP SCHEMA IF EXISTS %s CASCADE", extraSchemaName));
                        } catch (SQLException e) {
                            Assert.fail(e.getMessage());
                        }
                        new SchemaExport().drop(EnumSet.of(DATABASE), metadata2);
                    }
                } finally {
                    StandardServiceRegistryBuilder.destroy(ssr2);
                }
            } finally {
                // clean up
                new SchemaExport().drop(EnumSet.of(DATABASE), metadata1);
            }
            final List<String> sqlLines = Files.readAllLines(output.toPath(), Charset.defaultCharset());
            Assert.assertEquals(2, sqlLines.stream().filter(( s) -> s.equalsIgnoreCase("create sequence SEQ_TEST start 1 increment 1")).count());
        } catch (IOException e) {
            Assert.fail(e.getMessage());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr1);
        }
    }

    @Entity(name = "Box")
    @Table(name = "Box")
    public static class Box {
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEST")
        @SequenceGenerator(name = "TEST", sequenceName = "SEQ_TEST", allocationSize = 1)
        public Integer id;
    }
}

