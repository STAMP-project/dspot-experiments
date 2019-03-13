/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.timestamp;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class JdbcTimestampDefaultTimeZoneTest extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(true, false);

    @Test
    public void testTimeZone() {
        connectionProvider.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.timestamp.Person person = new org.hibernate.test.timestamp.Person();
            person.id = 1L;
            s.persist(person);
        });
        Assert.assertEquals(1, connectionProvider.getPreparedStatements().size());
        PreparedStatement ps = connectionProvider.getPreparedStatements().get(0);
        try {
            Mockito.verify(ps, Mockito.times(1)).setTimestamp(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Timestamp.class));
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.timestamp.Person person = s.find(.class, 1L);
            assertEquals(0, person.createdOn.getTime());
        });
    }

    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private Timestamp createdOn = new Timestamp(0);
    }
}

