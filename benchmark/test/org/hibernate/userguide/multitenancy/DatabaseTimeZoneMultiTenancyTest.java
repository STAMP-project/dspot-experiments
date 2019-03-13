/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.userguide.multitenancy;


import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.function.Consumer;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@RequiresDialect(H2Dialect.class)
public class DatabaseTimeZoneMultiTenancyTest extends BaseUnitTestCase {
    protected static final String FRONT_END_TENANT = "front_end";

    protected static final String BACK_END_TENANT = "back_end";

    // tag::multitenacy-hibernate-timezone-configuration-context-example[]
    private Map<String, ConnectionProvider> connectionProviderMap = new HashMap<>();

    private Map<String, TimeZone> timeZoneTenantMap = new HashMap<>();

    // end::multitenacy-hibernate-timezone-configuration-context-example[]
    private SessionFactory sessionFactory;

    public DatabaseTimeZoneMultiTenancyTest() {
        init();
    }

    // end::multitenacy-hibernate-timezone-configuration-registerConnectionProvider-example[]
    @Test
    public void testBasicExpectedBehavior() {
        // tag::multitenacy-hibernate-applying-timezone-configuration-example[]
        doInSession(DatabaseTimeZoneMultiTenancyTest.FRONT_END_TENANT, ( session) -> {
            DatabaseTimeZoneMultiTenancyTest.Person person = new DatabaseTimeZoneMultiTenancyTest.Person();
            person.setId(1L);
            person.setName("John Doe");
            person.setCreatedOn(LocalDateTime.of(2018, 11, 23, 12, 0, 0));
            session.persist(person);
        }, true);
        doInSession(DatabaseTimeZoneMultiTenancyTest.BACK_END_TENANT, ( session) -> {
            DatabaseTimeZoneMultiTenancyTest.Person person = new DatabaseTimeZoneMultiTenancyTest.Person();
            person.setId(1L);
            person.setName("John Doe");
            person.setCreatedOn(LocalDateTime.of(2018, 11, 23, 12, 0, 0));
            session.persist(person);
        }, true);
        doInSession(DatabaseTimeZoneMultiTenancyTest.FRONT_END_TENANT, ( session) -> {
            Timestamp personCreationTimestamp = ((Timestamp) (session.createNativeQuery(("select p.created_on " + ("from Person p " + "where p.id = :personId"))).setParameter("personId", 1L).getSingleResult()));
            Assert.assertEquals(Timestamp.valueOf(LocalDateTime.of(2018, 11, 23, 12, 0, 0)), personCreationTimestamp);
        }, true);
        doInSession(DatabaseTimeZoneMultiTenancyTest.BACK_END_TENANT, ( session) -> {
            Timestamp personCreationTimestamp = ((Timestamp) (session.createNativeQuery(("select p.created_on " + ("from Person p " + "where p.id = :personId"))).setParameter("personId", 1L).getSingleResult()));
            Assert.assertEquals(Timestamp.valueOf(LocalDateTime.of(2018, 11, 23, 12, 0, 0)), personCreationTimestamp);
        }, true);
        // end::multitenacy-hibernate-applying-timezone-configuration-example[]
        // tag::multitenacy-hibernate-not-applying-timezone-configuration-example[]
        doInSession(DatabaseTimeZoneMultiTenancyTest.FRONT_END_TENANT, ( session) -> {
            Timestamp personCreationTimestamp = ((Timestamp) (session.createNativeQuery(("select p.created_on " + ("from Person p " + "where p.id = :personId"))).setParameter("personId", 1L).getSingleResult()));
            log.infof("The created_on timestamp value is: [%s]", personCreationTimestamp);
            long timeZoneOffsetMillis = (Timestamp.valueOf(LocalDateTime.of(2018, 11, 23, 12, 0, 0)).getTime()) - (personCreationTimestamp.getTime());
            Assert.assertEquals(TimeZone.getTimeZone(ZoneId.systemDefault()).getRawOffset(), timeZoneOffsetMillis);
            log.infof("For the current time zone: [%s], the UTC time zone offset is: [%d]", TimeZone.getDefault().getDisplayName(), timeZoneOffsetMillis);
        }, false);
        // end::multitenacy-hibernate-not-applying-timezone-configuration-example[]
    }

    // end::multitenacy-hibernate-timezone-configuration-session-example[]
    @Entity(name = "Person")
    public static class Person {
        @Id
        private Long id;

        private String name;

        @Column(name = "created_on")
        private LocalDateTime createdOn;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public LocalDateTime getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(LocalDateTime createdOn) {
            this.createdOn = createdOn;
        }
    }
}

