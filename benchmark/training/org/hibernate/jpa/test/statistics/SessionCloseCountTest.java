/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.statistics;


import javax.persistence.EntityManager;
import org.hamcrest.core.Is;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.stat.Statistics;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-11602")
public class SessionCloseCountTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void sessionCountClosetShouldBeIncrementedWhenTheEntityManagerIsClosed() {
        final SessionFactoryImplementor entityManagerFactory = entityManagerFactory();
        final Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        EntityManager em = createEntityManager();
        Assert.assertThat("The session close count should be zero", statistics.getSessionCloseCount(), Is.is(0L));
        em.close();
        Assert.assertThat("The session close count was not incremented", statistics.getSessionCloseCount(), Is.is(1L));
    }
}

