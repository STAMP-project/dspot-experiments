/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.basic;


import java.util.Arrays;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jeremy Carnus
 * @author Guillaume Smet
 */
@TestForIssue(jiraKey = "HHH-12989")
public class InWithHeterogeneousCollectionTest extends BaseCoreFunctionalTestCase {
    @Test
    @RequiresDialect(H2Dialect.class)
    public void testCaseClause() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<org.hibernate.jpa.test.criteria.basic.Event> criteria = cb.createQuery(.class);
            Root<org.hibernate.jpa.test.criteria.basic.Event> eventRoot = criteria.from(.class);
            Path<String> namePath = eventRoot.get("name");
            Path<String> tagPath = eventRoot.get("tag");
            Expression<String> expression = cb.function("lower", .class, namePath);
            criteria.select(eventRoot);
            criteria.where(tagPath.in(Arrays.asList(expression, "my-tag")));
            List<org.hibernate.jpa.test.criteria.basic.Event> resultList = session.createQuery(criteria).getResultList();
            Assert.assertEquals(2, resultList.size());
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        private Long id;

        @Column
        private String name;

        @Column
        private String tag;

        protected Event() {
        }

        public Event(Long id, String name, String tag) {
            this.id = id;
            this.name = name;
            this.tag = tag;
        }

        public String getName() {
            return name;
        }

        public String getTag() {
            return tag;
        }
    }
}

