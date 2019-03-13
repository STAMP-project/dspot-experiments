/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.query.criteria.internal.expression;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vasyl Danyliuk
 */
public class SearchedCaseExpressionTest extends BaseCoreFunctionalTestCase {
    @Test
    @RequiresDialect(H2Dialect.class)
    public void testCaseClause() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.query.criteria.internal.expression.Event> criteria = cb.createQuery(.class);
            Root<org.hibernate.query.criteria.internal.expression.Event> event = criteria.from(.class);
            Path<org.hibernate.query.criteria.internal.expression.EventType> type = event.get("type");
            Expression<String> caseWhen = cb.<org.hibernate.query.criteria.internal.expression.EventType, String>selectCase(type).when(EventType.TYPE1, "Admin Event").when(EventType.TYPE2, "User Event").when(EventType.TYPE3, "Reporter Event").otherwise("");
            criteria.select(event);
            criteria.where(cb.equal(caseWhen, "Admin Event"));// OK when use cb.like() method and others

            List<org.hibernate.query.criteria.internal.expression.Event> resultList = session.createQuery(criteria).getResultList();
            Assert.assertNotNull(resultList);
        });
    }

    @Test
    @SkipForDialect(value = DB2Dialect.class, comment = "We would need casts in the case clauses. See HHH-12822.")
    public void testEqualClause() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.query.criteria.internal.expression.Event> criteria = cb.createQuery(.class);
            Root<org.hibernate.query.criteria.internal.expression.Event> event = criteria.from(.class);
            Path<org.hibernate.query.criteria.internal.expression.EventType> type = event.get("type");
            Expression<String> caseWhen = cb.<String>selectCase().when(cb.equal(type, EventType.TYPE1), "Type1").otherwise("");
            criteria.select(event);
            criteria.where(cb.equal(caseWhen, "Admin Event"));// OK when use cb.like() method and others

            List<org.hibernate.query.criteria.internal.expression.Event> resultList = session.createQuery(criteria).getResultList();
            Assert.assertNotNull(resultList);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13167")
    public void testMissingElseClause() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            org.hibernate.query.criteria.internal.expression.Event event = new org.hibernate.query.criteria.internal.expression.Event();
            event.id = 1L;
            event.type = EventType.TYPE1;
            session.persist(event);
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.query.criteria.internal.expression.Event> criteria = cb.createQuery(.class);
            Root<org.hibernate.query.criteria.internal.expression.Event> root = criteria.from(.class);
            Path<org.hibernate.query.criteria.internal.expression.EventType> type = root.get("type");
            Expression<String> caseWhen = cb.<String>selectCase().when(cb.equal(type, EventType.TYPE1), "Matched");
            criteria.select(root);
            criteria.where(cb.equal(caseWhen, "Matched"));
            org.hibernate.query.criteria.internal.expression.Event event = session.createQuery(criteria).getSingleResult();
            assertEquals(1L, ((long) (event.id)));
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        private Long id;

        @Column
        @Enumerated(EnumType.STRING)
        private SearchedCaseExpressionTest.EventType type;

        protected Event() {
        }

        public SearchedCaseExpressionTest.EventType getType() {
            return type;
        }

        public SearchedCaseExpressionTest.Event type(SearchedCaseExpressionTest.EventType type) {
            this.type = type;
            return this;
        }
    }

    public enum EventType {

        TYPE1,
        TYPE2,
        TYPE3;}
}

