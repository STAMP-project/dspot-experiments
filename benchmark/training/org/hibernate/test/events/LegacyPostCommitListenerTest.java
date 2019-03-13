/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.events;


import org.hibernate.IrrelevantEntity;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostDeleteEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test to ensure that the existing post commit behavior when using plain PostXEventListeners fire on both success and failure.
 *
 * @author ShawnClowater
 */
public class LegacyPostCommitListenerTest extends BaseCoreFunctionalTestCase {
    private PostInsertEventListener postCommitInsertEventListener = new LegacyPostCommitListenerTest.LegacyPostCommitInsertEventListener();

    private PostDeleteEventListener postCommitDeleteEventListener = new LegacyPostCommitListenerTest.LegacyPostCommitDeleteEventListener();

    private PostUpdateEventListener postCommitUpdateEventListener = new LegacyPostCommitListenerTest.LegacyPostCommitUpdateEventListener();

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitInsertListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitInsertEventListener) (postCommitInsertEventListener)).fired);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitInsertListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        // the legacy implementation fires the listener on failure as well
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitInsertEventListener) (postCommitInsertEventListener)).fired);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitUpdateListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session = openSession();
        transaction = session.beginTransaction();
        irrelevantEntity.setName("Irrelevant 2");
        session.update(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitUpdateEventListener) (postCommitUpdateEventListener)).fired);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitUpdateListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        irrelevantEntity.setName("Irrelevant 2");
        session.update(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        // the legacy implementation fires the listener on failure as well
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitUpdateEventListener) (postCommitUpdateEventListener)).fired);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitDeleteListenerSuccess() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        session.delete(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitDeleteEventListener) (postCommitDeleteEventListener)).fired);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1582")
    public void testPostCommitDeleteListenerRollback() {
        Session session = openSession();
        Transaction transaction = session.beginTransaction();
        IrrelevantEntity irrelevantEntity = new IrrelevantEntity();
        irrelevantEntity.setName("Irrelevant");
        session.save(irrelevantEntity);
        session.flush();
        transaction.commit();
        session.close();
        session = openSession();
        transaction = session.beginTransaction();
        session.delete(irrelevantEntity);
        session.flush();
        transaction.rollback();
        session.close();
        // the legacy implementation fires the listener on failure as well
        Assert.assertEquals(1, ((LegacyPostCommitListenerTest.LegacyPostCommitDeleteEventListener) (postCommitDeleteEventListener)).fired);
    }

    private class LegacyPostCommitDeleteEventListener implements PostDeleteEventListener {
        int fired;

        @Override
        public void onPostDelete(PostDeleteEvent event) {
            (fired)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }

    private class LegacyPostCommitUpdateEventListener implements PostUpdateEventListener {
        int fired;

        @Override
        public void onPostUpdate(PostUpdateEvent event) {
            (fired)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }

    private class LegacyPostCommitInsertEventListener implements PostInsertEventListener {
        int fired;

        @Override
        public void onPostInsert(PostInsertEvent event) {
            (fired)++;
        }

        @Override
        public boolean requiresPostCommitHanding(EntityPersister persister) {
            return true;
        }
    }
}

