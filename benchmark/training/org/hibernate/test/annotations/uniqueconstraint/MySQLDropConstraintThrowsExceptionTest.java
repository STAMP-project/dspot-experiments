/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.annotations.uniqueconstraint;


import AvailableSettings.CONNECTION_PROVIDER;
import AvailableSettings.HBM2DDL_AUTO;
import DialectChecks.SupportsJdbcDriverProxying;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11236")
@RequiresDialect(MySQL5Dialect.class)
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class MySQLDropConstraintThrowsExceptionTest extends BaseUnitTestCase {
    @Test
    public void testEnumTypeInterpretation() {
        final PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(false, false);
        final StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().enableAutoClose().applySetting(HBM2DDL_AUTO, "update").applySetting(CONNECTION_PROVIDER, connectionProvider).build();
        SessionFactory sessionFactory = null;
        try {
            final Metadata metadata = addAnnotatedClass(MySQLDropConstraintThrowsExceptionTest.Customer.class).buildMetadata();
            sessionFactory = metadata.buildSessionFactory();
            List<String> alterStatements = connectionProvider.getExecuteStatements().stream().filter(( sql) -> sql.toLowerCase().contains("alter ")).map(String::trim).collect(Collectors.toList());
            Assert.assertTrue(alterStatements.get(0).matches("alter table CUSTOMER\\s+drop index .*?"));
            Assert.assertTrue(alterStatements.get(1).matches("alter table CUSTOMER\\s+add constraint .*? unique \\(CUSTOMER_ID\\)"));
        } finally {
            if (sessionFactory != null) {
                sessionFactory.close();
            }
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    @Entity
    @Table(name = "CUSTOMER")
    public static class Customer {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "CUSTOMER_ACCOUNT_NUMBER")
        public Long customerAccountNumber;

        @Basic
        @Column(name = "CUSTOMER_ID", unique = true)
        public String customerId;

        @Basic
        @Column(name = "BILLING_ADDRESS")
        public String billingAddress;

        public Customer() {
        }
    }
}

