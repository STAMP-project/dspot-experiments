/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.graphs.mapped_by_id;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 *
 *
 * @author Oliver Breidenbach
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoadGraphFindByIdTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-10842")
    @FailureExpected(jiraKey = "HHH-10842")
    public void findByPrimaryKeyWithId() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.jpa.test.graphs.mapped_by_id.User result = em.find(.class, 1L, createProperties(em));
            Assert.assertNotNull(result.userStatistic.commentCount);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10842")
    public void findByPrimaryKeyWithQuery() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            org.hibernate.jpa.test.graphs.mapped_by_id.User result = createTypedQuery(em).getSingleResult();
            Assert.assertNotNull(result.userStatistic.commentCount);
        });
    }

    @Entity(name = "UserStatistic")
    public static class UserStatistic {
        @Id
        private Long id;

        private Integer commentCount;
    }

    @Entity(name = "User")
    @Table(name = "USERS")
    public static class User {
        @Id
        private Long id;

        private String name;

        @OneToOne(fetch = FetchType.LAZY, optional = false)
        @MapsId
        private LoadGraphFindByIdTest.UserStatistic userStatistic;
    }
}

