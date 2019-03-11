/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.criteria;


import java.util.Optional;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaQuery;
import org.hamcrest.core.Is;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10950")
public class SessionCreateQueryFromCriteriaTest extends BaseCoreFunctionalTestCase {
    @Test
    public void testUniqueResult() {
        final String entityName = "expected";
        try (Session session = openSession()) {
            final CriteriaQuery<SessionCreateQueryFromCriteriaTest.TestEntity> query = createTestEntityCriteriaQuery(entityName, session);
            final Optional<SessionCreateQueryFromCriteriaTest.TestEntity> result = session.createQuery(query).uniqueResultOptional();
            Assert.assertThat(result.isPresent(), Is.is(false));
        }
    }

    @Test
    public void testStreamMethod() {
        final String entityName = "expected";
        insertTestEntity(entityName);
        try (Session session = openSession()) {
            final CriteriaQuery<SessionCreateQueryFromCriteriaTest.TestEntity> query = createTestEntityCriteriaQuery(entityName, session);
            final Stream<SessionCreateQueryFromCriteriaTest.TestEntity> stream = session.createQuery(query).stream();
            Assert.assertThat(stream.count(), Is.is(1L));
        }
    }

    @Test
    @SkipForDialect(value = AbstractHANADialect.class, comment = "HANA only supports forward-only cursors")
    public void testScrollMethod() {
        final String entityName = "expected";
        insertTestEntity(entityName);
        try (Session session = openSession()) {
            final CriteriaQuery<SessionCreateQueryFromCriteriaTest.TestEntity> query = createTestEntityCriteriaQuery(entityName, session);
            try (final ScrollableResults scroll = session.createQuery(query).scroll()) {
                Assert.assertThat(scroll.first(), Is.is(true));
            }
        }
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private long id;

        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}

