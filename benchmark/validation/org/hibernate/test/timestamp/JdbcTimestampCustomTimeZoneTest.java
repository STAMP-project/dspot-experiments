/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.timestamp;


import DialectChecks.SupportsJdbcDriverProxying;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.TimeZone;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.MySQL5Dialect;
import org.hibernate.test.util.jdbc.PreparedStatementSpyConnectionProvider;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@SkipForDialect(MySQL5Dialect.class)
@RequiresDialectFeature(SupportsJdbcDriverProxying.class)
public class JdbcTimestampCustomTimeZoneTest extends BaseNonConfigCoreFunctionalTestCase {
    private PreparedStatementSpyConnectionProvider connectionProvider = new PreparedStatementSpyConnectionProvider(true, false);

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone("America/Los_Angeles");

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
            ArgumentCaptor<Calendar> calendarArgumentCaptor = ArgumentCaptor.forClass(Calendar.class);
            Mockito.verify(ps, Mockito.times(1)).setTimestamp(ArgumentMatchers.anyInt(), ArgumentMatchers.any(Timestamp.class), calendarArgumentCaptor.capture());
            Assert.assertEquals(JdbcTimestampCustomTimeZoneTest.TIME_ZONE, calendarArgumentCaptor.getValue().getTimeZone());
        } catch (SQLException e) {
            Assert.fail(e.getMessage());
        }
        connectionProvider.clear();
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.doWork(( connection) -> {
                try (Statement st = connection.createStatement()) {
                    try (ResultSet rs = st.executeQuery("select createdOn from Person")) {
                        while (rs.next()) {
                            Timestamp timestamp = rs.getTimestamp(1);
                            int offsetDiff = (TimeZone.getDefault().getOffset(0)) - (TIME_ZONE.getOffset(0));
                            assertEquals(Math.abs(Long.valueOf(offsetDiff).longValue()), Math.abs(timestamp.getTime()));
                        } 
                    }
                }
            });
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

