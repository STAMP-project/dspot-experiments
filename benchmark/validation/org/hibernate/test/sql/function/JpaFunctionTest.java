/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.sql.function;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.hql.internal.ast.QuerySyntaxException;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
@TestForIssue(jiraKey = "HHH-11620")
@RequiresDialect(H2Dialect.class)
public class JpaFunctionTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testWithoutComma() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Date now = entityManager.createQuery(("select FUNCTION('now') " + ("from Event " + "where id = :id")), .class).setParameter("id", 1L).getSingleResult();
            log.infof("Current time: {}", now);
        });
    }

    @Test
    public void testWithoutCommaFail() {
        try {
            TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
                String result = entityManager.createQuery(("select FUNCTION('substring' 'abc', 1,2) " + ("from Event " + "where id = :id")), .class).setParameter("id", 1L).getSingleResult();
                fail("Should have thrown exception");
            });
        } catch (Exception e) {
            Assert.assertEquals(QuerySyntaxException.class, e.getCause().getClass());
        }
    }

    @Test
    public void testWithComma() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            Date now = entityManager.createQuery(("select FUNCTION('now',) " + ("from Event " + "where id = :id")), .class).setParameter("id", 1L).getSingleResult();
            log.infof("Current time: {}", now);
        });
    }

    @Entity(name = "Event")
    public static class Event {
        @Id
        private Long id;

        private Date createdOn;

        private String message;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

