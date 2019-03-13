/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.subquery;


import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@TestForIssue(jiraKey = "HHH-13111")
public class SubqueryInSelectClauseJpaComplianceTest extends AbstractSubqueryInSelectClauseTest {
    @Test(expected = IllegalStateException.class)
    public void testSubqueryInSelectClause() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> query = cb.createTupleQuery();
            Root<Document> document = query.from(.class);
            Join<?, ?> contacts = document.join("contacts", JoinType.LEFT);
            Subquery<Long> personCount = query.subquery(.class);
            Root<Person> person = personCount.from(.class);
            personCount.select(cb.count(person)).where(cb.equal(contacts.get("id"), person.get("id")));
            query.multiselect(document.get("id"), personCount.getSelection());
            em.createQuery(query).getResultList();
        });
    }
}

