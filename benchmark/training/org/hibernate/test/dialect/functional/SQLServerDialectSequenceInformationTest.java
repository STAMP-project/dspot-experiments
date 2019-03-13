/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import AvailableSettings.URL;
import SequenceInformationExtractorLegacyImpl.INSTANCE;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Environment;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.SQLServer2012Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.schema.extract.spi.ExtractionContext;
import org.hibernate.tool.schema.extract.spi.SequenceInformation;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Frank Doherty
 */
@RequiresDialect({ SQLServer2012Dialect.class })
@TestForIssue(jiraKey = "HHH-13141")
public class SQLServerDialectSequenceInformationTest extends BaseUnitTestCase {
    private final String DATABASE_NAME = "hibernate_orm_test_seq";

    @Test
    public void testExtractSequenceInformationForSqlServerWithCaseSensitiveCollation() {
        String databaseNameToken = "databaseName=";
        String url = ((String) (Environment.getProperties().get(URL)));
        String[] tokens = url.split(databaseNameToken);
        String newUrl = ((tokens[0]) + databaseNameToken) + (DATABASE_NAME);
        Dialect dialect = Dialect.getDialect();
        StandardServiceRegistryBuilder ssrb = new StandardServiceRegistryBuilder();
        ssrb.applySettings(Collections.singletonMap(URL, newUrl));
        StandardServiceRegistry ssr = ssrb.build();
        try (Connection connection = ssr.getService(JdbcServices.class).getBootstrapJdbcConnectionAccess().obtainConnection()) {
            try (Statement statement = connection.createStatement()) {
                statement.execute("CREATE SEQUENCE ITEM_SEQ START WITH 100 INCREMENT BY 10");
            }
            JdbcEnvironment jdbcEnvironment = new org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentImpl(connection.getMetaData(), dialect);
            Iterable<SequenceInformation> sequenceInformations = INSTANCE.extractMetadata(new ExtractionContext.EmptyExtractionContext() {
                @Override
                public Connection getJdbcConnection() {
                    return connection;
                }

                @Override
                public JdbcEnvironment getJdbcEnvironment() {
                    return jdbcEnvironment;
                }
            });
            Assert.assertNotNull(sequenceInformations);
            SequenceInformation sequenceInformation = sequenceInformations.iterator().next();
            Assert.assertEquals("ITEM_SEQ", sequenceInformation.getSequenceName().getSequenceName().getText().toUpperCase());
            Assert.assertEquals(100, sequenceInformation.getStartValue().intValue());
            Assert.assertEquals(10, sequenceInformation.getIncrementValue().intValue());
        } catch (SQLException e) {
            log.error(e);
            Assert.fail(("Sequence information was not retrieved: " + (e.getMessage())));
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

