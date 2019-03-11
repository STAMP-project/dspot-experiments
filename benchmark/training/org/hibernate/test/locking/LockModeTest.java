/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.locking;


import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import org.hibernate.LockOptions;
import org.hibernate.dialect.SybaseASE15Dialect;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Make sure that directly specifying lock modes, even though deprecated, continues to work until removed.
 *
 * @author Steve Ebersole
 */
@TestForIssue(jiraKey = "HHH-5275")
@SkipForDialect(value = SybaseASE15Dialect.class, strictMatching = true, comment = "skip this test on Sybase ASE 15.5, but run it on 15.7, see HHH-6820")
public class LockModeTest extends BaseCoreFunctionalTestCase {
    private Long id;

    private CountDownLatch endLatch = new CountDownLatch(1);

    @Test
    @SuppressWarnings({ "deprecation" })
    public void testLoading() {
        // open a session, begin a transaction and lock row
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A it = session.byId(.class).with(LockOptions.UPGRADE).load(id);
            // make sure we got it
            assertNotNull(it);
            // that initial transaction is still active and so the lock should still be held.
            // Lets open another session/transaction and verify that we cannot update the row
            nowAttemptToUpdateRow();
        });
    }

    @Test
    public void testLegacyCriteria() {
        // open a session, begin a transaction and lock row
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A it = ((A) (session.createCriteria(.class).setLockMode(LockMode.PESSIMISTIC_WRITE).uniqueResult()));
            // make sure we got it
            assertNotNull(it);
            // that initial transaction is still active and so the lock should still be held.
            // Lets open another session/transaction and verify that we cannot update the row
            nowAttemptToUpdateRow();
        });
    }

    @Test
    public void testLegacyCriteriaAliasSpecific() {
        // open a session, begin a transaction and lock row
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A it = ((A) (session.createCriteria(.class).setLockMode("this", LockMode.PESSIMISTIC_WRITE).uniqueResult()));
            // make sure we got it
            assertNotNull(it);
            // that initial transaction is still active and so the lock should still be held.
            // Lets open another session/transaction and verify that we cannot update the row
            nowAttemptToUpdateRow();
        });
    }

    @Test
    public void testQuery() {
        // open a session, begin a transaction and lock row
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A it = ((A) (session.createQuery("from A a").setLockMode("a", LockMode.PESSIMISTIC_WRITE).uniqueResult()));
            // make sure we got it
            assertNotNull(it);
            // that initial transaction is still active and so the lock should still be held.
            // Lets open another session/transaction and verify that we cannot update the row
            nowAttemptToUpdateRow();
        });
    }

    @Test
    public void testQueryUsingLockOptions() {
        // todo : need an association here to make sure the alias-specific lock modes are applied correctly
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.createQuery("from A a").setLockOptions(new LockOptions(LockMode.PESSIMISTIC_WRITE)).uniqueResult();
            session.createQuery("from A a").setLockOptions(new LockOptions().setAliasSpecificLockMode("a", LockMode.PESSIMISTIC_WRITE)).uniqueResult();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-2735")
    public void testQueryLockModeNoneWithAlias() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // shouldn't throw an exception
            session.createQuery("SELECT a.value FROM A a where a.id = :id").setLockMode("a", LockMode.NONE).setParameter("id", id).list();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-2735")
    public void testQueryLockModePessimisticWriteWithAlias() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            // shouldn't throw an exception
            session.createQuery("SELECT MAX(a.id)+1 FROM A a where a.value = :value").setLockMode("a", LockMode.PESSIMISTIC_WRITE).setParameter("value", "it").list();
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12257")
    public void testRefreshLockedEntity() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A a = session.get(.class, id, LockMode.PESSIMISTIC_READ);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(.class.getName(), a);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, Collections.emptyMap());
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, null, Collections.emptyMap());
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12257")
    public void testRefreshWithExplicitLowerLevelLockMode() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A a = session.get(.class, id, LockMode.PESSIMISTIC_READ);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, LockMode.READ);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, LockModeType.READ);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, LockModeType.READ, Collections.emptyMap());
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12257")
    public void testRefreshWithExplicitHigherLevelLockMode() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A a = session.get(.class, id);
            checkLockMode(a, LockMode.READ, session);
            session.refresh(a, LockMode.UPGRADE_NOWAIT);
            checkLockMode(a, LockMode.UPGRADE_NOWAIT, session);
            session.refresh(a, LockModeType.PESSIMISTIC_READ);
            checkLockMode(a, LockMode.PESSIMISTIC_READ, session);
            session.refresh(a, LockModeType.PESSIMISTIC_WRITE, Collections.emptyMap());
            checkLockMode(a, LockMode.PESSIMISTIC_WRITE, session);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12257")
    public void testRefreshAfterUpdate() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            A a = session.get(.class, id);
            checkLockMode(a, LockMode.READ, session);
            a.setValue("new value");
            session.flush();
            checkLockMode(a, LockMode.WRITE, session);
            session.refresh(a);
            checkLockMode(a, LockMode.WRITE, session);
        });
    }
}

