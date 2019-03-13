/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.id.hhh12973;


import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import org.hibernate.dialect.PostgreSQL82Dialect;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.logger.LoggerInspectionRule;
import org.hibernate.testing.logger.Triggerable;
import org.hibernate.testing.transaction.TransactionUtil;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-12973")
@RequiresDialect(PostgreSQL82Dialect.class)
public class PostgreSQLSequenceGeneratorWithSerialTest extends BaseEntityManagerFunctionalTestCase {
    @Rule
    public LoggerInspectionRule logInspection = new LoggerInspectionRule(Logger.getMessageLogger(CoreMessageLogger.class, SequenceStyleGenerator.class.getName()));

    private Triggerable triggerable = logInspection.watchForLogMessages("HHH000497:");

    private static final String DROP_SEQUENCE = "DROP SEQUENCE IF EXISTS application_configurations_id_seq";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS application_configurations CASCADE";

    private static final String CREATE_TABLE = "CREATE TABLE application_configurations (id BIGSERIAL NOT NULL PRIMARY KEY)";

    @Test
    public void test() {
        final AtomicLong id = new AtomicLong();
        final int ITERATIONS = 51;
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            for (int i = 1; i <= ITERATIONS; i++) {
                org.hibernate.id.hhh12973.ApplicationConfiguration model = new org.hibernate.id.hhh12973.ApplicationConfiguration();
                entityManager.persist(model);
                id.set(model.getId());
            }
        });
        Assert.assertEquals(ITERATIONS, id.get());
    }

    @Entity
    @Table(name = "application_configurations")
    public static class ApplicationConfiguration {
        @Id
        @SequenceGenerator(name = "application_configurations_id_seq", sequenceName = "application_configurations_id_seq")
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_configurations_id_seq")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(final Long id) {
            this.id = id;
        }
    }
}

